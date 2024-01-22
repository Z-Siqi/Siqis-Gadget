package com.sqz.gadget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sqz.gadget.ui.AppLayout
import com.sqz.gadget.ui.theme.SiqisGadgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiqisGadgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppLayout()
                }
            }
        }
    }
}