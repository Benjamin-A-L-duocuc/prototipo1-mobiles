import { CheckCircle2, LocateFixed, MapPin, Navigation, Play } from 'lucide-react'
import MiniMap from '../components/MiniMap'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, OfflineBanner, SuccessBanner } from '../components/ui'
import { OT } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function TecnicoCheckIn() {
  const { go, checkinDone, setCheckinDone } = useDemo()

  return (
    <PhoneScreen title="Check-in" subtitle={`${OT.id} · ${OT.cliente}`}>
      <div className="space-y-3">
        <MiniMap pins={1} className="h-44" />

        <Card className="p-3.5">
          <div className="flex items-center gap-2">
            <div className="grid h-9 w-9 place-items-center rounded-xl bg-emerald-50 text-emerald-600">
              <LocateFixed className="h-4 w-4" />
            </div>
            <div className="flex-1">
              <p className="text-xs font-bold text-slate-900">Ubicación capturada</p>
              <p className="text-[10px] text-slate-500">Precisión ±8 m · GPS simulado</p>
            </div>
            <span className="rounded-full bg-emerald-50 px-2 py-0.5 text-[9px] font-bold text-emerald-700 ring-1 ring-inset ring-emerald-200">
              OK
            </span>
          </div>
          <div className="mt-3 grid grid-cols-2 gap-2 border-t border-slate-100 pt-3 text-[11px]">
            <div>
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Latitud</p>
              <p className="font-semibold text-slate-700">-33.4569</p>
            </div>
            <div>
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Longitud</p>
              <p className="font-semibold text-slate-700">-70.6483</p>
            </div>
            <div>
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Hora</p>
              <p className="font-semibold text-slate-700">09:41</p>
            </div>
            <div>
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Dirección</p>
              <p className="truncate font-semibold text-slate-700">{OT.direccion}</p>
            </div>
          </div>
        </Card>

        <OfflineBanner>
          Sin conexión: el check-in quedará en la cola local y se enviará al recuperar señal.
        </OfflineBanner>

        {checkinDone && (
          <SuccessBanner>Check-in registrado a las 09:41. Ya puedes comenzar la ejecución.</SuccessBanner>
        )}

        <div className="space-y-2 pt-1">
          {!checkinDone ? (
            <Button size="lg" className="w-full" onClick={() => setCheckinDone(true)}>
              <MapPin className="h-4 w-4" />
              Registrar check-in
            </Button>
          ) : (
            <Button size="lg" className="w-full" onClick={() => go('ejecucion')}>
              <Play className="h-4 w-4" />
              Continuar a ejecución
            </Button>
          )}
          <Button variant="outline" size="md" className="w-full" onClick={() => go('detalle')}>
            <Navigation className="h-4 w-4" />
            Volver al detalle
          </Button>
        </div>

        <p className="flex items-center justify-center gap-1.5 pb-2 text-[10px] text-slate-400">
          <CheckCircle2 className="h-3 w-3" />
          El check-in marca el inicio del tiempo de atención.
        </p>
      </div>
    </PhoneScreen>
  )
}
