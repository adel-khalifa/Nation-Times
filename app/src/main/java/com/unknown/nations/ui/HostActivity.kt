package com.unknown.nations.ui

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.unknown.nations.R
import com.unknown.nations.data.database.DataBaseInstance
import com.unknown.nations.repo.NewsRepo
import com.unknown.nations.repo.viewmodel.NewsFactory
import com.unknown.nations.repo.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.activity_host.*

class HostActivity : AppCompatActivity() {
    lateinit var mNewsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        val mNewsRepo = NewsRepo(DataBaseInstance(this))
        val mNewsFactory = NewsFactory(application, mNewsRepo)
        mNewsViewModel =
            ViewModelProvider(viewModelStore, mNewsFactory).get(NewsViewModel::class.java)
        bottomNavigationView.setupWithNavController(nav_host_fragment.findNavController())
        NavigationUI.setupActionBarWithNavController(
            this,
            findNavController(R.id.nav_host_fragment)
        )
        fun getCountryCode(): String {
            val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.networkCountryIso
        }

    }


}
