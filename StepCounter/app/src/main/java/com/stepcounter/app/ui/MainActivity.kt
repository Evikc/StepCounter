package com.stepcounter.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.stepcounter.app.service.StepCounterService
import com.stepcounter.app.ui.theme.StepCounterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.all { it }) {
            StepCounterService.start(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepCounterTheme {
                val navController = rememberNavController()
                val mainVm: MainViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel = mainVm,
                            goalEvents = mainVm.goalReachedWhileVisible,
                            onOpenStats = { navController.navigate("stats") },
                            onRequestStartService = { requestPermissionsAndStart() },
                        )
                    }
                    composable("stats") {
                        val statsVm: StatsViewModel = hiltViewModel()
                        StatsScreen(
                            viewModel = statsVm,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
        requestPermissionsAndStart()
    }

    private fun requestPermissionsAndStart() {
        val required = buildList {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
            if (Build.VERSION.SDK_INT >= 33) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (required.isEmpty()) {
            StepCounterService.start(this)
        } else {
            permissionLauncher.launch(required)
        }
    }
}
