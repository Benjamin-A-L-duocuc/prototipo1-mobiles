package com.example.myapplication.view.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun ClienteSeguimientoScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var signed by remember { mutableStateOf(false) }
    var conforme by remember { mutableStateOf(true) }
    var clientNameInput by remember { mutableStateOf(viewModel.currentUserName) }
    val tecnico = MockRepository.tecnico
    val ot = viewModel.selectedOT
    val estado = if (signed || ot.estado == "Conformada") "Conformada" else ot.estado

    PhoneScreen(
        title = "Seguimiento OT",
        subtitle = "${ot.id} · ${ot.cliente}",
        actions = {
            IconButton(onClick = {
                viewModel.logout()
                onNavigate(Rutas.LOGIN)
            }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header card
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
                    Text(text = ot.id, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Text(text = ot.cliente, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = ot.local, color = Color(0xFF94A3B8), fontSize = 11.sp)

                HorizontalDivider(color = Color(0xFF334155))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "EQUIPO", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = ot.equipo, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "TÉCNICO", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = tecnico.nombre, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "SLA", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = ot.sla, color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
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
                Text(text = "ESTADO DE LA OT", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                val isConformada = signed || ot.estado == "Conformada"
                val timelineItems = MockRepository.timeline.map {
                    if (it.hito == "Conformada") it.copy(fecha = if (isConformada) "27/10 · 10:40" else "Pendiente", hecho = isConformada)
                    else it
                }

                timelineItems.forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (item.hecho) Color(0xFF10B981) else Color.Transparent)
                                    .border(1.dp, if (item.hecho) Color(0xFF10B981) else Color(0xFF64748B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.hecho) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                            if (index < timelineItems.size - 1) {
                                Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color(0xFF334155)))
                            }
                        }
                        Text(
                            text = item.hito,
                            color = if (item.hecho) Color.White else Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = if (item.hecho) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = item.fecha, color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                }
            }

            // Técnico contact card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0EA5E9).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tecnico.iniciales, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tecnico.nombre, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = tecnico.rol, color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Phone, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                }
            }

            // Conformidad validation / signed state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                val isConformada = signed || ot.estado == "Conformada"
                if (isConformada) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF065F46).copy(alpha = 0.3f))
                            .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "Trabajo validado como conforme por el cliente.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = clientNameInput, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Firma registrada · Estado: Conformada", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                } else {
                    Text(text = "VALIDAR CONFORMIDAD", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = clientNameInput,
                        onValueChange = { clientNameInput = it },
                        label = { Text("Nombre del Cliente / Representante") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "[ Firma Digital: $clientNameInput ]", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Checkbox(
                            checked = conforme,
                            onCheckedChange = { conforme = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0EA5E9))
                        )
                        Text(text = "Declaro que el trabajo fue realizado y el equipo quedó operativo.", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            signed = true
                            viewModel.updateOtState(ot.id, "Conformada")
                        },
                        enabled = conforme && clientNameInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Firmar conformidad", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
