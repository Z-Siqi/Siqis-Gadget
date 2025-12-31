package com.sqz.gadget.ui.layout

import android.os.Build
import android.system.Os
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sqz.gadget.R

@Composable
fun SystemInfoLayout(navController: NavController, modifier: Modifier = Modifier) {
    val sdkInt = Build.VERSION.SDK_INT
    val androidVersion = Build.VERSION.RELEASE
    val device = Build.DEVICE
    val model = Build.MODEL
    val codename = Build.VERSION.CODENAME
    val id = Build.ID
    val machine = Os.uname().machine
    val securityPatch = Build.VERSION.SECURITY_PATCH
    val osUname = Os.uname().release
    val fingerprint = Build.FINGERPRINT
    val product = Build.PRODUCT
    val version = Os.uname().version
    val sysName = Os.uname().sysname

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = modifier.padding(10.dp)
            ) {
                Icon(painterResource(R.drawable.back), stringResource(R.string.back))
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(50.dp, 80.dp, 50.dp, 50.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                SelectionContainer {
                    Column(
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "SDK version: $sdkInt")
                        HorizontalDivider()
                        Text(text = "Android version: $androidVersion")
                        HorizontalDivider()
                        Text(text = "Device: $device")
                        HorizontalDivider()
                        Text(text = "Model: $model")
                        HorizontalDivider()
                        Text(text = "Development codename: $codename")
                        HorizontalDivider()
                        Text(text = "Build Number (id): $id")
                        HorizontalDivider()
                        Text(text = "Device architecture: $machine")
                        HorizontalDivider()
                        Text(text = "Security patch level: $securityPatch")
                        HorizontalDivider()
                        Text(text = "OS release (Kernel): $osUname")
                        HorizontalDivider()
                        Text(text = "Fingerprint: $fingerprint")
                        HorizontalDivider()
                        Text(text = "Product: $product")
                        HorizontalDivider()
                        Text(text = "The OS version: $version")
                        HorizontalDivider()
                        Text(text = "OS name (sys-name): $sysName")
                    }
                }
            }
        }
    }
}