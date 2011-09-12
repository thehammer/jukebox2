(ns jukebox-player.example
  (:import [javax.sound.sampled AudioSystem])
  (:use [jukebox-player.playable]
        [jukebox-player.mp4-track :as mp4]
        [jukebox-player.basic-track :as basic]))

(def buffer-size 4096)

(defn load-track [file]
  (if (re-matches #".*\.m4a$" file)
    (mp4/load-mp4-track file)
    (basic/load-basic-track file)))

(defn play [playable]
  (let [speaker (AudioSystem/getSourceDataLine (out-format playable))
        audio-stream (in-stream playable)
        buffer (byte-array buffer-size)]
    (doto speaker (.open) (.start))
    (loop [bytes-read (.read audio-stream buffer)]
      (when-not (= bytes-read -1)
        (.write speaker buffer 0 bytes-read)
        (recur (.read audio-stream buffer))))
     (doto speaker (.close))))

(defn -main [file & args]
  (let [track (load-track file)]
    (play track)))
