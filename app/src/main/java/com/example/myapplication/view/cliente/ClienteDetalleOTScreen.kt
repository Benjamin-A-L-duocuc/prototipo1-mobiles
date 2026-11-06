package com.example.myapplication.view.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.EvidenceThumbnail
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun ClienteDetalleOTScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ot = viewModel.selectedOT
    val conformity = viewModel.selectedConformity
    var firmante by remember(ot.id) { mutableStateOf(viewModel.currentUserName) }
    var observacion by remember(ot.id) { mutableStateOf("") }
    var acepta by remember(ot.id) { mutableStateOf(false) }
    var message by remember(ot.id) { mutableStateOf<String?>(null) }
    var showRejectDialog by remember(ot.id) { mutableStateOf(false) }

    val puedeFirmar = ot.estado == OtWorkflow.CERRADA && conformity == null

    PhoneScreen(
        title = ot.id,
        subtitle = "Detalle del servicio",
        onBack = { onNavigate(Rutas.CLIENTE) }
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
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ot.equipo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${ot.local} · ${ot.tipo}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    StatusBadge(ot.estado)
                }
                InfoLine(Icons.Filled.LocationOn, ot.direccion)
                InfoLine(Icons.Filled.Engineering, ot.tecnicoNombre ?: "Técnico por asignar")
                InfoLine(Icons.Filled.Schedule, "Programada: ${formatDateTime(ot.programadaPara)}")
                if (ot.descripcion.isNotBlank()) {
                    Text(ot.descripcion, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                }
            }

            if (viewModel.evidenceList.isNotEmpty()) {
                Text("EVIDENCIAS DEL TRABAJO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.evidenceList.forEach { evidence ->
                        EvidenceThumbnail(path = evidence.path, size = 76)
                    }
                }
                val comentario = viewModel.evidenceList.map { it.comentario }.firstOrNull { it.isNotBlank() }
                if (comentario != null) {
                    Text(comentario, color = Color(0xFFCBD5E1), fontSize = 9.sp)
                }
            }

            if (conformity != null) {
                val approved = conformity.conforme
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(if (approved) Color(0xFF064E3B) else Color(0xFF7F1D1D))
                        .border(1.dp, if (approved) Color(0xFF10B981) else Color(0xFFEF4444), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (approved) Icons.Filled.CheckCircle else Icons.Filled.Close,
                            contentDescription = null,
                            tint = if (approved) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (approved) "Conformidad registrada" else "Trabajo rechazado",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("Recibe: ${conformity.firmante}", color = Color(0xFFE2E8F0), fontSize = 10.sp)
                    if (conformity.observacion.isNotBlank()) {
                        Text(conformity.observacion, color = Color(0xFFE2E8F0), fontSize = 10.sp)
                    }
                    Text(formatDateTime(conformity.createdAt), color = Color(0xFFCBD5E1), fontSize = 9.sp)
                }
            } else if (puedeFirmar) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("CONFORMIDAD DEL CLIENTE", color = Color(0xFFFCD34D), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Revisa las evidencias y confirma la recepción del trabajo.", color = Color(0xFF94A3B8), fontSize = 9.sp)
                    OutlinedTextField(
                        value = firmante,
                        onValueChange = { firmante = it },
                        label = { Text("Nombre de quien recibe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        label = { Text("Observaciones") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = acepta, onCheckedChange = { acepta = it })
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Confirmo que el trabajo fue realizado conforme.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 10.sp
                        )
                    }
                    if (message != null) {
                        Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
                    }
                    Button(
                        onClick = {
                            if (firmante.trim().length < 3) {
                                message = "Ingresa el nombre de quien recibe."
                            } else if (!acepta) {
                                message = "Debes confirmar que el trabajo fue realizado conforme."
                            } else {
                                val error = viewModel.conformOt(ot.id, firmante, observacion, true)
                                message = error
                            }
                        },
                        enabled = acepta,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Confirmar conformidad", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5))
                    ) {
                        Text("Rechazar trabajo", fontSize = 10.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C4A6E))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "La conformidad estará disponible cuando el técnico cierre la OT.",
                        color = Color(0xFFBAE6FD),
                        fontSize = 9.sp
                    )
                }
            }

            Text("TRAZABILIDAD", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            if (viewModel.selectedEvents.isEmpty()) {
                Text("Sin eventos registrados.", color = Color(0xFF64748B), fontSize = 10.sp)
            } else {
                viewModel.selectedEvents.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF111C2E))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(event.tipo, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(event.detalle, color = Color(0xFF94A3B8), fontSize = 9.sp)
                            Text(formatShortDateTime(event.createdAt), color = Color(0xFF64748B), fontSize = 8.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Rechazar trabajo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Indica el motivo del rechazo (mínimo 10 caracteres).", fontSize = 11.sp)
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val error = if (firmante.trim().length < 3) {
                        "Ingresa el nombre de quien recibe."
                    } else if (observacion.trim().length < 10) {
                        "El motivo debe tener al menos 10 caracteres."
                    } else {
                        viewModel.conformOt(ot.id, firmante, observacion, false)
                    }
                    if (error == null) {
                        showRejectDialog = false
                    } else {
                        message = error
                    }
                }) { Text("Rechazar") }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) { Text("Volver") }
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
