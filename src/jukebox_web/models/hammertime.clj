(ns jukebox-web.models.hammertime
  (:use [clojure.contrib.string :only (blank?)])
  (:require [jukebox-web.models.db :as db]))

(def *model* "hammertimes")

(defn validate [hammertime]
  (conj {}
    (when (blank? (:name hammertime)) [:name "is required"])
    (when (blank? (:path hammertime)) [:path "is required"])
    (when (nil? (:start hammertime)) [:start "is required"])
    (when (nil? (:end hammertime)) [:end "is required"])))

(defn create! [hammertime]
  (db/insert *model* hammertime))

(defn find-by-name [name]
  (first (db/find-by-field *model* :name name)))
