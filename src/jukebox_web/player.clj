(ns jukebox-web.player)

(def *current-song* (jukebox.PlayableTrackFactory/build "/Users/pair/12-01 Come Together.m4a"))

(defn play [request]
  (.play *current-song*)
  {:status 302 :headers {"Location" "/playlist"}})

(defn pause [request]
  (.pause *current-song*)
  {:status 302 :headers {"Location" "/playlist"}})
