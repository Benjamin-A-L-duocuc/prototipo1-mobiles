import {
  Building2,
  CheckCircle2,
  CloudRain,
  RefreshCw,
  Wifi,
  WifiOff,
  Wrench,
  ClipboardList,
  XCircle,
} from 'lucide-react'
import { useDemo } from '../state/DemoContext'

function DarkSegmented({ options, value, onChange }) {
  return (
    <div className="inline-flex flex-wrap rounded-xl bg-slate-900/80 p-1 ring-1 ring-white/10">
      {options.map((option) => {
        const Icon = option.icon
        const active = option.value === value
        return (
          <button
            key={option.value}
            type="button"
            onClick={() => onChange(option.value)}
            className={`inline-flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-[11px] font-semibold transition ${
              active ? option.activeClass : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            {Icon && <Icon className="h-3.5 w-3.5" />}
            {option.label}
          </button>
        )
      })}
    </div>
  )
}

function Group({ label, children }) {
  return (
    <div className="flex flex-col gap-1.5">
      <span className="text-[9px] font-bold uppercase tracking-[0.18em] text-slate-500">{label}</span>
      {children}
    </div>
  )
}

export default function DemoBar() {
  const { role, goRole, connection, setConnection, scenario, setScenario, resetDemo, pending } = useDemo()

  const roleOptions = [
    { value: 'tecnico', label: 'Técnico', icon: Wrench, activeClass: 'bg-sky-500 text-white shadow-sm' },
    { value: 'jefe', label: 'Jefe', icon: ClipboardList, activeClass: 'bg-sky-500 text-white shadow-sm' },
    { value: 'cliente', label: 'Cliente', icon: Building2, activeClass: 'bg-sky-500 text-white shadow-sm' },
  ]

  const connectionOptions = [
    { value: 'online', label: 'En línea', icon: Wifi, activeClass: 'bg-emerald-500 text-white shadow-sm' },
    { value: 'offline', label: 'Sin conexión', icon: WifiOff, activeClass: 'bg-amber-400 text-slate-900 shadow-sm' },
  ]

  const scenarioOptions = [
    { value: 'normal', label: 'Normal', icon: CheckCircle2, activeClass: 'bg-slate-200 text-slate-900 shadow-sm' },
    { value: 'lluvia', label: 'Alerta lluvia', icon: CloudRain, activeClass: 'bg-amber-400 text-slate-900 shadow-sm' },
    { value: 'rechazo', label: 'Cliente rechaza', icon: XCircle, activeClass: 'bg-rose-500 text-white shadow-sm' },
  ]

  return (
    <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3 shadow-xl shadow-black/20 backdrop-blur">
      <div className="flex flex-wrap items-end gap-x-5 gap-y-3">
        <Group label="Rol">
          <DarkSegmented options={roleOptions} value={role} onChange={goRole} />
        </Group>

        <Group label="Conexión">
          <DarkSegmented options={connectionOptions} value={connection} onChange={setConnection} />
        </Group>

        <Group label="Escenario visual">
          <DarkSegmented options={scenarioOptions} value={scenario} onChange={setScenario} />
        </Group>

        <div className="ml-auto flex items-center gap-3">
          <div className="text-right">
            <p className="text-[9px] font-bold uppercase tracking-[0.18em] text-slate-500">Cola de sync</p>
            <p className={`text-xs font-semibold ${pending > 0 ? 'text-amber-300' : 'text-emerald-300'}`}>
              {pending > 0 ? `${pending} pendientes` : 'Todo sincronizado'}
            </p>
          </div>
          <button
            type="button"
            onClick={resetDemo}
            className="inline-flex h-9 items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-3 text-[11px] font-semibold text-slate-300 transition hover:bg-white/10"
          >
            <RefreshCw className="h-3.5 w-3.5" />
            Reiniciar
          </button>
        </div>
      </div>
    </div>
  )
}
