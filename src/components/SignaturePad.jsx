import { useEffect, useRef } from 'react'

export default function SignaturePad({ className = '' }) {
  const canvasRef = useRef(null)
  const drawing = useRef(false)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const dpr = window.devicePixelRatio || 1
    const rect = canvas.getBoundingClientRect()
    canvas.width = Math.max(1, rect.width * dpr)
    canvas.height = Math.max(1, rect.height * dpr)
    const context = canvas.getContext('2d')
    context.scale(dpr, dpr)
    context.lineWidth = 2
    context.lineCap = 'round'
    context.lineJoin = 'round'
    context.strokeStyle = '#0f172a'
  }, [])

  function position(event) {
    const rect = canvasRef.current.getBoundingClientRect()
    return { x: event.clientX - rect.left, y: event.clientY - rect.top }
  }

  function start(event) {
    drawing.current = true
    const { x, y } = position(event)
    const context = canvasRef.current.getContext('2d')
    context.beginPath()
    context.moveTo(x, y)
    if (event.currentTarget.setPointerCapture) event.currentTarget.setPointerCapture(event.pointerId)
  }

  function move(event) {
    if (!drawing.current) return
    const { x, y } = position(event)
    const context = canvasRef.current.getContext('2d')
    context.lineTo(x, y)
    context.stroke()
  }

  function end() {
    drawing.current = false
  }

  function clear() {
    const canvas = canvasRef.current
    const context = canvas.getContext('2d')
    context.clearRect(0, 0, canvas.width, canvas.height)
  }

  return (
    <div className={`relative ${className}`}>
      <canvas
        ref={canvasRef}
        onPointerDown={start}
        onPointerMove={move}
        onPointerUp={end}
        onPointerLeave={end}
        className="h-32 w-full touch-none rounded-xl border border-dashed border-slate-300 bg-slate-50"
      />
      <span className="pointer-events-none absolute left-3 top-2 text-[10px] text-slate-400">Firma aquí</span>
      <button
        type="button"
        onClick={clear}
        className="absolute right-2 top-2 rounded-md bg-white/90 px-2 py-1 text-[10px] font-semibold text-slate-500 shadow-sm"
      >
        Limpiar
      </button>
    </div>
  )
}
