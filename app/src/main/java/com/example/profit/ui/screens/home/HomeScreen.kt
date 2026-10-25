package com.example.profit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.profit.R
import com.example.profit.data.ProfitEntity
import com.example.profit.ui.navigation.NavigationDestination
import com.example.profit.ui.screens.ProfitTopBar
import com.example.profit.viewModel.AppViewModelProvider
import com.example.profit.viewModel.ProfitViewModel
import com.example.profit.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.profit.repository.DisplayMode

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

private val Green = Color(0xFF2E9E4F)
private val Red = Color(0xFFE53935)

private data class MonthRange(val start: Long, val end: Long, val label: String)

private fun monthRange(offset: Int): MonthRange {
    val cal = Calendar.getInstance().apply {
        add(Calendar.MONTH, offset)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1)
    val label = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(start))
    return MonthRange(start, cal.timeInMillis, label)
}

private fun categoryIcon(category: String): Pair<ImageVector, Color> = when (category) {
    "Хоол" -> Icons.Default.Restaurant to Color(0xFFFB8C00)
    "Автобус" -> Icons.Default.DirectionsBus to Color(0xFF3F51B5)
    "Цалин" -> Icons.Default.Work to Color(0xFF43A047)
    "Дэлгүүр" -> Icons.Default.ShoppingBag to Color(0xFFE91E63)
    "Бэлэг" -> Icons.Default.Star to Color(0xFFFFB300)
    else -> Icons.Default.Paid to Color(0xFF757575)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToProfitEntry: (String) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfitViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val allProfits by viewModel.profits.collectAsState(initial = emptyList())
    val mode by settingsViewModel.displayMode.collectAsState()

    var monthOffset by remember { mutableIntStateOf(0) }
    var itemToDelete by remember { mutableStateOf<ProfitEntity?>(null) }
    val range = remember(monthOffset) { monthRange(monthOffset) }

    val monthProfits = allProfits.filter { it.date >= range.start && it.date < range.end }

    val income = monthProfits.filter { it.type == "income" }.sumOf { it.amount.toDouble() }
    val expense = monthProfits.filter { it.type == "expense" }.sumOf { it.amount.toDouble() }

    val profits = monthProfits
        .filter { mode == DisplayMode.BOTH || it.type == mode }
        .sortedByDescending { it.date }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProfitTopBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Тохиргоо")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navigateToProfitEntry("income") },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Орлого нэмэх")
                }
                Button(
                    onClick = { navigateToProfitEntry("expense") },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Зарлага нэмэх")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { monthOffset-- }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Өмнөх сар")
                }
                Text(range.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { monthOffset++ }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Дараах сар")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (mode != DisplayMode.EXPENSE) {
                    SummaryBox(
                        title = "Орлого",
                        amount = income,
                        color = Green,
                        icon = Icons.Default.ArrowUpward,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (mode != DisplayMode.INCOME) {
                    SummaryBox(
                        title = "Зарлага",
                        amount = expense,
                        color = Red,
                        icon = Icons.Default.ArrowDownward,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (profits.isEmpty()) {
                Text("Энэ сард бүртгэл алга байна.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(profits, key = { it.id }) { item ->
                        ProfitRow(item = item, onDelete = { itemToDelete = item })
                    }
                }
            }
        }
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Устгах уу?") },
            text = { Text("\"${item.category}\" бүртгэлийг устгахдаа итгэлтэй байна уу?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProfit(item)
                    itemToDelete = null
                }) {
                    Text("Устгах", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Болих")
                }
            }
        )
    }
}

@Composable
private fun SummaryBox(
    title: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text("${"%,.0f".format(amount)}₮", fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ProfitRow(item: ProfitEntity, onDelete: () -> Unit) {
    val isIncome = item.type == "income"
    val (icon, tint) = categoryIcon(item.category)
    val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(item.date))

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(tint),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Text(
            text = item.category,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                (if (isIncome) "+ " else "- ") + "%,.0f₮".format(item.amount),
                color = if (isIncome) Green else Red,
                fontWeight = FontWeight.Bold
            )
            Text(dateText, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Устгах")
        }
    }
}