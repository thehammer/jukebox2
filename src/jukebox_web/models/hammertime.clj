(ns jukebox-web.models.hammertime
  (:use [clojure.contrib.string :only (blank?)])
  (:require [jukebox-web.models.db :as db]
            [corroborate.core :as co]))

(def *model* "hammertimes")

(defn validate [hammertime]
  (co/validate hammertime
    :name (co/is-required) 
    :file (co/is-required) 
    :start (co/is-required) 
    :end (co/is-required)))

(defn create! [hammertime]
  (let [errors (validate hammertime)]
    (when (empty? errors)
      (db/insert *model* hammertime))
    errors))

(defn find-by-name [name]
  (first (db/find-by-field *model* :name name)))

(defn find-all []
  (db/find-all *model*))
