package com.sqz.gadget.runtime

import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import com.sqz.gadget.ui.NavRoute
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavManager private constructor() {
    data class NavState(
        val navTo: NavRoute? = null,
        val requestBack: Boolean = false,
    )

    private var isNavControllerInitialized = false

    companion object {
        private val instance = NavManager()

        fun current(): NavManager = instance.also {
            if (!it.isNavControllerInitialized) {
                throw NullPointerException("NavHostController not initialized")
            }
        }

        val state: StateFlow<NavState> = instance._state.asStateFlow()

        /**
         * Navigate controller handler
         * @param navController NavHostController, expect [rememberNavController]
         * @param state NavState, expect [collectAsState] for correct navigation state handling
         */
        fun navController(
            navController: NavHostController,
            state: State<NavState>
        ): NavHostController {
            instance.isNavControllerInitialized = true
            return instance.navController(navController, state)
        }
    }

    private val _state = MutableStateFlow<NavState>(NavState())

    fun navigate(route: NavRoute) {
        _state.update { it.copy(navTo = route) }
    }

    private var _navBackProcess = false

    @OptIn(DelicateCoroutinesApi::class)
    fun requestBack() {
        if (!_navBackProcess) {
            _navBackProcess = true
            _state.update { it.copy(requestBack = true) }
            GlobalScope.launch { // Fix multiple click nav to nothing issue
                delay(700)
                _navBackProcess = false
            }
        }
    }

    private fun navController(
        navController: NavHostController,
        state: State<NavState>
    ): NavHostController {
        if (state.value.navTo != null) _state.update {
            navController.navigate(it.navTo!!.name)
            it.copy(navTo = null)
        }
        if (state.value.requestBack) _state.update {
            navController.popBackStack()
            it.copy(requestBack = false)
        }
        return navController
    }
}
