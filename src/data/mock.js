export const OT = {
  id: 'OT-1042',
  cliente: 'Supermercado Ñuñoa',
  local: 'Local Ñuñoa',
  direccion: 'Av. Irarrázaval 3400, Ñuñoa',
  equipo: 'Cámara frigorífica 4 puertas',
  equipoId: 'EQ-221',
  tipo: 'Correctivo',
  prioridad: 'Alta',
  estado: 'Asignada',
  sla: '1h 20m',
  distancia: '3,2 km',
  descripcion:
    'Cliente reporta alza de temperatura en cámara de lácteos. Revisar condensadora, circuito de refrigerante y estado del evaporador.',
  exterior: false,
}

export const OTS = [
  OT,
  {
    id: 'OT-1043',
    cliente: 'Retail Macul',
    local: 'Local Macul',
    direccion: 'Av. Macul 4500, Macul',
    equipo: 'Condensadora en azotea',
    equipoId: 'EQ-118',
    tipo: 'Preventivo',
    prioridad: 'Media',
    estado: 'Asignada',
    sla: '4h 10m',
    distancia: '5,8 km',
    descripcion: 'Mantención preventiva de condensadora ubicada en azotea. Revisar soportes y limpieza de serpentín.',
    exterior: true,
  },
  {
    id: 'OT-1044',
    cliente: 'Mall Plaza Oeste',
    local: 'Patio de comidas',
    direccion: 'Av. Vespucio 1200, Pudahuel',
    equipo: 'Chiller de agua helada',
    equipoId: 'EQ-305',
    tipo: 'Correctivo',
    prioridad: 'Alta',
    estado: 'En ejecución',
    sla: '0h 45m',
    distancia: '8,1 km',
    descripcion: 'Baja presión en circuito de agua helada. Revisar bomba y niveles.',
    exterior: false,
  },
  {
    id: 'OT-1045',
    cliente: 'Supermercado La Reina',
    local: 'Local La Reina',
    direccion: 'Av. Ossa 900, La Reina',
    equipo: 'Unidad exterior split',
    equipoId: 'EQ-402',
    tipo: 'Preventivo',
    prioridad: 'Baja',
    estado: 'Aceptada',
    sla: '6h',
    distancia: '6,4 km',
    descripcion: 'Limpieza de unidad exterior y revisión de soportes.',
    exterior: true,
  },
]

export const AGENDA_HOY = [
  {
    hora: '09:00',
    tecnico: 'Cristian Rojas',
    ot: 'OT-1042',
    cliente: 'Supermercado Ñuñoa',
    exterior: false,
    prioridad: 'Alta',
  },
  {
    hora: '11:30',
    tecnico: 'Paula Díaz',
    ot: 'OT-1043',
    cliente: 'Retail Macul',
    exterior: true,
    prioridad: 'Media',
  },
  {
    hora: '14:00',
    tecnico: 'Diego Soto',
    ot: 'OT-1044',
    cliente: 'Mall Plaza Oeste',
    exterior: false,
    prioridad: 'Alta',
  },
  {
    hora: '16:30',
    tecnico: 'Cristian Rojas',
    ot: 'OT-1045',
    cliente: 'Supermercado La Reina',
    exterior: true,
    prioridad: 'Baja',
  },
]

export const ACTIVIDADES = [
  'Revisar presión del circuito de refrigerante',
  'Limpiar serpentín del condensador',
  'Verificar funcionamiento del evaporador',
  'Registrar temperatura de salida',
]

export const MATERIALES = [
  { nombre: 'Gas refrigerante R410A', cantidad: 2, unidad: 'kg' },
  { nombre: 'Filtro secador', cantidad: 1, unidad: 'un' },
  { nombre: 'Cinta aislante térmica', cantidad: 2, unidad: 'rollo' },
]

export const SYNC_ITEMS = [
  { tipo: 'Check-in', detalle: '09:41 · GPS capturado', icono: 'checkin' },
  { tipo: 'Diagnóstico', detalle: '09:52 · Fuga en circuito', icono: 'diagnostico' },
  { tipo: 'Actividades', detalle: '10:05 · 3 actividades', icono: 'actividades' },
  { tipo: 'Evidencias', detalle: '10:20 · 2 fotografías', icono: 'evidencias' },
]

export const OT_PRIORITARIAS = [
  { id: 'OT-1042', cliente: 'Supermercado Ñuñoa', prioridad: 'Alta', estado: 'Asignada', sla: '1h 20m' },
  { id: 'OT-1044', cliente: 'Mall Plaza Oeste', prioridad: 'Alta', estado: 'En ejecución', sla: '0h 45m' },
  { id: 'OT-1043', cliente: 'Retail Macul', prioridad: 'Media', estado: 'Asignada', sla: '4h 10m' },
]

export const CLIENTE = {
  nombre: 'María Soto',
  cargo: 'Administradora de local',
  local: 'Supermercado Ñuñoa',
  email: 'maria.soto@supernunoa.cl',
}

export const TECNICO = {
  nombre: 'Cristian Rojas',
  rol: 'Técnico de terreno',
  iniciales: 'CR',
  telefono: '+56 9 8765 4321',
}

export const TIMELINE = [
  { hito: 'Creada', fecha: '27/10 · 08:10', hecho: true },
  { hito: 'Asignada', fecha: '27/10 · 08:25', hecho: true },
  { hito: 'Aceptada', fecha: '27/10 · 08:40', hecho: true },
  { hito: 'En ejecución', fecha: '27/10 · 09:41', hecho: true },
  { hito: 'Cerrada', fecha: '27/10 · 10:35', hecho: true },
  { hito: 'Conformada', fecha: 'Pendiente', hecho: false },
]
