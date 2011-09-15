(ns jukebox-player.tags
  (:import [java.io File]
           [java.util.logging Logger Level]
           [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey]))

(.setLevel (Logger/getLogger "org.jaudiotagger") Level/WARNING)

(defn extract-tags [file]
  (let [audio-file (AudioFileIO/read (File. file))
        tags (.getTag audio-file)]
    (conj {}
      [:artist (.getFirst tags FieldKey/ARTIST)]
      [:album (.getFirst tags FieldKey/ALBUM)]
      [:title (.getFirst tags FieldKey/TITLE)])))
