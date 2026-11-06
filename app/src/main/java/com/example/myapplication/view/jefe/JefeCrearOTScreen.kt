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
import androidx.compose.material.icons.automirrored.filled.ListAlt
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
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun JefeCrearOTScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var creada by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf("preventivo") }
    var prioridad by remember { mutableStateOf("media") }
    var disponible by remember { mutableStateOf("si") }
    var clienteInput by remember { mutableStateOf("Supermercado Ñuñoa") }
    var equipoInput by remember { mutableStateOf("Cámara frigorífica 4 puertas") }
    var descripcionInput by remember { mutableStateOf("Mantención y revisión general solicitada por local.") }
    var createdOtId by remember { mutableStateOf("OT-1046") }

    val sinDisponibilidad = disponible == "no"

    if (creada) {
        PhoneScreen(
            title = "Crear OT",
            subtitle = "Nueva orden de trabajo",
            onBack = { onNavigate(Rutas.DASHBOARD) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF065F46)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (sinDisponibilidad) "OT guardada sin asignar" else "OT creada y asignada",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (sinDisponibilidad) "$createdOtId quedó pendiente de asignación." else "$createdOtId asignada a Cristian Rojas para el 28/10.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "ID", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = createdOtId, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Cliente", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = clienteInput, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Equipo", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = equipoInput, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Prioridad", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = prioridad, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Técnico", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = if (sinDisponibilidad) "Sin asignar" else "Cristian Rojas", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onNavigate(Rutas.DASHBOARD) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Volver al panel", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    PhoneScreen(
        title = "Crear OT",
        subtitle = "Nueva orden de trabajo",
        onBack = { onNavigate(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "DETALLES DE LA OT", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = clienteInput,
                    onValueChange = { clienteInput = it },
                    label = { Text("Cliente / Empresa", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )

                OutlinedTextField(
                    value = equipoInput,
                    onValueChange = { equipoInput = it },
                    label = { Text("Equipo / Activo", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )

                OutlinedTextField(
                    value = descripcionInput,
                    onValueChange = { descripcionInput = it },
                    label = { Text("Descripción del problema", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Tipo de OT", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("preventivo" to "Preventivo", "correctivo" to "Correctivo").forEach { (v, label) ->
                            val sel = tipo == v
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) Color(0xFF0EA5E9) else Color(0xFF0F172A))
                                    .border(1.dp, if (sel) Color(0xFF38BDF8) else Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .clickable { tipo = v },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = if (sel) Color.White else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Prioridad", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("baja" to "Baja", "media" to "Media", "alta" to "Alta").forEach { (v, label) ->
                            val sel = prioridad == v
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) Color(0xFF0EA5E9) else Color(0xFF0F172A))
                                    .border(1.dp, if (sel) Color(0xFF38BDF8) else Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .clickable { prioridad = v },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = if (sel) Color.White else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "ASIGNACIÓN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Disponibilidad del técnico", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("si" to "Disponible", "no" to "Sin disponibilidad").forEach { (v, label) ->
                            val sel = disponible == v
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) Color(0xFF0EA5E9) else Color(0xFF0F172A))
                                    .border(1.dp, if (sel) Color(0xFF38BDF8) else Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .clickable { disponible = v },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = if (sel) Color.White else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (sinDisponibilidad) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(10.dp)
                    ) {
                        Text(text = "El técnico no tiene disponibilidad en ese horario. Se guardará como pendiente.", color = Color(0xFF92400E), fontSize = 10.sp)
                    }
                }
            }

            Button(
                onClick = {
                    createdOtId = viewModel.nextOtId()
                    val nextIdNum = createdOtId.removePrefix("OT-").toIntOrNull() ?: 1046
                    val newOt = OT(
                        id = createdOtId,
                        cliente = clienteInput.ifBlank { "Cliente General" },
                        local = "Local Principal",
                        direccion = "Av. Principal 123",
                        equipo = equipoInput.ifBlank { "Equipo Climatización" },
                        equipoId = "EQ-${100 + nextIdNum % 100}",
                        tipo = tipo.replaceFirstChar { it.uppercase() },
                        prioridad = prioridad.replaceFirstChar { it.uppercase() },
                        estado = if (sinDisponibilidad) "Pendiente" else "Asignada",
                        sla = "2h",
                        distancia = "3,2 km",
                        descripcion = descripcionInput.ifBlank { "Orden de trabajo creada desde panel de jefatura." },
                        exterior = false
                    )
                    viewModel.addNewOt(newOt)
                    creada = true
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (sinDisponibilidad) "Guardar sin asignar" else "Crear y asignar OT", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
