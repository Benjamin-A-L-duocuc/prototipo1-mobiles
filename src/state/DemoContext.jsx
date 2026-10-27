import { createContext, useContext, useState } from 'react'

const DemoContext = createContext(null)

const ROLE_HOME = {
  tecnico: 'jornada',
  jefe: 'dashboard',
  cliente: 'cliente',
}

export function DemoProvider({ children }) {
  const params = new URLSearchParams(window.location.search)
  const [role, setRole] = useState(params.get('role') || 'tecnico')
  const [connection, setConnection] = useState(params.get('connection') || 'offline')
  const [scenario, setScenario] = useState(params.get('scenario') || 'normal')
  const [synced, setSynced] = useState(false)
  const [otAccepted, setOtAccepted] = useState(false)
  const [checkinDone, setCheckinDone] = useState(false)
  const [signed, setSigned] = useState(false)
  const [rejected, setRejected] = useState(false)
  const [photos, setPhotos] = useState(2)
  const [screen, setScreen] = useState(params.get('screen') || 'login')
  const [history, setHistory] = useState([])

  const pending = synced ? 0 : 4

  function go(next) {
    setHistory((h) => [...h, screen])
    setScreen(next)
  }

  function back() {
    if (history.length === 0) return
    const previous = history[history.length - 1]
    setHistory((h) => h.slice(0, -1))
    setScreen(previous)
  }

  function goRole(nextRole) {
    setRole(nextRole)
    setScreen(ROLE_HOME[nextRole])
    setHistory(['login'])
  }

  function changeConnection(next) {
    setConnection(next)
    if (next === 'offline') setSynced(false)
  }

  function changeScenario(next) {
    setScenario(next)
    setRejected(false)
  }

  function syncNow() {
    if (connection === 'online') setSynced(true)
  }

  function resetDemo() {
    setRole('tecnico')
    setConnection('offline')
    setScenario('normal')
    setSynced(false)
    setOtAccepted(false)
    setCheckinDone(false)
    setSigned(false)
    setRejected(false)
    setPhotos(2)
    setScreen('login')
    setHistory([])
  }

  const value = {
      role,
      connection,
      scenario,
      synced,
      pending,
      otAccepted,
      checkinDone,
      signed,
      rejected,
      photos,
      screen,
      history,
      go,
      back,
      goRole,
      setConnection: changeConnection,
      setScenario: changeScenario,
      setOtAccepted,
      setCheckinDone,
      setSigned,
      setRejected,
      setPhotos,
      syncNow,
    resetDemo,
  }

  return <DemoContext.Provider value={value}>{children}</DemoContext.Provider>
}

export function useDemo() {
  const context = useContext(DemoContext)
  if (!context) throw new Error('useDemo debe usarse dentro de DemoProvider')
  return context
}
