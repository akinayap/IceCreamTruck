package com.akinayap.icecreamtruck.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.akinayap.icecreamtruck.R
import com.akinayap.icecreamtruck.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var viewBinding: ActivityMainBinding

    private var topLevelDest = setOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        topLevelDest = setOf(
            R.id.dest_login,
            R.id.dest_chat,
            R.id.dest_more
        )
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.app_navigation)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(topLevelDest)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemReselectedListener { item ->
            if (!topLevelDest.contains(navController.currentDestination?.id))
                NavigationUI.onNavDestinationSelected(item, navController)
        }
        checkUserExists()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack()
                }
            })
    }


    private fun checkUserExists() {
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            // TODO: Should switch to login fragment
            auth.signInWithEmailAndPassword("akina.yap@gmail.com", "Akina2N1").addOnCompleteListener(this){}
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return goBack() //navController.navigateUp(appBarConfiguration)
    }

    private fun goBack(): Boolean {
        Log.e("Curr: ", navController.currentDestination?.id.toString())
        if (topLevelDest.contains(navController.currentDestination?.id)) {
            finish()
            return true
        }
        return navController.navigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}