package com.example.kriptogan.ui.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kriptogan.ui.history.MonthSummary
import java.time.format.DateTimeFormatter

@Composable
fun MonthCard(
    monthSummary: MonthSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = monthSummary.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Worked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%.1f h", monthSummary.totalWorked),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Column {
                    Text(
                        text = "Expected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${monthSummary.totalExpected} h",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            if (monthSummary.netOvertime > 0 || monthSummary.netMissing > 0) {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Net: ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (monthSummary.netOvertime > 0) {
                            String.format("+%.1f h", monthSummary.netOvertime)
                        } else {
                            String.format("-%.1f h", monthSummary.netMissing)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (monthSummary.netOvertime > 0) {
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
