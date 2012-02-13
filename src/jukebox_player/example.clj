(ns jukebox-player.example
  (:require [jukebox-player.mp4-track :as mp4]
            [jukebox-player.basic-track :as basic])
  (:use [jukebox-player.playable]
        [jukebox-player.core]
        [jukebox-player.tags]))

(defn -main [& files]
  (println (map #(extract-tags %) files))

  (let [player (start files)]
    (play!)
    (Thread/sleep 5000)
    (skip!)
    (Thread/sleep 5000)
    (hammertime! (first files) 15 20 false)
    (.join player)))
