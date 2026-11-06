package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PhoneScreen(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // dark slate background matching prototype
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Column {
                    Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
            if (actions != null) {
                actions()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            content = content
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (bg, fg) = when (priority.lowercase()) {
        "alta" -> Pair(Color(0xFF7F1D1D), Color(0xFFFCA5A5))
        "media" -> Pair(Color(0xFF78350F), Color(0xFFFCD34D))
        else -> Pair(Color(0xFF065F46), Color(0xFF6EE7B7))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = priority, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "en ejecución" -> Pair(Color(0xFF0C4A6E), Color(0xFF7DD3FC))
        "aceptada" -> Pair(Color(0xFF1E3A8A), Color(0xFF93C5FD))
        "asignada" -> Pair(Color(0xFF1E40AF), Color(0xFFBFDBFE))
        "cerrada" -> Pair(Color(0xFF065F46), Color(0xFF6EE7B7))
        "conformada" -> Pair(Color(0xFF064E3B), Color(0xFFA7F3D0))
        "pendiente" -> Pair(Color(0xFF78350F), Color(0xFFFCD34D))
        "rechazada por cliente" -> Pair(Color(0xFF7F1D1D), Color(0xFFFCA5A5))
        "cancelada" -> Pair(Color(0xFF334155), Color(0xFF94A3B8))
        else -> Pair(Color(0xFF334155), Color(0xFFCBD5E1))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OfflineBanner(isOffline: Boolean) {
    if (isOffline) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF78350F))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CloudOff, contentDescription = "Offline", tint = Color(0xFFFCD34D))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Modo sin conexión. Los cambios se sincronizarán al recuperar red.",
                color = Color(0xFFFEF3C7),
                fontSize = 11.sp
            )
        }
    }
}
