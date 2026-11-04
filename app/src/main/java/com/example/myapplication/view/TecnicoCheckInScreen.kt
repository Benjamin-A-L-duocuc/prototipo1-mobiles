package com.example.myapplication.view

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.view.components.OfflineBanner
import com.example.myapplication.view.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoCheckInScreen(viewModel: AppViewModel) {
    val ot = MockRepository.defaultOT

    PhoneScreen(
        title = "Check-in",
        subtitle = "${ot.id} · ${ot.cliente}",
        onBack = { viewModel.navigateTo(Rutas.DETALLE) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Map box simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Map, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Ubicación del cliente en mapa", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = ot.direccion, color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }

            // Location card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF065F46)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ubicación capturada", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Precisión ±8 m · GPS simulado", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF065F46).copy(alpha = 0.5f))
                            .border(1.dp, Color(0xFF059669), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "OK", color = Color(0xFF6EE7B7), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "LATITUD", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "-33.4569", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "LONGITUD", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "-70.6483", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "HORA", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "09:41", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OfflineBanner(isOffline = viewModel.connection == "offline")

            if (viewModel.checkinDone) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "Check-in registrado a las 09:41. Ya puedes comenzar la ejecución.", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                if (!viewModel.checkinDone) {
                    Button(
                        onClick = { viewModel.doCheckIn() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrar check-in", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.navigateTo(Rutas.EJECUCION) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar a ejecución", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(Rutas.DETALLE) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver al detalle")
                }
            }
        }
    }
}
