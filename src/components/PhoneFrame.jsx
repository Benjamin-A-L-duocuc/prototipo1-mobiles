import { Wifi, WifiOff } from 'lucide-react'
import { useDemo } from '../state/DemoContext'

function StatusBar() {
  const { connection } = useDemo()
  return (
    <div className="relative z-10 flex shrink-0 items-center justify-between bg-white px-6 pb-1 pt-2 text-[11px] font-semibold text-slate-900">
      <span>09:41</span>
      <div className="absolute left-1/2 top-2 h-5 w-24 -translate-x-1/2 rounded-full bg-slate-900" />
      <div className="flex items-center gap-1.5">
        {connection === 'online' ? (
          <Wifi className="h-3 w-3" />
        ) : (
          <WifiOff className="h-3 w-3 text-amber-600" />
        )}
        <span className="text-[9px] font-bold">5G</span>
        <div className="h-2.5 w-5 rounded-sm border border-slate-400 p-0.5">
          <div className="h-full w-3/4 rounded-[1px] bg-slate-800" />
        </div>
      </div>
    </div>
  )
}

export default function PhoneFrame({ children }) {
  return (
    <div className="relative mx-auto w-[390px] max-w-full">
      <div className="rounded-[2.6rem] border border-slate-700/80 bg-slate-950 p-2.5 shadow-[0_35px_90px_-25px_rgba(0,0,0,0.9)]">
        <div className="relative flex h-[780px] flex-col overflow-hidden rounded-[2.1rem] bg-slate-50">
          <StatusBar />
          <div className="min-h-0 flex-1">{children}</div>
        </div>
      </div>
    </div>
  )
}
