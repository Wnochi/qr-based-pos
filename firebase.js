const admin = require("firebase-admin");
const serviceAccount = require("./qr-pos-system-214e4-firebase-adminsdk-fbsvc-1f5a1d694b.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: "qr-pos-system.appspot.com"
});

const db = admin.firestore();
const bucket = admin.storage().bucket();

// Export both Firestore `db` and `bucket` for other modules to use
module.exports = { db, bucket };
