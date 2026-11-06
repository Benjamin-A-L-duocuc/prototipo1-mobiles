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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
fun TecnicoEjecucionScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var step by remember { mutableStateOf("diagnostico") }
    var diagnosticoText by remember {
        mutableStateOf("Se detecta baja presión en el circuito de refrigerante y acumulación de polvo en el condensador. Se recomienda recarga y limpieza.")
    }
    val actividades = MockRepository.actividades
    val actividadEstados = remember { mutableStateMapOf(0 to true, 1 to true, 2 to false, 3 to false) }
    val materiales = remember { mutableStateMapOf(0 to 2, 1 to 1, 2 to 2) }

    val doneCount = when (step) {
        "diagnostico" -> 1
        "actividades" -> 2
        "materiales" -> 3
        else -> 4
    }

    val ot = viewModel.selectedOT
    PhoneScreen(
        title = "Ejecución",
        subtitle = "${ot.id} · En ejecución",
        onBack = { onNavigate(Rutas.DETALLE) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OfflineBanner(isOffline = viewModel.connection == "offline")

            // Progress bar & steps
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Paso $doneCount de 4", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Text(text = "${(doneCount * 25)}%", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { doneCount / 4f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = Color(0xFF0EA5E9),
                    trackColor = Color(0xFF334155),
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val stepList = listOf("diagnostico" to "Diagnóstico", "actividades" to "Actividades", "materiales" to "Materiales", "evidencias" to "Evidencias")
                    stepList.forEachIndexed { index, (sVal, sLabel) ->
                        val active = sVal == step
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                if (sVal == "evidencias") {
                                    onNavigate(Rutas.EVIDENCIAS)
                                } else {
                                    step = sVal
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (active) Color(0xFF0EA5E9) else Color(0xFF1E293B))
                                    .border(1.dp, if (active) Color(0xFF38BDF8) else Color(0xFF334155), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${index + 1}", color = if (active) Color.White else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = sLabel, color = if (active) Color(0xFF38BDF8) else Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                    }
                }
            }

            // Step Content
            when (step) {
                "diagnostico" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Text(text = "DIAGNÓSTICO TÉCNICO", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = diagnosticoText,
                            onValueChange = { diagnosticoText = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0EA5E9),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Guardado local · pendiente de sincronizar", color = Color(0xFF10B981), fontSize = 10.sp)
                    }
                }
                "actividades" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "ACTIVIDADES REALIZADAS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        actividades.forEachIndexed { index, act ->
                            val done = actividadEstados[index] == true
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A))
                                    .clickable { actividadEstados[index] = !done }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (done) Color(0xFF10B981) else Color.Transparent)
                                        .border(1.dp, if (done) Color(0xFF10B981) else Color(0xFF64748B), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (done) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                                Text(text = act, color = if (done) Color.White else Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }
                    }
                }
                "materiales" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "MATERIALES UTILIZADOS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        MockRepository.materiales.forEachIndexed { index, mat ->
                            val cant = materiales[index] ?: mat.cantidad
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = mat.nombre, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "$cant ${mat.unidad}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { materiales[index] = maxOf(0, cant - 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    Text(text = "$cant", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { materiales[index] = cant + 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (step == "diagnostico") step = "actividades"
                    else if (step == "actividades") step = "materiales"
                    else onNavigate(Rutas.EVIDENCIAS)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (step == "materiales") "Continuar a evidencias" else "Siguiente paso", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}
