import { Camera, CheckCircle2, Wifi, WifiOff } from 'lucide-react'
import { useDemo } from '../state/DemoContext'

export function Card({ className = '', children }) {
  return <div className={`rounded-2xl border border-slate-200 bg-white shadow-sm ${className}`}>{children}</div>
}

const buttonVariants = {
  primary: 'bg-sky-600 text-white shadow-sm hover:bg-sky-700 active:bg-sky-800',
  secondary: 'bg-slate-100 text-slate-800 hover:bg-slate-200',
  outline: 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50',
  danger: 'bg-rose-600 text-white hover:bg-rose-700',
  success: 'bg-emerald-600 text-white hover:bg-emerald-700',
  ghost: 'text-slate-600 hover:bg-slate-100',
}

const buttonSizes = {
  sm: 'h-8 px-3 text-xs',
  md: 'h-10 px-4 text-sm',
  lg: 'h-12 px-5 text-base',
}

export function Button({ variant = 'primary', size = 'md', className = '', children, ...props }) {
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition disabled:cursor-not-allowed disabled:opacity-50 ${buttonVariants[variant]} ${buttonSizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

const badgeTones = {
  slate: 'bg-slate-100 text-slate-700 ring-slate-200',
  sky: 'bg-sky-50 text-sky-700 ring-sky-200',
  emerald: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  amber: 'bg-amber-50 text-amber-700 ring-amber-200',
  rose: 'bg-rose-50 text-rose-700 ring-rose-200',
  violet: 'bg-violet-50 text-violet-700 ring-violet-200',
}

export function Badge({ tone = 'slate', className = '', children }) {
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-[10px] font-semibold ring-1 ring-inset ${badgeTones[tone]} ${className}`}
    >
      {children}
    </span>
  )
}

export function Segmented({ options, value, onChange, className = '' }) {
  return (
    <div className={`inline-flex rounded-xl bg-slate-100 p-1 ${className}`}>
      {options.map((option) => {
        const Icon = option.icon
        const active = option.value === value
        return (
          <button
            key={option.value}
            type="button"
            onClick={() => onChange(option.value)}
            className={`inline-flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-semibold transition ${
              active ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-700'
            }`}
          >
            {Icon && <Icon className="h-3.5 w-3.5" />}
            {option.label}
          </button>
        )
      })}
    </div>
  )
}

export function Field({ label, hint, children }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-[11px] font-semibold uppercase tracking-wide text-slate-500">{label}</span>
      {children}
      {hint && <span className="mt-1 block text-[11px] text-slate-400">{hint}</span>}
    </label>
  )
}

const fieldClasses =
  'w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-sky-500 focus:ring-2 focus:ring-sky-100'

export function Input({ className = '', ...props }) {
  return <input className={`${fieldClasses} ${className}`} {...props} />
}

export function Select({ className = '', children, ...props }) {
  return (
    <select className={`${fieldClasses} ${className}`} {...props}>
      {children}
    </select>
  )
}

export function Textarea({ className = '', ...props }) {
  return <textarea className={`${fieldClasses} min-h-24 resize-none ${className}`} {...props} />
}

export function Stat({ label, value, icon: Icon, tone = 'sky' }) {
  const tones = {
    sky: 'bg-sky-50 text-sky-600',
    amber: 'bg-amber-50 text-amber-600',
    rose: 'bg-rose-50 text-rose-600',
    emerald: 'bg-emerald-50 text-emerald-600',
    violet: 'bg-violet-50 text-violet-600',
  }
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-3 shadow-sm">
      <div className={`mb-2 grid h-8 w-8 place-items-center rounded-lg ${tones[tone]}`}>
        {Icon && <Icon className="h-4 w-4" />}
      </div>
      <p className="text-xl font-bold leading-none text-slate-900">{value}</p>
      <p className="mt-1 text-[10px] font-medium uppercase tracking-wide text-slate-400">{label}</p>
    </div>
  )
}

export function ConnectionBadge() {
  const { connection, pending } = useDemo()
  const online = connection === 'online'
  return (
    <span
      className={`inline-flex shrink-0 items-center gap-1 rounded-full px-2 py-1 text-[10px] font-semibold ring-1 ring-inset ${
        online ? 'bg-emerald-50 text-emerald-700 ring-emerald-200' : 'bg-amber-50 text-amber-700 ring-amber-200'
      }`}
    >
      {online ? <Wifi className="h-3 w-3" /> : <WifiOff className="h-3 w-3" />}
      {online ? 'En línea' : pending > 0 ? `Offline · ${pending}` : 'Sin conexión'}
    </span>
  )
}

export function OfflineBanner({ children }) {
  const { connection } = useDemo()
  if (connection !== 'offline') return null
  return (
    <div className="flex items-start gap-2 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2.5 text-[11px] leading-snug text-amber-800">
      <WifiOff className="mt-0.5 h-4 w-4 shrink-0" />
      <span>
        {children ||
          'Sin conexión: los registros se guardan en el dispositivo y se sincronizan cuando vuelva la señal.'}
      </span>
    </div>
  )
}

export function SuccessBanner({ children }) {
  return (
    <div className="flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2.5 text-[11px] font-medium text-emerald-800">
      <CheckCircle2 className="h-4 w-4 shrink-0" />
      {children}
    </div>
  )
}

export function PriorityBadge({ priority }) {
  const tone = priority === 'Alta' ? 'rose' : priority === 'Media' ? 'amber' : 'slate'
  return <Badge tone={tone}>{priority}</Badge>
}

export function StatusBadge({ status }) {
  const tones = {
    Asignada: 'sky',
    Aceptada: 'violet',
    'En ejecución': 'amber',
    Cerrada: 'emerald',
    Conformada: 'emerald',
    Cancelada: 'slate',
    Rechazada: 'rose',
    Pendiente: 'slate',
  }
  return <Badge tone={tones[status] || 'slate'}>{status}</Badge>
}

export function PhotoTile({ index }) {
  return (
    <div className="relative aspect-square overflow-hidden rounded-xl border border-slate-200 bg-gradient-to-br from-slate-200 via-slate-300 to-slate-400">
      <div
        className="absolute inset-0 opacity-40"
        style={{
          backgroundImage:
            'linear-gradient(135deg, rgba(255,255,255,.85) 25%, transparent 25%), linear-gradient(225deg, rgba(255,255,255,.55) 25%, transparent 25%)',
          backgroundSize: '18px 18px',
        }}
      />
      <div className="absolute inset-x-3 bottom-6 h-10 rounded-md bg-slate-500/40" />
      <div className="absolute inset-x-6 bottom-10 h-12 rounded-md bg-slate-600/30" />
      <Camera className="absolute left-1/2 top-1/2 h-7 w-7 -translate-x-1/2 -translate-y-1/2 text-slate-600" />
      <span className="absolute bottom-1.5 left-2 rounded bg-white/70 px-1.5 py-0.5 text-[9px] font-semibold text-slate-700">
        Foto {index}
      </span>
    </div>
  )
}
