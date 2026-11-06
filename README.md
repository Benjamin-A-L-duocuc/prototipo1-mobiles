# ACIS Servicios Técnicos

Aplicación Android para la gestión de órdenes de trabajo en terreno (DSY1105).

El proyecto se encuentra en refactorización desde un prototipo visual hacia una aplicación funcional con persistencia local. La interfaz de los tres perfiles está implementada y una parte del flujo ya persiste en SQLite; el resto del dominio todavía es estático o simulado.

## Stack

- Kotlin 2.2.10
- Jetpack Compose con Material 3
- Navigation Compose
- SQLite local con `SQLiteOpenHelper`
- MVVM con `AndroidViewModel` y estado de Compose
- Gradle 9.8.2 / AGP 9.3.3
- `compileSdk` y `targetSdk` 37, `minSdk` 24

## Requisitos

- Android Studio reciente
- JDK 17 o superior
- Android SDK 37
- Emulador o dispositivo Android

## Ejecución

```powershell
.\gradlew.bat assembleDebug
```

Abrir el proyecto en Android Studio y ejecutar el módulo `app` en un emulador o dispositivo. El APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Usuarios demo

| Usuario | Rol |
| --- | --- |
| `cristian.rojas` | Técnico de terreno |
| `gerardo.jefe` | Jefe de mantenimiento |
| `maria.cliente` | Cliente |

La pantalla de login valida usuario y rol contra SQLite. La validación de contraseña todavía no está implementada.

## Funcionalidades actuales

- Login por rol con usuarios almacenados en SQLite.
- Navegación con `NavHost` y pila de navegación real.
- Listado de OT desde SQLite.
- Creación, edición y eliminación de OT desde el panel de jefatura.
- Cambio de estado de OT (asignada, aceptada, en ejecución, conformada).
- Registro de conformidad del cliente en SQLite.
- Cola de sincronización local básica.
- Detección de conectividad con `NetworkMonitor`.

## Limitaciones conocidas

- Cámara, GPS, check-in, check-out y evidencias todavía son simulados.
- Diagnóstico, actividades y materiales no se persisten.
- La sincronización offline no tiene backend ni resolución de conflictos.
- Las alertas meteorológicas y el cálculo de SLA son estáticos.
- Los catálogos de clientes, locales y equipos no tienen CRUD.
- No existen marcas de tiempo ni historial de estados.
- Las pruebas automatizadas son mínimas.

## Estructura

```text
app/src/main/java/com/example/myapplication/
├── MainActivity.kt
├── data/
│   ├── LocalDatabaseHelper.kt
│   └── repository/
│       ├── MockRepository.kt
│       └── OtRepository.kt
├── model/
│   └── OT.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Rutas.kt
├── ui/
│   ├── components/
│   └── theme/
├── util/
│   └── NetworkMonitor.kt
├── view/
│   ├── auth/
│   ├── cliente/
│   ├── jefe/
│   └── tecnico/
└── viewmodel/
    ├── AppViewModel.kt
    └── AuthViewModel.kt
```

## Pruebas

```powershell
.\gradlew.bat testDebugUnitTest
```

## Contexto del proyecto

Ver `PROJECT_CONTEXT.md` para el detalle de la arquitectura, el estado real y el backlog de trabajo.
