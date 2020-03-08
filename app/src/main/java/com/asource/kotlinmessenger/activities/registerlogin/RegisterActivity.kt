package com.asource.kotlinmessenger.activities.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asource.kotlinmessenger.R
import com.asource.kotlinmessenger.activities.messages.LatestMessagesActivity
import com.asource.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            select_photo_imageview_register.setImageBitmap(bitmap)
            select_photo_button_register.alpha = 0f

        }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text email/pass", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(!it.isSuccessful) return@addOnCompleteListener
            uploadImageToFireBaseStorage()
        }.addOnFailureListener {
            Log.d("MainActivity", "Fail to create user ${it.message}")
            Toast.makeText(this, "Fail to create user ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFireBaseStorage() {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        var ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("MainActivity", it.metadata?.path)
            ref.downloadUrl.addOnSuccessListener {
                Log.d("MainActivity", it.toString())
                saveUserToFirebaseDatabase(it.toString())
            }
        }.addOnFailureListener {
            Log.d("MainActivity", it.message)
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user).addOnSuccessListener {
            Log.d("MainActivity", "Usuario ${user} añadido correctamente")

            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }.addOnFailureListener {
            Log.d("MainActivity", it.message)
        }
    }
}
