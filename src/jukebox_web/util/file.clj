(ns jukebox-web.util.file
  (:import [java.io File])
  (:require [clojure.contrib.string :as clojure-string]
            [clojure.java.io :as io])
  (:use [ring.util.codec :only (url-encode)]))

(defn not-dotfiles [file]
  (not (.startsWith (.getName file) ".")))

(defn directory? [file]
  (.isDirectory file))

(defn mp3? [file]
  (.endsWith (.getName file) ".mp3"))

(defn default-filter [file]
  (and (not-dotfiles file)
       (or (directory? file) (mp3? file))))

(defn relativize [parent child]
  (let [parent-uri (.toURI (io/file parent))
        child-uri (.toURI (io/file child))]
    (io/file (.getPath (.relativize parent-uri child-uri)))))

(defn relative-uri [file]
   (url-encode (.getPath file)))

(defn file-path [& parts]
  (clojure.string/join File/separator parts))

(defn mkdir-p [path]
  (.mkdirs (io/as-file path)))

(defn mv [from to]
  (.renameTo (io/as-file from) (io/as-file to)))

(defn strip-slashes [string]
  (clojure-string/replace-str "/" " " string))

(defn ls [dir path & [filters]]
  (let [filterer (or filters default-filter)
        files (filter filterer (.listFiles (io/file dir path)))]
    (map #(relativize dir %) files)))
