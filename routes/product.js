const express = require("express");
const router = express.Router();
const QRCode = require("qrcode");
const { db } = require("../firebase");

// Add product route
router.post("/addProduct", async (req, res) => {
  try {
    const { name, price, photoBase64 } = req.body;

    // Generate QR code as base64 string (data URL)
    const qrDataUrl = await QRCode.toDataURL(name);
    // Strip the data URL prefix and store pure Base64 to match the Java service
    const qrBase64 = qrDataUrl.replace(/^data:image\/[a-z]+;base64,/, "");

    // Normalize photoBase64: accept either data URL or pure Base64
    let purePhotoBase64 = null;
    if (photoBase64) {
      purePhotoBase64 = photoBase64.replace(/^data:image\/[a-z]+;base64,/, "");
    }

    // Save to Firestore
    const docRef = await db.collection("products").add({
      name,
      price,
      qrCodeBase64: qrBase64,
      photoBase64: purePhotoBase64,
      createdAt: new Date()
    });

    res.json({ id: docRef.id, name, price, qrCodeBase64: qrBase64, photoBase64: purePhotoBase64 });
  } catch (err) {
    console.error(err);
    res.status(500).send("Error saving product");
  }
});

module.exports = router;
