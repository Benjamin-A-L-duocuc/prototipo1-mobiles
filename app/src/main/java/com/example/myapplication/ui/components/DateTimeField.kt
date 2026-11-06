package com.example.myapplication.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.util.formatDateTime
import java.util.Calendar

@Composable
fun DateTimeField(
    label: String,
    timestamp: Long?,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 11.sp)
        OutlinedButton(
            onClick = {
                val initial = Calendar.getInstance().apply {
                    timeInMillis = timestamp ?: System.currentTimeMillis()
                }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val date = Calendar.getInstance().apply {
                            timeInMillis = initial.timeInMillis
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                date.set(Calendar.HOUR_OF_DAY, hour)
                                date.set(Calendar.MINUTE, minute)
                                date.set(Calendar.SECOND, 0)
                                date.set(Calendar.MILLISECOND, 0)
                                onValueChange(date.timeInMillis)
                            },
                            initial.get(Calendar.HOUR_OF_DAY),
                            initial.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    initial.get(Calendar.YEAR),
                    initial.get(Calendar.MONTH),
                    initial.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = formatDateTime(timestamp), color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
