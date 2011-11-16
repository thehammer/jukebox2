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

(defn tracks [file]
  (if (.isDirectory file)
    (map tracks (.listFiles file))
    (.toString file)))

(defn matches? [file]
  (.matches (.getName file) (str "(?i).*" @matcher ".*")))

(defn select [file]
  (if (.isDirectory file)
    (if (matches? file)
      (tracks file)
      (map select (.listFiles file)))
    (if (matches? file)
      (.toString file))))

(defn execute [text]
  (let [library (io/file library/*music-library*)]
    (dosync (ref-set matcher text))
    (when (.exists library)
      (filter clean (flatten (map select (.listFiles library)))))))
