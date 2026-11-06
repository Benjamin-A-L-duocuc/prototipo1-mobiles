# ACIS Servicios Técnicos

Aplicación Android para la gestión de órdenes de trabajo en terreno (DSY1105).

El proyecto se encuentra en refactorización desde un prototipo visual hacia una aplicación funcional con persistencia local. La interfaz de los tres perfiles está implementada y el flujo técnico ya integra cámara y GPS reales; algunos módulos de planificación y sincronización siguen siendo locales o simulados.

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

| Usuario | Rol | Contraseña |
| --- | --- | --- |
| `cristian.rojas` | Técnico de terreno | `password123` |
| `gerardo.jefe` | Jefe de mantenimiento | `password123` |
| `maria.cliente` | Cliente | `password123` |

El login valida usuario, rol y contraseña contra SQLite. Las contraseñas se almacenan como hash SHA-256 y los formularios usan validadores centralizados.

## Funcionalidades actuales

- Login por rol con credenciales almacenadas y validadas en SQLite.
- Formularios con validación por campo, mensajes de error, íconos y retroalimentación visual.
- Navegación con `NavHost`, pila real y transiciones animadas.
- Listado de OT desde SQLite.
- Creación, edición y eliminación de OT desde el panel de jefatura.
- Cambio de estado de OT (asignada, aceptada, en ejecución, conformada).
- Check-in con GPS real del dispositivo, permisos de ubicación y persistencia local.
- Evidencias fotográficas con cámara real, permisos, miniaturas y persistencia local.
- Registro de conformidad del cliente en SQLite.
- Cola de sincronización local básica.
- Detección de conectividad con `NetworkMonitor`.

## Limitaciones conocidas

- Diagnóstico, actividades y materiales no se persisten.
- La sincronización offline no tiene backend ni resolución de conflictos.
- Las alertas meteorológicas y el cálculo de SLA son estáticos.
- Los catálogos de clientes, locales y equipos no tienen CRUD.
- No existen marcas de tiempo ni historial completo de estados.
- La firma de conformidad es un placeholder, aunque el estado de conformidad sí persiste.

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
│   ├── LocationProvider.kt
│   ├── NetworkMonitor.kt
│   └── PasswordHasher.kt
├── validation/
│   └── Validators.kt
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
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Las pruebas unitarias cubren invariantes del repositorio demo, el hasher de contraseñas y los validadores de formularios.

## Contexto del proyecto

La fuente de verdad del estado actual es el código, las pruebas y el material de contexto de la asignatura. `PROJECT_CONTEXT.md` puede estar desactualizado.
