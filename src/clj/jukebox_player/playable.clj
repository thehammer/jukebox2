(ns jukebox-player.playable)

(defprotocol Playable
  (in-stream [track])
  (out-format [track]))
