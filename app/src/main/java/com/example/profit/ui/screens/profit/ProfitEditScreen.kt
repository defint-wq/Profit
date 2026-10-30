package com.example.profit.ui.screens.profit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.profit.R
import com.example.profit.data.ProfitEntity
import com.example.profit.ui.navigation.NavigationDestination
import com.example.profit.ui.screens.ProfitTopBar
import com.example.profit.viewModel.AppViewModelProvider
import com.example.profit.viewModel.ProfitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ProfitEditDestination : NavigationDestination {
    override val route = "profit_edit"
    override val titleRes = R.string.app_name
    const val ID_ARG = "profitId"
    val routeWithArgs = "$route/{$ID_ARG}"
}

private val EditGreen = Color(0xFF2E9E4F)
private val EditRed = Color(0xFFE53935)

private fun editCategoryIcon(category: String): Pair<ImageVector, Color> = when (category) {
    "Хоол" -> Icons.Default.Restaurant to Color(0xFFFB8C00)
    "Автобус" -> Icons.Default.DirectionsBus to Color(0xFF3F51B5)
    "Цалин" -> Icons.Default.Work to Color(0xFF43A047)
    "Дэлгүүр" -> Icons.Default.ShoppingBag to Color(0xFFE91E63)
    "Бэлэг" -> Icons.Default.Star to Color(0xFFFFB300)
    else -> Icons.Default.Paid to Color(0xFF757575)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitEditScreen(
    profitId: Int,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfitViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val allProfits by viewModel.profits.collectAsState(initial = emptyList())
    val item = allProfits.firstOrNull { it.id == profitId }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProfitTopBar(
                title = "Бүртгэл засах",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        if (item == null) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            ProfitEditBody(
                item = item,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
                onSaved = navigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfitEditBody(
    item: ProfitEntity,
    viewModel: ProfitViewModel,
    modifier: Modifier = Modifier,
    onSaved: () -> Unit
) {
    var type by remember(item.id) { mutableStateOf(item.type) }
    var category by remember(item.id) { mutableStateOf(item.category) }
    var amountText by remember(item.id) {
        mutableStateOf(
            if (item.amount % 1f == 0f) item.amount.toLong().toString() else item.amount.toString()
        )
    }
    var dateMillis by remember(item.id) { mutableStateOf(item.date) }

    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isIncome = type == "income"
    val accent = if (isIncome) EditGreen else EditRed

    val categories = if (isIncome)
        listOf("Цалин", "Бэлэг", "Бусад орлого")
    else
        listOf("Хоол", "Автобус", "Дэлгүүр", "Бусад зарлага")

    val amount = amountText.replace(',', '.').toFloatOrNull()
    val isValid = amount != null && amount > 0 && category.isNotBlank()
    val dateText = remember(dateMillis) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateMillis))
    }

    val fieldShape = RoundedCornerShape(16.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = accent,
        focusedLabelColor = accent,
        cursorColor = accent
    )

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = isIncome,
                onClick = { if (!isIncome) { type = "income"; category = "" } },
                shape = SegmentedButtonDefaults.itemShape(0, 2),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = EditGreen,
                    activeContentColor = Color.White,
                    activeBorderColor = EditGreen
                )
            ) { Text("Орлого", fontWeight = FontWeight.Bold) }
            SegmentedButton(
                selected = !isIncome,
                onClick = { if (isIncome) { type = "expense"; category = "" } },
                shape = SegmentedButtonDefaults.itemShape(1, 2),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = EditRed,
                    activeContentColor = Color.White,
                    activeBorderColor = EditRed
                )
            ) { Text("Зарлага", fontWeight = FontWeight.Bold) }
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ангилал") },
                leadingIcon = if (category.isNotBlank()) {
                    {
                        val (icon, tint) = editCategoryIcon(category)
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(tint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                } else null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                shape = fieldShape,
                colors = fieldColors,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { name ->
                    val (icon, tint) = editCategoryIcon(name)
                    DropdownMenuItem(
                        text = { Text(name) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(tint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        },
                        onClick = { category = name; expanded = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Дүн (₮)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = amountText.isNotEmpty() && amount == null,
            singleLine = true,
            shape = fieldShape,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Огноо") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Огноо сонгох")
                }
            },
            shape = fieldShape,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = isValid,
            onClick = {
                amount?.let {
                    viewModel.updateProfit(
                        item.copy(
                            type = type,
                            category = category,
                            amount = it,
                            date = dateMillis
                        )
                    )
                    onSaved()
                }
            },
            shape = fieldShape,
            colors = ButtonDefaults.buttonColors(containerColor = accent),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("ХАДГАЛАХ", fontWeight = FontWeight.Bold) }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK", color = accent) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Болих") }
            }
        ) { DatePicker(state = pickerState) }
    }
}