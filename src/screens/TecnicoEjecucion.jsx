import { Camera, Check, ChevronRight, Minus, Plus } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, OfflineBanner, Textarea } from '../components/ui'
import { ACTIVIDADES, MATERIALES, OT } from '../data/mock'
import { useDemo } from '../state/DemoContext'

const steps = [
  { value: 'diagnostico', label: 'Diagnóstico' },
  { value: 'actividades', label: 'Actividades' },
  { value: 'materiales', label: 'Materiales' },
  { value: 'evidencias', label: 'Evidencias' },
]

export default function TecnicoEjecucion() {
  const { go } = useDemo()
  const [step, setStep] = useState('diagnostico')
  const [diagnostico, setDiagnostico] = useState(
    'Se detecta baja presión en el circuito de refrigerante y acumulación de polvo en el condensador. Se recomienda recarga y limpieza.',
  )
  const [actividades, setActividades] = useState(ACTIVIDADES.map((nombre, index) => ({ nombre, done: index < 2 })))
  const [materiales, setMateriales] = useState(MATERIALES)

  function toggleActividad(index) {
    setActividades((items) => items.map((item, i) => (i === index ? { ...item, done: !item.done } : item)))
  }

  function addActividad() {
    setActividades((items) => [...items, { nombre: 'Nueva actividad registrada', done: true }])
  }

  function changeCantidad(index, delta) {
    setMateriales((items) =>
      items.map((item, i) => (i === index ? { ...item, cantidad: Math.max(0, item.cantidad + delta) } : item)),
    )
  }

  function addMaterial() {
    setMateriales((items) => [...items, { nombre: 'Filtro de aire', cantidad: 1, unidad: 'un' }])
  }

  const doneCount = step === 'diagnostico' ? 1 : step === 'actividades' ? 2 : 3

  return (
    <PhoneScreen title="Ejecución" subtitle={`${OT.id} · En ejecución`}>
      <div className="space-y-3">
        <OfflineBanner />

        <div>
          <div className="mb-2 flex items-center justify-between text-[10px] font-medium text-slate-400">
            <span>Paso {doneCount} de 4</span>
            <span>{Math.round((doneCount / 4) * 100)}%</span>
          </div>
          <div className="h-1.5 overflow-hidden rounded-full bg-slate-200">
            <div
              className="h-full rounded-full bg-sky-500 transition-all"
              style={{ width: `${(doneCount / 4) * 100}%` }}
            />
          </div>
          <div className="mt-3 flex items-start justify-between gap-1">
            {steps.map((item, index) => {
              const active = item.value === step
              const complete = index < doneCount - 1
              return (
                <button
                  key={item.value}
                  type="button"
                  onClick={() => (item.value === 'evidencias' ? go('evidencias') : setStep(item.value))}
                  className="flex flex-1 flex-col items-center gap-1"
                >
                  <span
                    className={`grid h-7 w-7 place-items-center rounded-full text-[10px] font-bold transition ${
                      active
                        ? 'bg-sky-600 text-white shadow-sm'
                        : complete
                          ? 'bg-emerald-100 text-emerald-700'
                          : 'bg-slate-100 text-slate-400'
                    }`}
                  >
                    {complete ? <Check className="h-3.5 w-3.5" /> : index + 1}
                  </span>
                  <span className={`text-[9px] font-semibold ${active ? 'text-sky-700' : 'text-slate-400'}`}>
                    {item.label}
                  </span>
                </button>
              )
            })}
          </div>
        </div>

        {step === 'diagnostico' && (
          <Card className="p-3.5">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Diagnóstico técnico</p>
            <Textarea
              value={diagnostico}
              onChange={(event) => setDiagnostico(event.target.value)}
              className="mt-2 min-h-32 text-[11px]"
            />
            <div className="mt-2.5 flex flex-wrap gap-1.5">
              {['Fuga de refrigerante', 'Compresor', 'Evaporador', 'Sensor'].map((tag) => (
                <span
                  key={tag}
                  className="rounded-full border border-slate-200 bg-slate-50 px-2 py-1 text-[9px] font-medium text-slate-500"
                >
                  {tag}
                </span>
              ))}
            </div>
            <p className="mt-3 text-[10px] text-emerald-600">Guardado local · pendiente de sincronizar</p>
          </Card>
        )}

        {step === 'actividades' && (
          <Card className="p-3.5">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Actividades realizadas</p>
            <div className="mt-2.5 space-y-2">
              {actividades.map((item, index) => (
                <button
                  key={`${item.nombre}-${index}`}
                  type="button"
                  onClick={() => toggleActividad(index)}
                  className="flex w-full items-center gap-2.5 rounded-xl border border-slate-200 bg-slate-50 px-2.5 py-2 text-left"
                >
                  <span
                    className={`grid h-4 w-4 shrink-0 place-items-center rounded ${
                      item.done ? 'bg-emerald-500 text-white' : 'border border-slate-300 bg-white'
                    }`}
                  >
                    {item.done && <Check className="h-3 w-3" />}
                  </span>
                  <span className={`text-[11px] ${item.done ? 'text-slate-700' : 'text-slate-400'}`}>
                    {item.nombre}
                  </span>
                </button>
              ))}
            </div>
            <Button variant="outline" size="sm" className="mt-3 w-full" onClick={addActividad}>
              <Plus className="h-3.5 w-3.5" />
              Agregar actividad
            </Button>
          </Card>
        )}

        {step === 'materiales' && (
          <Card className="p-3.5">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Materiales utilizados</p>
            <div className="mt-2.5 space-y-2">
              {materiales.map((item, index) => (
                <div key={item.nombre} className="flex items-center gap-2 rounded-xl border border-slate-200 p-2.5">
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-[11px] font-semibold text-slate-700">{item.nombre}</p>
                    <p className="text-[9px] text-slate-400">
                      {item.cantidad} {item.unidad}
                    </p>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      type="button"
                      onClick={() => changeCantidad(index, -1)}
                      className="grid h-6 w-6 place-items-center rounded-lg bg-slate-100 text-slate-600"
                    >
                      <Minus className="h-3 w-3" />
                    </button>
                    <span className="w-5 text-center text-[11px] font-bold text-slate-700">{item.cantidad}</span>
                    <button
                      type="button"
                      onClick={() => changeCantidad(index, 1)}
                      className="grid h-6 w-6 place-items-center rounded-lg bg-sky-50 text-sky-600"
                    >
                      <Plus className="h-3 w-3" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
            <Button variant="outline" size="sm" className="mt-3 w-full" onClick={addMaterial}>
              <Plus className="h-3.5 w-3.5" />
              Agregar material
            </Button>
          </Card>
        )}

        <Button size="lg" className="w-full" onClick={() => go('evidencias')}>
          <Camera className="h-4 w-4" />
          Continuar a evidencias
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
    </PhoneScreen>
  )
}
