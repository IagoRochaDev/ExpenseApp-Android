package com.devrochaiago.expenseapp.ui.feature.statistics

import android.content.res.Configuration
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devrochaiago.expenseapp.ui.theme.ExpenseAppTheme


data class CategoryStat(
    val id: Int,
    val name: String,
    val amount: Float,
    val formattedAmount: String,
    val color: Color,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {

    val mockStats = listOf(
        CategoryStat(1, "Moradia", 1500f, "R$ 1.500,00", Color(0xFF3B82F6), Icons.Default.Home),
        CategoryStat(2, "Alimentação", 900f, "R$ 900,00", Color(0xFFF59E0B), Icons.Default.Restaurant),
        CategoryStat(3, "Supermercado", 600f, "R$ 600,00", Color(0xFF10B981), Icons.Default.ShoppingCart),
        CategoryStat(4, "Lazer", 300f, "R$ 300,00", Color(0xFF8B5CF6), Icons.Default.Movie)
    )

    val totalAmount = mockStats.sumOf { it.amount.toDouble() }.toFloat()

    Scaffold(
        topBar = { //TODO: Repensar esse topo de dela
            TopAppBar(
                title = { Text("Estatísticas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // 1. Gráfico Visual (Donut Chart)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(
                        data = mockStats,
                        totalAmount = totalAmount,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            // Título da Lista
            item {
                Text(
                    text = "Despesas por Categoria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 2. Lista Resumo por Categoria
            items(mockStats, key = { it.id }) { stat ->
                val percentage = (stat.amount / totalAmount)
                CategoryStatItem(
                    stat = stat,
                    percentage = percentage,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<CategoryStat>,
    totalAmount: Float,
    modifier: Modifier = Modifier
) {
    // Animação simples para o gráfico "crescer" ao abrir a tela
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSweepAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000),
        label = "DonutChartAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f // Começa no topo (12 horas)
            val strokeWidth = 32.dp.toPx()

            data.forEach { stat ->
                // Calcula a proporção dessa categoria em relação ao total animado (360 graus)
                val sweepAngle = (stat.amount / totalAmount) * animateSweepAngle

                drawArc(
                    color = stat.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }

        // Texto no centro do Donut
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "R$ ${"%.2f".format(totalAmount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun CategoryStatItem(
    stat: CategoryStat,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = stat.color.copy(alpha = 0.1f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = stat.name,
                        tint = stat.color
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Textos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stat.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Valor
                Text(
                    text = stat.formattedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Progresso
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color.Transparent, RoundedCornerShape(50)),
                color = stat.color,
                trackColor = stat.color.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}


@Preview(name = "Light Mode", showBackground = true)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun StatisticsScreenPreview() {
    ExpenseAppTheme {
        StatisticsScreen()
    }
}