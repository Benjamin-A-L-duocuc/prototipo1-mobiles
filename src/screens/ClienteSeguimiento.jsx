import {
  CheckCircle2,
  ClipboardCheck,
  FileText,
  Phone,
  ShieldCheck,
  XCircle,
} from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import SignaturePad from '../components/SignaturePad'
import {
  Button,
  Card,
  PhotoTile,
  PriorityBadge,
  StatusBadge,
  SuccessBanner,
  Textarea,
} from '../components/ui'
import { CLIENTE, OT, TECNICO, TIMELINE } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function ClienteSeguimiento() {
  const { scenario, signed, setSigned, rejected, setRejected, photos } = useDemo()
  const [conforme, setConforme] = useState(true)
  const [motivo, setMotivo] = useState('El equipo quedó operativo, pero solicito revisar el sensor de temperatura.')
  const [reporte, setReporte] = useState(false)

  const estado = signed ? 'Conformada' : rejected ? 'Rechazada' : 'Cerrada'

  const timeline = TIMELINE.map((item) => {
    if (item.hito !== 'Conformada') return item
    return {
      ...item,
      hito: rejected ? 'Rechazada' : 'Conformada',
      fecha: signed || rejected ? '27/10 · 10:40' : 'Pendiente',
      hecho: signed || rejected,
    }
  })

  return (
    <PhoneScreen title="Seguimiento OT" subtitle={`${OT.id} · ${OT.cliente}`}>
      <div className="space-y-3">
        <div className="rounded-2xl bg-gradient-to-br from-slate-900 to-slate-800 p-3.5 text-white shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-1.5">
              <PriorityBadge priority={OT.prioridad} />
              <StatusBadge status={estado} />
            </div>
            <span className="text-[10px] font-semibold text-slate-400">{OT.id}</span>
          </div>
          <p className="mt-2.5 text-base font-bold">{OT.cliente}</p>
          <p className="text-[11px] text-slate-400">{OT.local}</p>
          <div className="mt-3 grid grid-cols-3 gap-2 border-t border-white/10 pt-2.5 text-[10px]">
            <div>
              <p className="text-slate-500">Equipo</p>
              <p className="mt-0.5 font-semibold text-slate-200">Cámara frigorífica</p>
            </div>
            <div>
              <p className="text-slate-500">Técnico</p>
              <p className="mt-0.5 font-semibold text-slate-200">Cristian R.</p>
            </div>
            <div>
              <p className="text-slate-500">SLA</p>
              <p className="mt-0.5 font-semibold text-emerald-300">Cumplido</p>
            </div>
          </div>
        </div>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Estado de la OT</p>
          <div className="mt-2.5 space-y-2">
            {timeline.map((item, index, array) => (
              <div key={item.hito} className="flex items-center gap-2.5">
                <div className="flex flex-col items-center">
                  <span
                    className={`grid h-4 w-4 place-items-center rounded-full ${
                      item.hecho ? 'bg-emerald-500 text-white' : 'border border-slate-300 bg-white'
                    }`}
                  >
                    {item.hecho && <CheckCircle2 className="h-3 w-3" />}
                  </span>
                  {index < array.length - 1 && <span className="h-4 w-px bg-slate-200" />}
                </div>
                <p className={`flex-1 text-[11px] ${item.hecho ? 'font-semibold text-slate-700' : 'text-slate-400'}`}>
                  {item.hito}
                </p>
                <span className="text-[9px] text-slate-400">{item.fecha}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="p-3.5">
          <div className="flex items-center gap-2.5">
            <div className="grid h-9 w-9 place-items-center rounded-full bg-sky-100 text-[10px] font-bold text-sky-700">
              {TECNICO.iniciales}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-[11px] font-bold text-slate-800">{TECNICO.nombre}</p>
              <p className="text-[9px] text-slate-400">{TECNICO.rol}</p>
            </div>
            <button
              type="button"
              className="grid h-8 w-8 place-items-center rounded-full bg-slate-100 text-slate-500"
              aria-label="Llamar al técnico"
            >
              <Phone className="h-3.5 w-3.5" />
            </button>
          </div>
        </Card>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Evidencias</p>
          <div className="mt-2 grid grid-cols-2 gap-2">
            {Array.from({ length: Math.min(photos, 4) }).map((_, index) => (
              <PhotoTile key={index} index={index + 1} />
            ))}
          </div>
        </Card>

        <Card className="p-3.5">
          <div className="flex items-center justify-between">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Reporte de intervención</p>
            <FileText className="h-4 w-4 text-slate-400" />
          </div>
          <p className="mt-2 text-[10px] leading-relaxed text-slate-500">
            Diagnóstico: baja presión en circuito de refrigerante, corregida con recarga y limpieza de condensador.
          </p>
          <div className="mt-2 flex items-center gap-2 text-[9px] text-slate-400">
            <span className="rounded-full bg-slate-100 px-2 py-0.5">4 actividades</span>
            <span className="rounded-full bg-slate-100 px-2 py-0.5">3 materiales</span>
            <span className="rounded-full bg-slate-100 px-2 py-0.5">{photos} fotos</span>
          </div>
          <Button variant="outline" size="sm" className="mt-2.5 w-full" onClick={() => setReporte((value) => !value)}>
            <FileText className="h-3.5 w-3.5" />
            {reporte ? 'Reporte generado (demo)' : 'Descargar reporte'}
          </Button>
        </Card>

        {signed ? (
          <Card className="border-emerald-200 p-3.5">
            <SuccessBanner>Trabajo validado como conforme por {CLIENTE.nombre}.</SuccessBanner>
            <div className="mt-2 rounded-xl border border-emerald-200 bg-emerald-50/50 p-3">
              <p className="font-[cursive] text-xl text-slate-700">María Soto</p>
              <p className="mt-1 text-[9px] text-slate-500">Firma registrada · 27/10 · 10:40</p>
            </div>
          </Card>
        ) : scenario === 'rechazo' ? (
          <Card className="border-rose-200 p-3.5">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">
              Variante: cliente rechaza
            </p>
            {rejected ? (
              <div className="mt-2 space-y-2">
                <div className="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-3 py-2.5 text-[11px] text-rose-800">
                  <XCircle className="mt-0.5 h-4 w-4 shrink-0" />
                  <span>Rechazo registrado. El jefe de mantenimiento revisará el caso y coordinará una nueva visita.</span>
                </div>
                <Button variant="outline" size="md" className="w-full" onClick={() => setRejected(false)}>
                  Volver a la variante de conformidad
                </Button>
              </div>
            ) : (
              <div className="mt-2 space-y-2">
                <Textarea
                  value={motivo}
                  onChange={(event) => setMotivo(event.target.value)}
                  className="min-h-20 text-[11px]"
                />
                <Button variant="danger" size="lg" className="w-full" onClick={() => setRejected(true)}>
                  <XCircle className="h-4 w-4" />
                  Registrar rechazo
                </Button>
              </div>
            )}
          </Card>
        ) : (
          <Card className="p-3.5">
            <div className="flex items-center gap-2">
              <ClipboardCheck className="h-4 w-4 text-slate-400" />
              <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">
                Validar conformidad
              </p>
            </div>
            <div className="mt-2 space-y-2.5">
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
                <ShieldCheck className="h-4 w-4" />
                Firmar conformidad
              </Button>
            </div>
          </Card>
        )}

        <p className="flex items-center justify-center gap-1.5 pb-2 text-[10px] text-slate-400">
          <CheckCircle2 className="h-3 w-3" />
          {signed ? 'Ciclo de la OT completado' : 'La OT se cerró y espera tu validación'}
        </p>
      </div>
    </PhoneScreen>
  )
}
