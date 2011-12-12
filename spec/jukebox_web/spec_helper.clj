(ns jukebox-web.spec-helper
  (:require [fleetdb.embedded :as fleetdb]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist :as playlist])
  (:use [speclj.core]))

(defn with-database-connection []
  (around [spec]
    (let [connection (fleetdb/init-ephemeral)]
      (binding [db/*db* connection]
        (spec))
      (fleetdb/close connection))))

(defn with-test-music-library []
  (around [spec]
    (binding [library/*music-library* "spec/music"]
      (spec))))

(defn with-smaller-weight-threshold []
  (around [spec]
    (binding [playlist/*weight-threshold* 3]
      (spec))))

(defn filename [file]
  (last (clojure.string/split file #"/")))
