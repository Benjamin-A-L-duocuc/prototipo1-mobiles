import { CheckCircle2, Clock, CloudOff, RefreshCw, Wifi, WifiOff } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, SuccessBanner } from '../components/ui'
import { SYNC_ITEMS } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function SyncCenter() {
  const { go, connection, pending, syncNow, synced } = useDemo()
  const [syncing, setSyncing] = useState(false)
  const online = connection === 'online'

  function handleSync() {
    if (!online || syncing) return
    setSyncing(true)
    window.setTimeout(() => {
      syncNow()
      setSyncing(false)
    }, 900)
  }

  return (
    <PhoneScreen title="Sincronización" subtitle="Cola de registros de la OT">
      <div className="space-y-3">
        <Card className={`p-3.5 ${online ? 'border-emerald-200' : 'border-amber-200'}`}>
          <div className="flex items-center gap-3">
            <div
              className={`grid h-11 w-11 place-items-center rounded-2xl ${
                online ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'
              }`}
            >
              {online ? <Wifi className="h-5 w-5" /> : <WifiOff className="h-5 w-5" />}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-bold text-slate-900">{online ? 'Conectado' : 'Sin conexión'}</p>
              <p className="text-[10px] text-slate-500">
                {online
                  ? 'Puedes enviar los registros pendientes.'
                  : 'Puedes seguir trabajando; los datos se guardan localmente.'}
              </p>
            </div>
            <span
              className={`h-2.5 w-2.5 rounded-full ${
                online ? 'bg-emerald-500' : 'animate-soft-pulse bg-amber-500'
              }`}
            />
          </div>
        </Card>

        <div className="flex items-center justify-between">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Registros de la OT</p>
          <span
            className={`rounded-full px-2 py-0.5 text-[9px] font-bold ring-1 ring-inset ${
              pending > 0
                ? 'bg-amber-50 text-amber-700 ring-amber-200'
                : 'bg-emerald-50 text-emerald-700 ring-emerald-200'
            }`}
          >
            {pending > 0 ? `${pending} pendientes` : 'Sincronizado'}
          </span>
        </div>

        <Card className="divide-y divide-slate-100 p-0">
          {SYNC_ITEMS.map((item) => (
            <div key={item.tipo} className="flex items-center gap-3 px-3.5 py-2.5">
              <div
                className={`grid h-8 w-8 place-items-center rounded-lg ${
                  pending > 0 ? 'bg-amber-50 text-amber-600' : 'bg-emerald-50 text-emerald-600'
                }`}
              >
                {pending > 0 ? <Clock className="h-4 w-4" /> : <CheckCircle2 className="h-4 w-4" />}
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-[11px] font-semibold text-slate-800">{item.tipo}</p>
                <p className="truncate text-[9px] text-slate-400">{item.detalle}</p>
              </div>
              <span className={`text-[9px] font-semibold ${pending > 0 ? 'text-amber-600' : 'text-emerald-600'}`}>
                {pending > 0 ? 'Pendiente' : 'Enviado'}
              </span>
            </div>
          ))}
        </Card>

        {synced && <SuccessBanner>Todo sincronizado. La OT ya está actualizada en el servidor.</SuccessBanner>}

        {!online && (
          <div className="flex items-start gap-2 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2.5 text-[11px] leading-snug text-amber-800">
            <CloudOff className="mt-0.5 h-4 w-4 shrink-0" />
            <span>Conéctate para sincronizar. Los registros no se pierden mientras estés sin señal.</span>
          </div>
        )}

        <Button
          size="lg"
          className="w-full"
          disabled={!online || syncing || pending === 0}
          onClick={handleSync}
        >
          <RefreshCw className={`h-4 w-4 ${syncing ? 'animate-spin' : ''}`} />
          {syncing ? 'Sincronizando...' : pending === 0 ? 'Todo sincronizado' : 'Sincronizar ahora'}
        </Button>

        <div className="flex items-center justify-between rounded-xl bg-slate-100 px-3 py-2 text-[10px] text-slate-500">
          <span>Última sincronización</span>
          <span className="font-semibold text-slate-700">{synced ? 'Hoy · 10:40' : 'Hoy · 09:30'}</span>
        </div>

        <Button variant="outline" size="md" className="w-full" onClick={() => go('jornada')}>
          Volver a mi jornada
        </Button>
      </div>
    </PhoneScreen>
  )
}
