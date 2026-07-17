package com.naufal.cheddar.ui.screens.insights

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naufal.cheddar.data.Note
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun InsightsScreen(notes: List<Note>) {
    var isWeekSelected by remember { mutableStateOf(true) }

    // State for navigation and animation
    val today = remember { LocalDate.now() }
    var viewedDate by remember { mutableStateOf(today) }
    var slideDirection by remember { mutableIntStateOf(1) } // 1 for next (swipe left), -1 for prev (swipe right)

    val fullDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

    // Dynamically filter notes based on the currently viewed week or month
    val currentNotes = remember(notes, viewedDate, isWeekSelected) {
        if (isWeekSelected) {
            val start = viewedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val end = start.plusDays(6)
            notes.filter {
                val noteDate = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                !noteDate.isBefore(start) && !noteDate.isAfter(end)
            }
        } else {
            val start = viewedDate.withDayOfMonth(1)
            val end = viewedDate.with(TemporalAdjusters.lastDayOfMonth())
            notes.filter {
                val noteDate = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                !noteDate.isBefore(start) && !noteDate.isAfter(end)
            }
        }
    }

    // Dynamic Stats Calculation
    val totalEntries = currentNotes.size
    val longestWordCount = currentNotes.maxOfOrNull { note ->
        note.content.split(Regex("\\s+")).filter { it.isNotBlank() }.size
    } ?: 0

    val mostActiveTime = remember(currentNotes) {
        if (currentNotes.isEmpty()) "00"
        else {
            val hours = currentNotes.map {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).hour
            }
            val mostCommonHour = hours.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: 0
            String.format(java.util.Locale.getDefault(), "%02d", mostCommonHour)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Week / Month Toggle
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = isWeekSelected,
                onClick = { isWeekSelected = true },
                label = { Text("Week", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = { Icon(Icons.Default.ViewColumn, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            FilterChip(
                selected = !isWeekSelected,
                onClick = { isWeekSelected = false },
                label = { Text("Month", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = { Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        // Top Stats Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = mostActiveTime, label = "Most\nactive\ntime")
                StatItem(value = totalEntries.toString(), label = "Total\nentries")
                StatItem(value = longestWordCount.toString(), label = "Longest\nword\ncount")
            }
        }

        // Calendar Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Header: Today's Date and Navigation Arrows
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = today.format(fullDateFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Surface(
                            onClick = {
                                slideDirection = -1
                                viewedDate = if (isWeekSelected) viewedDate.minusWeeks(1) else viewedDate.minusMonths(1)
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                        }

                        Surface(
                            onClick = {
                                slideDirection = 1
                                viewedDate = if (isWeekSelected) viewedDate.plusWeeks(1) else viewedDate.plusMonths(1)
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sub-header: Viewed Month and Reset Icon
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = viewedDate.format(monthFormatter),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = {
                            slideDirection = if (today.isAfter(viewedDate)) 1 else -1
                            viewedDate = today
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(32.dp) // Keeps it tight and aligned under the arrows
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Reset to Today",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // M3E Animated Calendar Grid
                AnimatedContent(
                    targetState = viewedDate,
                    transitionSpec = {
                        val animationDuration = 300
                        if (slideDirection > 0) {
                            (slideInHorizontally(animationSpec = tween(animationDuration)) { width -> width } + fadeIn(tween(animationDuration))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(animationDuration)) { width -> -width } + fadeOut(tween(animationDuration)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(animationDuration)) { width -> -width } + fadeIn(tween(animationDuration))) togetherWith
                                    (slideOutHorizontally(animationSpec = tween(animationDuration)) { width -> width } + fadeOut(tween(animationDuration)))
                        }
                    },
                    label = "calendar_swipe"
                ) { animatedDate ->
                    CalendarGrid(
                        viewedDate = animatedDate,
                        isWeekSelected = isWeekSelected,
                        today = today
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(viewedDate: LocalDate, isWeekSelected: Boolean, today: LocalDate) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Day of Week Header
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isWeekSelected) {
            val startOfWeek = viewedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            Row(modifier = Modifier.fillMaxWidth()) {
                (0..6).forEach { offset ->
                    val date = startOfWeek.plusDays(offset.toLong())
                    DayCell(date = date, today = today, modifier = Modifier.weight(1f))
                }
            }
        } else {
            val startOfMonth = viewedDate.withDayOfMonth(1)
            val daysInMonth = startOfMonth.lengthOfMonth()
            // Adjust so Monday is 0, Sunday is 6
            val firstDayOffset = startOfMonth.dayOfWeek.value - 1

            var currentDay = 1
            for (row in 0..5) {
                if (currentDay > daysInMonth) break
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        if (row == 0 && col < firstDayOffset) {
                            Box(modifier = Modifier.weight(1f)) // Empty leading space
                        } else if (currentDay <= daysInMonth) {
                            val date = startOfMonth.withDayOfMonth(currentDay)
                            DayCell(date = date, today = today, modifier = Modifier.weight(1f))
                            currentDay++
                        } else {
                            Box(modifier = Modifier.weight(1f)) // Empty trailing space
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, today: LocalDate, modifier: Modifier) {
    val isToday = date == today

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isToday) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Normal),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}