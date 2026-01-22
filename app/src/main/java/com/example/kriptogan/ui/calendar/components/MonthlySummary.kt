package com.example.kriptogan.ui.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kriptogan.util.TimeCalculationUtils

@Composable
fun MonthlySummary(
    calculation: TimeCalculationUtils.MonthlyCalculation?,
    modifier: Modifier = Modifier
) {
    if (calculation == null) return

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Total Worked",
                    value = String.format("%.1f h", calculation.totalWorked)
                )
                SummaryItem(
                    label = "Expected",
                    value = "${calculation.totalExpected} h"
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Overtime",
                    value = String.format("+%.1f h", calculation.totalOvertime),
                    valueColor = MaterialTheme.colorScheme.tertiary
                )
                SummaryItem(
                    label = "Missing",
                    value = String.format("-%.1f h", calculation.totalMissing),
                    valueColor = MaterialTheme.colorScheme.error
                )
            }

            if (calculation.netOvertime > 0 || calculation.netMissing > 0) {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Net: ",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (calculation.netOvertime > 0) {
                            String.format("+%.1f h", calculation.netOvertime)
                        } else {
                            String.format("-%.1f h", calculation.netMissing)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (calculation.netOvertime > 0) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
