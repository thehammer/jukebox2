(ns jukebox-web.spec-helper
  (:require [fleetdb.embedded :as fleetdb]
            [jukebox-web.models.db :as db])
  (:use [speclj.core]))

(defn with-database-connection []
  (around [spec]
    (let [connection (fleetdb/init-ephemeral)]
      (binding [db/*db* connection]
        (spec))
      (fleetdb/close connection))))
