(ns jukebox-player.tags
  (:import [java.util.logging Logger Level]
           [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey])
  (:require [clojure.java.io :as io]))

(.setLevel (Logger/getLogger "org.jaudiotagger") Level/WARNING)

(defn extract-tags [file]
  (if file
    (let [audio-file (AudioFileIO/read (io/as-file file))
          tags (.getTag audio-file)
          header (.getAudioHeader audio-file)]
      (conj {}
        [:artist (.getFirst tags FieldKey/ARTIST)]
        [:album (.getFirst tags FieldKey/ALBUM)]
        [:title (.getFirst tags FieldKey/TITLE)]
        [:duration (.getTrackLength header)]))
    (conj {}
      [:artist "No Artist"]
      [:album "No Album"]
      [:title "No Title"]
      [:duration 0])))
