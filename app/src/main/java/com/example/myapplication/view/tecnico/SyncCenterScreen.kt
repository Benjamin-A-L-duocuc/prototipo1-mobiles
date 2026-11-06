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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SyncCenterScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val online = viewModel.connection == "online"
    var syncing by remember { mutableStateOf(false) }
    var synced by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val syncList = viewModel.syncItems

    PhoneScreen(
        title = "Sincronización",
        subtitle = "Cola de registros de la OT",
        onBack = { onNavigate(Rutas.JORNADA) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, if (online) Color(0xFF059669) else Color(0xFFD97706), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (online) Color(0xFF065F46) else Color(0xFF78350F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (online) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                        contentDescription = null,
                        tint = if (online) Color(0xFF6EE7B7) else Color(0xFFFCD34D),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (online) "Conectado" else "Sin conexión", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (online) "Puedes enviar los registros pendientes." else "Puedes seguir trabajando; los datos se guardan localmente.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (online) Color(0xFF10B981) else Color(0xFFF59E0B))
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "REGISTROS DE LA OT", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewModel.pending > 0) Color(0xFFFEF3C7) else Color(0xFFD1FAE5))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (viewModel.pending > 0) "${viewModel.pending} pendientes" else "Sincronizado",
                        color = if (viewModel.pending > 0) Color(0xFFD97706) else Color(0xFF059669),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sync items list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            ) {
                if (syncList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No hay registros pendientes de sincronización.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                } else {
                    syncList.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (viewModel.pending > 0) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (viewModel.pending > 0) Icons.Filled.Schedule else Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = if (viewModel.pending > 0) Color(0xFFD97706) else Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.tipo, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = item.detalle, color = Color(0xFF94A3B8), fontSize = 9.sp)
                            }
                            Text(
                                text = if (viewModel.pending > 0) "Pendiente" else "Enviado",
                                color = if (viewModel.pending > 0) Color(0xFFF59E0B) else Color(0xFF10B981),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (index < syncList.size - 1) {
                            HorizontalDivider(color = Color(0xFF334155))
                        }
                    }
                }
            }

            if (synced || viewModel.pending == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "No hay registros pendientes en este dispositivo.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                }
            }

            Button(
                onClick = {
                    if (!syncing) {
                        syncing = true
                        scope.launch {
                            delay(900)
                            viewModel.syncAll()
                            syncing = false
                            synced = true
                        }
                    }
                },
                enabled = !syncing && viewModel.pending > 0 && online,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        syncing -> "Sincronizando..."
                        viewModel.pending == 0 -> "Todo sincronizado"
                        !online -> "Sin conexión para sincronizar"
                        else -> "Sincronizar ahora"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { onNavigate(Rutas.JORNADA) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Volver a mi jornada")
            }
        }
    }
}
