package com.sqz.gadget.ui.layout.main

import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.ui.util.fastForEach
import com.sqz.gadget.R
import com.sqz.gadget.ui.NavRoute

class DashboardItem {

    data class Option(
        val title: String,
        val icon: Int,
        val description: String? = null,
        val navGoal: NavRoute,
    )

    enum class Category(val item: List<Option>) {
        Calculate(
            item = listOf(
                Option(
                    title = "Calculate The Circle",
                    icon = R.drawable.circle,
                    description = "Calculate the area, circumference and diameter",
                    navGoal = NavRoute.CircleUnit
                ),
                Option(
                    title = "Length Unit Conversion",
                    icon = R.drawable.length,
                    navGoal = NavRoute.LengthUnit
                ),
                Option(
                    title = "Hormone Units Conversion",
                    icon = R.drawable.calculate,
                    navGoal = NavRoute.HormoneUnit
                ),
            )
        ),
        Dev(
            item = listOf(
                Option(
                    title = "Screen Information",
                    icon = R.drawable.screen,
                    navGoal = NavRoute.ScreenInfo
                ),
            )
        ),
    }

    @Suppress("unused") // Not implemented yet
    @RememberInComposition
    fun list(): Map<Category, Option> {
        var map = mapOf<Category, Option>()
        Category.entries.fastForEach { category ->
            category.item.fastForEach { option ->
                map = map + (category to option)
            }
        }
        return map
    }
}
