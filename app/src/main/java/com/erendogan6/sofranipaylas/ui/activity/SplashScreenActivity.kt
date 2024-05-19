package com.erendogan6.sofranipaylas.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.erendogan6.sofranipaylas.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Oturum kontrolü yap
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Kullanıcı oturum açmış, ana sayfaya yönlendir
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            // Kullanıcı oturum açmamış, giriş sayfasına yönlendir
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
