package com.maks.island

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maks.island.ui.navigation.MaksIslandNavHost
import com.maks.island.ui.theme.MaksIslandTheme
import com.maks.island.viewmodel.IslandViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: IslandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            MaksIslandTheme(
                darkTheme = settings.darkMode,
                dynamicColor = settings.useMaterialYou,
                pureBlack = settings.pureBlackMode,
            ) {
                MaksIslandNavHost(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: running launch checks")
        viewModel.onAppLaunch()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: refreshing after permission/settings flow")
        viewModel.onAppLaunch()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
