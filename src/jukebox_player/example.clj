(ns jukebox-player.example
  (:use [jukebox-player.playable]
        [jukebox-player.core]
        [jukebox-player.mp4-track :as mp4]
        [jukebox-player.basic-track :as basic]))

(defn -main [& files]
  (let [player (start files)]
    (play!)
    (Thread/sleep 5000)
    (skip!)
    (Thread/sleep 5000)
    (hammertime! (first files) 15 20)
    (Thread/sleep 5000)
    (stop!)
    (.join player)))
