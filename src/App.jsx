import { Snowflake } from 'lucide-react'
import PhoneFrame from './components/PhoneFrame'
import DemoBar from './demo/DemoBar'
import DemoGuide from './demo/DemoGuide'
import ClienteSeguimiento from './screens/ClienteSeguimiento'
import JefeAgenda from './screens/JefeAgenda'
import JefeCrearOT from './screens/JefeCrearOT'
import JefeDashboard from './screens/JefeDashboard'
import Login from './screens/Login'
import SyncCenter from './screens/SyncCenter'
import TecnicoCheckIn from './screens/TecnicoCheckIn'
import TecnicoCierre from './screens/TecnicoCierre'
import TecnicoDetalleOT from './screens/TecnicoDetalleOT'
import TecnicoEjecucion from './screens/TecnicoEjecucion'
import TecnicoEvidencias from './screens/TecnicoEvidencias'
import TecnicoJornada from './screens/TecnicoJornada'
import { DemoProvider, useDemo } from './state/DemoContext'

const screens = {
  login: Login,
  jornada: TecnicoJornada,
  detalle: TecnicoDetalleOT,
  checkin: TecnicoCheckIn,
  ejecucion: TecnicoEjecucion,
  evidencias: TecnicoEvidencias,
  cierre: TecnicoCierre,
  sync: SyncCenter,
  dashboard: JefeDashboard,
  crear: JefeCrearOT,
  agenda: JefeAgenda,
  cliente: ClienteSeguimiento,
}

function ScreenRouter() {
  const { screen } = useDemo()
  const Screen = screens[screen] || Login
  return <Screen />
}

function Shell() {
  return (
    <div className="relative min-h-screen overflow-x-hidden bg-[#070d18] text-slate-100">
      <div className="pointer-events-none fixed inset-0 bg-[radial-gradient(circle_at_18%_0%,rgba(14,165,233,0.14),transparent_38%),radial-gradient(circle_at_82%_100%,rgba(6,182,212,0.09),transparent_42%)]" />

      <div className="relative mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:py-8">
        <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div className="flex items-center gap-3">
            <div className="grid h-11 w-11 place-items-center rounded-2xl bg-sky-500/15 ring-1 ring-sky-400/25">
              <Snowflake className="h-6 w-6 text-sky-300" />
            </div>
            <div>
              <p className="text-[10px] font-bold uppercase tracking-[0.28em] text-sky-400">ACIS · DSY1105</p>
              <h1 className="mt-0.5 text-xl font-bold leading-tight text-white sm:text-2xl">
                Prototipo visual de gestión de OT
              </h1>
              <p className="mt-0.5 text-[11px] text-slate-400">
                Órdenes de trabajo en terreno · App Android simulada en web
              </p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-1.5">
            {['Sin backend', 'Datos simulados', 'Android simulado'].map((label) => (
              <span
                key={label}
                className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-[10px] font-medium text-slate-300"
              >
                {label}
              </span>
            ))}
          </div>
        </header>

        <div className="mt-5">
          <DemoBar />
        </div>

        <div className="mt-6 flex flex-col items-center gap-6 lg:flex-row lg:items-start lg:justify-center">
          <div className="shrink-0">
            <PhoneFrame>
              <ScreenRouter />
            </PhoneFrame>
          </div>
          <div className="w-full max-w-md lg:w-[340px] lg:max-w-[340px]">
            <DemoGuide />
          </div>
        </div>

        <footer className="mt-8 border-t border-white/5 pt-4 text-center text-[10px] text-slate-600">
          Prototipo visual · ACIS Servicios Técnicos Ltda. · DSY1105
        </footer>
      </div>
    </div>
  )
}

export default function App() {
  return (
    <DemoProvider>
      <Shell />
    </DemoProvider>
  )
}
