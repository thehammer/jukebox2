(ns jukebox.window)
;;; there is probably a GClosure specific way to do this. Look it up later

(def onload-events (atom []))

(defn register-onload! [f]
  (swap! onload-events conj f))

(defn onload []
  (doseq [onload-event @onload-events]
    (onload-event)))

(set! (.-onload js/window) onload)
