(ns jukebox-web.models.hammertime
  (:require [jukebox-player.core :as player]
            [jukebox-web.models.db :as db]
            [corroborate.core :as co]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.cron :as cron])
  (:import [it.sauronsoftware.cron4j Scheduler]))

(def *model* "hammertimes")

(def *scheduled-tasks* (ref []))
(def *scheduler* (ref (Scheduler.)))

(co/defvalidator validate
  :name (co/is-required)
  :file (co/is-required)
  :start (co/is-required)
  :end (co/is-required)
  :schedule (co/is-required))

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
  (let [{:keys [file start end pause]} hammertime]
    (player/hammertime! (library/file-on-disk file) (read-string start) (read-string end) pause)))

(defn- schedule [hammertime]
  (cron/schedule! (:schedule hammertime) #(do (println "playing hammertime:" hammertime) (play! hammertime))))

(defn schedule-all! []
  (cron/clear!)
  (doseq [hammertime (find-all)]
    (schedule hammertime)))
