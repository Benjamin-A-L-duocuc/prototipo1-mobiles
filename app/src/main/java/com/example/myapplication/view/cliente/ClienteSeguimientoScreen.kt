package com.example.myapplication.view.cliente

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
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
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.formatShortDateTime
import com.example.myapplication.util.initials
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun ClienteSeguimientoScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val ots = viewModel.visibleOts.sortedByDescending { it.programadaPara ?: it.creadaEn }
    val pendientesConformidad = ots.count { it.estado == OtWorkflow.CERRADA }
    val conformadas = ots.count { it.estado == OtWorkflow.CONFORMADA }

    PhoneScreen(
        title = "Mis servicios",
        subtitle = "Seguimiento de tus órdenes de trabajo",
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
                    Text("${ots.size} órdenes asociadas", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$pendientesConformidad", color = Color(0xFFFCD34D), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("por firmar", color = Color(0xFF94A3B8), fontSize = 8.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClientStat(Modifier.weight(1f), "ACTIVAS", "${ots.count { !OtWorkflow.estaCerrada(it.estado) && it.estado != OtWorkflow.CANCELADA }}", Color(0xFF38BDF8))
                ClientStat(Modifier.weight(1f), "POR CONFORMAR", "$pendientesConformidad", Color(0xFFFCD34D))
                ClientStat(Modifier.weight(1f), "CONFORMADAS", "$conformadas", Color(0xFF6EE7B7))
            }

            if (pendientesConformidad > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF78350F))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tienes $pendientesConformidad trabajo(s) listo(s) para revisar y firmar.", color = Color(0xFFFEF3C7), fontSize = 9.sp)
                }
            }

            Text("HISTORIAL DE SERVICIOS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

            if (ots.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Assignment, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(24.dp))
                    Text("No tienes órdenes registradas.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Cuando se cree una OT para tu empresa aparecerá aquí.", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            } else {
                ots.forEach { ot ->
                    ClientOtCard(ot = ot, onClick = {
                        viewModel.selectOt(ot.id)
                        onNavigate(Rutas.CLIENTE_DETALLE)
                    })
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ClientOtCard(ot: OT, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(
                1.dp,
                if (ot.estado == OtWorkflow.CERRADA) Color(0xFFF59E0B) else Color(0xFF334155),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ot.id, color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(ot.equipo, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${ot.local} · ${ot.tipo}", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
            StatusBadge(ot.estado)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text("Programada: ${formatShortDateTime(ot.programadaPara)}", color = Color(0xFFCBD5E1), fontSize = 9.sp, modifier = Modifier.weight(1f))
            Text(
                if (ot.estado == OtWorkflow.CERRADA) "Firmar" else OtWorkflow.siguientePaso(ot),
                color = if (ot.estado == OtWorkflow.CERRADA) Color(0xFFFCD34D) else Color(0xFF38BDF8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
        }
        Text("Técnico: ${ot.tecnicoNombre ?: "Por asignar"}", color = Color(0xFF64748B), fontSize = 8.sp)
    }
}

@Composable
private fun ClientStat(modifier: Modifier = Modifier, title: String, value: String, tint: Color) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(title, color = Color(0xFF94A3B8), fontSize = 7.sp, fontWeight = FontWeight.Bold)
        Text(value, color = tint, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}
