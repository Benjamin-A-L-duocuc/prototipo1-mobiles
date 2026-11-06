package com.example.myapplication.view.tecnico

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.OtWorkflow
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.ui.components.StatusBadge
import com.example.myapplication.util.LocationProvider
import com.example.myapplication.util.LocationResult
import com.example.myapplication.util.formatAccuracy
import com.example.myapplication.util.formatCoordinate
import com.example.myapplication.util.formatDateTime
import com.example.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun TecnicoCheckInScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val locationProvider = remember { LocationProvider(context) }
    val scope = rememberCoroutineScope()
    val ot = viewModel.selectedOT
    val existing = viewModel.selectedCheckIn

    var result by remember { mutableStateOf<LocationResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember { mutableStateOf(locationProvider.hasPermission()) }
    var manualWithoutGps by remember { mutableStateOf(false) }

    fun locate() {
        if (!locationProvider.hasPermission()) {
            error = "Sin permiso de ubicación. Puedes registrar el check-in sin GPS."
            return
        }
        if (!locationProvider.isLocationEnabled()) {
            val cached = locationProvider.getLastKnownLocation(24 * 60 * 60 * 1000L)
            if (cached != null) {
                result = cached.copy(source = "last_known")
                error = null
            } else {
                error = "La ubicación está desactivada. Actívala o continúa sin GPS."
            }
            return
        }
        loading = true
        error = null
        scope.launch {
            val found = withTimeoutOrNull(15_000L) {
                runCatching { locationProvider.getCurrentLocation() }.getOrElse { failure ->
                    val cached = locationProvider.getLastKnownLocation(24 * 60 * 60 * 1000L)
                    if (cached != null) {
                        cached.copy(source = "last_known")
                    } else {
                        error = failure.message ?: "No se pudo obtener la ubicación."
                        null
                    }
                }
            }
            if (found != null) {
                result = if (found.fromCache) found.copy(source = "last_known") else found
                error = null
            } else if (error == null) {
                val cached = locationProvider.getLastKnownLocation(24 * 60 * 60 * 1000L)
                if (cached != null) {
                    result = cached.copy(source = "last_known")
                } else {
                    error = "No se obtuvo una ubicación a tiempo. Puedes continuar sin GPS."
                }
            }
            loading = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionGranted = locationProvider.hasPermission()
        if (permissionGranted) locate() else error = "Permiso denegado. Puedes continuar sin GPS."
    }

    LaunchedEffect(ot.id) {
        if (existing == null) locate()
    }

    fun confirm(sourceOverride: String? = null) {
        val source = sourceOverride ?: when {
            manualWithoutGps -> "none"
            result == null -> "none"
            result?.source == "last_known" -> "last_known"
            result?.location?.provider == "network" -> "network"
            else -> "gps"
        }
        val latitude = if (manualWithoutGps) null else result?.location?.latitude
        val longitude = if (manualWithoutGps) null else result?.location?.longitude
        val accuracy = if (manualWithoutGps) null else result?.location?.accuracy
        val failure = viewModel.doCheckIn(
            id = ot.id,
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy,
            timestamp = System.currentTimeMillis(),
            source = source
        )
        if (failure == null) {
            onNavigate(Rutas.EJECUCION)
        } else {
            message = failure
        }
    }

    PhoneScreen(
        title = "Check-in",
        subtitle = ot.id,
        onBack = { onNavigate(Rutas.DETALLE) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(ot.cliente, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${ot.local} · ${ot.direccion}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                StatusBadge(ot.estado)
            }

            if (existing != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Check-in ya registrado", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "${formatCoordinate(existing.latitude)}, ${formatCoordinate(existing.longitude)}",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp
                    )
                    Text(
                        "${formatAccuracy(existing.accuracy)} · ${sourceLabel(existing.source)} · ${formatDateTime(existing.createdAt)}",
                        color = Color(0xFF6EE7B7),
                        fontSize = 10.sp
                    )
                    Button(
                        onClick = { onNavigate(Rutas.EJECUCION) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Continuar a ejecución", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ubicación del check-in", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Se guarda para respaldar la visita en terreno.", color = Color(0xFF94A3B8), fontSize = 9.sp)
                        }
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF38BDF8), strokeWidth = 2.dp)
                        }
                    }

                    when {
                        result != null -> {
                            Text(
                                "${formatCoordinate(result?.location?.latitude)}, ${formatCoordinate(result?.location?.longitude)}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                "${formatAccuracy(result?.location?.accuracy)} · ${sourceLabel(result?.source ?: "gps")}",
                                color = Color(0xFF7DD3FC),
                                fontSize = 10.sp
                            )
                        }
                        manualWithoutGps -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOff, contentDescription = null, tint = Color(0xFFFCD34D), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Check-in sin ubicación GPS.", color = Color(0xFFFCD34D), fontSize = 10.sp)
                            }
                        }
                        error != null -> {
                            Text(error.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
                        }
                        else -> {
                            Text("Esperando ubicación...", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }

                    if (error != null && result == null && !manualWithoutGps) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { locate() }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Reintentar", fontSize = 10.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Activar GPS", fontSize = 10.sp)
                            }
                        }
                    }

                    if (!permissionGranted) {
                        OutlinedButton(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.GpsFixed, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Permitir ubicación", fontSize = 10.sp)
                        }
                    }

                    if (result == null && !manualWithoutGps) {
                        OutlinedButton(
                            onClick = { manualWithoutGps = true; error = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Continuar sin GPS", fontSize = 10.sp)
                        }
                    }
                }

                if (message != null) {
                    Text(message.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
                }

                Button(
                    onClick = { confirm() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !loading && (result != null || manualWithoutGps),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Registrar check-in", fontWeight = FontWeight.Bold)
                }

                Text(
                    "El GPS no es obligatorio. Si el dispositivo no entrega ubicación, puedes continuar y quedará registrado sin coordenadas.",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp
                )
            }
        }
    }
}

private fun sourceLabel(source: String): String = when (source.lowercase()) {
    "gps" -> "GPS"
    "network" -> "Red"
    "last_known" -> "Última ubicación conocida"
    "none" -> "Sin GPS"
    else -> source
}
