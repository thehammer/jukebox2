(ns jukebox-web.models.cron-test
  (:require [jukebox-web.models.cron :as cron])
  (:use [clojure.test]))

(use-fixtures :each (fn [f] (cron/clear!) (f)))

(deftest schedule-starts-with-nothing
  (is (= 0 (count @cron/*scheduled-tasks*)))
  (is (= [] (cron/scheduled-patterns))))

(deftest can-schedule-functions
  (cron/schedule! "1 2 * * *" identity)
  (cron/schedule! "3 4 * * *" identity)
  (is (= 2 (count @cron/*scheduled-tasks*)))
  (is (= ["1 2 * * *", "3 4 * * *"] (cron/scheduled-patterns))))

(deftest clears-out-scheduled-tasks
  (cron/schedule! "1 2 * * *" identity)
  (cron/clear!)
  (is (= 0 (count @cron/*scheduled-tasks*)))
  (is (= [] (cron/scheduled-patterns))))
