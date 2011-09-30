(ns jukebox-web.controllers.sockets
  (:use lamina.core))

(def broadcast-channel (channel))

(defn chat-handler [ch handshake]
  (receive ch
           (fn [req]
             (siphon (map* #(str req ": " %) ch) broadcast-channel)
             (siphon broadcast-channel ch))))
