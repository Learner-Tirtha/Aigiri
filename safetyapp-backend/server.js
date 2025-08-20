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
const firebaseConfig = {
      apiKey: "AIzaSyCkJQ_lWGO_b7Y4KcPGqN5w-z7RKkk_NjE",
      authDomain: "uninotify-3e948.firebaseapp.com",
      databaseURL:
        "https://uninotify-3e948-default-rtdb.asia-southeast1.firebasedatabase.app",
      projectId: "uninotify-3e948",
      storageBucket: "uninotify-3e948.appspot.com",
      messagingSenderId: "696692515833",
      appId: "1:696692515833:web:d1ab7c5ae955feec8e9328",
    };
    firebase.initializeApp(firebaseConfig);
    const Realtimedb = firebase.database();
const app = express();
app.use(cors());
app.use(express.json());


// Environment variables
const {
  JWT_SECRET,
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
app.post("/send-sos", async (req, res) => {
    console.log("IN Send SOS");
    try {
        const { userId, message } = req.body;

        if (!userId || !message) {
            console.log("Empty");
            return res.status(400).json({ error: "Missing userId or message" });
        }

        // Fetch emergency contacts from Firestore
        const contactsSnapshot = await db
            .collection("User")
            .doc(userId)
            .collection("emergency_contacts")
            .orderBy("priority", "asc")
            .get();

        if (contactsSnapshot.empty) {
            console.log("empty");
            return res.status(404).json({ error: "No emergency contacts found" });
        }
        const script = `Hello, this is an important safety alert. You are listed as a trusted contact. Please check your phone messages immediately.she may need your help. She is waiting for you.Once again, you are her trusted person. Please check your phone now.`;
        console.log("Enter in SOS");

        const phoneNumbers = contactsSnapshot.docs.map(doc => doc.data().phoneNumber);

        // Send SMS and Voice Call to each contact
        const sendAlerts = phoneNumbers.map(async phone => {
            // Send SMS
            await twilioClient.messages.create({
                body: message,
                from: TWILIO_PHONE_NUMBER,
                to: phone
            });

            // Make Voice Call
            await twilioClient.calls.create({
                twiml: `<Response><Say voice="alice" language="en-IN">${script}</Say></Response>`,
                from: TWILIO_PHONE_NUMBER,
                to: phone
            });
        });

        await Promise.all(sendAlerts);

        // Log the alert
        await db.collection("sos_alerts").add({
            userId,
            message,
            timestamp: new Date().toISOString()
        });

        console.log(`🚨 SOS sent via SMS and Voice to ${phoneNumbers.length} contacts`);
        res.status(200).json({ success: true });

    } catch (err) {
        console.error("Failed to send SOS:", err);
        res.status(500).json({ error: "Failed to send SOS" });
    }
});
app.post("/send-sos", async (req, res) => {
    console.log("IN Send SOS");
    try {
        const { userId } = req.body;

        if (!userId) {
            console.log("Empty");
            return res.status(400).json({ error: "Missing userId" });
        }

        const userRef = Realtimedb.ref(`/users/${userId}`);
        const userSnap = await userRef.get();
        let sessionId;

        if (userSnap.exists()) {
            const userData = userSnap.val();

            if (userData.isActive) {
                // Already active: update mode to "sos"
                sessionId = userData.sessionId;
                await userRef.update({ mode: "sos" });
            } else {
                // Not active: create new session
                if (userData.sessionId) {
                    await Realtimedb.ref(`/sos_session/${userData.sessionId}`).remove();
                }
                sessionId = `session_${Date.now()}`;
                await userRef.update({ isActive: true, sessionId, mode: "sos" });
                await Realtimedb.ref(`/sos_session/${sessionId}/users/${userId}`).set({});
            }
        } else {
            // First time user
            sessionId = `session_${Date.now()}`;
            await userRef.set({ isActive: true, sessionId, mode: "sos" });
            await Realtimedb.ref(`/sos_session/${sessionId}/users/${userId}`).set({});
        }

        // Fetch emergency contacts from Firestore
        const contactsSnapshot = await db
            .collection("User")
            .doc(userId)
            .collection("emergency_contacts")
            .orderBy("priority", "asc")
            .get();

        const userDoc = await db.collection("User").doc(userId).get();
        const phoneNumber = userDoc.exists ? userDoc.data().phoneNo : null;

        if (contactsSnapshot.empty) {
            console.log("No emergency contacts found");
            return res.status(404).json({ error: "No emergency contacts found" });
        }

        const script = `Hello, this is an important safety alert. You are listed as a trusted contact. Please check your phone messages immediately. She may need your help. She is waiting for you. Once again, you are her trusted person. Please check your phone now.`;

        const phoneNumbers = contactsSnapshot.docs.map(doc => doc.data().phoneNumber);
        const message = `Emergency SOS Alert: "I am in danger and need your help\nMy Phone Number: ${phoneNumber}\nLive_Location: https://uninotify-3e948.web.app/live-location-osm/?sessionId=${sessionId}&userId=${userId}"`;

        // Send SMS and Voice Call to each contact
        const sendAlerts = phoneNumbers.map(async phone => {
            await twilioClient.messages.create({
                body: message,
                from: TWILIO_PHONE_NUMBER,
                to: phone
            });

            await twilioClient.calls.create({
                twiml: `<Response><Say voice="alice" language="en-IN">${script}</Say></Response>`,
                from: TWILIO_PHONE_NUMBER,
                to: phone
            });
        });

        await Promise.all(sendAlerts);

        // Log the alert
        await db.collection("sos_alerts").add({
            userId,
            message,
            timestamp: new Date().toISOString()
        });

        console.log(`🚨 SOS sent via SMS and Voice to ${phoneNumbers.length} contacts`);
        res.status(200).json({ success: true });

    } catch (err) {
        console.error("Failed to send SOS:", err);
        res.status(500).json({ error: "Failed to send SOS" });
    }
});

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
