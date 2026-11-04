package com.example.myapplication.view.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.OfflineBanner
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoCierreScreen(viewModel: AppViewModel) {
    var signed by remember { mutableStateOf(false) }
    var conforme by remember { mutableStateOf(true) }
    val cliente = MockRepository.cliente
    val ot = MockRepository.defaultOT

    PhoneScreen(
        title = "Cierre de OT",
        subtitle = "${ot.id} · ${ot.cliente}",
        onBack = { viewModel.navigateTo(Rutas.EVIDENCIAS) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OfflineBanner(isOffline = viewModel.connection == "offline")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(text = "RESUMEN DE LA INTERVENCIÓN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(text = "CHECK-IN", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "09:41", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(text = "CHECK-OUT", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "10:35", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(text = "ACTIVIDADES", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "4 registradas", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp)
                    ) {
                        Text(text = "EVIDENCIAS", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "2 fotografías", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "CONFORMIDAD DEL CLIENTE", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = cliente.nombre, color = Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (signed) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF065F46).copy(alpha = 0.3f))
                            .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "OT cerrada y firmada por ${cliente.nombre} a las 10:36.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "María Soto", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Firma digital registrada · ${cliente.cargo}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Rutas.SYNC) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ir a sincronización (${viewModel.pending})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "[ Área de Firma Digital ]", color = Color(0xFF64748B), fontSize = 12.sp)
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
                        onClick = { signed = true },
                        enabled = conforme,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar OT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
