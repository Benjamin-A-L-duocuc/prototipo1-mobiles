package com.example.myapplication.view.jefe

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
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.myapplication.model.OtEvento
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.DateTimeField
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.viewmodel.AppViewModel
import java.util.Calendar

@Composable
fun JefeOTDetailScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ot = viewModel.selectedOT
    var selectedTecnicoId by remember(ot.id) { mutableStateOf(ot.tecnicoId ?: 0L) }
    var scheduledAt by remember(ot.id) {
        mutableStateOf(ot.programadaPara ?: (System.currentTimeMillis() + 60 * 60 * 1000L))
    }
    var showTecnicoMenu by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    LaunchedEffect(ot.id, ot.tecnicoId, ot.programadaPara) {
        selectedTecnicoId = ot.tecnicoId ?: 0L
        scheduledAt = ot.programadaPara ?: (System.currentTimeMillis() + 60 * 60 * 1000L)
    }

    val selectedTecnico = viewModel.technicians.find { it.id == selectedTecnicoId }
    val activos = viewModel.technicians.filter { it.activo }

    PhoneScreen(
        title = ot.id,
        subtitle = "Detalle y asignación",
        onBack = { onNavigate(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ot.cliente, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${ot.local} · ${ot.tipo}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        StatusBadge(ot.estado)
                        PriorityBadge(ot.prioridad)
                    }
                }
                InfoLine(Icons.Filled.LocationOn, ot.direccion)
                InfoLine(Icons.Filled.Engineering, if (ot.equipo.isBlank()) "Sin equipo" else ot.equipo)
                InfoLine(Icons.Filled.Schedule, "Programada: ${formatDateTime(ot.programadaPara)}")
                InfoLine(Icons.Filled.Event, "SLA ${ot.sla} · ${ot.distancia}")
                if (ot.exterior) {
                    InfoLine(Icons.Filled.Warning, "Equipo exterior")
                }
                if (ot.descripcion.isNotBlank()) {
                    Text(ot.descripcion, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                }
            }

            if (OtWorkflow.puedeReasignar(ot)) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (ot.tecnicoId == null) "ASIGNAR TÉCNICO" else "REASIGNAR TÉCNICO",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showTecnicoMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedTecnico?.let {
                                    "${it.nombre}${if (!it.disponible) " · no disponible" else ""}"
                                } ?: "Seleccionar técnico",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                        DropdownMenu(
                            expanded = showTecnicoMenu,
                            onDismissRequest = { showTecnicoMenu = false }
                        ) {
                            activos.forEach { tecnico ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(tecnico.nombre, fontSize = 12.sp)
                                            Text(
                                                if (tecnico.disponible) "Disponible" else "No disponible",
                                                fontSize = 9.sp,
                                                color = if (tecnico.disponible) Color(0xFF10B981) else Color(0xFFF59E0B)
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedTecnicoId = tecnico.id
                                        showTecnicoMenu = false
                                    }
                                )
                            }
                        }
                    }
                    DateTimeField(
                        label = "Fecha y hora programada",
                        timestamp = scheduledAt,
                        onValueChange = { scheduledAt = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (message != null) {
                        Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            message = if (selectedTecnicoId <= 0L) {
                                "Selecciona un técnico."
                            } else {
                                viewModel.assignOt(ot.id, selectedTecnicoId, scheduledAt)
                            }
                            if (message == null) message = "OT asignada correctamente."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                    ) {
                        Text(if (ot.tecnicoId == null) "Asignar OT" else "Reasignar OT", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MiniStat("${viewModel.selectedActivities.size}", "Actividades")
                MiniStat("${viewModel.evidenceList.size}", "Evidencias")
                MiniStat(if (viewModel.selectedCheckIn == null) "Pendiente" else "Registrado", "Check-in")
            }

            if (viewModel.selectedConformity != null) {
                val conformity = viewModel.selectedConformity!!
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Conformidad del cliente", color = Color(0xFF6EE7B7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(conformity.firmante, color = Color.White, fontSize = 11.sp)
                    if (conformity.observacion.isNotBlank()) {
                        Text(conformity.observacion, color = Color(0xFFA7F3D0), fontSize = 10.sp)
                    }
                    Text(formatDateTime(conformity.createdAt), color = Color(0xFF6EE7B7), fontSize = 9.sp)
                }
            }

            Text("TRAZABILIDAD", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            if (viewModel.selectedEvents.isEmpty()) {
                Text("Sin eventos registrados.", color = Color(0xFF64748B), fontSize = 10.sp)
            } else {
                viewModel.selectedEvents.forEach { event ->
                    EventRow(event)
                }
            }

            if (OtWorkflow.puedeCancelar(ot)) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5))
                ) {
                    Text("Cancelar OT", fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar ${ot.id}") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    label = { Text("Motivo (10 a 500 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val error = viewModel.cancelOt(ot.id, cancelReason)
                    if (error == null) {
                        showCancelDialog = false
                        message = "OT cancelada."
                    } else {
                        message = error
                    }
                }) { Text("Cancelar OT") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Volver") }
            }
        )
    }
}

@Composable
private fun InfoLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, color = Color(0xFFCBD5E1), fontSize = 10.sp)
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF94A3B8), fontSize = 8.sp)
    }
}

@Composable
private fun EventRow(event: OtEvento) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF111C2E))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF0C4A6E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Comment, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(event.tipo, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(event.detalle, color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("${event.actor} · ${formatShortDateTime(event.createdAt)}", color = Color(0xFF64748B), fontSize = 8.sp)
        }
    }
}
