import { MapPin } from 'lucide-react'

export default function MiniMap({ pins = 1, className = '' }) {
  const positions = [
    { top: '32%', left: '42%', tone: 'text-rose-600' },
    { top: '55%', left: '68%', tone: 'text-sky-600' },
    { top: '24%', left: '72%', tone: 'text-emerald-600' },
  ]

  return (
    <div className={`relative overflow-hidden rounded-2xl border border-slate-200 bg-[#e8eef3] ${className}`}>
      <div
        className="absolute inset-0 opacity-60"
        style={{
          backgroundImage:
            'linear-gradient(#cbd5e1 1px, transparent 1px), linear-gradient(90deg, #cbd5e1 1px, transparent 1px)',
          backgroundSize: '26px 26px',
        }}
      />
      <div className="absolute -left-10 top-10 h-24 w-40 rotate-12 rounded-full bg-sky-200/70" />
      <div className="absolute bottom-0 right-0 h-28 w-32 rounded-tl-[3rem] bg-emerald-200/70" />
      <div className="absolute left-1/3 top-1/4 h-16 w-24 rounded-lg bg-slate-300/80" />
      <div className="absolute bottom-1/4 right-1/4 h-20 w-20 rounded-lg bg-slate-300/80" />
      <div className="absolute left-2 top-1/2 h-1.5 w-full -rotate-6 bg-white/70" />
      {positions.slice(0, pins).map((pin, index) => (
        <div key={index} className="absolute" style={{ top: pin.top, left: pin.left }}>
          <span className="absolute -inset-3 rounded-full bg-white/60" />
          <MapPin className={`relative h-7 w-7 ${pin.tone} drop-shadow`} fill="currentColor" />
        </div>
      ))}
      <div className="absolute bottom-2 right-2 rounded-md bg-white/85 px-2 py-1 text-[9px] font-medium text-slate-500">
        Vista simulada
      </div>
    </div>
  )
}
