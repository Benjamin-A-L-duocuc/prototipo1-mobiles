import { Camera, ChevronRight, ImagePlus } from 'lucide-react'
import { useState } from 'react'
import PhoneScreen from '../components/PhoneScreen'
import { Button, Card, OfflineBanner, PhotoTile, Textarea } from '../components/ui'
import { OT } from '../data/mock'
import { useDemo } from '../state/DemoContext'

export default function TecnicoEvidencias() {
  const { go, photos, setPhotos } = useDemo()
  const [comentario, setComentario] = useState('Se adjuntan fotos del condensador antes y después de la limpieza.')

  return (
    <PhoneScreen title="Evidencias" subtitle={`${OT.id} · Fotos de la intervención`}>
      <div className="space-y-3">
        <OfflineBanner>
          Las fotografías se guardan en el dispositivo. Se subirán al sincronizar la OT.
        </OfflineBanner>

        <Card className="p-3.5">
          <div className="flex items-center justify-between">
            <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">
              Fotografías ({photos})
            </p>
            <span className="rounded-full bg-amber-50 px-2 py-0.5 text-[9px] font-semibold text-amber-700 ring-1 ring-inset ring-amber-200">
              Local
            </span>
          </div>
          <div className="mt-2.5 grid grid-cols-2 gap-2">
            {Array.from({ length: photos }).map((_, index) => (
              <PhotoTile key={index} index={index + 1} />
            ))}
            <button
              type="button"
              onClick={() => setPhotos((value) => value + 1)}
              className="grid aspect-square place-items-center rounded-xl border border-dashed border-slate-300 bg-slate-50 text-slate-400 transition hover:border-sky-400 hover:text-sky-500"
            >
              <div className="flex flex-col items-center gap-1">
                <ImagePlus className="h-6 w-6" />
                <span className="text-[9px] font-semibold">Agregar foto</span>
              </div>
            </button>
          </div>
          <Button variant="outline" size="sm" className="mt-3 w-full" onClick={() => setPhotos((value) => value + 1)}>
            <Camera className="h-3.5 w-3.5" />
            Tomar foto
          </Button>
        </Card>

        <Card className="p-3.5">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400">Comentario de evidencia</p>
          <Textarea
            value={comentario}
            onChange={(event) => setComentario(event.target.value)}
            className="mt-2 min-h-20 text-[11px]"
          />
        </Card>

        <Button size="lg" className="w-full" onClick={() => go('cierre')}>
          Continuar al cierre
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
    </PhoneScreen>
  )
}
