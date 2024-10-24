package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.ui.HistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Cek apakah pengguna sudah login
        if (auth.currentUser != null) {
            // Inisialisasi BottomNavigationView
            bottomNavigationView = findViewById(R.id.bottom_navigation)

            // Set default fragment (Home)
            loadFragment(HomeFragment())

            // Listener untuk BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_homeFragment -> {
                        loadFragment(HomeFragment())
                        true
                    }
                    R.id.nav_historyFragment -> {
                        loadFragment(HistoryFragment())
                        true
                    }
                    R.id.nav_profileFragment -> {
                        loadFragment(ProfileFragment())
                        true
                    }
                    else -> false
                }
            }
        } else {
            // Jika belum login, buka halaman login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Menutup MainActivity agar tidak dapat diakses kembali sebelum login
        }
    }

    // Fungsi untuk mengganti fragment
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
