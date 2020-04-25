package com.udf.showcase.main.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.udf.showcase.LOGIN_TAG
import com.udf.showcase.MAIN_TAG
import com.udf.showcase.R
import com.udf.showcase.SampleApp
import com.udf.showcase.login.view.LoginFragment
import com.udf.showcase.main.di.ActivityComponent
import com.udf.showcase.main.di.ActivityModule

class MainActivity : AppCompatActivity() {

    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent = (application as SampleApp)
                .appComponent
                .plusActivityComponent(ActivityModule(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        if (supportFragmentManager.findFragmentByTag(MAIN_TAG) != null) {
            return
        }

        if (supportFragmentManager.findFragmentByTag(LOGIN_TAG) == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, LoginFragment(), LOGIN_TAG)
                    .commit()
        }
    }
}
