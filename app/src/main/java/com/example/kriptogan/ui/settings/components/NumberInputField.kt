package com.example.kriptogan.ui.settings.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    helperText: String,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { newValue ->
            val intValue = newValue.toIntOrNull()
            if (intValue != null && intValue >= minValue && intValue <= maxValue) {
                onValueChange(intValue)
            } else if (newValue.isEmpty()) {
                onValueChange(minValue)
            }
        },
        label = { Text(label) },
        supportingText = { Text(helperText) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}
