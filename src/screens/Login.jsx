import { ArrowRight, Building2, ClipboardList, Snowflake, Wrench } from 'lucide-react'
import { useDemo } from '../state/DemoContext'

const roles = [
  {
    value: 'tecnico',
    label: 'Técnico de terreno',
    description: 'Mis OT, check-in, evidencias y cierre',
    icon: Wrench,
  },
  {
    value: 'jefe',
    label: 'Jefe de mantenimiento',
    description: 'Planificar, asignar, supervisar y reportar',
    icon: ClipboardList,
  },
  {
    value: 'cliente',
    label: 'Cliente',
    description: 'Seguimiento de OT y conformidad',
    icon: Building2,
  },
]

export default function Login() {
  const { goRole } = useDemo()

  return (
    <div className="relative flex h-full flex-col overflow-y-auto overflow-x-hidden bg-gradient-to-b from-slate-950 via-slate-900 to-cyan-950 px-5 py-7 text-white">
      <div className="pointer-events-none absolute -right-16 top-8 h-48 w-48 rounded-full bg-sky-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -left-16 bottom-10 h-40 w-40 rounded-full bg-cyan-400/10 blur-3xl" />

      <div className="relative">
        <div className="flex items-center gap-3">
          <div className="grid h-12 w-12 place-items-center rounded-2xl bg-sky-500/20 ring-1 ring-sky-400/30">
            <Snowflake className="h-7 w-7 text-sky-300" />
          </div>
          <div>
            <p className="text-2xl font-extrabold tracking-tight">ACIS</p>
            <p className="text-[11px] text-slate-400">Servicios Técnicos Ltda.</p>
          </div>
        </div>

        <h1 className="mt-8 text-[26px] font-bold leading-tight">
          Gestión de órdenes de trabajo en terreno
        </h1>
        <p className="mt-2 text-sm text-slate-400">Prototipo visual · DSY1105</p>
        <p className="mt-1 text-[11px] text-slate-500">Selecciona un perfil para explorar la aplicación.</p>

        <div className="mt-6 space-y-3">
          {roles.map((role) => {
            const Icon = role.icon
            return (
              <button
                key={role.value}
                type="button"
                onClick={() => goRole(role.value)}
                className="group flex w-full items-center gap-3 rounded-2xl border border-white/10 bg-white/5 p-3.5 text-left transition hover:border-sky-400/40 hover:bg-white/10"
              >
                <div className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-sky-500/15 text-sky-300">
                  <Icon className="h-5 w-5" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold">{role.label}</p>
                  <p className="text-[11px] text-slate-400">{role.description}</p>
                </div>
                <ArrowRight className="h-4 w-4 shrink-0 text-slate-500 transition group-hover:text-sky-300" />
              </button>
            )
          })}
        </div>

        <div className="mt-8 rounded-xl border border-white/10 bg-white/5 p-3 text-[11px] leading-snug text-slate-400">
          <p className="font-semibold text-slate-200">Demo sin backend</p>
          <p className="mt-1">
            Los datos son simulados. Usa los switches de arriba para cambiar de rol, conexión y escenario visual.
          </p>
        </div>
      </div>
    </div>
  )
}
