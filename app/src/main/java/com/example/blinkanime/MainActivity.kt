package com.example.blinkanime

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.blinkanime.databinding.ActivityMainBinding
import com.example.blinkanime.mainFragment.MainPageFragment
import com.example.blinkanime.misc.*
import com.example.blinkanime.newsFragment.NewsPageFragment
import com.example.blinkanime.scheduleFragment.SchedulePageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.blinkanime.animeRecommendationActivity.AnimeRecommendation
import com.example.blinkanime.animelist.AnimeListActivity
import com.example.blinkanime.mainFragment.AnimeScraper
import com.example.blinkanime.mainFragment.MainViewModel
import com.example.blinkanime.mainFragment.RetrofitInstance
import com.example.blinkanime.seasonalanime.SeasonalAnimeActivity
import com.example.blinkanime.topanimes.TopAnimeActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope

import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomBar: BottomNavigationView

    private lateinit var mainPageFragment: MainPageFragment
    private lateinit var newsPageFragment: NewsPageFragment
    private lateinit var schedulePageFragment: SchedulePageFragment

    // sharedPreference
    private val sharedPrefrence: SharedPreferences by lazy {
        getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    }

    private var pickedFragment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        pickedFragment = intent.getStringExtra("fragment") ?: ""

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        bottomBar = binding.bottomNavigation



        // request post notification permission
        requestPostNotificationPermission()
        // add settings menu to toolbar
        addSettingsMenu()
        // setup action bar toolbar
        setupSideBar()
        // setup navigation bar
        setupNavigationBar()
        // switch Tabs based on intent
        switchTabs(pickedFragment)
        // Update Real-debrid data
        updateRealDebrid()
        // setup site settings
        setupSiteSettings()
        // setup app theme
        setupAppTheme()


//       CoroutineScope(lifecycleScope.coroutineContext).launch {
//            val animeList = MALScraper().getAllAnimes()
//           Log.e("TAG", "onCreate: $animeList" )
//        }

        // Cancel all previous work and start scheduleInitialWork
        WorkManager.getInstance(this).cancelAllWork()
        scheduleInitialWork(this)

        // log unhandled exceptions
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            LogError().logError(this, getStackTrace(e))
        }

        mainActivityViewModel.updateLoadingData(true)
        CoroutineScope(lifecycleScope.coroutineContext).launch {
            val animeList = AnimeScraper().getAllAnimes()
            mainActivityViewModel.updateAnimeList(animeList?: emptyList())
            mainActivityViewModel.updateLoadingData(false)
        }
    }

    private fun setupAppTheme() {
        val textLayout = binding.navigationView.menu.findItem(R.id.theme_settings).actionView as? TextInputLayout
        val dropDown = (textLayout?.editText as? AutoCompleteTextView)
        val items = arrayOf("dark", "light")
        (textLayout?.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)

        //get current site from sharedPreference
        val theme = sharedPrefrence.getString("theme", "light")
        dropDown?.setText(theme, false)
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        dropDown?.setOnItemClickListener { parent, _, position, _ ->
            val theme = parent.getItemAtPosition(position).toString()
            sharedPrefrence.edit().putString("theme", theme).apply()
            AppCompatDelegate.setDefaultNightMode(
                when (theme) {
                    "light" -> AppCompatDelegate.MODE_NIGHT_NO
                    "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        }


    }

    private fun setupSiteSettings() {
        val textLayout = binding.navigationView.menu.findItem(R.id.site_settings).actionView as? TextInputLayout
        val dropDown = textLayout?.editText as? AutoCompleteTextView
        val items = arrayOf("witanime", "anime4up")
        (textLayout?.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        //get current site from sharedPreference
        val currentSite = sharedPrefrence.getString("currentSite", "witanime") ?: "witanime"
        dropDown?.setText(currentSite, false)

        when (currentSite) {
            "witanime" ->  RetrofitInstance.updateBaseUrl("https://witanime.com/")
            "anime4up" -> RetrofitInstance.updateBaseUrl("https://w1.anime4up.tv/")
            else -> RetrofitInstance.updateBaseUrl("https://witanime.com/")
        }

        dropDown?.setOnItemClickListener { parent, _, position, _ ->
            val site = parent.getItemAtPosition(position).toString()
            sharedPrefrence.edit().putString("currentSite", site).apply()
            when (site) {
                "witanime" -> RetrofitInstance.updateBaseUrl("https://witanime.com/")
                "anime4up" -> RetrofitInstance.updateBaseUrl("https://w1.anime4up.tv/")
                else -> RetrofitInstance.updateBaseUrl("https://witanime.com/")
            }
        }
    }

    private fun addSettingsMenu() {
        val settingsMenu = binding.toolbar.menu
        settingsMenu.add(0, 0, 0, "Settings")
        settingsMenu.getItem(0).setOnMenuItemClickListener {
            true
        }
    }

    // get a string representation of an exception stack trace
    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }

    // Switch Tabs based on intent
    private fun requestPostNotificationPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    // Set up Side bar with menu items
    private fun setupSideBar() {
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.animelist -> {
                    // start anime list activity
                    val intent = Intent(this, AnimeListActivity::class.java)
                    startActivity(intent)
                }
                R.id.animeRecom -> {
                    // start anime list activity
                    val intent = Intent(this, AnimeRecommendation::class.java)
                    startActivity(intent)
                }
                R.id.animeTop -> {
                    // start anime list activity
                    val intent = Intent(this, TopAnimeActivity::class.java)
                    startActivity(intent)
                }
                R.id.seasonalAnime -> {
                    // start anime list activity
                    val intent = Intent(this, SeasonalAnimeActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // set up Navigation bar
    private fun setupNavigationBar() {
        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (!this::mainPageFragment.isInitialized) {
                        mainPageFragment = MainPageFragment()
                    }
                    replaceFragment(mainPageFragment, "Latest Episodes")
                    true
                }
                R.id.navigation_schedule -> {
                    if (!this::schedulePageFragment.isInitialized) {
                        schedulePageFragment = SchedulePageFragment()
                    }
                    replaceFragment(schedulePageFragment, "Anime Schedule")
                    true
                }
                R.id.navigation_news -> {
                    if (!this::newsPageFragment.isInitialized) {
                        newsPageFragment = NewsPageFragment()
                    }
                    replaceFragment(newsPageFragment, "Anime News")
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        supportActionBar?.title = title
    }

    private fun switchTabs(fragment: String = "NewsFragment") {
        when (fragment) {
            "MainFragment" -> {
                val homeMenuItem = bottomBar.menu.findItem(R.id.navigation_home)
                homeMenuItem?.let {
                    it.isChecked = true
                    bottomBar.selectedItemId = it.itemId
                }
            }
            "ScheduleFragment" -> {
                val scheduleMenuItem = bottomBar.menu.findItem(R.id.navigation_schedule)
                scheduleMenuItem?.let {
                    it.isChecked = true
                    bottomBar.selectedItemId = it.itemId
                }
            }
            "NewsFragment" -> {
                val newsMenuItem = bottomBar.menu.findItem(R.id.navigation_news)
                newsMenuItem?.let {
                    it.isChecked = true
                    bottomBar.selectedItemId = it.itemId
                }
            }
        }
    }

    private fun updateRealDebrid() {
        lifecycleScope.launch {
            val accountData: UserData
            when (val result = RealDebridAPI().getUserData()) {
                is UserDataResult.Success -> {
                    accountData = result.data
                    // handle success
                    val debrid_user = findViewById<TextView>(R.id.debrid_user)
                    val debrid_premium = findViewById<TextView>(R.id.debrid_premium)
                    val debrid_points = findViewById<TextView>(R.id.debrid_points)
                    val debrid_premium_card = findViewById<CardView>(R.id.debrid_premium_card)
                    val debrid_premium_text = findViewById<TextView>(R.id.debrid_premium_text)

                    runOnUiThread {
                        // change text
                        debrid_user.text = accountData.username
                        debrid_premium.text = RealDebridAPI().formatDateWithDaysLeft(accountData.expiration)
                        debrid_points.text = accountData.points.toString()
                        if (accountData.type == "premium") {
                            debrid_premium_card.setCardBackgroundColor(Color.parseColor("#4D996F"))
                            debrid_premium_text.text = "Premium"
                            debrid_premium_text.setTextColor(Color.parseColor("#FFFFFF"))
                        } else {
                            debrid_premium_card.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                            debrid_premium_text.text = "Free"
                            debrid_premium_text.setTextColor(Color.parseColor("#000000"))
                        }
                    }
                }
                is UserDataResult.Error -> {
                    Log.e("MainActivity", "Error getting user data")
                    // handle error
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setupSideBar()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    companion object {
        val mainActivityViewModel = ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)
    }

}