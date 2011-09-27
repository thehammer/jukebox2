(ns jukebox-web.util.file
  (:use [ring.util.codec :only (url-encode)]))

(defn relative-uri [file]
   (url-encode (.getPath file)))
