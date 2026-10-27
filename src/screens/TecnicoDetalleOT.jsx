import { CheckCircle2, Clock, MapPin, Play, Thermometer, Wrench } from 'lucide-react'
import PhoneScreen from '../components/PhoneScreen'
import {
  Button,
  Card,
  OfflineBanner,
  PriorityBadge,
  StatusBadge,
  SuccessBanner,
} from '../components/ui'
import { OT, TECNICO } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function TecnicoDetalleOT() {
  const { go, otAccepted, setOtAccepted, checkinDone } = useDemo()

  const estado = checkinDone ? 'En ejecución' : otAccepted ? 'Aceptada' : 'Asignada'

  return (
    <PhoneScreen title={OT.id} subtitle="Detalle de la orden de trabajo">
      <div className="space-y-3">
        <Card className="p-3.5">
          <div className="flex items-center justify-between gap-2">
            <div className="flex items-center gap-1.5">
              <PriorityBadge priority={OT.prioridad} />
              <StatusBadge status={estado} />
            </div>
            <span className="text-[10px] font-semibold text-slate-400">{OT.tipo}</span>
          </div>
          <p className="mt-2.5 text-base font-bold leading-tight text-slate-900">{OT.cliente}</p>
          <p className="text-[11px] text-slate-500">{OT.local}</p>

          <div className="mt-3 space-y-2 border-t border-slate-100 pt-3 text-[11px]">
            <div className="flex items-start gap-2">
              <MapPin className="mt-0.5 h-3.5 w-3.5 shrink-0 text-slate-400" />
              <span className="text-slate-600">{OT.direccion}</span>
            </div>
            <div className="flex items-start gap-2">
              <Thermometer className="mt-0.5 h-3.5 w-3.5 shrink-0 text-slate-400" />
              <span className="text-slate-600">
                {OT.equipo} · {OT.equipoId}
              </span>
            </div>
            <div className="flex items-start gap-2">
              <Clock className="mt-0.5 h-3.5 w-3.5 shrink-0 text-slate-400" />
              <span className="text-slate-600">SLA restante: {OT.sla}</span>
            </div>
            <div className="flex items-start gap-2">
              <Wrench className="mt-0.5 h-3.5 w-3.5 shrink-0 text-slate-400" />
              <span className="text-slate-600">
                Técnico: {TECNICO.nombre}
              </span>
            </div>
          </div>
        </Card>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Descripción del cliente</p>
          <p className="mt-1.5 text-[11px] leading-relaxed text-slate-600">{OT.descripcion}</p>
        </Card>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Trazabilidad</p>
          <div className="mt-2.5 space-y-2">
            {[
              { label: 'Creada', done: true, time: '08:10' },
              { label: 'Asignada', done: true, time: '08:25' },
              { label: 'Aceptada', done: otAccepted, time: otAccepted ? '08:40' : '—' },
              { label: 'Check-in', done: checkinDone, time: checkinDone ? '09:41' : '—' },
            ].map((item, index, array) => (
              <div key={item.label} className="flex items-center gap-2.5">
                <div className="flex flex-col items-center">
                  <span
                    className={`grid h-4 w-4 place-items-center rounded-full ${
                      item.done ? 'bg-emerald-500 text-white' : 'border border-slate-300 bg-white'
                    }`}
                  >
                    {item.done && <CheckCircle2 className="h-3 w-3" />}
                  </span>
                  {index < array.length - 1 && <span className="h-4 w-px bg-slate-200" />}
                </div>
                <p className={`flex-1 text-[11px] ${item.done ? 'font-semibold text-slate-700' : 'text-slate-400'}`}>
                  {item.label}
                </p>
                <span className="text-[10px] text-slate-400">{item.time}</span>
              </div>
            ))}
          </div>
        </Card>

        <OfflineBanner />

        {otAccepted && !checkinDone && <SuccessBanner>OT aceptada. El siguiente paso es registrar el check-in.</SuccessBanner>}
        {checkinDone && <SuccessBanner>Check-in registrado. La OT está en ejecución.</SuccessBanner>}

        <div className="space-y-2 pt-1">
          {!otAccepted && (
            <Button size="lg" className="w-full" onClick={() => setOtAccepted(true)}>
              <CheckCircle2 className="h-4 w-4" />
              Aceptar OT
            </Button>
          )}
          {otAccepted && !checkinDone && (
            <Button size="lg" className="w-full" onClick={() => go('checkin')}>
              <MapPin className="h-4 w-4" />
              Iniciar check-in
            </Button>
          )}
          {checkinDone && (
            <Button size="lg" className="w-full" onClick={() => go('ejecucion')}>
              <Play className="h-4 w-4" />
              Continuar ejecución
            </Button>
          )}
          <Button variant="outline" size="md" className="w-full" onClick={() => go('jornada')}>
            Volver a mi jornada
          </Button>
        </div>
      </div>
    </PhoneScreen>
  )
}
