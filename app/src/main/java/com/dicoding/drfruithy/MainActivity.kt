package com.dicoding.drfruithy
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.drfruithy.data.pref.UserPreference
import com.dicoding.drfruithy.databinding.ActivityMainBinding
import com.dicoding.drfruithy.di.Injection
import com.dicoding.drfruithy.ui.profile.ProfileViewModel
import com.dicoding.drfruithy.ui.profile.dataStore
import com.dicoding.drfruithy.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_article, R.id.navigation_saved, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Observe theme settings and apply dark mode
        darkMode()
    }

    private fun darkMode() {
        val preferences = applicationContext.dataStore
        val userPreference = UserPreference.getInstance(preferences)
        val repository = Injection.provideRepository(this)
        val factory = ViewModelFactory(repository)
        val profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        profileViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()

        // Check if current destination is navigation_profile
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        if (navController.currentDestination?.id == R.id.navigation_profile) {
            supportActionBar?.apply {
                title = "Pengaturan"
                displayOptions = ActionBar.DISPLAY_SHOW_TITLE
                setDisplayShowTitleEnabled(true)
            }
        }
    }
}

