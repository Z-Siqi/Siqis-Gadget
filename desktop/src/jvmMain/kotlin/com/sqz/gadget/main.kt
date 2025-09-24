package com.sqz.gadget

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sqz.gadget.ui.App
import gadget.desktop.generated.resources.Res
import gadget.desktop.generated.resources.app
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

fun main() = application {
    var init: Boolean by rememberSaveable { mutableStateOf(false) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Siqi's Gadget",
        icon = painterResource(Res.drawable.app),
    ) {
        if (!init) {
            window.minimumSize = Dimension(750, 500)
            init = true
        }
        MaterialTheme {
            App(window = window)
        }
    }
}
