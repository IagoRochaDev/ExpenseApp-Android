package com.devrochaiago.expenseapp.ui.feature.addtransaction

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devrochaiago.expenseapp.ui.theme.ExpenseAppTheme
import com.devrochaiago.expenseapp.ui.theme.GreenIncome
import com.devrochaiago.expenseapp.ui.theme.RedExpense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier.Companion,
    onNavigateBack: () -> Unit = {}
) {

    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    // Categorias Mock
    val categories = if (isExpense) {
        listOf("Alimentação", "Transporte", "Lazer", "Contas")
    } else {
        listOf("Salário", "Freelance", "Rendimentos")
    }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // Date Picker Mock
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("19 de Março de 2026") }

    Scaffold(
        topBar = { //TODO: Repensar esse topo de dela
            TopAppBar(
                title = { Text("Nova Transação") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.Companion
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.Companion.height(16.dp))

            // 1. Seletor de Tipo (Despesa vs Receita)
            TransactionTypeSelector(
                isExpense = isExpense,
                onTypeSelected = {
                    isExpense = it; selectedCategory = if (it) "Alimentação" else "Salário"
                }
            )

            Spacer(modifier = Modifier.Companion.height(32.dp))

            // 2. Campo de Valor Gigante
            Text(
                text = "Valor",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    textAlign = TextAlign.Companion.Center,
                    fontWeight = FontWeight.Companion.Bold,
                    color = if (isExpense) RedExpense else GreenIncome
                ),
                prefix = {
                    Text(
                        "R$ ",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Companion.Transparent,
                    unfocusedBorderColor = Color.Companion.Transparent,
                    focusedContainerColor = Color.Companion.Transparent,
                    unfocusedContainerColor = Color.Companion.Transparent
                ),
                placeholder = {
                    Text(
                        "0,00",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Companion.Center,
                        modifier = Modifier.Companion.fillMaxWidth()
                    )
                },
                modifier = Modifier.Companion.fillMaxWidth()
            )

            Spacer(modifier = Modifier.Companion.height(32.dp))

            // 3. Campos de Detalhes
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título (Ex: Conta de Luz)") },
                modifier = Modifier.Companion.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.Companion.height(16.dp))

            // Dropdown de Categoria usando M3
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory },
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.Companion.menuAnchor().fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.Companion.height(16.dp))

            // Seletor de Data (Visual)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Selecionar Data"
                        )
                    }
                },
                modifier = Modifier.Companion.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.Companion.weight(1f)) // Empurra o botão de salvar para o fundo

            // 4. Botão de Salvar
            Button(
                onClick = { /* TODO: Salvar transação e voltar */ onNavigateBack() },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExpense) RedExpense else GreenIncome
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.Companion.width(8.dp))
                Text("Salvar Transação", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            }
        }

        // Diálogo do DatePicker M3
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

// Subcomponente Isolado para o Toggle Button
@Composable
private fun TransactionTypeSelector(
    isExpense: Boolean,
    onTypeSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.Companion.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botão Despesa
        OutlinedButton(
            onClick = { onTypeSelected(true) },
            modifier = Modifier.Companion.weight(1f).height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isExpense) RedExpense.copy(alpha = 0.1f) else Color.Companion.Transparent,
                contentColor = if (isExpense) RedExpense else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(
                    if (isExpense) RedExpense else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Text("Despesa", fontWeight = FontWeight.Companion.SemiBold)
        }

        // Botão Receita
        OutlinedButton(
            onClick = { onTypeSelected(false) },
            modifier = Modifier.Companion.weight(1f).height(48.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (!isExpense) GreenIncome.copy(alpha = 0.1f) else Color.Companion.Transparent,
                contentColor = if (!isExpense) GreenIncome else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(
                    if (!isExpense) GreenIncome else MaterialTheme.colorScheme.outline
                )
            )
        ) {
            Text("Receita", fontWeight = FontWeight.Companion.SemiBold)
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AddTransactionScreenPreview() {
    ExpenseAppTheme {
        AddTransactionScreen()
    }
}
