import { CheckCircle2, CloudOff, PenLine, RefreshCw, XCircle } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import SignaturePad from '../components/SignaturePad'
import { Button, Card, OfflineBanner, SuccessBanner, Textarea } from '../components/ui'
import { CLIENTE, OT } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function TecnicoCierre() {
  const { go, scenario, signed, setSigned, rejected, setRejected, photos, pending } = useDemo()
  const [conforme, setConforme] = useState(true)
  const [motivo, setMotivo] = useState('El equipo quedó operativo, pero solicito revisión del sensor de temperatura.')

  return (
    <PhoneScreen title="Cierre de OT" subtitle={`${OT.id} · ${OT.cliente}`}>
      <div className="space-y-3">
        <OfflineBanner>
          La firma y el cierre se guardarán en el dispositivo hasta que se recupere la conexión.
        </OfflineBanner>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Resumen de la intervención</p>
          <div className="mt-2 grid grid-cols-2 gap-2 text-[11px]">
            <div className="rounded-lg bg-slate-50 p-2">
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Check-in</p>
              <p className="font-semibold text-slate-700">09:41</p>
            </div>
            <div className="rounded-lg bg-slate-50 p-2">
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Check-out</p>
              <p className="font-semibold text-slate-700">10:35</p>
            </div>
            <div className="rounded-lg bg-slate-50 p-2">
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Actividades</p>
              <p className="font-semibold text-slate-700">4 registradas</p>
            </div>
            <div className="rounded-lg bg-slate-50 p-2">
              <p className="text-[9px] uppercase tracking-wide text-slate-400">Evidencias</p>
              <p className="font-semibold text-slate-700">{photos} fotografías</p>
            </div>
          </div>
          <p className="mt-2 text-[10px] leading-relaxed text-slate-500">
            Diagnóstico: baja presión en circuito de refrigerante, corregida con recarga y limpieza de condensador.
          </p>
        </Card>

        {scenario === 'rechazo' ? (
          <Card className="p-3.5">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">
              Variante: el cliente rechaza
            </p>
            <Textarea
              value={motivo}
              onChange={(event) => setMotivo(event.target.value)}
              className="mt-2 min-h-20 text-[11px]"
            />
            {rejected ? (
              <div className="mt-2.5 space-y-2">
                <div className="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-3 py-2.5 text-[11px] text-rose-800">
                  <XCircle className="mt-0.5 h-4 w-4 shrink-0" />
                  <span>Rechazo registrado. El jefe de mantenimiento fue notificado para revisar el caso.</span>
                </div>
                <Button variant="outline" size="md" className="w-full" onClick={() => go('jornada')}>
                  Volver a mi jornada
                </Button>
              </div>
            ) : (
              <Button variant="danger" size="lg" className="mt-2.5 w-full" onClick={() => setRejected(true)}>
                <XCircle className="h-4 w-4" />
                Registrar rechazo
              </Button>
            )}
          </Card>
        ) : (
          <Card className="p-3.5">
            <div className="flex items-center justify-between">
              <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Conformidad del cliente</p>
              <span className="text-[10px] font-semibold text-slate-500">{CLIENTE.nombre}</span>
            </div>

            {signed ? (
              <div className="mt-2.5 space-y-2">
                <SuccessBanner>OT cerrada y firmada por {CLIENTE.nombre} a las 10:36.</SuccessBanner>
                <div className="rounded-xl border border-emerald-200 bg-emerald-50/50 p-3">
                  <p className="font-[cursive] text-xl text-slate-700">María Soto</p>
                  <p className="mt-1 text-[9px] text-slate-500">Firma registrada · {CLIENTE.cargo}</p>
                </div>
                <Button size="lg" className="w-full" onClick={() => go('sync')}>
                  <RefreshCw className="h-4 w-4" />
                  Ir a sincronización {pending > 0 ? `(${pending})` : ''}
                </Button>
              </div>
            ) : (
              <div className="mt-2.5 space-y-2.5">
                <SignaturePad />
                <label className="flex items-start gap-2 text-[10px] text-slate-600">
                  <input
                    type="checkbox"
                    checked={conforme}
                    onChange={(event) => setConforme(event.target.checked)}
                    className="mt-0.5 h-3.5 w-3.5 rounded border-slate-300 text-sky-600"
                  />
                  Declaro que el trabajo fue realizado y el equipo quedó operativo.
                </label>
                <Button size="lg" className="w-full" disabled={!conforme} onClick={() => setSigned(true)}>
                  <PenLine className="h-4 w-4" />
                  Finalizar OT
                </Button>
              </div>
            )}
          </Card>
        )}

        {signed && (
          <p
            className={`flex items-center justify-center gap-1.5 text-[10px] ${
              pending > 0 ? 'text-amber-600' : 'text-emerald-600'
            }`}
          >
            {pending > 0 ? <CloudOff className="h-3 w-3" /> : <CheckCircle2 className="h-3 w-3" />}
            {pending > 0 ? 'Cierre pendiente de sincronizar' : 'Cierre sincronizado'}
          </p>
        )}
      </div>
    </PhoneScreen>
  )
}
