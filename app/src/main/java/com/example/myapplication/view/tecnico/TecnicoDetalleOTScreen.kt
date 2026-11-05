package com.example.myapplication.view.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.OfflineBanner
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoDetalleOTScreen(viewModel: AppViewModel) {
    val ot = viewModel.selectedOT
    val tecnico = MockRepository.tecnico
    val estado = if (viewModel.checkinDone) "En ejecución" else if (viewModel.otAccepted) "Aceptada" else ot.estado

    PhoneScreen(
        title = ot.id,
        subtitle = "Detalle de la orden de trabajo",
        onBack = { viewModel.navigateTo(Rutas.JORNADA) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card main info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        PriorityBadge(priority = ot.prioridad)
                        StatusBadge(status = estado)
                    }
                    Text(text = ot.tipo, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Text(text = ot.cliente, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = ot.local, color = Color(0xFF94A3B8), fontSize = 11.sp)

                HorizontalDivider(color = Color(0xFF334155))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Place, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Text(text = ot.direccion, color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Thermostat, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Text(text = "${ot.equipo} · ${ot.equipoId}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Text(text = "SLA restante: ${ot.sla}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Build, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Text(text = "Técnico: ${tecnico.nombre}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                }
            }

            // Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(text = "DESCRIPCIÓN DEL CLIENTE", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = ot.descripcion, color = Color(0xFFCBD5E1), fontSize = 11.sp, lineHeight = 16.sp)
            }

            // Timeline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(text = "TRAZABILIDAD", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                val steps = listOf(
                    Triple("Creada", true, "08:10"),
                    Triple("Asignada", true, "08:25"),
                    Triple("Aceptada", viewModel.otAccepted, if (viewModel.otAccepted) "08:40" else "—"),
                    Triple("Check-in", viewModel.checkinDone, if (viewModel.checkinDone) "09:41" else "—")
                )

                steps.forEachIndexed { index, (label, done, time) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (done) Color(0xFF10B981) else Color.Transparent)
                                    .border(1.dp, if (done) Color(0xFF10B981) else Color(0xFF64748B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (done) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                            }
                            if (index < steps.size - 1) {
                                Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color(0xFF334155)))
                            }
                        }
                        Text(
                            text = label,
                            color = if (done) Color.White else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = time, color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }

            OfflineBanner(isOffline = viewModel.connection == "offline")

            if (viewModel.otAccepted && !viewModel.checkinDone) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "OT aceptada. El siguiente paso es registrar el check-in.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                }
            } else if (viewModel.checkinDone) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "Check-in registrado. La OT está en ejecución.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                }
            }

            // Action buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                if (!viewModel.otAccepted) {
                    Button(
                        onClick = { viewModel.acceptOt() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aceptar OT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (!viewModel.checkinDone) {
                    Button(
                        onClick = { viewModel.navigateTo(Rutas.CHECKIN) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar check-in", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.navigateTo(Rutas.EJECUCION) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar ejecución", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(Rutas.JORNADA) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Volver a mi jornada")
                }
            }
        }
    }
}
