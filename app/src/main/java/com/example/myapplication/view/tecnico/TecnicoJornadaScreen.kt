package com.example.myapplication.view.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.myapplication.ui.components.OfflineBanner
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.PriorityBadge
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.util.formatTime
import com.example.myapplication.util.initials
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoJornadaScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ots = viewModel.visibleOts.sortedBy { it.programadaPara ?: Long.MAX_VALUE }
    val activas = ots.count { !OtWorkflow.estaCerrada(it.estado) && it.estado != OtWorkflow.CANCELADA }

    PhoneScreen(
        title = "Mi jornada",
        subtitle = "Órdenes asignadas",
        actions = {
            Row {
                IconButton(onClick = { onNavigate(Rutas.SYNC) }) {
                    Icon(Icons.Filled.CloudSync, contentDescription = "Sincronización", tint = Color.White)
                }
                IconButton(onClick = {
                    viewModel.logout()
                    onNavigate(Rutas.LOGIN)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
                }
            }
        }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF0EA5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials(viewModel.currentUserName), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(viewModel.currentUserName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Técnico de terreno · ${ots.size} OT asignadas", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$activas", color = Color(0xFF38BDF8), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("activas", color = Color(0xFF94A3B8), fontSize = 8.sp)
                }
            }

            OfflineBanner(viewModel.connection == "offline")

            if (viewModel.scenario == "lluvia") {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF78350F))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Alerta climática: revisa las visitas con equipos exteriores.", color = Color(0xFFFEF3C7), fontSize = 9.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("PRÓXIMAS VISITAS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Ordenadas por horario", color = Color(0xFF64748B), fontSize = 9.sp)
            }

            if (ots.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(24.dp))
                    Text("No tienes OT asignadas.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Cuando el supervisor asigne trabajo aparecerá aquí.", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            } else {
                ots.forEach { ot ->
                    JornadaCard(ot = ot, onClick = {
                        viewModel.selectOt(ot.id)
                        onNavigate(Rutas.DETALLE)
                    })
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .clickable { onNavigate(Rutas.SYNC) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CloudSync, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sincronización", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (viewModel.pending == 0) "Todo sincronizado" else "${viewModel.pending} cambios pendientes",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun JornadaCard(ot: OT, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ot.id, color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(ot.cliente, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${ot.local} · ${ot.tipo}", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusBadge(ot.estado)
                PriorityBadge(ot.prioridad)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(ot.direccion, color = Color(0xFFCBD5E1), fontSize = 9.sp, modifier = Modifier.weight(1f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = if (ot.programadaPara == null) "Sin programar" else "${formatTime(ot.programadaPara)} · ${formatShortDateTime(ot.programadaPara)}",
                color = Color(0xFFCBD5E1),
                fontSize = 9.sp,
                modifier = Modifier.weight(1f)
            )
            Text(OtWorkflow.siguientePaso(ot), color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
        }
    }
}
