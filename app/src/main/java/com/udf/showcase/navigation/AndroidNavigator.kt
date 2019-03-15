package com.udf.showcase.navigation

import android.support.v4.app.FragmentActivity
import com.udf.showcase.MAIN_TAG
import com.udf.showcase.R
import com.udf.showcase.main.view.MainFragment
import com.udf.showcase.navigation.Navigator


class AndroidNavigator(private val activity: FragmentActivity) : Navigator {

    override fun goToMainScreen() {
        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, MainFragment(), MAIN_TAG)
            .commitNow()
    }

}