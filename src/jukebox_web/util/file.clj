(ns jukebox-web.util.file
  (:import [java.io File])
  (:require [clojure.contrib.string :as clojure-string])
  (:use [clojure.java.io :only (as-file)]
        [ring.util.codec :only (url-encode)]))

(defn relative-uri [file]
   (url-encode (.getPath file)))

(defn file-path [& parts]
  (clojure.string/join File/separator parts))

(defn mkdir-p [path]
  (.mkdirs (as-file path)))

(defn mv [from to]
  (.renameTo (as-file from) (as-file to)))

(defn strip-slashes [string]
  (clojure-string/replace-str "/" " " string))

