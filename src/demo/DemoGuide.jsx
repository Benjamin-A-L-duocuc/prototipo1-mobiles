import { Info, MousePointerClick, SlidersHorizontal, Wifi, WifiOff } from 'lucide-react'
import { useDemo } from '../state/DemoContext'

const roleLabel = { tecnico: 'Técnico de terreno', jefe: 'Jefe de mantenimiento', cliente: 'Cliente' }
const connectionLabel = { online: 'En línea', offline: 'Sin conexión' }
const scenarioLabel = { normal: 'Normal', lluvia: 'Alerta de lluvia', rechazo: 'Cliente rechaza' }

export default function DemoGuide() {
  const { role, connection, scenario, pending } = useDemo()

  return (
    <aside className="space-y-4">
      <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4">
        <div className="flex items-center gap-2">
          <SlidersHorizontal className="h-4 w-4 text-sky-400" />
          <h2 className="text-sm font-bold text-white">Cómo usar los switches</h2>
        </div>
        <ul className="mt-3 space-y-2.5 text-[12px] leading-snug text-slate-400">
          <li>
            <span className="font-semibold text-slate-200">Rol:</span> cambia la vista entre técnico, jefe y cliente.
          </li>
          <li>
            <span className="font-semibold text-slate-200">Conexión:</span> muestra el estado offline y la cola de
            sincronización. No hay red real.
          </li>
          <li>
            <span className="font-semibold text-slate-200">Escenario:</span> cambia la variante visual de las
            pantallas (lluvia o rechazo del cliente).
          </li>
        </ul>
      </div>

      <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4">
        <div className="flex items-center gap-2">
          <MousePointerClick className="h-4 w-4 text-sky-400" />
          <h2 className="text-sm font-bold text-white">Guion de demo</h2>
        </div>
        <ol className="mt-3 space-y-2.5 text-[12px] leading-snug text-slate-400">
          <li>
            <span className="font-semibold text-slate-200">1.</span> Entra como Técnico y activa "Sin conexión".
          </li>
          <li>
            <span className="font-semibold text-slate-200">2.</span> Abre la OT, acéptala y registra el check-in.
          </li>
          <li>
            <span className="font-semibold text-slate-200">3.</span> Completa diagnóstico, actividades, materiales y
            evidencias.
          </li>
          <li>
            <span className="font-semibold text-slate-200">4.</span> Cierra la OT con firma y revisa la cola de
            sincronización.
          </li>
          <li>
            <span className="font-semibold text-slate-200">5.</span> Cambia a "En línea" y sincroniza.
          </li>
          <li>
            <span className="font-semibold text-slate-200">6.</span> Cambia a Jefe y luego a Cliente para ver el
            cierre del ciclo.
          </li>
        </ol>
      </div>

      <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4">
        <div className="flex items-center gap-2">
          <Info className="h-4 w-4 text-sky-400" />
          <h2 className="text-sm font-bold text-white">Estado actual</h2>
        </div>
        <dl className="mt-3 space-y-2 text-[12px]">
          <div className="flex items-center justify-between gap-2">
            <dt className="text-slate-500">Rol</dt>
            <dd className="font-semibold text-slate-200">{roleLabel[role]}</dd>
          </div>
          <div className="flex items-center justify-between gap-2">
            <dt className="text-slate-500">Conexión</dt>
            <dd className="flex items-center gap-1 font-semibold text-slate-200">
              {connection === 'online' ? <Wifi className="h-3 w-3" /> : <WifiOff className="h-3 w-3" />}
              {connectionLabel[connection]}
            </dd>
          </div>
          <div className="flex items-center justify-between gap-2">
            <dt className="text-slate-500">Escenario</dt>
            <dd className="font-semibold text-slate-200">{scenarioLabel[scenario]}</dd>
          </div>
          <div className="flex items-center justify-between gap-2">
            <dt className="text-slate-500">Pendientes</dt>
            <dd className={`font-semibold ${pending > 0 ? 'text-amber-300' : 'text-emerald-300'}`}>{pending}</dd>
          </div>
        </dl>
      </div>

      <p className="px-1 text-[10px] leading-relaxed text-slate-600">
        Prototipo visual sin backend. Los switches solo cambian la apariencia; no hay lógica real de red, GPS, cámara
        ni sincronización.
      </p>
    </aside>
  )
}
