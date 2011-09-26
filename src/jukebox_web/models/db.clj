(ns jukebox-web.models.db
  (:import [java.util UUID]
           [java.io File])
  (:require [fleetdb.embedded :as fleetdb])
  (:use [clojure.contrib.string :only [as-str]]))

(def *db* :no-database-connection)

(defn open-db [file]
  (if (.exists (File. file))
    (fleetdb/load-persistent file)
    (fleetdb/init-persistent file)))

(defn close-db [connection]
  (fleetdb/close connection))

(defn- add-id [record id]
  (conj record ["id" id]))

(defn- generate-id []
  (str (UUID/randomUUID)))

(defn- keys-to-strings [record]
  (reduce #(conj %1 [(as-str (first %2)) (nth %2 1)]) {} record))

(defn- keys-to-keywords [record]
  (reduce #(conj %1 [(keyword (first %2)) (nth %2 1)]) {} record))

(defn insert [model record]
  (let [id (generate-id)]
    (fleetdb/query *db* ["insert" (as-str model) (add-id (keys-to-strings record) id)])
    (conj record {:id id})))

(defn find-by-field [model field value]
  (map keys-to-keywords (fleetdb/query *db* ["select" (as-str model) {"where" ["=" (as-str field) value]}])))

(defn find-all [model]
  (map keys-to-keywords (fleetdb/query *db* ["select" (as-str model)])))

(defn update [model updates field value]
  (fleetdb/query *db* ["update" (as-str model) (keys-to-strings updates) {"where" ["=" (as-str field) value]}]))
