import {
  Activity,
  AlertTriangle,
  ArrowRight,
  CalendarDays,
  CheckCircle2,
  ClipboardList,
  CloudRain,
  Plus,
  ShieldCheck,
  Timer,
  Users,
} from 'lucide-react'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, PriorityBadge, Stat, StatusBadge } from '../components/ui'
import { OT_PRIORITARIAS } from '../data/mock'
import { useDemo } from '../state/DemoContext'

const barras = [
  { label: 'Creadas', value: 4, color: 'bg-slate-400' },
  { label: 'Asignadas', value: 5, color: 'bg-sky-500' },
  { label: 'En ejecución', value: 2, color: 'bg-amber-500' },
  { label: 'Cerradas', value: 8, color: 'bg-emerald-500' },
]

export default function JefeDashboard() {
  const { go, scenario } = useDemo()

  return (
    <PhoneScreen title="Panel de mantenimiento" subtitle="Jueves 27 de octubre">
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Resumen operativo</p>
            <p className="text-sm font-bold text-slate-900">Hola, Gerardo</p>
          </div>
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-900 text-[11px] font-bold text-white">
            GG
          </div>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <Stat label="OT abiertas" value="12" icon={ClipboardList} tone="sky" />
          <Stat label="SLA en riesgo" value="3" icon={Timer} tone="rose" />
          <Stat label="Técnicos activos" value="5" icon={Users} tone="emerald" />
          <Stat label="Cerradas hoy" value="8" icon={CheckCircle2} tone="violet" />
        </div>

        {scenario === 'lluvia' && (
          <div className="rounded-2xl border border-amber-200 bg-amber-50 p-3.5">
            <div className="flex items-start gap-2.5">
              <div className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-amber-100 text-amber-700">
                <CloudRain className="h-5 w-5" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-xs font-bold text-amber-900">Alerta climática 15:00–18:00</p>
                <p className="mt-0.5 text-[10px] leading-snug text-amber-800">
                  Lluvia y viento fuerte previstos. 3 visitas con equipos en azotea o patio podrían reprogramarse.
                </p>
                <button
                  type="button"
                  onClick={() => go('agenda')}
                  className="mt-2 inline-flex items-center gap-1 rounded-lg bg-amber-500 px-2.5 py-1.5 text-[10px] font-bold text-white"
                >
                  Ver agenda
                  <ArrowRight className="h-3 w-3" />
                </button>
              </div>
            </div>
          </div>
        )}

        <div className="grid grid-cols-2 gap-2">
          <Button size="md" className="w-full" onClick={() => go('crear')}>
            <Plus className="h-4 w-4" />
            Crear OT
          </Button>
          <Button variant="outline" size="md" className="w-full" onClick={() => go('agenda')}>
            <CalendarDays className="h-4 w-4" />
            Agenda
          </Button>
        </div>

        <Card className="p-3.5">
          <div className="flex items-center justify-between">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Cumplimiento SLA</p>
            <ShieldCheck className="h-4 w-4 text-emerald-500" />
          </div>
          <div className="mt-2.5 flex items-center gap-4">
            <div
              className="relative h-16 w-16 shrink-0 rounded-full"
              style={{ background: 'conic-gradient(#10b981 0 92%, #e2e8f0 92% 100%)' }}
            >
              <div className="absolute inset-1.5 grid place-items-center rounded-full bg-white">
                <span className="text-sm font-extrabold text-slate-900">92%</span>
              </div>
            </div>
            <div className="min-w-0 flex-1 space-y-1">
              <p className="text-[11px] font-semibold text-slate-700">Cumplimiento del mes</p>
              <p className="text-[10px] text-slate-500">23 de 25 OT dentro del plazo pactado.</p>
              <p className="text-[10px] font-medium text-emerald-600">+4% vs. mes anterior</p>
            </div>
          </div>
        </Card>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">OT por estado</p>
          <div className="mt-2.5 space-y-2">
            {barras.map((barra) => (
              <div key={barra.label} className="flex items-center gap-2">
                <span className="w-20 text-[10px] text-slate-500">{barra.label}</span>
                <div className="h-2 flex-1 overflow-hidden rounded-full bg-slate-100">
                  <div className={`h-full rounded-full ${barra.color}`} style={{ width: `${(barra.value / 10) * 100}%` }} />
                </div>
                <span className="w-4 text-right text-[10px] font-bold text-slate-600">{barra.value}</span>
              </div>
            ))}
          </div>
        </Card>

        <div className="flex items-center justify-between">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">OT prioritarias</p>
          <span className="inline-flex items-center gap-1 text-[9px] font-semibold text-rose-500">
            <AlertTriangle className="h-3 w-3" />
            3 en riesgo
          </span>
        </div>

        <div className="space-y-2">
          {OT_PRIORITARIAS.map((ot) => (
            <Card key={ot.id} className="flex items-center gap-2.5 p-3">
              <div className="grid h-8 w-8 shrink-0 place-items-center rounded-lg bg-slate-100 text-slate-500">
                <Activity className="h-4 w-4" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="truncate text-[11px] font-bold text-slate-800">{ot.cliente}</p>
                <p className="text-[9px] text-slate-400">
                  {ot.id} · SLA {ot.sla}
                </p>
              </div>
              <div className="flex shrink-0 flex-col items-end gap-1">
                <PriorityBadge priority={ot.prioridad} />
                <StatusBadge status={ot.estado} />
              </div>
            </Card>
          ))}
        </div>
      </div>
    </PhoneScreen>
  )
}
