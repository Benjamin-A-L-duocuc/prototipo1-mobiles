import { ChevronLeft } from 'lucide-react'
import { useDemo } from '../state/DemoContext'
import { ConnectionBadge } from './ui'

export default function PhoneScreen({
  title,
  subtitle,
  children,
  back = true,
  right,
  contentClassName = '',
  headerClassName = '',
}) {
  const { back: goBack } = useDemo()

  return (
    <div className="flex h-full flex-col bg-slate-50">
      <header
        className={`flex items-center gap-2 border-b border-slate-200 bg-white/95 px-3 py-2.5 backdrop-blur ${headerClassName}`}
      >
        {back ? (
          <button
            type="button"
            onClick={goBack}
            className="grid h-8 w-8 shrink-0 place-items-center rounded-full text-slate-500 transition hover:bg-slate-100"
            aria-label="Volver"
          >
            <ChevronLeft className="h-5 w-5" />
          </button>
        ) : (
          <div className="w-1" />
        )}
        <div className="min-w-0 flex-1">
          <h1 className="truncate text-[13px] font-bold leading-tight text-slate-900">{title}</h1>
          {subtitle && <p className="truncate text-[10px] leading-tight text-slate-500">{subtitle}</p>}
        </div>
        {right}
        <ConnectionBadge />
      </header>
      <div className={`app-scroll min-h-0 flex-1 overflow-y-auto overflow-x-hidden ${contentClassName || 'px-4 py-4'}`}>
        <div className="animate-fade-in">{children}</div>
      </div>
    </div>
  )
}
