package com.example.myapplication.view.jefe

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Cliente
import com.example.myapplication.model.Equipo
import com.example.myapplication.model.LocalComercial
import com.example.myapplication.model.Tecnico
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.ui.components.PhoneScreen
import com.example.myapplication.viewmodel.AppViewModel

private enum class CatalogTab { TECNICOS, CLIENTES, LOCALES, EQUIPOS }

@Composable
fun JefeCatalogoScreen(viewModel: AppViewModel, onNavigate: (String) -> Unit) {
    var tab by remember { mutableStateOf(CatalogTab.TECNICOS) }
    var search by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    var showTecnicoDialog by remember { mutableStateOf(false) }
    var editingTecnico by remember { mutableStateOf<Tecnico?>(null) }
    var showClienteDialog by remember { mutableStateOf(false) }
    var editingCliente by remember { mutableStateOf<Cliente?>(null) }
    var showLocalDialog by remember { mutableStateOf(false) }
    var editingLocal by remember { mutableStateOf<LocalComercial?>(null) }
    var showEquipoDialog by remember { mutableStateOf(false) }
    var editingEquipo by remember { mutableStateOf<Equipo?>(null) }

    val query = search.trim().lowercase()

    PhoneScreen(
        title = "Catálogos",
        subtitle = "Técnicos, clientes, locales y equipos",
        onBack = { onNavigate(Rutas.DASHBOARD) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TabChip("Técnicos", tab == CatalogTab.TECNICOS, Modifier.weight(1f)) { tab = CatalogTab.TECNICOS }
                TabChip("Clientes", tab == CatalogTab.CLIENTES, Modifier.weight(1f)) { tab = CatalogTab.CLIENTES }
                TabChip("Locales", tab == CatalogTab.LOCALES, Modifier.weight(1f)) { tab = CatalogTab.LOCALES }
                TabChip("Equipos", tab == CatalogTab.EQUIPOS, Modifier.weight(1f)) { tab = CatalogTab.EQUIPOS }
            }

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Buscar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    when (tab) {
                        CatalogTab.TECNICOS -> { editingTecnico = null; showTecnicoDialog = true }
                        CatalogTab.CLIENTES -> { editingCliente = null; showClienteDialog = true }
                        CatalogTab.LOCALES -> { editingLocal = null; showLocalDialog = true }
                        CatalogTab.EQUIPOS -> { editingEquipo = null; showEquipoDialog = true }
                    }
                    message = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    when (tab) {
                        CatalogTab.TECNICOS -> "Nuevo técnico"
                        CatalogTab.CLIENTES -> "Nuevo cliente"
                        CatalogTab.LOCALES -> "Nuevo local"
                        CatalogTab.EQUIPOS -> "Nuevo equipo"
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            if (message != null) {
                Text(message.orEmpty(), color = Color(0xFF7DD3FC), fontSize = 10.sp)
            }

            when (tab) {
                CatalogTab.TECNICOS -> {
                    val items = viewModel.technicians.filter {
                        query.isBlank() || it.nombre.lowercase().contains(query) || it.username.lowercase().contains(query)
                    }
                    if (items.isEmpty()) EmptyCatalog("No hay técnicos registrados.")
                    items.forEach { tecnico ->
                        CatalogRow(
                            icon = Icons.Filled.Person,
                            title = tecnico.nombre,
                            subtitle = "@${tecnico.username} · ${tecnico.telefono.ifBlank { "Sin teléfono" }}",
                            badge = when {
                                !tecnico.activo -> "Inactivo"
                                tecnico.disponible -> "Disponible"
                                else -> "Ocupado"
                            },
                            badgeTint = when {
                                !tecnico.activo -> Color(0xFF94A3B8)
                                tecnico.disponible -> Color(0xFF6EE7B7)
                                else -> Color(0xFFFCD34D)
                            },
onEdit = { editingTecnico = tecnico; showTecnicoDialog = true; message = null },
                            onToggle = {
                                viewModel.toggleTecnicoActivo(tecnico)
                                message = null
                            }
                        )
                    }
                }
                CatalogTab.CLIENTES -> {
                    val items = viewModel.customers.filter {
                        query.isBlank() || it.nombre.lowercase().contains(query) || it.contacto.lowercase().contains(query)
                    }
                    if (items.isEmpty()) EmptyCatalog("No hay clientes registrados.")
                    items.forEach { cliente ->
                        CatalogRow(
                            icon = Icons.Filled.Group,
                            title = cliente.nombre,
                            subtitle = "${cliente.contacto} · ${cliente.email}",
                            badge = if (cliente.activo) "Activo" else "Inactivo",
                            badgeTint = if (cliente.activo) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                            onEdit = { editingCliente = cliente; showClienteDialog = true; message = null },
                            onToggle = {
                                viewModel.toggleClienteActivo(cliente)
                                message = null
                            }
                        )
                    }
                }
                CatalogTab.LOCALES -> {
                    val items = viewModel.locations.filter {
                        query.isBlank() || it.nombre.lowercase().contains(query) || it.direccion.lowercase().contains(query)
                    }
                    if (items.isEmpty()) EmptyCatalog("No hay locales registrados.")
                    items.forEach { local ->
                        CatalogRow(
                            icon = Icons.Filled.Store,
                            title = local.nombre,
                            subtitle = "${viewModel.clienteName(local.clienteId)} · ${local.direccion}, ${local.comuna}",
                            badge = if (local.activo) "Activo" else "Inactivo",
                            badgeTint = if (local.activo) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                            onEdit = { editingLocal = local; showLocalDialog = true; message = null },
                            onToggle = {
                                viewModel.toggleLocalActivo(local)
                                message = null
                            }
                        )
                    }
                }
                CatalogTab.EQUIPOS -> {
                    val items = viewModel.equipment.filter {
                        query.isBlank() || it.nombre.lowercase().contains(query) || it.id.lowercase().contains(query)
                    }
                    if (items.isEmpty()) EmptyCatalog("No hay equipos registrados.")
                    items.forEach { equipo ->
                        CatalogRow(
                            icon = Icons.Filled.Build,
                            title = equipo.nombre,
                            subtitle = "${equipo.id} · ${viewModel.localName(equipo.localId)}${if (equipo.exterior) " · Exterior" else ""}",
                            badge = if (equipo.activo) equipo.tipo else "Inactivo",
                            badgeTint = if (equipo.activo) Color(0xFF7DD3FC) else Color(0xFF94A3B8),
                            onEdit = { editingEquipo = equipo; showEquipoDialog = true; message = null },
                            onToggle = {
                                viewModel.toggleEquipoActivo(equipo)
                                message = null
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showTecnicoDialog) {
        TecnicoDialog(
            tecnico = editingTecnico,
            onDismiss = { showTecnicoDialog = false },
            onSave = { username, nombre, telefono, password, disponible ->
                val error = editingTecnico?.let {
                    viewModel.updateTecnico(it.copy(username = username, nombre = nombre, telefono = telefono, disponible = disponible), password)
                } ?: viewModel.addTecnico(username, nombre, telefono, password, disponible)
                if (error == null) {
                    message = if (editingTecnico == null) "Técnico creado." else "Técnico actualizado."
                    showTecnicoDialog = false
                }
                error
            }
        )
    }

    if (showClienteDialog) {
        ClienteDialog(
            cliente = editingCliente,
            onDismiss = { showClienteDialog = false },
            onSave = { nombre, contacto, email, telefono ->
                val error = editingCliente?.let {
                    viewModel.updateCliente(it.copy(nombre = nombre, contacto = contacto, email = email, telefono = telefono))
                } ?: viewModel.addCliente(nombre, contacto, email, telefono)
                if (error == null) {
                    message = if (editingCliente == null) "Cliente creado." else "Cliente actualizado."
                    showClienteDialog = false
                }
                error
            }
        )
    }

    if (showLocalDialog) {
        LocalDialog(
            local = editingLocal,
            customers = viewModel.customers.filter { it.activo },
            onDismiss = { showLocalDialog = false },
            onSave = { clienteId, nombre, direccion, comuna, latitud, longitud ->
                val error = editingLocal?.let {
                    viewModel.updateLocal(it.copy(clienteId = clienteId, nombre = nombre, direccion = direccion, comuna = comuna, latitud = latitud, longitud = longitud))
                } ?: viewModel.addLocal(clienteId, nombre, direccion, comuna, latitud, longitud)
                if (error == null) {
                    message = if (editingLocal == null) "Local creado." else "Local actualizado."
                    showLocalDialog = false
                }
                error
            }
        )
    }

    if (showEquipoDialog) {
        EquipoDialog(
            equipo = editingEquipo,
            locations = viewModel.locations.filter { it.activo },
            onDismiss = { showEquipoDialog = false },
            onSave = { id, localId, nombre, tipo, exterior, ultimaMantencion ->
                val error = editingEquipo?.let {
                    viewModel.updateEquipo(it.copy(localId = localId, nombre = nombre, tipo = tipo, exterior = exterior, ultimaMantencion = ultimaMantencion))
                } ?: viewModel.addEquipo(id, localId, nombre, tipo, exterior, ultimaMantencion)
                if (error == null) {
                    message = if (editingEquipo == null) "Equipo creado." else "Equipo actualizado."
                    showEquipoDialog = false
                }
                error
            }
        )
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 9.sp) },
        modifier = modifier
    )
}

@Composable
private fun CatalogRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    badgeTint: Color,
    onEdit: () -> Unit,
    onToggle: () -> Unit
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
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text(badge, color = badgeTint, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Editar",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(16.dp).clickable(onClick = onEdit)
        )
        TextButton(onClick = onToggle) {
            Text(if (badge == "Inactivo") "Activar" else "Desactivar", fontSize = 8.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
private fun EmptyCatalog(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}

@Composable
private fun TecnicoDialog(
    tecnico: Tecnico?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> String?
) {
    var username by remember(tecnico?.id) { mutableStateOf(tecnico?.username.orEmpty()) }
    var nombre by remember(tecnico?.id) { mutableStateOf(tecnico?.nombre.orEmpty()) }
    var telefono by remember(tecnico?.id) { mutableStateOf(tecnico?.telefono.orEmpty()) }
    var password by remember(tecnico?.id) { mutableStateOf("") }
    var disponible by remember(tecnico?.id) { mutableStateOf(tecnico?.disponible ?: true) }
    var error by remember(tecnico?.id) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tecnico == null) "Nuevo técnico" else "Editar técnico") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(username, { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(
                    password,
                    { password = it },
                    label = { Text(if (tecnico == null) "Contraseña" else "Nueva contraseña (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = disponible, onCheckedChange = { disponible = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Disponible para asignación", fontSize = 11.sp)
                }
                if (error != null) Text(error.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { error = onSave(username, nombre, telefono, password, disponible) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ClienteDialog(
    cliente: Cliente?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> String?
) {
    var nombre by remember(cliente?.id) { mutableStateOf(cliente?.nombre.orEmpty()) }
    var contacto by remember(cliente?.id) { mutableStateOf(cliente?.contacto.orEmpty()) }
    var email by remember(cliente?.id) { mutableStateOf(cliente?.email.orEmpty()) }
    var telefono by remember(cliente?.id) { mutableStateOf(cliente?.telefono.orEmpty()) }
    var error by remember(cliente?.id) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (cliente == null) "Nuevo cliente" else "Editar cliente") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(contacto, { contacto = it }, label = { Text("Contacto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                if (error != null) Text(error.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { error = onSave(nombre, contacto, email, telefono) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun LocalDialog(
    local: LocalComercial?,
    customers: List<Cliente>,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, String, Double?, Double?) -> String?
) {
    var clienteId by remember(local?.id) { mutableStateOf(local?.clienteId ?: customers.firstOrNull()?.id ?: 0L) }
    var nombre by remember(local?.id) { mutableStateOf(local?.nombre.orEmpty()) }
    var direccion by remember(local?.id) { mutableStateOf(local?.direccion.orEmpty()) }
    var comuna by remember(local?.id) { mutableStateOf(local?.comuna.orEmpty()) }
    var latitud by remember(local?.id) { mutableStateOf(local?.latitud?.toString().orEmpty()) }
    var longitud by remember(local?.id) { mutableStateOf(local?.longitud?.toString().orEmpty()) }
    var showClientMenu by remember { mutableStateOf(false) }
    var error by remember(local?.id) { mutableStateOf<String?>(null) }
    val selected = customers.find { it.id == clienteId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (local == null) "Nuevo local" else "Editar local") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    OutlinedButton(onClick = { showClientMenu = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selected?.nombre ?: "Seleccionar cliente", fontSize = 11.sp)
                    }
                    DropdownMenu(expanded = showClientMenu, onDismissRequest = { showClientMenu = false }) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.nombre) },
                                onClick = { clienteId = customer.id; showClientMenu = false }
                            )
                        }
                    }
                }
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre del local") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(comuna, { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(latitud, { latitud = it }, label = { Text("Latitud (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(longitud, { longitud = it }, label = { Text("Longitud (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                if (error != null) Text(error.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = onSave(
                    clienteId,
                    nombre,
                    direccion,
                    comuna,
                    latitud.toDoubleOrNull(),
                    longitud.toDoubleOrNull()
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EquipoDialog(
    equipo: Equipo?,
    locations: List<LocalComercial>,
    onDismiss: () -> Unit,
    onSave: (String, Long, String, String, Boolean, String) -> String?
) {
    var id by remember(equipo?.id) { mutableStateOf(equipo?.id.orEmpty()) }
    var localId by remember(equipo?.id) { mutableStateOf(equipo?.localId ?: locations.firstOrNull()?.id ?: 0L) }
    var nombre by remember(equipo?.id) { mutableStateOf(equipo?.nombre.orEmpty()) }
    var tipo by remember(equipo?.id) { mutableStateOf(equipo?.tipo ?: "Refrigeración") }
    var exterior by remember(equipo?.id) { mutableStateOf(equipo?.exterior ?: false) }
    var ultimaMantencion by remember(equipo?.id) { mutableStateOf(equipo?.ultimaMantencion.orEmpty()) }
    var showLocalMenu by remember { mutableStateOf(false) }
    var error by remember(equipo?.id) { mutableStateOf<String?>(null) }
    val selected = locations.find { it.id == localId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipo == null) "Nuevo equipo" else "Editar equipo") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(id, { id = it }, label = { Text("ID de equipo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = equipo == null)
                Box {
                    OutlinedButton(onClick = { showLocalMenu = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selected?.nombre ?: "Seleccionar local", fontSize = 11.sp)
                    }
                    DropdownMenu(expanded = showLocalMenu, onDismissRequest = { showLocalMenu = false }) {
                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location.nombre) },
                                onClick = { localId = location.id; showLocalMenu = false }
                            )
                        }
                    }
                }
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(tipo, { tipo = it }, label = { Text("Tipo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = exterior, onCheckedChange = { exterior = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Equipo exterior", fontSize = 11.sp)
                }
                OutlinedTextField(ultimaMantencion, { ultimaMantencion = it }, label = { Text("Última mantención") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                if (error != null) Text(error.orEmpty(), color = Color(0xFFFCA5A5), fontSize = 10.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = onSave(id, localId, nombre, tipo, exterior, ultimaMantencion)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
