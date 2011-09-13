(ns jukebox-player.example
  (:use [jukebox-player.playable]
        [jukebox-player.player]
        [jukebox-player.mp4-track :as mp4]
        [jukebox-player.basic-track :as basic]))


(defn -main [& files]
  (let [player (Thread. #(start files))]
    (.start player)
    (play)
    (Thread/sleep 10000)
    (pause)
    (Thread/sleep 10000)
    (play)
    (Thread/sleep 10000)
    (stop)
    (.join player)))
