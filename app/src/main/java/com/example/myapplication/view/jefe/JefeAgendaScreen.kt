package com.example.myapplication.view.jefe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.addDays
import com.example.myapplication.util.endOfDay
import com.example.myapplication.util.formatDate
import com.example.myapplication.util.formatTime
import com.example.myapplication.util.startOfDay
import com.example.myapplication.util.startOfWeek
import com.example.myapplication.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun JefeAgendaScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var weekStart by remember { mutableLongStateOf(startOfWeek(System.currentTimeMillis())) }
    var selectedDay by remember { mutableLongStateOf(startOfDay(System.currentTimeMillis())) }
    var tecnicoFilter by remember { mutableStateOf<Long?>(null) }
    val days = (0..6).map { addDays(weekStart, it) }

    val dayOts = viewModel.otList
        .filter { ot ->
            val scheduled = ot.programadaPara
            scheduled != null && scheduled in startOfDay(selectedDay)..endOfDay(selectedDay) &&
                (tecnicoFilter == null || ot.tecnicoId == tecnicoFilter) &&
                ot.estado != OtWorkflow.CANCELADA
        }
        .sortedBy { it.programadaPara }

    val hasExteriorOutdoor = dayOts.any { it.exterior }

    PhoneScreen(
        title = "Agenda semanal",
        subtitle = "Planificación de visitas técnicas",
        onBack = { onNavigate(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    weekStart = addDays(weekStart, -7)
                    selectedDay = startOfDay(weekStart)
                }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Semana anterior", tint = Color.White)
                }
                Text("Semana del ${formatDate(weekStart)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    weekStart = addDays(weekStart, 7)
                    selectedDay = startOfDay(weekStart)
                }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Semana siguiente", tint = Color.White)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                days.forEach { day ->
                    DayCell(
                        timestamp = day,
                        selected = startOfDay(day) == startOfDay(selectedDay),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDay = startOfDay(day) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = tecnicoFilter == null,
                    onClick = { tecnicoFilter = null },
                    label = { Text("Todos", fontSize = 9.sp) }
                )
                viewModel.technicians.filter { it.activo }.forEach { tecnico ->
                    FilterChip(
                        selected = tecnicoFilter == tecnico.id,
                        onClick = { tecnicoFilter = tecnico.id },
                        label = { Text(tecnico.nombre.substringBefore(" "), fontSize = 9.sp) }
                    )
                }
            }

            if (viewModel.scenario == "lluvia" && hasExteriorOutdoor) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF78350F))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Hay visitas con equipos exteriores. Revisa si conviene reprogramar.",
                        color = Color(0xFFFEF3C7),
                        fontSize = 9.sp
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("VISITAS DEL DÍA", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("${dayOts.size} programadas", color = Color(0xFF64748B), fontSize = 9.sp)
            }

            if (dayOts.isEmpty()) {
                EmptyAgenda("No hay visitas programadas para este día.")
            } else {
                dayOts.forEach { ot ->
                    AgendaRow(
                        time = formatTime(ot.programadaPara),
                        title = ot.cliente,
                        local = "${ot.local} · ${ot.direccion}",
                        tecnico = viewModel.tecnicoName(ot.tecnicoId),
                        estado = ot.estado,
                        priority = ot.prioridad,
                        exterior = ot.exterior,
                        onClick = {
                            viewModel.selectOt(ot.id)
                            onNavigate(Rutas.JEFE_DETALLE)
                        }
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SIN PROGRAMAR", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("${viewModel.pendingAssignmentOts.size}", color = Color(0xFF64748B), fontSize = 9.sp)
            }
            if (viewModel.pendingAssignmentOts.isEmpty()) {
                EmptyAgenda("No hay OT pendientes de programación.")
            } else {
                viewModel.pendingAssignmentOts.forEach { ot ->
                    AgendaRow(
                        time = "Pendiente",
                        title = ot.cliente,
                        local = "${ot.local} · ${ot.equipo}",
                        tecnico = "Sin asignar",
                        estado = ot.estado,
                        priority = ot.prioridad,
                        exterior = ot.exterior,
                        onClick = {
                            viewModel.selectOt(ot.id)
                            onNavigate(Rutas.JEFE_DETALLE)
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DayCell(timestamp: Long, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(timestamp).replace(".", "")
    val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
    Column(
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color(0xFF0EA5E9) else Color(0xFF1E293B))
            .border(1.dp, if (selected) Color(0xFF38BDF8) else Color(0xFF334155), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(dayName, color = if (selected) Color.White else Color(0xFF94A3B8), fontSize = 8.sp)
        Text("$dayNumber", color = if (selected) Color.White else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AgendaRow(
    time: String,
    title: String,
    local: String,
    tecnico: String,
    estado: String,
    priority: String,
    exterior: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.width(42.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Text(time, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(local, color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text(tecnico, color = Color(0xFF64748B), fontSize = 9.sp)
            if (exterior) {
                Text("Equipo exterior", color = Color(0xFFFCD34D), fontSize = 8.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StatusBadge(estado)
            PriorityBadge(priority)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun EmptyAgenda(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CloudOff, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
        Text(text, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}
