(ns jukebox-web.models.track-search
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.library :as library]
            [clojure.string :as s]
            [clojure.java.io :as io])
  (:use [ring.util.codec :only (url-encode)]
        [jukebox-web.util.file :only (has-known-extension?)])
  (:import [java.io File]
           [java.util]))

(defn- audio [file]
  (re-find #"mp3|m4a$" file))

(defn uri [song]
  (url-encode (s/replace song "music/" "")))

(defn tracks [file]
  (if (.isDirectory file)
    (keep tracks (.listFiles file))
    (.toString file)))

(defn matches [match? file]
  (if (.isDirectory file)
    (if (match? file)
      (tracks file)
      (keep (partial matches match?) (.listFiles file)))
    (if (match? file)
      (.toString file))))

(defn execute [text]
  (let [library (io/file library/*music-library*)
        match? (fn [file] (.matches (.getName file) (str "(?i).*" text ".*")))]
    (when (.exists library)
      (filter audio (flatten (keep (partial matches match?) (.listFiles library)))))))
