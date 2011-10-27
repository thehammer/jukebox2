(ns jukebox-web.spec-helper
  (:require [fleetdb.embedded :as fleetdb]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.library :as library])
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
