import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'

export default function useHomeSocket(homeId, onMessage) {
  const onMessageRef = useRef(onMessage)
  onMessageRef.current = onMessage

  useEffect(() => {
    if (!homeId) return

    const client = new Client({
      brokerURL: `ws://localhost:8080/ws`,
      reconnectDelay: 2000,
      onConnect: () => {
        client.subscribe(`/topic/home/${homeId}`, () => {
          onMessageRef.current()
        })
      },
      onStompError: (frame) => {
        console.error('WebSocket error', frame)
      },
    })

    client.activate()

    return () => {
      client.deactivate()
    }
  }, [homeId])
}
