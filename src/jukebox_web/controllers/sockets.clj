(ns jukebox-web.controllers.sockets
  (:use lamina.core))

(def broadcast-channel (channel))

(defn chat-handler [ch handshake]
  (receive ch
           (fn [name]
             (siphon (map* #(str name ": " %) ch) broadcast-channel)
             (siphon broadcast-channel ch))))
