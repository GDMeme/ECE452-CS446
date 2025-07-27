// Use CommonJS for compatibility with Firebase
const functions = require("firebase-functions/v2/auth");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

exports.createUserProfile = functions.onAuthUserCreated(async (event) => {
  const user = event.data;
  if (!user) {
    console.log("No user data.");
    return;
  }

  const userId = user.uid;
  const username = user.displayName || `user_${userId.substring(0, 8)}`;
  const initialXP = 0;

  const userDocRef = db.collection("users").doc(userId);

  try {
    await userDocRef.set({
      userId: userId,
      username: username,
      userXP: initialXP,
      createdAt: user.metadata?.creationTime || new Date().toISOString(),
      email: user.email || null,
    });

    console.log(`User profile created for ${userId}`);
  } catch (err) {
    console.error(`Failed to create profile:`, err);
  }
});
