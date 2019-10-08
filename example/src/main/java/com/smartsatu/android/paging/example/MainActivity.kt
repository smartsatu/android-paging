package com.smartsatu.android.paging.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.stetho.Stetho
import com.smartsatu.android.paging.example.data.UsersRepository
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(application)
        setContentView(R.layout.activity_main)
        UsersRepository.initDb(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
