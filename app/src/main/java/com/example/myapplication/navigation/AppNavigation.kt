package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.view.auth.LoginScreen
import com.example.myapplication.view.cliente.ClienteSeguimientoScreen
import com.example.myapplication.view.jefe.JefeAgendaScreen
import com.example.myapplication.view.jefe.JefeCrearOTScreen
import com.example.myapplication.view.jefe.JefeDashboardScreen
import com.example.myapplication.view.tecnico.*
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070D18))) {
        when (viewModel.currentRoute) {
            Rutas.LOGIN -> LoginScreen(viewModel)
            Rutas.JORNADA -> TecnicoJornadaScreen(viewModel)
            Rutas.DETALLE -> TecnicoDetalleOTScreen(viewModel)
            Rutas.CHECKIN -> TecnicoCheckInScreen(viewModel)
            Rutas.EJECUCION -> TecnicoEjecucionScreen(viewModel)
            Rutas.EVIDENCIAS -> TecnicoEvidenciasScreen(viewModel)
            Rutas.CIERRE -> TecnicoCierreScreen(viewModel)
            Rutas.SYNC -> SyncCenterScreen(viewModel)
            Rutas.DASHBOARD -> JefeDashboardScreen(viewModel)
            Rutas.CREAR_OT -> JefeCrearOTScreen(viewModel)
            Rutas.AGENDA -> JefeAgendaScreen(viewModel)
            Rutas.CLIENTE -> ClienteSeguimientoScreen(viewModel)
            else -> LoginScreen(viewModel)
        }
    }
}
