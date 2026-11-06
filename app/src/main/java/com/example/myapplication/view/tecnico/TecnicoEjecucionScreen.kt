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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.myapplication.model.Material
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoEjecucionScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ot = viewModel.selectedOT
    var diagnosis by remember(ot.id) { mutableStateOf(ot.diagnostico) }
    var message by remember(ot.id) { mutableStateOf<String?>(null) }
    var activitiesMessage by remember(ot.id) { mutableStateOf<String?>(null) }
    var newActivity by remember(ot.id) { mutableStateOf("") }
    var materialsMessage by remember(ot.id) { mutableStateOf<String?>(null) }
    var materialName by remember(ot.id) { mutableStateOf("") }
    var materialUnit by remember(ot.id) { mutableStateOf("unidad") }
    var materialQuantity by remember(ot.id) { mutableStateOf("1") }
    var materials by remember(ot.id) { mutableStateOf(viewModel.selectedMaterials) }

    LaunchedEffect(ot.id, viewModel.selectedMaterials) {
        if (materials != viewModel.selectedMaterials) {
            materials = viewModel.selectedMaterials
        }
    }

    val activities = viewModel.selectedActivities
    val completed = activities.count { it.completada }
    val evidenceCount = viewModel.evidenceList.size
    val checkIn = viewModel.selectedCheckIn

    PhoneScreen(
        title = "Ejecución",
        subtitle = ot.id,
        onBack = { onNavigate(Rutas.DETALLE) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ot.cliente, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${ot.local} · ${ot.equipo}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                    if (checkIn != null) {
                        Text("Check-in: ${formatShortDateTime(checkIn.createdAt)}", color = Color(0xFF6EE7B7), fontSize = 8.sp)
                    }
                }
                StatusBadge(ot.estado)
            }

            Text("DIAGNÓSTICO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = diagnosis,
                onValueChange = { diagnosis = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                label = { Text("Describe el diagnóstico técnico") }
            )
            OutlinedButton(
                onClick = {
                    if (diagnosis.trim().length < 5) {
                        message = "El diagnóstico debe tener al menos 5 caracteres."
                    } else {
                        viewModel.saveDiagnostico(diagnosis)
                        message = "Diagnóstico guardado."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Science, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Guardar diagnóstico", fontSize = 10.sp)
            }
            if (message != null) Text(message.orEmpty(), color = Color(0xFF7DD3FC), fontSize = 9.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ACTIVIDADES", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("$completed/${activities.size} completadas", color = Color(0xFF64748B), fontSize = 9.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newActivity,
                    onValueChange = { newActivity = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Nueva actividad") }
                )
                Spacer(Modifier.width(6.dp))
                IconButton(onClick = {
                    if (newActivity.trim().length < 3) {
                        activitiesMessage = "Describe la actividad."
                    } else {
                        viewModel.addActivity(newActivity)
                        newActivity = ""
                        activitiesMessage = null
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar actividad", tint = Color(0xFF38BDF8))
                }
            }
            if (activitiesMessage != null) Text(activitiesMessage.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 9.sp)

            if (activities.isEmpty()) {
                Text("Aún no hay actividades. Agrega al menos una para cerrar la OT.", color = Color(0xFF64748B), fontSize = 9.sp)
            } else {
                activities.forEach { activity ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = activity.completada,
                            onCheckedChange = { viewModel.toggleActivity(activity) }
                        )
                        Text(
                            activity.descripcion,
                            color = if (activity.completada) Color(0xFF94A3B8) else Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.deleteActivity(activity) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar actividad", tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MATERIALES", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Opcional", color = Color(0xFF64748B), fontSize = 9.sp)
            }

            materials.forEach { material ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(material.nombre, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(material.unidad, color = Color(0xFF94A3B8), fontSize = 8.sp)
                    }
                    IconButton(onClick = {
                        materials = materials.map {
                            if (it == material) it.copy(cantidad = (it.cantidad - 1).coerceAtLeast(0)) else it
                        }
                    }) {
                        Icon(Icons.Filled.Remove, contentDescription = "Quitar", modifier = Modifier.size(14.dp))
                    }
                    Text("${material.cantidad}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        materials = materials.map {
                            if (it == material) it.copy(cantidad = it.cantidad + 1) else it
                        }
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar", modifier = Modifier.size(14.dp))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = materialName,
                    onValueChange = { materialName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Material") }
                )
                Spacer(Modifier.width(4.dp))
                OutlinedTextField(
                    value = materialQuantity,
                    onValueChange = { materialQuantity = it.filter(Char::isDigit) },
                    modifier = Modifier.width(54.dp),
                    singleLine = true,
                    label = { Text("Cant.") }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = materialUnit,
                    onValueChange = { materialUnit = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Unidad") }
                )
                Spacer(Modifier.width(6.dp))
                OutlinedButton(onClick = {
                    val quantity = materialQuantity.toIntOrNull() ?: 0
                    if (materialName.trim().length < 3 || quantity <= 0) {
                        materialsMessage = "Indica material y cantidad válida."
                    } else {
                        materials = materials + Material(materialName.trim(), quantity, materialUnit.trim().ifBlank { "unidad" })
                        materialName = ""
                        materialQuantity = "1"
                        materialsMessage = null
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar", fontSize = 10.sp)
                }
            }
            if (materialsMessage != null) Text(materialsMessage.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 9.sp)
            OutlinedButton(
                onClick = {
                    viewModel.saveMaterials(materials)
                    materialsMessage = "Materiales guardados."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar materiales", fontSize = 10.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Evidencias", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$evidenceCount fotografías adjuntas", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
                OutlinedButton(onClick = { onNavigate(Rutas.EVIDENCIAS) }) {
                    Text("Gestionar", fontSize = 9.sp)
                }
            }

            Button(
                onClick = {
                    if (diagnosis.trim().length < 5) {
                        message = "Guarda un diagnóstico antes de continuar."
                    } else if (activities.isEmpty()) {
                        message = "Agrega al menos una actividad."
                    } else if (evidenceCount == 0) {
                        message = "Agrega al menos una evidencia fotográfica."
                    } else {
                        viewModel.saveDiagnostico(diagnosis)
                        viewModel.saveMaterials(materials)
                        onNavigate(Rutas.CIERRE)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Continuar al cierre", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
