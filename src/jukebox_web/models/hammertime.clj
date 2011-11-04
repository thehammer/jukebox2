(ns jukebox-web.models.hammertime
  (:use [clojure.contrib.string :only (blank?)])
  (:require [jukebox-player.core :as player]
            [jukebox-web.models.db :as db]
            [corroborate.core :as co]
            [jukebox-web.models.library :as library])
  (:import [it.sauronsoftware.cron4j Scheduler]))

(def *model* "hammertimes")

(def *scheduled-tasks* (atom []))
(def *scheduler* (Scheduler.))

(defn validate [hammertime]
  (co/validate hammertime
    :name (co/is-required)
    :file (co/is-required)
    :start (co/is-required)
    :end (co/is-required)
    :schedule (co/is-required)))

(defn create! [hammertime]
  (let [errors (validate hammertime)]
    (when (empty? errors)
      (db/insert *model* hammertime))
    errors))

(defn delete-by-id! [id]
  (db/delete *model* id))

(defn find-by-id [id]
  (first (db/find-by-field *model* :id id)))

(defn find-by-name [name]
  (first (db/find-by-field *model* :name name)))

(defn find-all []
  (db/find-all *model*))

(defn update! [hammertime hammertime-args]
  (let [errors (validate (conj hammertime hammertime-args))]
    (if (empty? errors)
      (db/update *model* hammertime-args :id (:id hammertime)))
    errors))

(defn play! [hammertime]
  (let [{:keys [file start end]} hammertime]
    (player/hammertime! (library/file-on-disk file) (read-string start) (read-string end))))

(defn- schedule [hammertime]
  (let [id (.schedule *scheduler* (:schedule hammertime) #(do (println "playing hammertime:" hammertime) (play! hammertime)))]
    (swap! *scheduled-tasks* conj id)))

(defn schedule-all! []
  (doseq [hammertime (find-all)]
    (schedule hammertime))
  (.start *scheduler*))
