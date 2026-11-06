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
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.myapplication.model.OtEvento
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatAccuracy
import com.example.myapplication.util.formatCoordinate
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoDetalleOTScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ot = viewModel.selectedOT
    var message by remember(ot.id) { mutableStateOf<String?>(null) }
    var showRejectDialog by remember(ot.id) { mutableStateOf(false) }
    var rejectReason by remember(ot.id) { mutableStateOf("") }
    val checkIn = viewModel.selectedCheckIn

    PhoneScreen(
        title = ot.id,
        subtitle = OtWorkflow.siguientePaso(ot),
        onBack = { onNavigate(Rutas.JORNADA) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                InfoLine(Icons.Filled.Engineering, "${ot.equipo} · ${ot.equipoId}")
                InfoLine(Icons.Filled.Schedule, "Programada: ${formatDateTime(ot.programadaPara)}")
                if (ot.exterior) InfoLine(Icons.Filled.Warning, "Equipo exterior")
                if (ot.descripcion.isNotBlank()) {
                    Text(ot.descripcion, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                }
            }

            if (message != null) {
                Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0C4A6E))
                    .border(1.dp, Color(0xFF0EA5E9), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("SIGUIENTE ACCIÓN", color = Color(0xFF7DD3FC), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(OtWorkflow.siguientePaso(ot), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(actionDescription(ot.estado), color = Color(0xFFBAE6FD), fontSize = 9.sp)
                }
            }

            when (ot.estado) {
                OtWorkflow.ASIGNADA -> {
                    Button(
                        onClick = {
                            message = viewModel.acceptOt(ot.id)
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Aceptar OT", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5))
                    ) {
                        Text("Rechazar asignación", fontSize = 11.sp)
                    }
                }
                OtWorkflow.ACEPTADA -> {
                    Button(
                        onClick = { onNavigate(Rutas.CHECKIN) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Registrar check-in", fontWeight = FontWeight.Bold)
                    }
                }
                OtWorkflow.EN_EJECUCION -> {
                    Button(
                        onClick = { onNavigate(Rutas.EJECUCION) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                    ) {
                        Text("Continuar ejecución", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onNavigate(Rutas.EVIDENCIAS) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Gestionar evidencias (${viewModel.evidenceList.size})", fontSize = 11.sp)
                    }
                }
                OtWorkflow.CERRADA -> {
                    Button(
                        onClick = { onNavigate(Rutas.EVIDENCIAS) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Ver evidencias (${viewModel.evidenceList.size})", fontSize = 11.sp)
                    }
                    Text(
                        "El trabajo está cerrado. Falta la conformidad del cliente.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
                OtWorkflow.CONFORMADA, OtWorkflow.RECHAZADA_CLIENTE, OtWorkflow.CANCELADA -> {
                    Text(OtWorkflow.siguientePaso(ot), color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                else -> {
                    Text("La OT aún no está lista para ejecución.", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }

            if (checkIn != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text("CHECK-IN REGISTRADO", color = Color(0xFF6EE7B7), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${formatCoordinate(checkIn.latitude)}, ${formatCoordinate(checkIn.longitude)}", color = Color.White, fontSize = 10.sp)
                    Text("${formatAccuracy(checkIn.accuracy)} · ${checkIn.source} · ${formatShortDateTime(checkIn.createdAt)}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            }

            Text("TRAZABILIDAD", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            if (viewModel.selectedEvents.isEmpty()) {
                Text("Sin eventos registrados.", color = Color(0xFF64748B), fontSize = 10.sp)
            } else {
                viewModel.selectedEvents.forEach { event -> EventRow(event) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Rechazar ${ot.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("La OT volverá a asignación. Indica el motivo (10 a 300 caracteres).", fontSize = 11.sp)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val error = viewModel.rejectOt(ot.id, rejectReason)
                    if (error == null) {
                        showRejectDialog = false
                        onNavigate(Rutas.JORNADA)
                    } else {
                        rejectReason = rejectReason
                        message = error
                        showRejectDialog = false
                    }
                }) { Text("Rechazar") }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) { Text("Volver") }
            }
        )
    }
}

private fun actionDescription(estado: String): String = when (estado) {
    OtWorkflow.ASIGNADA -> "Confirma que puedes realizar la visita en el horario indicado."
    OtWorkflow.ACEPTADA -> "Registra tu llegada al local con ubicación si está disponible."
    OtWorkflow.EN_EJECUCION -> "Completa diagnóstico, actividades, materiales y evidencias."
    OtWorkflow.CERRADA -> "El cliente debe revisar y firmar la conformidad."
    else -> "Revisa el estado de la orden."
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
