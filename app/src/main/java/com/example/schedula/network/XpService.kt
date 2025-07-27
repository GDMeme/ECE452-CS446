import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun updateUserXP(xpDelta: Int, onComplete: ((Boolean) -> Unit)? = null) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    if (currentUser != null) {
        val userRef = db.collection("users").document(currentUser.uid)

        userRef.update("userXP", FieldValue.increment(xpDelta.toLong()))
            .addOnSuccessListener {
                Log.d("XP_UPDATE", "Incremented XP by $xpDelta for user ${currentUser.uid}")
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e("XP_UPDATE", "Failed to increment XP: ${e.localizedMessage}")
                onComplete?.invoke(false)
            }
    } else {
        Log.e("XP_UPDATE", "No authenticated user.")
        onComplete?.invoke(false)
    }
}
