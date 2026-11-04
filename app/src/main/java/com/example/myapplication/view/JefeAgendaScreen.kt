package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.myapplication.data.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.view.components.PhoneScreen
import com.example.myapplication.view.components.PriorityBadge
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun JefeAgendaScreen(viewModel: AppViewModel) {
    var dia by remember { mutableStateOf("lun") }
    var reprogramadas by remember { mutableStateOf(false) }
    val lluvia = viewModel.scenario == "lluvia"
    val agenda = MockRepository.agendaHoy

    PhoneScreen(
        title = "Agenda",
        subtitle = "Semana 27–31 de octubre",
        onBack = { viewModel.navigateTo(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Days selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val dias = listOf(
                    Triple("lun", "Lun", "27"),
                    Triple("mar", "Mar", "28"),
                    Triple("mie", "Mié", "29"),
                    Triple("jue", "Jue", "30"),
                    Triple("vie", "Vie", "31")
                )
                dias.forEach { (d, label, num) ->
                    val sel = dia == d
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) Color(0xFF0EA5E9) else Color(0xFF1E293B))
                            .border(1.dp, if (sel) Color(0xFF38BDF8) else Color(0xFF334155), RoundedCornerShape(10.dp))
                            .clickable { dia = d }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = label, color = if (sel) Color.White else Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = num, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            if (lluvia) {
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
                            Text(text = "2 visitas con equipos exteriores en riesgo", color = Color(0xFF78350F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Lluvia y viento fuerte entre 15:00 y 18:00. Reprograma o adelanta estas visitas.", color = Color(0xFF92400E), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { reprogramadas = !reprogramadas },
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (reprogramadas) Color(0xFF10B981) else Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (reprogramadas) "Reprogramadas" else "Reprogramar visitas", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "VISITAS DEL DÍA", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "${agenda.size} programadas", color = Color(0xFF94A3B8), fontSize = 10.sp)
            }

            agenda.forEach { visita ->
                val enRiesgo = lluvia && visita.exterior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (enRiesgo) Color(0xFF78350F).copy(alpha = 0.3f) else Color(0xFF1E293B))
                        .border(1.dp, if (enRiesgo) Color(0xFFD97706) else Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = visita.hora, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = visita.cliente, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            PriorityBadge(priority = visita.prioridad)
                        }
                        Text(text = "${visita.ot} · ${visita.tecnico}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (visita.exterior) Icons.Filled.WbCloudy else Icons.Filled.WbSunny,
                                contentDescription = null,
                                tint = if (visita.exterior) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (visita.exterior) "Equipo exterior" else "Interior",
                                color = if (visita.exterior) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.navigateTo(Rutas.DASHBOARD) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
            ) {
                Text("Volver al panel", color = Color.White)
            }
        }
    }
}
