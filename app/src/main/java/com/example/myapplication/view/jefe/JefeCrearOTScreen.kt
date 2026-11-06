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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.example.myapplication.model.OT
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.DateTimeField
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun JefeCrearOTScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val customers = viewModel.customers.filter { it.activo }
    val locations = viewModel.locations.filter { it.activo }
    val equipment = viewModel.equipment.filter { it.activo }
    val technicians = viewModel.technicians.filter { it.activo && it.disponible }

    var selectedClienteId by remember { mutableStateOf(customers.firstOrNull()?.id ?: 0L) }
    var selectedLocalId by remember {
        mutableStateOf(locations.firstOrNull { it.clienteId == selectedClienteId }?.id ?: 0L)
    }
    var selectedEquipoId by remember {
        mutableStateOf(equipment.firstOrNull { it.localId == selectedLocalId }?.id.orEmpty())
    }
    var tipo by remember { mutableStateOf("Preventivo") }
    var prioridad by remember { mutableStateOf("Media") }
    var descripcion by remember { mutableStateOf("") }
    var asignarAhora by remember { mutableStateOf(technicians.isNotEmpty()) }
    var selectedTecnicoId by remember { mutableStateOf(technicians.firstOrNull()?.id ?: 0L) }
    var scheduledAt by remember { mutableStateOf(System.currentTimeMillis() + 60 * 60 * 1000L) }
    var message by remember { mutableStateOf<String?>(null) }

    var showClienteMenu by remember { mutableStateOf(false) }
    var showLocalMenu by remember { mutableStateOf(false) }
    var showEquipoMenu by remember { mutableStateOf(false) }
    var showTecnicoMenu by remember { mutableStateOf(false) }

    val cliente = customers.find { it.id == selectedClienteId }
    val localesCliente = locations.filter { it.clienteId == selectedClienteId }
    val local = localesCliente.find { it.id == selectedLocalId }
    val equiposLocal = equipment.filter { it.localId == selectedLocalId }
    val equipo = equiposLocal.find { it.id == selectedEquipoId }
    val tecnico = technicians.find { it.id == selectedTecnicoId }

    PhoneScreen(
        title = "Crear OT",
        subtitle = "Datos, equipo y asignación",
        onBack = { onNavigate(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("CLIENTE Y UBICACIÓN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            SelectField(
                label = "Cliente",
                value = cliente?.nombre ?: "Seleccionar cliente",
                expanded = showClienteMenu,
                onExpand = { showClienteMenu = true },
                onDismiss = { showClienteMenu = false }
            ) {
                customers.forEach { customer ->
                    DropdownMenuItem(
                        text = { Text(customer.nombre) },
                        onClick = {
                            selectedClienteId = customer.id
                            val nextLocal = locations.firstOrNull { it.clienteId == customer.id }
                            selectedLocalId = nextLocal?.id ?: 0L
                            selectedEquipoId = equipment.firstOrNull { it.localId == selectedLocalId }?.id.orEmpty()
                            showClienteMenu = false
                        }
                    )
                }
            }
            SelectField(
                label = "Local",
                value = local?.let { "${it.nombre} · ${it.comuna}" } ?: "Seleccionar local",
                expanded = showLocalMenu,
                onExpand = { showLocalMenu = true },
                onDismiss = { showLocalMenu = false }
            ) {
                localesCliente.forEach { location ->
                    DropdownMenuItem(
                        text = { Text("${location.nombre} · ${location.comuna}") },
                        onClick = {
                            selectedLocalId = location.id
                            selectedEquipoId = equipment.firstOrNull { it.localId == location.id }?.id.orEmpty()
                            showLocalMenu = false
                        }
                    )
                }
            }
            if (local != null) {
                Text("${local.direccion}, ${local.comuna}", color = Color(0xFF64748B), fontSize = 9.sp)
            }

            Text("EQUIPO Y TRABAJO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            SelectField(
                label = "Equipo",
                value = equipo?.let { "${it.nombre} · ${it.id}" } ?: "Seleccionar equipo",
                expanded = showEquipoMenu,
                onExpand = { showEquipoMenu = true },
                onDismiss = { showEquipoMenu = false }
            ) {
                equiposLocal.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.nombre} · ${item.id}") },
                        onClick = { selectedEquipoId = item.id; showEquipoMenu = false }
                    )
                }
            }
            if (equipo?.exterior == true) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF78350F))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Equipo exterior: verifica condiciones climáticas.", color = Color(0xFFFEF3C7), fontSize = 9.sp)
                }
            }

            Text("Tipo de mantenimiento", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Preventivo", "Correctivo", "Emergencia").forEach { option ->
                    FilterChip(
                        selected = tipo == option,
                        onClick = { tipo = option },
                        label = { Text(option, fontSize = 9.sp) }
                    )
                }
            }

            Text("Prioridad", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Baja", "Media", "Alta").forEach { option ->
                    FilterChip(
                        selected = prioridad == option,
                        onClick = { prioridad = option },
                        label = { Text(option, fontSize = 9.sp) }
                    )
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del trabajo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Asignar al crear", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Si no asignas, la OT queda pendiente.", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
                Switch(checked = asignarAhora, onCheckedChange = { asignarAhora = it })
            }

            if (asignarAhora) {
                SelectField(
                    label = "Técnico",
                    value = tecnico?.nombre ?: "Seleccionar técnico",
                    expanded = showTecnicoMenu,
                    onExpand = { showTecnicoMenu = true },
                    onDismiss = { showTecnicoMenu = false }
                ) {
                    technicians.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nombre) },
                            onClick = { selectedTecnicoId = item.id; showTecnicoMenu = false }
                        )
                    }
                }
                DateTimeField(
                    label = "Fecha y hora programada",
                    timestamp = scheduledAt,
                    onValueChange = { scheduledAt = it }
                )
            }

            if (message != null) {
                Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }

            Button(
                onClick = {
                    message = when {
                        cliente == null -> "Selecciona un cliente."
                        local == null -> "Selecciona un local."
                        equipo == null -> "Selecciona un equipo."
                        descripcion.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres."
                        asignarAhora && tecnico == null -> "Selecciona un técnico disponible."
                        else -> null
                    }
                    if (message != null) return@Button

                    val selectedCliente = cliente!!
                    val selectedLocal = local!!
                    val selectedEquipo = equipo!!
                    val ot = OT(
                        id = viewModel.nextOtId(),
                        cliente = selectedCliente.nombre,
                        local = selectedLocal.nombre,
                        direccion = "${selectedLocal.direccion}, ${selectedLocal.comuna}",
                        equipo = selectedEquipo.nombre,
                        equipoId = selectedEquipo.id,
                        tipo = tipo,
                        prioridad = prioridad,
                        estado = OtWorkflow.PENDIENTE,
                        sla = when (prioridad) {
                            "Alta" -> "2h"
                            "Media" -> "4h"
                            else -> "6h"
                        },
                        distancia = "—",
                        descripcion = descripcion.trim(),
                        exterior = selectedEquipo.exterior,
                        clienteId = selectedCliente.id,
                        localId = selectedLocal.id,
                        programadaPara = if (asignarAhora) scheduledAt else null,
                        creadaEn = System.currentTimeMillis()
                    )
                    viewModel.addNewOt(ot)
                    if (asignarAhora) {
                        val error = viewModel.assignOt(ot.id, selectedTecnicoId, scheduledAt)
                        if (error != null) {
                            viewModel.deleteOt(ot.id)
                            message = error
                            return@Button
                        }
                    }
                    onNavigate(Rutas.DASHBOARD)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (asignarAhora) "Crear y asignar OT" else "Crear OT pendiente", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SelectField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, color = Color(0xFFCBD5E1), fontSize = 10.sp)
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = onExpand, modifier = Modifier.fillMaxWidth()) {
                Text(value, color = Color.White, fontSize = 11.sp)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            content()
        }
    }
}
