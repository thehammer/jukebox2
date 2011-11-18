(ns jukebox-web.models.cron
  (:import [it.sauronsoftware.cron4j Scheduler]))

(def *scheduled-tasks* (ref []))
(def *scheduler* (ref (Scheduler.)))

(defn clear! []
  (dosync
    (when (.isStarted @*scheduler*)
      (.stop @*scheduler*))
    (ref-set *scheduler* (Scheduler.))
    (ref-set *scheduled-tasks* [])
    (.start @*scheduler*)))

(defn scheduled-patterns []
  (let [pattern #(str (.getSchedulingPattern @*scheduler* %))]
    (map pattern @*scheduled-tasks*)))

(defn schedule! [pattern function]
  (let [id (.schedule @*scheduler* pattern function)]
    (dosync
      (alter *scheduled-tasks* conj id))))
