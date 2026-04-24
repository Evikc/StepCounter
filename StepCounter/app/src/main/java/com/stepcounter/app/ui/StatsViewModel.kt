package com.stepcounter.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepcounter.app.data.StepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class DayBar(val shortLabel: String, val steps: Int)

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: StepsRepository,
) : ViewModel() {

    val lastSevenDays: StateFlow<List<DayBar>> = repository.observeLastSevenDays()
        .map { entities ->
            val today = LocalDate.now()
            val byDay = entities.associateBy { it.dateEpochDay }
            (0..6).map { index ->
                val date = today.minusDays(6L - index)
                val shortLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                val steps = byDay[date.toEpochDay()]?.stepCount ?: 0
                DayBar(shortLabel = shortLabel, steps = steps)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
