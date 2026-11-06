package com.example.myapplication.view.jefe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.OT
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun JefeDashboardScreen(viewModel: AppViewModel) {
    var editingOt by remember { mutableStateOf<OT?>(null) }
    var deletingOtId by remember { mutableStateOf<String?>(null) }

    // Dialog Edit OT
    if (editingOt != null) {
        var clienteEdit by remember { mutableStateOf(editingOt!!.cliente) }
        var equipoEdit by remember { mutableStateOf(editingOt!!.equipo) }
        var prioridadEdit by remember { mutableStateOf(editingOt!!.prioridad) }
        var estadoEdit by remember { mutableStateOf(editingOt!!.estado) }

        AlertDialog(
            onDismissRequest = { editingOt = null },
            title = { Text("Editar Orden ${editingOt!!.id}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = clienteEdit,
                        onValueChange = { clienteEdit = it },
                        label = { Text("Cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = equipoEdit,
                        onValueChange = { equipoEdit = it },
                        label = { Text("Equipo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Baja", "Media", "Alta").forEach { p ->
                            FilterChip(
                                selected = prioridadEdit == p,
                                onClick = { prioridadEdit = p },
                                label = { Text(p, fontSize = 10.sp) }
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Asignada", "En ejecución", "Cerrada").forEach { e ->
                            FilterChip(
                                selected = estadoEdit == e,
                                onClick = { estadoEdit = e },
                                label = { Text(e, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updated = editingOt!!.copy(
                        cliente = clienteEdit,
                        equipo = equipoEdit,
                        prioridad = prioridadEdit,
                        estado = estadoEdit
                    )
                    viewModel.updateOt(updated)
                    editingOt = null
                }) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingOt = null }) { Text("Cancelar") }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Dialog Delete OT
    if (deletingOtId != null) {
        AlertDialog(
            onDismissRequest = { deletingOtId = null },
            title = { Text("Eliminar Orden", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar permanentemente la orden $deletingOtId?", color = Color(0xFFCBD5E1), fontSize = 12.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOt(deletingOtId!!)
                        deletingOtId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingOtId = null }) { Text("Cancelar") }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    PhoneScreen(
        title = "Panel de mantenimiento",
        subtitle = "Jueves 27 de octubre"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "RESUMEN OPERATIVO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Hola, ${viewModel.currentUserName}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "GJ", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Stats grid (2x2)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), title = "OT ABIERTAS", value = "${viewModel.otList.count { it.estado != "Cerrada" && it.estado != "Conformada" }}", tint = Color(0xFF0EA5E9))
                StatCard(modifier = Modifier.weight(1f), title = "SLA EN RIESGO", value = "${viewModel.atRiskSlaCount}", tint = Color(0xFFEF4444))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(modifier = Modifier.weight(1f), title = "TÉCNICOS ACTIVOS", value = "5", tint = Color(0xFF10B981))
                StatCard(modifier = Modifier.weight(1f), title = "CERRADAS HOY", value = "${viewModel.otList.count { it.estado == "Cerrada" || it.estado == "Conformada" }}", tint = Color(0xFF8B5CF6))
            }

            if (viewModel.scenario == "lluvia") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFDE68A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.WbCloudy, contentDescription = null, tint = Color(0xFFB45309))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Alerta climática 15:00–18:00", color = Color(0xFF78350F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Lluvia y viento fuerte previstos. 3 visitas en azotea podrían reprogramarse.", color = Color(0xFF92400E), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Rutas.AGENDA) },
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("Ver agenda", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // Quick action buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.navigateTo(Rutas.CREAR_OT) },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Crear OT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Rutas.AGENDA) },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agenda", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Dynamic SLA Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "CUMPLIMIENTO SLA (30 DÍAS)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF065F46)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${viewModel.monthlyCompliancePercentage}%", color = Color(0xFF6EE7B7), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text(text = "Cumplimiento calculado", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${viewModel.otList.count { it.estado == "Cerrada" || it.estado == "Conformada" }} de ${viewModel.otList.size} OT completadas.", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        Text(text = "Cálculo en base a SQLite", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // OT Prioritarias list with Edit & Delete actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "ÓRDENES DE TRABAJO (CRUD)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "${viewModel.otList.size} registradas", color = Color(0xFF0EA5E9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            val ots = viewModel.otList
            ots.forEach { ot ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(16.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ot.cliente, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${ot.id} · ${ot.equipo}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            PriorityBadge(priority = ot.prioridad)
                            StatusBadge(status = ot.estado)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { editingOt = ot }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                            }
                            IconButton(onClick = { deletingOtId = ot.id }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, tint: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(text = title, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = tint, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}
