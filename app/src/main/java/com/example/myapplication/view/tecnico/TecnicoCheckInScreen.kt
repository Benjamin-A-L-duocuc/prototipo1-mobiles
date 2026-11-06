package com.example.myapplication.view.tecnico

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.OfflineBanner
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.util.LocationProvider
import com.example.myapplication.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun TecnicoCheckInScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val ot = viewModel.selectedOT
    val checkedIn = ot.estado in setOf("En ejecución", "Cerrada", "Conformada")
    val storedCheckIn = viewModel.selectedCheckIn
    val locationProvider = remember { LocationProvider(context) }
    var currentLocation by remember(ot.id) { mutableStateOf<Location?>(null) }
    var isLocating by remember(ot.id) { mutableStateOf(false) }
    var locationError by remember(ot.id) { mutableStateOf<String?>(null) }
    var requestKey by remember(ot.id) { mutableStateOf(0) }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    LaunchedEffect(requestKey) {
        if (requestKey == 0) return@LaunchedEffect
        isLocating = true
        locationError = null
        try {
            val location = withTimeoutOrNull(15000) { locationProvider.getCurrentLocation() }
            if (location == null) {
                locationError = "No se obtuvo una ubicación en 15 segundos. Verifica el GPS e intenta nuevamente."
            } else {
                currentLocation = location
            }
        } catch (error: Exception) {
            locationError = error.message ?: "No se pudo obtener la ubicación."
        } finally {
            isLocating = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            requestKey += 1
        } else {
            locationError = "Permiso de ubicación denegado. Habilítalo para registrar el check-in."
        }
    }

    val requestLocation = {
        locationError = null
        if (hasLocationPermission()) {
            requestKey += 1
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val displayLatitude = storedCheckIn?.latitude ?: currentLocation?.latitude
    val displayLongitude = storedCheckIn?.longitude ?: currentLocation?.longitude
    val displayAccuracy = storedCheckIn?.accuracy ?: currentLocation?.accuracy
    val displayTimestamp = storedCheckIn?.createdAt ?: currentLocation?.time
    val hasLocation = displayLatitude != null && displayLongitude != null

    PhoneScreen(
        title = "Check-in",
        subtitle = "${ot.id} · ${ot.cliente}",
        onBack = { onNavigate(Rutas.DETALLE) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (hasLocation) Icons.Filled.MyLocation else Icons.Filled.LocationSearching,
                        contentDescription = null,
                        tint = if (hasLocation) Color(0xFF10B981) else Color(0xFF0EA5E9),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (hasLocation) "Ubicación GPS capturada" else "Ubicación GPS pendiente",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hasLocation) {
                            "${formatCoordinate(displayLatitude)}, ${formatCoordinate(displayLongitude)}"
                        } else {
                            ot.direccion
                        },
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (hasLocation) Color(0xFF065F46) else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = null,
                            tint = if (hasLocation) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasLocation) "Ubicación capturada" else "Ubicación pendiente",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                storedCheckIn != null -> "Registro guardado localmente"
                                isLocating -> "Obteniendo GPS del dispositivo..."
                                locationError != null -> locationError.orEmpty()
                                else -> "Precisión ${formatAccuracy(displayAccuracy)}"
                            },
                            color = if (locationError != null && !hasLocation) Color(0xFFFCA5A5) else Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (hasLocation) Color(0xFF065F46).copy(alpha = 0.5f) else Color(0xFF334155))
                            .border(
                                1.dp,
                                if (hasLocation) Color(0xFF059669) else Color(0xFF475569),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (hasLocation) "OK" else "PENDIENTE",
                            color = if (hasLocation) Color(0xFF6EE7B7) else Color(0xFFCBD5E1),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "LATITUD", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = displayLatitude?.let { formatCoordinate(it) } ?: "--",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(text = "LONGITUD", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = displayLongitude?.let { formatCoordinate(it) } ?: "--",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(text = "PRECISIÓN", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = formatAccuracy(displayAccuracy),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(text = "HORA", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = displayTimestamp?.let { formatTime(it) } ?: "--:--",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = locationError != null && !checkedIn,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF7F1D1D))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = locationError.orEmpty(), color = Color(0xFFFEE2E2), fontSize = 11.sp)
                }
            }

            OfflineBanner(isOffline = viewModel.connection == "offline")

            if (checkedIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF059669), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (storedCheckIn != null) {
                            "Check-in registrado a las ${formatTime(storedCheckIn.createdAt)}. Ya puedes comenzar la ejecución."
                        } else {
                            "Esta OT ya se encuentra en ejecución."
                        },
                        color = Color(0xFF6EE7B7),
                        fontSize = 11.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                if (!checkedIn) {
                    if (currentLocation == null) {
                        Button(
                            onClick = { requestLocation() },
                            enabled = !isLocating,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0EA5E9),
                                disabledContainerColor = Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLocating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Obteniendo ubicación...", color = Color.White, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Filled.MyLocation, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Capturar ubicación GPS", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                currentLocation?.let {
                                    viewModel.doCheckIn(it.latitude, it.longitude, it.accuracy, it.time)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrar check-in", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { requestLocation() },
                            enabled = !isLocating,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar ubicación")
                        }
                    }
                } else {
                    Button(
                        onClick = { onNavigate(Rutas.EJECUCION) },
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
                    onClick = { onNavigate(Rutas.DETALLE) },
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

private fun formatCoordinate(value: Double): String =
    String.format(Locale.getDefault(), "%.6f", value)

private fun formatAccuracy(accuracy: Float?): String =
    if (accuracy == null || accuracy <= 0f) "No informada" else "±${accuracy.toInt()} m"

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
