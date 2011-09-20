(ns jukebox-web.models.hammertime
  (:use [clojure.contrib.string :only (blank?)])
  (:require [jukebox-web.models.db :as db]))

(def *model* "hammertimes")

(defn validate [hammertime]
  (conj {}
    (when (blank? (:name hammertime)) [:name "is required"])
    (when (blank? (:file hammertime)) [:file "is required"])
    (when (nil? (:start hammertime)) [:start "is required"])
    (when (nil? (:end hammertime)) [:end "is required"])))

(defn create! [hammertime]
  (let [errors (validate hammertime)]
    (when (empty? errors)
      (db/insert *model* hammertime))
    errors))

(defn find-by-name [name]
  (first (db/find-by-field *model* :name name)))
