package com.example.schedula

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//The AuthRepository Singleton
object AuthenticationRepo {
    private val auth = Firebase.auth

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if(task.isSuccessful) {
                onResult(true, null)
            }
            else {
                onResult(false, task.exception?.message)
            }
        }
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if(task.isSuccessful) {
                onResult(true, null)
            }
            else {
                onResult(false, task.exception?.message)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }


    //function for getting the name of the current user

}