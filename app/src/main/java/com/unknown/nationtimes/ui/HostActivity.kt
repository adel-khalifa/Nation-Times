package com.unknown.nationtimes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.unknown.nationtimes.R
import com.unknown.nationtimes.data.database.DataBaseInstance
import com.unknown.nationtimes.repo.NewsRepo
import com.unknown.nationtimes.repo.viewmodel.NewsFactory
import com.unknown.nationtimes.repo.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.activity_host.*

class HostActivity : AppCompatActivity() {
lateinit var mNewsViewModel : NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        val mNewsRepo = NewsRepo(DataBaseInstance(this))
        val mNewsFactory = NewsFactory(mNewsRepo)
        mNewsViewModel =
            ViewModelProvider(viewModelStore, mNewsFactory).get(NewsViewModel::class.java)
    bottomNavigationView.setupWithNavController(nav_host_fragment.findNavController())

        }
}
