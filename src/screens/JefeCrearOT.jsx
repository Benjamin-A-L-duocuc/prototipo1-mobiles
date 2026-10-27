import { AlertTriangle, CalendarDays, CheckCircle2, ClipboardCheck, UserRound } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, Field, Input, Segmented, Select, Textarea } from '../components/ui'
import { useDemo } from '../state/DemoContext'

export default function JefeCrearOT() {
  const { go } = useDemo()
  const [tipo, setTipo] = useState('preventivo')
  const [prioridad, setPrioridad] = useState('media')
  const [disponible, setDisponible] = useState('si')
  const [creada, setCreada] = useState(false)

  const sinDisponibilidad = disponible === 'no'

  if (creada) {
    return (
      <PhoneScreen title="Crear OT" subtitle="Nueva orden de trabajo">
        <div className="flex h-full flex-col items-center justify-center py-8 text-center">
          <div className="grid h-16 w-16 place-items-center rounded-full bg-emerald-50 text-emerald-600">
            <CheckCircle2 className="h-8 w-8" />
          </div>
          <h2 className="mt-3 text-base font-bold text-slate-900">
            {sinDisponibilidad ? 'OT guardada sin asignar' : 'OT creada y asignada'}
          </h2>
          <p className="mt-1 text-[11px] text-slate-500">
            {sinDisponibilidad
              ? 'OT-1046 quedó pendiente de asignación. El jefe recibirá una alerta.'
              : 'OT-1046 asignada a Cristian Rojas para el 28/10 a las 09:00.'}
          </p>
          <Card className="mt-4 w-full p-3 text-left">
            <div className="flex items-center justify-between text-[11px]">
              <span className="text-slate-500">Cliente</span>
              <span className="font-semibold text-slate-800">Supermercado Ñuñoa</span>
            </div>
            <div className="mt-2 flex items-center justify-between text-[11px]">
              <span className="text-slate-500">Equipo</span>
              <span className="font-semibold text-slate-800">Cámara frigorífica</span>
            </div>
            <div className="mt-2 flex items-center justify-between text-[11px]">
              <span className="text-slate-500">Prioridad</span>
              <span className="font-semibold text-slate-800">{prioridad}</span>
            </div>
            <div className="mt-2 flex items-center justify-between text-[11px]">
              <span className="text-slate-500">Técnico</span>
              <span className="font-semibold text-slate-800">{sinDisponibilidad ? 'Sin asignar' : 'Cristian Rojas'}</span>
            </div>
          </Card>
          <Button size="lg" className="mt-4 w-full" onClick={() => go('dashboard')}>
            Volver al panel
          </Button>
        </div>
      </PhoneScreen>
    )
  }

  return (
    <PhoneScreen title="Crear OT" subtitle="Nueva orden de trabajo">
      <div className="space-y-3">
        <Card className="space-y-3 p-3.5">
          <Field label="Cliente">
            <Select defaultValue="nunoa">
              <option value="nunoa">Supermercado Ñuñoa</option>
              <option value="macul">Retail Macul</option>
              <option value="mall">Mall Plaza Oeste</option>
            </Select>
          </Field>
          <Field label="Local">
            <Select defaultValue="local-nunoa">
              <option value="local-nunoa">Local Ñuñoa · Av. Irarrázaval 3400</option>
              <option value="local-macul">Local Macul · Av. Macul 4500</option>
            </Select>
          </Field>
          <Field label="Equipo">
            <Select defaultValue="eq-221">
              <option value="eq-221">Cámara frigorífica 4 puertas · EQ-221</option>
              <option value="eq-118">Condensadora azotea · EQ-118</option>
            </Select>
          </Field>

          <div>
            <p className="mb-1.5 text-[11px] font-semibold uppercase tracking-wide text-slate-500">Tipo de OT</p>
            <Segmented
              value={tipo}
              onChange={setTipo}
              options={[
                { value: 'preventivo', label: 'Preventivo' },
                { value: 'correctivo', label: 'Correctivo' },
              ]}
            />
          </div>

          <div>
            <p className="mb-1.5 text-[11px] font-semibold uppercase tracking-wide text-slate-500">Prioridad</p>
            <Segmented
              value={prioridad}
              onChange={setPrioridad}
              options={[
                { value: 'baja', label: 'Baja' },
                { value: 'media', label: 'Media' },
                { value: 'alta', label: 'Alta' },
              ]}
            />
          </div>

          <Field label="Descripción">
            <Textarea
              defaultValue="Revisar alza de temperatura en cámara de lácteos y estado del condensador."
              className="min-h-20 text-[11px]"
            />
          </Field>

          <div className="grid grid-cols-2 gap-2">
            <Field label="Fecha">
              <Input type="date" lang="es-CL" defaultValue="2026-10-28" className="text-[11px]" />
            </Field>
            <Field label="Hora">
              <Input type="time" lang="es-CL" defaultValue="09:00" className="text-[11px]" />
            </Field>
          </div>
        </Card>

        <Card className="p-3.5">
          <div className="flex items-center gap-2">
            <UserRound className="h-4 w-4 text-slate-400" />
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Asignación</p>
          </div>
          <Field label="Técnico" hint="Variante visual para mostrar ambos escenarios.">
            <Select defaultValue="cristian" className="mt-1.5">
              <option value="cristian">Cristian Rojas · 5 visitas hoy</option>
              <option value="paula">Paula Díaz · 4 visitas hoy</option>
              <option value="diego">Diego Soto · 6 visitas hoy</option>
            </Select>
          </Field>
          <div className="mt-2.5">
            <p className="mb-1.5 text-[11px] font-semibold uppercase tracking-wide text-slate-500">
              Disponibilidad del técnico
            </p>
            <Segmented
              value={disponible}
              onChange={setDisponible}
              options={[
                { value: 'si', label: 'Disponible' },
                { value: 'no', label: 'Sin disponibilidad' },
              ]}
            />
          </div>

          {sinDisponibilidad && (
            <div className="mt-2.5 flex items-start gap-2 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2.5 text-[10px] leading-snug text-amber-800">
              <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
              <span>
                El técnico no tiene disponibilidad en ese horario. Puedes guardar la OT como pendiente de asignación.
              </span>
            </div>
          )}
        </Card>

        <Button size="lg" className="w-full" onClick={() => setCreada(true)}>
          <ClipboardCheck className="h-4 w-4" />
          {sinDisponibilidad ? 'Guardar sin asignar' : 'Crear y asignar OT'}
        </Button>

        <p className="flex items-center justify-center gap-1.5 pb-2 text-[10px] text-slate-400">
          <CalendarDays className="h-3 w-3" />
          La fecha y hora son simuladas para la demo.
        </p>
      </div>
    </PhoneScreen>
  )
}
