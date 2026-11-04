package com.example.myapplication.view

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
import com.example.myapplication.data.MockRepository
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.view.components.OfflineBanner
import com.example.myapplication.view.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun TecnicoEvidenciasScreen(viewModel: AppViewModel) {
    var photoCount by remember { mutableStateOf(2) }
    var comentario by remember { mutableStateOf("Se adjuntan fotos del condensador antes y después de la limpieza.") }

    PhoneScreen(
        title = "Evidencias",
        subtitle = "${MockRepository.defaultOT.id} · Fotos de la intervención",
        onBack = { viewModel.navigateTo(Rutas.EJECUCION) }
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "FOTOGRAFÍAS ($photoCount)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Local", color = Color(0xFFD97706), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..photoCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(24.dp))
                                Text(text = "Foto #$i", color = Color(0xFF94A3B8), fontSize = 9.sp)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { photoCount++ }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.AddAPhoto, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(20.dp))
                                Text(text = "Agregar", color = Color(0xFF38BDF8), fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { photoCount++ },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tomar foto", color = Color.White, fontSize = 11.sp)
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
                Text(text = "COMENTARIO DE EVIDENCIA", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                )
            }

            Button(
                onClick = { viewModel.navigateTo(Rutas.CIERRE) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar al cierre", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}
