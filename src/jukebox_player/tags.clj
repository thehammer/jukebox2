(ns jukebox-player.tags
  (:use [jukebox-player.core])
  (:import [java.util.logging Logger Level]
           [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey])
  (:require [clojure.java.io :as io]
            [clojure.contrib.math :as math]))

(.setLevel (Logger/getLogger "org.jaudiotagger") Level/WARNING)

(defn format-time [time]
  (let [minutes (int (math/floor (/ time 60.0)))
       seconds (int (mod time 60))]
  (str (format "%02d" minutes) ":" (format "%02d" seconds))))

(defn extract-tags [file]
  (let [audio-file (AudioFileIO/read (io/as-file file))
        tags (.getTag audio-file)
        header (.getAudioHeader audio-file)]
    (conj {}
      [:artist (.getFirst tags FieldKey/ARTIST)]
      [:album (.getFirst tags FieldKey/ALBUM)]
      [:title (.getFirst tags FieldKey/TITLE)]
      [:current (format-time (current-time))]
      [:duration (format-time (.getTrackLength header))])))
