(ns jukebox-player.core
  (:use [jukebox-player.playable]
        [jukebox-player.mp4-track :as mp4]
        [jukebox-player.basic-track :as basic])
  (:import [javax.sound.sampled AudioSystem]))

(def buffer-size 4096)
(def player-state (atom :stop))

(defn pause! [] (reset! player-state :pause))
(defn play!  [] (reset! player-state :play))
(defn skip!  [] (reset! player-state :skip))
(defn stop!  [] (reset! player-state :stop))

(defn load-track [file]
  (if (re-matches #".*\.m4a$" file)
    (mp4/load-mp4-track file)
    (basic/load-basic-track file)))

(defn- build-line-out [playable]
  (AudioSystem/getSourceDataLine (out-format playable)))

(defn- byte-index-for-time [playable time]
  (let [format (out-format playable)]
    (* time (* (.getFrameRate format) (.getFrameSize format)))))

(defn- play-snippet [playable start-time end-time]
  (let [start-index (byte-index-for-time playable start-time)
        snippet-length (- (byte-index-for-time playable end-time) start-index)
        buffer (byte-array buffer-size)
        speaker (build-line-out playable)
        audio-stream (in-stream playable)]
    (doto speaker (.open) (.start))
    (.skip audio-stream start-index)
    (loop [bytes-to-play (.read audio-stream buffer)
           bytes-played 0]
      (when-not (or (> bytes-played snippet-length) (= bytes-to-play -1))
        (.write speaker buffer 0 bytes-to-play)
        (recur (.read audio-stream buffer) (+ bytes-played bytes-to-play))))
    (doto speaker (.close))))

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
        :skip (do (play!) (recur files-to-play))
        nil))))

(defn start [files]
  (let [player (Thread. #(start-player files))]
    (.start player)
    player))

(defn hammertime! [file start end]
  (let [old-state @player-state]
    (when (= :play @player-state) (pause!))
    (play-snippet (load-track file) start end)
    (reset! player-state old-state)))
