# Project Context - ACIS Servicios Técnicos (DSY1105)

This document describes the real state of the project after the cleanup refactor. It intentionally separates what is implemented from what is still simulated.

## 1. Overview

- Domain: field service management for HVAC and commercial refrigeration maintenance.
- Course: DSY1105, Android mobile application development.
- Current state: functional visual prototype with partial local persistence. It is not production-ready.
- Application label: `ACIS Servicios Técnicos`.
- Legacy package and application ID: `com.example.myapplication`.

## 2. Build and Stack

- Kotlin 2.2.10
- Jetpack Compose with Material 3 (BOM 2026.02.01)
- Navigation Compose 2.7.7
- Local SQLite via `SQLiteOpenHelper`
- MVVM with `AndroidViewModel` and Compose state
- Gradle 9.8.2, AGP 9.3.3
- `compileSdk` 37, `targetSdk` 37, `minSdk` 24
- Java compatibility 11

Build commands:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## 3. Architecture and Navigation

- `MainActivity` hosts `AppNavigation` inside the Compose theme.
- `AppNavigation` uses a real `NavHost` with `rememberNavController()`.
- Routes are defined in `navigation/Rutas.kt`.
- Screens receive an `onNavigate: (String) -> Unit` callback instead of owning navigation state.
- `AppViewModel` holds session, role, OT list, selected OT, sync items and connectivity state.
- `AuthViewModel` validates username and role against the `users` table.
- Navigation helper behavior:
  - login clears the login destination from the back stack,
  - logout clears the whole stack,
  - navigating to an existing route pops back to it when possible.

### Current ViewModel coverage

- `AppViewModel`: session, OT operations, sync queue and connectivity.
- `AuthViewModel`: authentication against SQLite.
- The previous feature-specific unused ViewModels were removed during cleanup.

## 4. Local Database

Database: `prototipo_acis.db`, version 3.

Tables created by `LocalDatabaseHelper`:

1. `ots`: work orders.
   - `id`, `cliente`, `local`, `direccion`, `equipo`, `equipoId`, `tipo`, `prioridad`, `estado`, `sla`, `distancia`, `descripcion`, `exterior`.
2. `users`: seeded users.
   - `id`, `username`, `role`, `name`.
3. `clients`: seeded client catalog.
   - `id`, `nombre`, `cargo`, `local`, `email`.
4. `equipment`: seeded equipment catalog.
   - `id`, `nombre`, `local`, `ubicacion`, `exterior`, `ultima_mantencion`.
5. `materials`: seeded materials.
   - `id`, `ot_id`, `nombre`, `cantidad`, `unidad`.
6. `sync_items`: simple offline queue.
   - `id`, `tipo`, `detalle`, `icono`.

### Actual database usage

- `ots`: read and full CRUD through `OtRepository`.
- `sync_items`: insert, read and clear.
- `users`: read only during login.
- `clients`, `equipment`, `materials`: seeded only. No UI or repository CRUD exists yet.
- There are no timestamps, status history, assigned technician, evidence records, local IDs or conflict fields.

`OtRepository` now reads directly from SQLite and does not fall back to mock data when a table is empty.

## 5. Seeded Users

- `cristian.rojas` / role `tecnico`
- `gerardo.jefe` / role `jefe`
- `maria.cliente` / role `cliente`

Authentication currently validates that the username exists for the selected role. The password field is not validated or stored.

## 6. Implemented Workflows

### Technician

- Login with a seeded technician user.
- View OT assigned data loaded from SQLite.
- Open OT detail.
- Accept an OT, persisting state `Aceptada`.
- Register check-in, persisting state `En ejecución` and adding a sync queue item.
- Navigate through execution, evidence and closing screens.

### Supervisor

- View operational dashboard.
- Create OT and persist it.
- Edit client, equipment, priority and state inline.
- Delete OT permanently.
- View agenda generated from the OT list.

### Client

- View OT status and a static milestone timeline.
- Enter a name and sign conformity, persisting state `Conformada`.

## 7. Simulated or Incomplete Areas

These areas must not be treated as complete:

- Camera: the evidence button appends a text item. It does not launch a camera intent or store images.
- FileProvider and camera permissions exist in the manifest but are not used by a real capture flow.
- GPS and location: coordinates and times are hardcoded.
- Check-in and check-out: no device timestamps, no check-out persistence, no location history.
- Diagnosis, activities and materials: stored only in screen-local Compose state.
- Evidence records: not persisted.
- Closing an OT: does not set state `Cerrada`, does not validate evidence and does not record close time.
- Offline sync: waiting is simulated with a delay; queue clearing is local only. There is no backend, retry policy or conflict resolution.
- Connectivity: `NetworkMonitor` updates `AppViewModel.connection`, but synchronization is not automatic.
- Weather: no public weather API integration. The rain scenario is a hardcoded state.
- SLA: dashboard compliance is clamped to a minimum of 85% and is not calculated from real timestamps.
- At-risk SLA count only counts high-priority open OTs.
- Active technician count is hardcoded.
- Client catalog and equipment catalog have no CRUD.
- Technician assignment is not modeled in the OT table.
- No audit trail or status timestamps.
- No real digital signature image.
- No automated tests beyond a trivial unit test.

## 8. Current File Structure

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
│   │   └── CommonComponents.kt
│   └── theme/
├── util/
│   └── NetworkMonitor.kt
├── view/
│   ├── auth/
│   │   └── LoginScreen.kt
│   ├── cliente/
│   │   └── ClienteSeguimientoScreen.kt
│   ├── jefe/
│   │   ├── JefeAgendaScreen.kt
│   │   ├── JefeCrearOTScreen.kt
│   │   └── JefeDashboardScreen.kt
│   └── tecnico/
│       ├── SyncCenterScreen.kt
│       ├── TecnicoCheckInScreen.kt
│       ├── TecnicoCierreScreen.kt
│       ├── TecnicoDetalleOTScreen.kt
│       ├── TecnicoEjecucionScreen.kt
│       ├── TecnicoEvidenciasScreen.kt
│       └── TecnicoJornadaScreen.kt
└── viewmodel/
    ├── AppViewModel.kt
    └── AuthViewModel.kt
```

## 9. Cleanup Already Completed

- Removed duplicated legacy screens under `view/*.kt`.
- Removed duplicated `view/components/CommonComponents.kt`.
- Removed duplicated `data/MockRepository.kt`.
- Removed unused `TecnicoViewModel`, `JefeViewModel` and `ClienteViewModel`.
- Moved navigation from a manual `currentRoute` switch to Navigation Compose.
- Replaced deprecated Material icons with auto-mirrored variants.
- Fixed `MockRepository` imports after consolidation.
- Removed mock fallbacks from `OtRepository`.
- Wired real connectivity state from `NetworkMonitor`.
- Added project README and aligned this context document with reality.

## 10. Recommended Next Work

1. Define the real domain model and SQLite schema: assigned technician, venues, equipment, timestamps, status history, materials, evidence and sync queue metadata.
2. Implement a real OT state machine with validations and audit timestamps.
3. Persist technician execution data: diagnosis, activities, materials and evidence references.
4. Implement real camera capture and protected file storage.
5. Implement check-in/check-out with device time and location.
6. Replace the simulated sync flow with automatic queue processing, retry, conflict handling and a real remote source or clearly documented local-only mode.
7. Implement SLA rules and calculations from real timestamps.
8. Add weather API integration or explicitly scope it out.
9. Add form validation, loading states, animations and automated tests.
10. Split `AppViewModel` into feature-level ViewModels once the domain model is stable.

## 11. Verification Status

- Last verified command: `.\gradlew.bat assembleDebug testDebugUnitTest`.
- Build result: successful.
- Test result: one trivial unit test passed.
- Git remote: `https://github.com/Benjamin-A-L-duocuc/prototipo1-mobiles`.
- Branch: `main`.
