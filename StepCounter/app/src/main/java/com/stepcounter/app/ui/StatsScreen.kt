package com.stepcounter.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.stepcounter.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBack: () -> Unit,
) {
    val days by viewModel.lastSevenDays.collectAsStateWithLifecycle()
    val modelProducer = remember { CartesianChartModelProducer() }
    val scrollState: VicoScrollState = rememberVicoScrollState(scrollEnabled = false)

    LaunchedEffect(days) {
        if (days.isEmpty()) return@LaunchedEffect
        modelProducer.runTransaction {
            columnSeries {
                series(days.map { it.steps })
            }
        }
    }

    val bottomFormatter = remember(days) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, days.lastIndex.coerceAtLeast(0))
            days.getOrNull(index)?.shortLabel.orEmpty()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stats_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            ProvideVicoTheme(rememberM3VicoTheme()) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomFormatter),
                    ),
                    modelProducer = modelProducer,
                    scrollState = scrollState,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
