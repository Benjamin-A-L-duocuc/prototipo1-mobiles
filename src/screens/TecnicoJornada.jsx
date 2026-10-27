import {
  AlertTriangle,
  ChevronRight,
  Clock,
  CloudRain,
  List,
  Map,
  MapPin,
  RefreshCw,
  Thermometer,
} from 'lucide-react'
import { useState } from 'react'
import MiniMap from '../components/MiniMap'
import PhoneScreen from '../components/PhoneScreen'
import { Card, OfflineBanner, PriorityBadge, Segmented, StatusBadge } from '../components/ui'
import { OTS, OT as OT_BASE, TECNICO } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function TecnicoJornada() {
  const { go, scenario, pending, otAccepted, checkinDone, connection } = useDemo()
  const [view, setView] = useState('lista')

  function estadoFor(ot) {
    if (ot.id !== OT_BASE.id) return ot.estado
    if (checkinDone) return 'En ejecución'
    if (otAccepted) return 'Aceptada'
    return 'Asignada'
  }

  return (
    <PhoneScreen title="Mi jornada" subtitle="Jueves 27 de octubre">
      <div className="space-y-3">
        <div className="flex items-center gap-3 rounded-2xl bg-gradient-to-r from-sky-600 to-cyan-500 p-3.5 text-white shadow-sm">
          <div className="grid h-11 w-11 place-items-center rounded-full bg-white/20 text-sm font-bold">
            {TECNICO.iniciales}
          </div>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-bold">Hola, Cristian</p>
            <p className="text-[11px] text-sky-50/90">5 visitas hoy · 2 con equipo exterior</p>
          </div>
          <Thermometer className="h-5 w-5 text-sky-100/80" />
        </div>

        <OfflineBanner />

        {scenario === 'lluvia' && (
          <div className="flex items-start gap-2 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2.5 text-[11px] leading-snug text-amber-800">
            <CloudRain className="mt-0.5 h-4 w-4 shrink-0" />
            <span>
              Lluvia prevista a las 15:00. Tienes 2 visitas con equipos en azotea o patio. La jefatura puede
              reprogramarlas.
            </span>
          </div>
        )}

        <div className="flex items-center justify-between">
          <Segmented
            value={view}
            onChange={setView}
            options={[
              { value: 'lista', label: 'Lista', icon: List },
              { value: 'mapa', label: 'Mapa', icon: Map },
            ]}
          />
          <span className="text-[10px] font-medium text-slate-400">4 OT</span>
        </div>

        {view === 'mapa' ? (
          <div className="relative">
            <MiniMap pins={3} className="h-56" />
            <div className="absolute left-3 top-3 rounded-xl border border-white/70 bg-white/90 px-3 py-2 shadow-sm backdrop-blur">
              <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Ruta de hoy</p>
              <p className="text-xs font-bold text-slate-900">3 OT cerca de ti</p>
            </div>
          </div>
        ) : (
          <div className="space-y-2.5">
            {OTS.map((ot) => (
              <button key={ot.id} type="button" onClick={() => go('detalle')} className="block w-full text-left">
                <Card className="p-3.5 transition hover:border-sky-300 hover:shadow-md">
                  <div className="flex items-center justify-between gap-2">
                    <div className="flex items-center gap-1.5">
                      <PriorityBadge priority={ot.prioridad} />
                      <StatusBadge status={estadoFor(ot)} />
                    </div>
                    <span className="text-[10px] font-bold text-slate-400">{ot.id}</span>
                  </div>
                  <p className="mt-2 text-sm font-bold leading-tight text-slate-900">{ot.cliente}</p>
                  <p className="text-[11px] text-slate-500">{ot.equipo}</p>
                  <div className="mt-2.5 flex items-center gap-3 text-[10px] font-medium text-slate-500">
                    <span className="inline-flex items-center gap-1">
                      <Clock className="h-3 w-3" />
                      SLA {ot.sla}
                    </span>
                    <span className="inline-flex items-center gap-1">
                      <MapPin className="h-3 w-3" />
                      {ot.distancia}
                    </span>
                    {ot.exterior && (
                      <span className="inline-flex items-center gap-1 text-amber-600">
                        <AlertTriangle className="h-3 w-3" />
                        Exterior
                      </span>
                    )}
                  </div>
                  <div className="mt-3 flex items-center justify-between border-t border-slate-100 pt-2">
                    <span className="text-[10px] text-slate-400">Toca para ver el detalle</span>
                    <ChevronRight className="h-4 w-4 text-slate-400" />
                  </div>
                </Card>
              </button>
            ))}
          </div>
        )}

        <button
          type="button"
          onClick={() => go('sync')}
          className="flex w-full items-center justify-between rounded-2xl border border-slate-200 bg-white p-3.5 text-left shadow-sm transition hover:border-sky-300"
        >
          <div className="flex items-center gap-3">
            <div
              className={`grid h-9 w-9 place-items-center rounded-xl ${
                pending > 0 ? 'bg-amber-50 text-amber-600' : 'bg-emerald-50 text-emerald-600'
              }`}
            >
              <RefreshCw className={`h-4 w-4 ${pending > 0 && connection === 'offline' ? 'animate-soft-pulse' : ''}`} />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-900">Centro de sincronización</p>
              <p className="text-[10px] text-slate-500">
                {pending > 0 ? `${pending} registros pendientes` : 'Todo sincronizado'}
              </p>
            </div>
          </div>
          <ChevronRight className="h-4 w-4 text-slate-400" />
        </button>
      </div>
    </PhoneScreen>
  )
}
