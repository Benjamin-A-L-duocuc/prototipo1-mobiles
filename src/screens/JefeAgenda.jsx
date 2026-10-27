import { CloudRain, RefreshCw, Sun, Users } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import { Badge, Button, Card, PriorityBadge } from '../components/ui'
import { AGENDA_HOY } from '../data/mock'
import { useDemo } from '../state/DemoContext'

const dias = [
  { value: 'lun', label: 'Lun', numero: '27' },
  { value: 'mar', label: 'Mar', numero: '28' },
  { value: 'mie', label: 'Mié', numero: '29' },
  { value: 'jue', label: 'Jue', numero: '30' },
  { value: 'vie', label: 'Vie', numero: '31' },
]

const carga = [
  { nombre: 'Cristian Rojas', visitas: 5, max: 6 },
  { nombre: 'Paula Díaz', visitas: 4, max: 6 },
  { nombre: 'Diego Soto', visitas: 6, max: 6 },
]

export default function JefeAgenda() {
  const { go, scenario } = useDemo()
  const [dia, setDia] = useState('lun')
  const [reprogramadas, setReprogramadas] = useState(false)
  const lluvia = scenario === 'lluvia'

  return (
    <PhoneScreen title="Agenda" subtitle="Semana 27–31 de octubre">
      <div className="space-y-3">
        <div className="grid grid-cols-5 gap-1.5">
          {dias.map((item) => {
            const active = item.value === dia
            return (
              <button
                key={item.value}
                type="button"
                onClick={() => setDia(item.value)}
                className={`rounded-xl border px-1 py-2 text-center transition ${
                  active
                    ? 'border-sky-500 bg-sky-50 text-sky-700'
                    : 'border-slate-200 bg-white text-slate-500 hover:border-slate-300'
                }`}
              >
                <p className="text-[9px] font-semibold uppercase">{item.label}</p>
                <p className="text-sm font-bold">{item.numero}</p>
              </button>
            )
          })}
        </div>

        {lluvia && (
          <div className="rounded-2xl border border-amber-200 bg-amber-50 p-3.5">
            <div className="flex items-start gap-2.5">
              <div className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-amber-100 text-amber-700">
                <CloudRain className="h-5 w-5" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-xs font-bold text-amber-900">2 visitas con equipos exteriores en riesgo</p>
                <p className="mt-0.5 text-[10px] leading-snug text-amber-800">
                  Lluvia y viento fuerte entre 15:00 y 18:00. Reprograma o adelanta estas visitas.
                </p>
                <Button
                  variant={reprogramadas ? 'outline' : 'primary'}
                  size="sm"
                  className="mt-2"
                  onClick={() => setReprogramadas((value) => !value)}
                >
                  <RefreshCw className="h-3.5 w-3.5" />
                  {reprogramadas ? 'Reprogramadas' : 'Reprogramar visitas'}
                </Button>
              </div>
            </div>
          </div>
        )}

        <div className="flex items-center justify-between">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Visitas del día</p>
          <Badge tone="slate">{AGENDA_HOY.length} programadas</Badge>
        </div>

        <div className="space-y-2">
          {AGENDA_HOY.map((visita) => {
            const enRiesgo = lluvia && visita.exterior
            return (
              <Card
                key={`${visita.hora}-${visita.ot}`}
                className={`p-3 ${enRiesgo ? 'border-amber-300 bg-amber-50/40' : ''}`}
              >
                <div className="flex items-start gap-2.5">
                  <div className="w-9 shrink-0 text-center">
                    <p className="text-[10px] font-bold text-slate-700">{visita.hora}</p>
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-1.5">
                      <p className="truncate text-[11px] font-bold text-slate-800">{visita.cliente}</p>
                      <PriorityBadge priority={visita.prioridad} />
                    </div>
                    <p className="text-[9px] text-slate-400">
                      {visita.ot} · {visita.tecnico}
                    </p>
                    <div className="mt-1 flex items-center gap-1.5">
                      {visita.exterior ? (
                        <span className="inline-flex items-center gap-1 text-[9px] font-medium text-amber-700">
                          <CloudRain className="h-3 w-3" />
                          Equipo exterior
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-[9px] font-medium text-slate-400">
                          <Sun className="h-3 w-3" />
                          Interior
                        </span>
                      )}
                      {enRiesgo && reprogramadas && (
                        <span className="rounded-full bg-amber-100 px-1.5 py-0.5 text-[8px] font-bold text-amber-800">
                          Por reprogramar
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </Card>
            )
          })}
        </div>

        <Card className="p-3.5">
          <div className="flex items-center gap-2">
            <Users className="h-4 w-4 text-slate-400" />
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Carga por técnico</p>
          </div>
          <div className="mt-2.5 space-y-2">
            {carga.map((tecnico) => (
              <div key={tecnico.nombre} className="flex items-center gap-2">
                <span className="w-24 truncate text-[10px] text-slate-600">{tecnico.nombre}</span>
                <div className="h-2 flex-1 overflow-hidden rounded-full bg-slate-100">
                  <div
                    className={`h-full rounded-full ${tecnico.visitas >= tecnico.max ? 'bg-rose-500' : 'bg-sky-500'}`}
                    style={{ width: `${(tecnico.visitas / tecnico.max) * 100}%` }}
                  />
                </div>
                <span className="w-8 text-right text-[9px] font-bold text-slate-500">
                  {tecnico.visitas}/{tecnico.max}
                </span>
              </div>
            ))}
          </div>
        </Card>

        <Button variant="outline" size="md" className="w-full" onClick={() => go('dashboard')}>
          Volver al panel
        </Button>
      </div>
    </PhoneScreen>
  )
}
