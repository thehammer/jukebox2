(ns jukebox-player.core
  (:use [jukebox-player.playable]
        [jukebox-player.mp4-track :as mp4]
        [jukebox-player.basic-track :as basic])
  (:import [javax.sound.sampled AudioSystem]))

(def buffer-size 4096)
(def player-state (atom :stop))

(defn load-track [file]
  (if (re-matches #".*\.m4a$" file)
    (mp4/load-mp4-track file)
    (basic/load-basic-track file)))

(defn- build-line-out [playable]
  (AudioSystem/getSourceDataLine (out-format playable)))

(defn play-track [playable]
  (let [speaker (build-line-out playable)
        audio-stream (in-stream playable)
        buffer (byte-array buffer-size)]
    (doto speaker (.open) (.start))
    (loop [bytes-read (.read audio-stream buffer)]
      (condp = @player-state
        :play (when-not (= bytes-read -1)
                (.write speaker buffer 0 bytes-read)
                (recur (.read audio-stream buffer)))
        :pause (do (Thread/sleep 100) (recur bytes-read))
        nil))
     (doto speaker (.close))))

(defn- start-player [files]
  (loop [files-to-play files]
    (when-let [file (first files-to-play)]
      (condp = @player-state
        :play (do (play-track (load-track file)) (recur (rest files-to-play)))
        :stop (do (Thread/sleep 100) (recur files-to-play))
        :skip (do (reset! player-state :play) (recur (rest files-to-play)))
        nil))))

(defn start [files]
  (let [player (Thread. #(start-player files))]
    (.start player)
    player))

(defn pause [] (reset! player-state :pause))
(defn play  [] (reset! player-state :play))
(defn skip  [] (reset! player-state :skip))
(defn stop  [] (reset! player-state :stop))
