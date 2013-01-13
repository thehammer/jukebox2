(ns jukebox-web.models.track-search
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.library :as library]
            [clojure.string :as s]
            [clojure.java.io :as io])
  (:use [ring.util.codec :only (url-encode)]
        [jukebox-web.util.file :only (has-known-extension?)])
  (:import [java.io File]
           [java.util]))

(def matcher (ref ""))

(defn- audio [file]
  (re-find #"mp3|m4a$" file))

(defn uri [song]
  (url-encode (s/replace song "music/" "")))

(defn tracks [file]
  (if (.isDirectory file)
    (keep tracks (.listFiles file))
    (.toString file)))

(defn matches? [file]
  (.matches (.getName file) (str "(?i).*" @matcher ".*")))

(defn matches [file]
  (if (.isDirectory file)
    (if (matches? file)
      (tracks file)
      (keep matches (.listFiles file)))
    (if (matches? file)
      (.toString file))))

(defn execute [text]
  (let [library (io/file library/*music-library*)]
    (dosync (ref-set matcher text))
    (when (.exists library)
      (filter audio (flatten (keep matches (.listFiles library)))))))
