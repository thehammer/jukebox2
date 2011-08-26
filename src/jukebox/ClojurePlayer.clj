(ns jukebox.ClojurePlayer
    (:gen-class))

(defn -main
  []
  (.play (jukebox.PlayableTrackFactory/build "/Users/Hammer/Documents/6-11 In My Life.m4a")))