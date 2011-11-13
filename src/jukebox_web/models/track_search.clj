(ns jukebox-web.models.track-search
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.library :as library]
            [clojure.string :as s]
            [clojure.java.io :as io])
  (:import [java.io File]
           [java.util]))

(def matcher (ref ""))

(defn clean [file]
  (not (nil? file)))

(defn select [file]
  (when (.matches (.getName file) (str "(?i).*" @matcher ".*"))
    (if (.isDirectory file)
      (map select (.listFiles file))
      (.toString file))))

(defn execute [text]
  (let [library (io/file library/*music-library*)]
    (dosync (ref-set matcher text))
    (when (.exists library)
      (filter clean (flatten (map select (.listFiles library)))))))
