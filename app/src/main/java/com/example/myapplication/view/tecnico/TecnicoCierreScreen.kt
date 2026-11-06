package com.example.myapplication.view.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.OtWorkflow
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.util.formatTime
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoCierreScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ot = viewModel.selectedOT
    val activities = viewModel.selectedActivities
    val materials = viewModel.selectedMaterials
    val evidence = viewModel.evidenceList
    val checkIn = viewModel.selectedCheckIn
    var message by remember(ot.id) { mutableStateOf<String?>(null) }
    var closed by remember(ot.id) { mutableStateOf(ot.estado == OtWorkflow.CERRADA) }

    PhoneScreen(
        title = "Cierre de OT",
        subtitle = ot.id,
        onBack = { onNavigate(Rutas.EJECUCION) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ot.cliente, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${ot.local} · ${ot.equipo}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text("Técnico: ${ot.tecnicoNombre ?: viewModel.currentUserName}", color = Color(0xFF64748B), fontSize = 9.sp)
                }
                StatusBadge(ot.estado)
            }

            if (closed) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Intervención cerrada", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("El check-out quedó registrado. La OT queda disponible para la conformidad del cliente.", color = Color(0xFFA7F3D0), fontSize = 10.sp)
                    Text("Cierre: ${formatDateTime(ot.cerradaEn ?: System.currentTimeMillis())}", color = Color(0xFF6EE7B7), fontSize = 9.sp)
                    Button(
                        onClick = { onNavigate(Rutas.JORNADA) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Volver a mi jornada", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("RESUMEN DE LA INTERVENCIÓN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Science, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Diagnóstico", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            ot.diagnostico.ifBlank { "Sin diagnóstico registrado" },
                            color = if (ot.diagnostico.isBlank()) Color(0xFFFCA5A5) else Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Actividades", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${activities.count { it.completada }} de ${activities.size} completadas", color = Color.White, fontSize = 10.sp)
                        activities.forEach { activity ->
                            Text(
                                "${if (activity.completada) "✓" else "○"} ${activity.descripcion}",
                                color = if (activity.completada) Color(0xFFA7F3D0) else Color(0xFFCBD5E1),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Evidencias", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${evidence.size} fotografías adjuntas", color = Color.White, fontSize = 10.sp)
                    }
                }
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Jornada", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Check-in: ${checkIn?.let { formatTime(it.createdAt) } ?: "Sin registro"}",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        Text(
                            "Check-out: ${formatTime(System.currentTimeMillis())}",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                if (materials.isNotEmpty()) {
                    Text(
                        "Materiales: ${materials.joinToString { "${it.nombre} x${it.cantidad} ${it.unidad}" }}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 9.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C4A6E))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Al cerrar, el cliente deberá revisar el trabajo y registrar su conformidad con nombre y observaciones.",
                    color = Color(0xFFBAE6FD),
                    fontSize = 9.sp
                )
            }

            if (message != null) {
                Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }

            if (!closed) {
                Button(
                    onClick = {
                        val error = viewModel.closeOt(ot.id)
                        if (error == null) {
                            closed = true
                            message = null
                        } else {
                            message = error
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Finalizar intervención", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onNavigate(Rutas.EJECUCION) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a ejecución", fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
