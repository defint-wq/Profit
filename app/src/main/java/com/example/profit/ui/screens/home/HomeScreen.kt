package com.example.profit.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.profit.R
import com.example.profit.data.ProfitEntity
import com.example.profit.repository.DisplayMode
import com.example.profit.ui.navigation.NavigationDestination
import com.example.profit.ui.screens.ProfitTopBar
import com.example.profit.viewModel.AppViewModelProvider
import com.example.profit.viewModel.ProfitViewModel
import com.example.profit.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

private val Green = Color(0xFF2E9E4F)
private val Red = Color(0xFFE53935)
private const val MASK = "*****"

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
    navigateToEdit: (Int) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfitViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val allProfits by viewModel.profits.collectAsState(initial = emptyList())
    val mode by settingsViewModel.displayMode.collectAsState()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var monthOffset by remember { mutableIntStateOf(0) }
    var itemToDelete by remember { mutableStateOf<ProfitEntity?>(null) }
    var summaryRevealed by remember { mutableStateOf(false) }
    val revealedIds = remember { mutableStateListOf<Long>() }
    val range = remember(monthOffset) { monthRange(monthOffset) }

    val monthProfits = allProfits.filter { it.date >= range.start && it.date < range.end }
    val income = monthProfits.filter { it.type == "income" }.sumOf { it.amount.toDouble() }
    val expense = monthProfits.filter { it.type == "expense" }.sumOf { it.amount.toDouble() }

    val profits = monthProfits
        .filter { mode == DisplayMode.BOTH || it.type == mode }
        .sortedByDescending { it.date }

    val summary: @Composable () -> Unit = {
        SummaryRow(
            mode = mode,
            income = income,
            expense = expense,
            revealed = summaryRevealed,
            onToggle = { summaryRevealed = !summaryRevealed }
        )
    }
    val list: @Composable (Modifier) -> Unit = { listModifier ->
        EntryList(
            profits = profits,
            revealedIds = revealedIds,
            onEdit = { navigateToEdit(it.id) },
            onDelete = { itemToDelete = it },
            modifier = listModifier
        )
    }

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
            if (!isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AddButton("Орлого нэмэх", Green, Modifier.weight(1f)) {
                        navigateToProfitEntry("income")
                    }
                    AddButton("Зарлага нэмэх", Red, Modifier.weight(1f)) {
                        navigateToProfitEntry("expense")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (!isLandscape) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MonthSwitcherHorizontal(
                    label = range.label,
                    onPrev = { monthOffset-- },
                    onNext = { monthOffset++ }
                )
                summary()
                list(Modifier.weight(1f))
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MonthSwitcherVertical(
                    label = range.label,
                    onUp = { monthOffset++ },
                    onDown = { monthOffset-- },
                    modifier = Modifier.fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    summary()
                    list(Modifier.weight(1f))
                }
                Column(
                    modifier = Modifier
                        .width(180.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AddButton("Орлого нэмэх", Green, Modifier.fillMaxWidth()) {
                        navigateToProfitEntry("income")
                    }
                    AddButton("Зарлага нэмэх", Red, Modifier.fillMaxWidth()) {
                        navigateToProfitEntry("expense")
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
                }) { Text("Устгах", color = Red) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Болих") }
            }
        )
    }
}

@Composable
private fun AddButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}

@Composable
private fun MonthSwitcherHorizontal(label: String, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Өмнөх сар")
        }
        Text(label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Дараах сар")
        }
    }
}

@Composable
private fun MonthSwitcherVertical(
    label: String,
    onUp: () -> Unit,
    onDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(88.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onUp) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Дараах сар")
        }
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDown) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Өмнөх сар")
        }
    }
}

@Composable
private fun SummaryRow(
    mode: String,
    income: Double,
    expense: Double,
    revealed: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (mode != DisplayMode.EXPENSE) {
            SummaryBox("Орлого", income, Green, Icons.Default.ArrowUpward, revealed, onToggle, Modifier.weight(1f))
        }
        if (mode != DisplayMode.INCOME) {
            SummaryBox("Зарлага", expense, Red, Icons.Default.ArrowDownward, revealed, onToggle, Modifier.weight(1f))
        }
    }
}

@Composable
private fun EntryList(
    profits: List<ProfitEntity>,
    revealedIds: MutableList<Long>,
    onEdit: (ProfitEntity) -> Unit,
    onDelete: (ProfitEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (profits.isEmpty()) {
        Text("Энэ сард бүртгэл алга байна.", modifier = modifier)
    } else {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profits, key = { it.id }) { item ->
                val id = item.id.toLong()
                ProfitRow(
                    item = item,
                    revealed = id in revealedIds,
                    onToggle = { if (id in revealedIds) revealedIds.remove(id) else revealedIds.add(id) },
                    onLongPress = { onEdit(item) },
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SummaryBox(
    title: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    revealed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .combinedClickable(onClick = onToggle)
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
            Text(
                if (revealed) "${"%,.0f".format(amount)}₮" else MASK,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfitRow(
    item: ProfitEntity,
    revealed: Boolean,
    onToggle: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = item.type == "income"
    val (icon, tint) = categoryIcon(item.category)
    val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(item.date))
    val amountText = if (revealed) "%,.0f₮".format(item.amount) else MASK

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onToggle, onLongClick = onLongPress)
            .padding(vertical = 4.dp),
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
                (if (isIncome) "+ " else "- ") + amountText,
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