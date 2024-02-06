package com.sqz.gadget.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.R
import com.sqz.gadget.ui.layout.calculate.ValueState

@Composable
fun AppLayout(valueState: ValueState, navController: NavController, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        LazyColumn {
            item {
                AppCard(
                    intent = {
                        valueState.calculateMode = "circle"
                        navController.navigate("CalculateLayout")
                    },
                    text = "Calculate The Circle",
                    painter = R.drawable.calculate,
                    contentDescription = "Circle",
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
            item {
                AppCard(
                    intent = { navController.navigate("ScreenLayout") },
                    text = "Check the Screen",
                    painter = R.drawable.screen,
                    contentDescription = "Screen",
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
            item {
                AppCard(
                    intent = { navController.navigate("TypingLayout") },
                    text = "Test Typing",
                    painter = R.drawable.text,
                    contentDescription = "text",
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
        }
    }
}

@Composable
private fun AppCard(
    intent: () -> Unit,
    text: String,
    painter: Int,
    contentDescription: String,
    colors: CardColors,
    modifier: Modifier = Modifier,
) {
    val screenCard = if (LocalConfiguration.current.screenWidthDp < 368) {
        modifier.height(100.dp)
    } else {
        modifier.height(75.dp)
    }
    val screenIcon = if (LocalConfiguration.current.screenWidthDp < 392) {
        modifier
            .padding(end = 10.dp, top = 10.dp)
            .wrapContentHeight(Alignment.Top)
    } else {
        modifier.padding(end = 27.dp)
    }
    Card(
        colors = colors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
                then screenCard
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(onClick = intent)
        ) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                painter = painterResource(painter),
                contentDescription = contentDescription,
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.End)
                        then screenIcon,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    val valueState: ValueState = viewModel()
    AppLayout(valueState, navController)
}