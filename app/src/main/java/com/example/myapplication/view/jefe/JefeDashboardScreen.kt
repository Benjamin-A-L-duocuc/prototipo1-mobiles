package com.example.myapplication.view.jefe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.util.isToday
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun JefeDashboardScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val abiertas = viewModel.otList.filter {
        it.estado != OtWorkflow.CERRADA && it.estado != OtWorkflow.CONFORMADA &&
            it.estado != OtWorkflow.RECHAZADA_CLIENTE && it.estado != OtWorkflow.CANCELADA
    }
    val operacion = abiertas.filter { it.estado != OtWorkflow.PENDIENTE }
    val cerradasHoy = viewModel.otList.count {
        (it.estado == OtWorkflow.CERRADA || it.estado == OtWorkflow.CONFORMADA) &&
            isToday(it.cerradaEn ?: it.conformadaEn)
    }
    val tecnicosDisponibles = viewModel.technicians.count { it.activo && it.disponible }

    PhoneScreen(
        title = "Panel de mantenimiento",
        subtitle = "Planifica, asigna y supervisa las OT",
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
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "RESUMEN OPERATIVO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Hola, ${viewModel.currentUserName}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "GJ", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(Modifier.weight(1f), "OT ABIERTAS", "${abiertas.size}", Color(0xFF0EA5E9))
                StatCard(Modifier.weight(1f), "POR ASIGNAR", "${viewModel.pendingAssignmentOts.size}", Color(0xFFF59E0B))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(Modifier.weight(1f), "TÉCNICOS DISPONIBLES", "$tecnicosDisponibles", Color(0xFF10B981))
                StatCard(Modifier.weight(1f), "CERRADAS HOY", "$cerradasHoy", Color(0xFF8B5CF6))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onNavigate(Rutas.CREAR_OT) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Crear OT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onNavigate(Rutas.AGENDA) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agenda", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onNavigate(Rutas.CATALOGO) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Filled.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catálogos", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (viewModel.scenario == "lluvia") {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WbCloudy, contentDescription = null, tint = Color(0xFFB45309))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alerta climática", color = Color(0xFF78350F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Revisa las visitas con equipos exteriores antes de reprogramar.", color = Color(0xFF92400E), fontSize = 9.sp)
                    }
                    Text("Ver agenda", color = Color(0xFFB45309), fontSize = 9.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(Rutas.AGENDA) })
                }
            }

            SectionHeader("REQUIEREN ASIGNACIÓN", "${viewModel.pendingAssignmentOts.size} pendientes")
            if (viewModel.pendingAssignmentOts.isEmpty()) {
                EmptyCard("No hay OT pendientes de asignación.")
            } else {
                viewModel.pendingAssignmentOts.forEach { ot ->
                    AssignmentRow(ot = ot, onClick = {
                        viewModel.selectOt(ot.id)
                        onNavigate(Rutas.JEFE_DETALLE)
                    })
                }
            }

            SectionHeader("EN OPERACIÓN", "${operacion.size} activas")
            if (operacion.isEmpty()) {
                EmptyCard("No hay OT en operación.")
            } else {
                operacion.forEach { ot ->
                    OperationRow(ot = ot, tecnico = viewModel.tecnicoName(ot.tecnicoId), onClick = {
                        viewModel.selectOt(ot.id)
                        onNavigate(Rutas.JEFE_DETALLE)
                    })
                }
            }

            SectionHeader("CERRADAS Y CONFORMADAS", "${viewModel.otList.count { it.estado == OtWorkflow.CERRADA || it.estado == OtWorkflow.CONFORMADA }} registradas")
            val cerradas = viewModel.otList
                .filter { it.estado == OtWorkflow.CERRADA || it.estado == OtWorkflow.CONFORMADA }
                .take(4)
            if (cerradas.isEmpty()) {
                EmptyCard("Aún no hay OT cerradas.")
            } else {
                cerradas.forEach { ot ->
                    OperationRow(ot = ot, tecnico = viewModel.tecnicoName(ot.tecnicoId), onClick = {
                        viewModel.selectOt(ot.id)
                        onNavigate(Rutas.JEFE_DETALLE)
                    })
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = detail, color = Color(0xFF64748B), fontSize = 9.sp)
    }
}

@Composable
private fun EmptyCard(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}

@Composable
private fun AssignmentRow(ot: OT, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF78350F)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(ot.cliente, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${ot.id} · ${ot.local}", color = Color(0xFF94A3B8), fontSize = 9.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            PriorityBadge(ot.prioridad)
            Spacer(Modifier.height(4.dp))
            Text("Asignar", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OperationRow(ot: OT, tecnico: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(ot.cliente, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${ot.id} · $tecnico", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text(formatShortDateTime(ot.programadaPara), color = Color(0xFF64748B), fontSize = 9.sp)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StatusBadge(ot.estado)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, tint: Color) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(text = title, color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = tint, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}
