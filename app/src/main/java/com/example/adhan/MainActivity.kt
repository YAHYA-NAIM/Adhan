 package com.example.adhan

 import android.os.Bundle
 import androidx.appcompat.app.AppCompatActivity
 import androidx.fragment.app.Fragment
 import com.google.android.material.bottomnavigation.BottomNavigationView

 class MainActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         if (savedInstanceState == null) {
             openFragment(Home())
         }

         val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

         bottomNav.setOnItemSelectedListener { item ->
             when (item.itemId) {
                 R.id.home -> {
                     openFragment(Home())
                     true
                 }
                 R.id.qibla -> {
                     openFragment(qibla())
                     true
                 }
                 else -> false
             }
         }
     }
     private fun openFragment(fragment: Fragment) {
         supportFragmentManager.beginTransaction()
             .replace(R.id.framelayout, fragment)
             .commit()
     }
 }
