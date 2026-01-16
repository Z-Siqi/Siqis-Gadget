package com.sqz.gadget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.sqz.gadget.ui.MainLayout
import com.sqz.gadget.ui.theme.SiqisGadgetTheme
import com.sqz.gadget.ui.theme.SystemBarsColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiqisGadgetTheme {
                SystemBarsColor.CreateSystemBars(window) {
                    SystemBarsColor.current.let {
                        it.setStateBarColor(MaterialTheme.colorScheme.surfaceContainerLow)
                        it.setNavBarColor(MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
                if (isSystemInDarkTheme()) {
                    SystemBarsColor.current.setLightBars(lightState = true, lightNav = true)
                } else {
                    SystemBarsColor.current.setLightBars(lightState = false, lightNav = false)
                }
                val notButtonNav = WindowInsets.navigationBars.getBottom(LocalDensity.current) < 100
                val windowInsetsPadding = if (!notButtonNav) // if nav mode is not gesture mode
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars) else Modifier
                Surface(
                    modifier = Modifier.fillMaxSize() then windowInsetsPadding,
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout(
                        context = applicationContext,
                        view = window.decorView,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
