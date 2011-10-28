(ns jukebox-player.tags
  (:import [java.util.logging Logger Level]
           [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey])
  (:require [clojure.java.io :as io]
            [clojure.contrib.math :as math]))

(.setLevel (Logger/getLogger "org.jaudiotagger") Level/WARNING)

(defn convert-duration [duration]
  (let [minutes (int (math/floor (/ duration 60.0)))
       seconds (mod duration 60)]
  (str (str minutes) ":" (str seconds))))

(defn extract-tags [file]
  (let [audio-file (AudioFileIO/read (io/as-file file))
        tags (.getTag audio-file)
        header (.getAudioHeader audio-file)]
    (conj {}
      [:artist (.getFirst tags FieldKey/ARTIST)]
      [:album (.getFirst tags FieldKey/ALBUM)]
      [:title (.getFirst tags FieldKey/TITLE)]
      [:duration (convert-duration (.getTrackLength header))])))
