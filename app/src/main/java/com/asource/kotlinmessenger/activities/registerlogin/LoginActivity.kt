package com.asource.kotlinmessenger.activities.registerlogin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asource.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.elevation = 0f

        login_button_login.setOnClickListener {
            var email = email_editText_login.text.toString()
            var pass = password_editText3_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnSuccessListener {

            }.addOnFailureListener {
                Log.d("LoginActivity", "Fallo al loggear: ${it.message}")
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        back_to_register_textView.setOnClickListener {
            finish()
        }

    }
}