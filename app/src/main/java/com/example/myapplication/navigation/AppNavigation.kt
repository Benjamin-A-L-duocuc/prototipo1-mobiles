package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.view.auth.LoginScreen
import com.example.myapplication.view.cliente.ClienteSeguimientoScreen
import com.example.myapplication.view.jefe.JefeAgendaScreen
import com.example.myapplication.view.jefe.JefeCrearOTScreen
import com.example.myapplication.view.jefe.JefeDashboardScreen
import com.example.myapplication.view.tecnico.SyncCenterScreen
import com.example.myapplication.view.tecnico.TecnicoCheckInScreen
import com.example.myapplication.view.tecnico.TecnicoCierreScreen
import com.example.myapplication.view.tecnico.TecnicoDetalleOTScreen
import com.example.myapplication.view.tecnico.TecnicoEjecucionScreen
import com.example.myapplication.view.tecnico.TecnicoEvidenciasScreen
import com.example.myapplication.view.tecnico.TecnicoJornadaScreen
import com.example.myapplication.viewmodel.AppViewModel

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val navigate = remember(navController) {
        { route: String -> navController.navigateToRoute(route) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070D18))) {
        NavHost(navController = navController, startDestination = Rutas.LOGIN) {
            composable(Rutas.LOGIN) {
                LoginScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.JORNADA) {
                TecnicoJornadaScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.DETALLE) {
                TecnicoDetalleOTScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.CHECKIN) {
                TecnicoCheckInScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.EJECUCION) {
                TecnicoEjecucionScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.EVIDENCIAS) {
                TecnicoEvidenciasScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.CIERRE) {
                TecnicoCierreScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.SYNC) {
                SyncCenterScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.DASHBOARD) {
                JefeDashboardScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.CREAR_OT) {
                JefeCrearOTScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.AGENDA) {
                JefeAgendaScreen(viewModel = viewModel, onNavigate = navigate)
            }
            composable(Rutas.CLIENTE) {
                ClienteSeguimientoScreen(viewModel = viewModel)
            }
        }
    }
}

private fun NavHostController.navigateToRoute(route: String) {
    if (route == Rutas.LOGIN) {
        navigate(route) {
            popUpTo(graph.id) { inclusive = true }
            launchSingleTop = true
        }
        return
    }

    if (currentDestination?.route == Rutas.LOGIN) {
        navigate(route) {
            popUpTo(Rutas.LOGIN) { inclusive = true }
            launchSingleTop = true
        }
        return
    }

    if (!popBackStack(route, inclusive = false)) {
        navigate(route) { launchSingleTop = true }
    }
}
