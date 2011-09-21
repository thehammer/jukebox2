(ns jukebox-web.controllers.library
  (:import [java.io File]
           [java.util UUID])
  (:require [clojure.contrib.duck-streams :as ds]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [jukebox-player.tags :as tags]))

(def *music-library* "music")

(defn- file-path [& parts]
  (str/join File/separator parts))

(defn- save-file [tempfile user ext]
  (let [file-name (io/as-file (file-path *music-library* user (str (UUID/randomUUID) "." ext)))]
    (ds/copy tempfile file-name)))

(defn upload [request]
  (let [{:keys [tempfile filename]} (-> request :params :file)
        user (-> request :params :user)
        extension (last (str/split filename #"\."))]
    (save-file tempfile user extension))
    (println "upload complete")
  "upload complete")
