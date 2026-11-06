package com.example.myapplication.view.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun TecnicoJornadaScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var viewMode by remember { mutableStateOf("lista") }
    val tecnico = MockRepository.tecnico
    val ots = viewModel.otList
    val exteriorCount = ots.count { it.exterior }

    PhoneScreen(
        title = "Mi jornada",
        subtitle = "Jueves 27 de octubre",
        actions = {
            IconButton(onClick = {
                viewModel.logout()
                onNavigate(Rutas.LOGIN)
            }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            }
            IconButton(onClick = { onNavigate(Rutas.SYNC) }) {
                Box {
                    Icon(Icons.Filled.Refresh, contentDescription = "Sync", tint = Color.White)
                    if (viewModel.pending > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B))
                                .align(Alignment.TopEnd)
                        )
                    }
                }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF06B6D4))))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tecnico.iniciales, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Hola, ${tecnico.nombre}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${ots.size} visitas hoy · $exteriorCount con equipo exterior", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
                }
                Icon(Icons.Filled.Thermostat, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
            }

            OfflineBanner(isOffline = viewModel.connection == "offline")

            if (viewModel.scenario == "lluvia") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WbCloudy, contentDescription = null, tint = Color(0xFFB45309))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lluvia prevista a las 15:00. Tienes $exteriorCount visitas con equipos en azotea o patio.",
                        color = Color(0xFF92400E),
                        fontSize = 11.sp
                    )
                }
            }

            // Tabs Lista / Mapa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    listOf("lista" to "Lista", "mapa" to "Mapa").forEach { (v, label) ->
                        val selected = viewMode == v
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) Color(0xFF0EA5E9) else Color.Transparent)
                                .clickable { viewMode = v }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(text = "${ots.size} OT", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }

            if (viewMode == "mapa") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .clickable {
                            ots.firstOrNull()?.let { recentOt ->
                                viewModel.selectOt(recentOt.id)
                                onNavigate(Rutas.DETALLE)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Mapa interactivo de ruta", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Toca para abrir OT más reciente (${ots.firstOrNull()?.id ?: "OT"})", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            } else {
                ots.forEach { ot ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.selectOt(ot.id)
                                onNavigate(Rutas.DETALLE)
                            }
                            .padding(14.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                PriorityBadge(priority = ot.prioridad)
                                StatusBadge(status = ot.estado)
                            }
                            Text(text = ot.id, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = ot.cliente, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = ot.equipo, color = Color(0xFF94A3B8), fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                                Text(text = "SLA ${ot.sla}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Place, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
                                Text(text = ot.distancia, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            if (ot.exterior) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                                    Text(text = "Exterior", color = Color(0xFFF59E0B), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Sync card button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Rutas.SYNC) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewModel.pending > 0) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = if (viewModel.pending > 0) Color(0xFFD97706) else Color(0xFF059669),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Centro de sincronización", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (viewModel.pending > 0) "${viewModel.pending} registros pendientes" else "Todo sincronizado",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF94A3B8))
            }
        }
    }
}
