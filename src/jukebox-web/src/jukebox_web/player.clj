(ns jukebox-web.player)

(defn play [request]
  (println "playing")
  {:status 302 :headers {"Location" "/playlist"}})

(defn pause [request]
  (println "pausing")
  {:status 302 :headers {"Location" "/playlist"}})
