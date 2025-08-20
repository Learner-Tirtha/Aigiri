require('dotenv').config();
const express = require('express');
const twilio = require('twilio');

const app = express();
const port = 3000;

// Middleware to parse JSON request bodies
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Twilio client
const client = twilio(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);

// POST /send-otp — expects JSON body
app.post('/send-otp', async (req, res) => {
    const { phoneNumber, otp } = req.body;

    if (!phoneNumber || !otp) {
        return res.status(400).send("Missing phoneNumber or otp");
    }

    console.log('Received phoneNumber:', phoneNumber);

    // Sanitize phone number
    let sanitizedPhoneNumber = phoneNumber.replace(/\s/g, '');
    if (!sanitizedPhoneNumber.startsWith('+')) {
        sanitizedPhoneNumber = `+${sanitizedPhoneNumber}`;
    }

    try {
        const message = await client.messages.create({
            body: `Your SafeDisclose OTP is: ${otp}`,
            from: process.env.TWILIO_PHONE_NUMBER,
            to: sanitizedPhoneNumber
        });

        console.log(`Message SID: ${message.sid}`);
        res.status(200).send('OTP sent successfully');
    } catch (error) {
        console.error('Twilio Error:', error);
        res.status(500).send(`Failed to send OTP: ${error.message}`);
    }
});

// Start server
app.listen(port, '0.0.0.0', () => {
    console.log(`Server running on port ${port}`);
});
