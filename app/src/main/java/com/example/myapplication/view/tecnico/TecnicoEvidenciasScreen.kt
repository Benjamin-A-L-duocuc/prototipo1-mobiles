package com.example.myapplication.view.tecnico

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myapplication.model.Evidence
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.OfflineBanner
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.validation.Validators
import com.example.myapplication.viewmodel.AppViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun TecnicoEvidenciasScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val ot = viewModel.selectedOT
    val photos = viewModel.evidenceList
    var comentario by rememberSaveable(ot.id) { mutableStateOf("") }
    var comentarioTocado by rememberSaveable(ot.id) { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var statusIsError by remember { mutableStateOf(false) }
    var pendingPath by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = pendingPath
        pendingPath = null
        if (success && path != null) {
            viewModel.addEvidence(path)
            statusIsError = false
            statusMessage = "Fotografía guardada localmente."
        } else {
            path?.let { File(it).delete() }
            statusIsError = true
            statusMessage = "No se capturó la fotografía."
        }
    }

    val launchCamera = {
        val directory = File(context.filesDir, "evidencias").apply { mkdirs() }
        val file = File(directory, "evidencia_${ot.id}_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingPath = file.absolutePath
        cameraLauncher.launch(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            launchCamera()
        } else {
            statusIsError = true
            statusMessage = "Permiso de cámara denegado. Habilítalo para adjuntar evidencias."
        }
    }

    val takePhoto = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val comentarioLimpio = comentario.trim()
    val comentarioValidationError = Validators.evidenceComment(comentario)
    val comentarioError = if (comentarioTocado) comentarioValidationError else null
    val canContinue = photos.isNotEmpty() && comentarioValidationError == null

    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            delay(3500)
            statusMessage = null
        }
    }

    PhoneScreen(
        title = "Evidencias",
        subtitle = "${ot.id} · Fotos de la intervención",
        onBack = { onNavigate(Rutas.EJECUCION) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OfflineBanner(isOffline = viewModel.connection == "offline")

            AnimatedVisibility(
                visible = statusMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (statusIsError) Color(0xFF7F1D1D) else Color(0xFF065F46))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (statusIsError) Icons.Filled.ErrorOutline else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (statusIsError) Color(0xFFFCA5A5) else Color(0xFF6EE7B7)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusMessage.orEmpty(),
                        color = if (statusIsError) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                        fontSize = 11.sp
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
                    .animateContentSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "FOTOGRAFÍAS (${photos.size})", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Cámara real", color = Color(0xFFD97706), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (photos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Aún no hay fotografías de esta OT.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text(text = "La primera foto es obligatoria para continuar.", color = Color(0xFF64748B), fontSize = 9.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        photos.forEach { evidence ->
                            EvidenceRow(
                                evidence = evidence,
                                onDelete = {
                                    viewModel.deleteEvidence(evidence)
                                    statusIsError = false
                                    statusMessage = "Fotografía eliminada."
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { takePhoto() },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar nueva fotografía", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                    onValueChange = {
                        comentario = it
                        comentarioTocado = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    isError = comentarioError != null,
                    supportingText = {
                        if (comentarioError != null) {
                            Text(text = comentarioError, color = Color(0xFFFCA5A5), fontSize = 10.sp)
                        } else {
                            Text(text = "${comentarioLimpio.length}/300", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color(0xFF334155),
                        errorBorderColor = Color(0xFFEF4444),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                )
            }

            if (photos.isEmpty()) {
                Text(
                    text = "Agrega al menos una fotografía para habilitar el cierre.",
                    color = Color(0xFFFCA5A5),
                    fontSize = 10.sp
                )
            } else if (comentarioValidationError != null) {
                Text(
                    text = "Completa el comentario de evidencia para habilitar el cierre.",
                    color = Color(0xFFFCA5A5),
                    fontSize = 10.sp
                )
            }

            Button(
                onClick = { onNavigate(Rutas.CIERRE) },
                enabled = canContinue,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar al cierre (${photos.size} fotos)", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
private fun EvidenceRow(evidence: Evidence, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EvidenceThumbnail(
            path = evidence.path,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Evidencia fotográfica", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = formatTimestamp(evidence.createdAt),
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar foto", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EvidenceThumbnail(path: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, path) {
        value = withContext(Dispatchers.IO) {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            var sampleSize = 1
            while (bounds.outWidth / sampleSize > 480 || bounds.outHeight / sampleSize > 480) {
                sampleSize *= 2
            }
            val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            BitmapFactory.decodeFile(path, options)?.asImageBitmap()
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap,
                contentDescription = "Evidencia fotográfica",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(22.dp))
        }
    }
}

private fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
