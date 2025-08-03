require("dotenv").config();
const express = require("express");
const admin = require("firebase-admin");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");
const { AccessToken } = require("livekit-server-sdk");
const { v4: uuidv4 } = require("uuid");
const cors = require("cors");
const twilio = require("twilio");

admin.initializeApp({
  credential: admin.credential.cert(require(process.env.GOOGLE_APPLICATION_CREDENTIALS)),
});
const db = admin.firestore();

const app = express();
app.use(cors());
app.use(express.json());


// Environment variables
const {
  JWT_SECRET,
  LIVEKIT_API_KEY,
  LIVEKIT_API_SECRET,
  LIVEKIT_WS_URL,
  TWILIO_ACCOUNT_SID,
  TWILIO_AUTH_TOKEN,
  TWILIO_PHONE_NUMBER,
} = process.env;

const twilioClient = twilio(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
// Login endpoint
app.post("/login", async (req, res) => {
     try {
         const { identifier, password } = req.body;

         // Query Firestore for user with identifier (e.g. username or email)
         const userQuery = await db
             .collection("User")
             .where("username", "==", identifier)
             .limit(1)
             .get();

         if (userQuery.empty) {
             return res.status(401).json({ error: "Invalid credentials" });
         }

         const userDoc = userQuery.docs[0];
         const userData = userDoc.data();

         // userData.password is assumed hashed with bcrypt
         const validPassword = await bcrypt.compare(password, userData.password);

         if (!validPassword) {
             console.log("Received password:", JSON.stringify(password));
             console.log("Hash from Firestore:", userData.password);
             return res.status(401).json({ error: password });
         }

         // Generate JWT token
         const token = jwt.sign(
             {
                 userId: userDoc.id,
                 identifier: userData.identifier,
             },
             JWT_SECRET,
             { expiresIn: "7d" }
         );

         // Send token and userId to client
         res.json({ token, userId: userDoc.id });
     } catch (error) {
         console.error("Login error", error);
         res.status(500).json({ error: "Internal server error" });
    }

 });
app.post("/start-session", async (req, res) => {
  const { jwt: userJwt, userId } = req.body;

  if (!userJwt || !userId) {
    return res.status(400).json({ message: "JWT and userId required" });
  }

  try {
    const decoded = jwt.verify(userJwt, JWT_SECRET);

    if (decoded.userId !== userId) {
      return res.status(403).json({ message: "Token does not match user ID" });
    }

    const roomId = uuidv4();
    const timestamp = new Date().toISOString();

      const accessToken = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET, {
          identity: `user-${userId}`,
      });
      accessToken.addGrant({
          roomJoin: true,
          room: roomId,
          canPublish: true,
          canSubscribe: true,
      });

      const token = await accessToken.toJwt(); 

      const joinUrl = `${LIVEKIT_WS_URL}/?room=${roomId}&user=user-${userId}&token=${token}`;

    const sessionData = {
      userId,
      roomId,
      sessionId: "",
      joinUrl,
      timestamp,
      recordingUrl: null,
    };

    const docRef = await db.collection("live_sessions").add(sessionData);
    await docRef.update({ sessionId: docRef.id });

    const contactsSnapshot = await db
      .collection(`User/${userId}/emergency_contacts`)
      .get();

    const contacts = contactsSnapshot.docs.map(doc => doc.data());

    // Send SMS via Twilio
    //for (const contact of contacts) {
    //  const smsMessage = ` Emergency Alert!\nJoin live session: ${joinUrl}`;

    //  try {
    //    await twilioClient.messages.create({
    //      body: smsMessage,
    //      from: TWILIO_PHONE_NUMBER,
    //        to: contact.phoneNumber, // Make sure this is in E.164 format (e.g. +91xxxxxxxxxx)
    //    });

    //    console.log(` SMS sent to ${contact.name} at ${contact.phoneNumber}`);
    //  } catch (smsErr) {
    //    console.error(` Failed to send SMS to ${contact.phoneNo}:`, smsErr.message);
    //  }
    //}

      const wsUrl = LIVEKIT_WS_URL; // Base websocket URL without params
      console.log(token, docRef.id);
      return res.status(200).json({
          message: "Live session started and contacts alerted via SMS.",
          sessionId: docRef.id,
          joinUrl,
          token,    
          wsUrl     
      });

  } catch (err) {
    console.error("Error in /start-session:", err);
    return res.status(500).json({
      message: "Internal Server Error",
      error: err.message,
    });
  }
});
app.post("/livekit-webhook", async (req, res) => {
  try {
    const { event, room_id, url } = req.body;

    if (event === "recording.finished" && room_id && url) {
      // Search for the session by roomId
      const q = db.collection("live_sessions").where("roomId", "==", room_id);
      const snapshot = await q.get();

      if (snapshot.empty) {
        console.warn(`No session found for roomId: ${room_id}`);
        return res.status(404).json({ message: "Session not found" });
      }

      const docRef = snapshot.docs[0].ref;

      await docRef.update({
        recordingUrl: url,
        endedAt: new Date().toISOString(),
      });

      console.log(`✅ Recording URL updated for room: ${room_id}`);
      return res.status(200).json({ message: "Recording updated via webhook" });
    }

    return res.status(400).json({ message: "Unhandled event or missing fields" });
  } catch (error) {
    console.error("Webhook processing failed:", error);
    return res.status(500).json({ message: "Server error" });
  }
});
app.get("/profile", async (req, res) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader?.startsWith("Bearer ")) {
      return res.status(401).json({ error: "Unauthorized" });
    }

    const token = authHeader.substring(7);
    const decoded = jwt.verify(token, JWT_SECRET);

    const userDoc = await db.collection("User").doc(decoded.userId).get();
    if (!userDoc.exists) {
      return res.status(404).json({ error: "User not found" });
    }

    res.json(userDoc.data());
  } catch (error) {
    console.error("Profile error", error);
    res.status(401).json({ error: "Invalid or expired token" });
  }
});

app.post("/send-sos", async (req, res) => {
       console.log("IN Send SOS")
    try {
        const { userId, message } = req.body;

        if (!userId || !message) {
            console.log("Empty")
            return res.status(400).json({ error: "Missing userId or message" });
        }

        // Fetch emergency contacts from Firestore subcollection
        const contactsSnapshot = await db
            .collection("User")
            .doc(userId)
            .collection("emergency_contacts")
            .orderBy("priority", "asc") // Optional: sort by priority
            .get();

        if (contactsSnapshot.empty) {
            console.log("empty")
            return res.status(404).json({ error: "No emergency contacts found" });
        }
        console.log("Enter in SOS")
        const phoneNumbers = contactsSnapshot.docs.map(doc => doc.data().phoneNumber);

        // Send SOS message to each contact
        const sendMessages = phoneNumbers.map(phone => {
            return twilioClient.messages.create({
                body: message,
                from: TWILIO_PHONE_NUMBER,
                to: phone
            });
        });

        await Promise.all(sendMessages);

        // Store the SOS record (optional)
        await db.collection("sos_alerts").add({
            userId,
            message,
            timestamp: new Date().toISOString()
        });

        console.log(`🚨 SOS sent to ${phoneNumbers.length} contacts`);
        res.status(200).json({ success: true });

    } catch (err) {
        console.error("Failed to send SOS:", err);
        res.status(500).json({ error: "Failed to send SOS" });
    }
});
const PORT = process.env.PORT || 3001;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
