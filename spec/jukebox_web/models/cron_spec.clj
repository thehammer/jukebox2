(ns jukebox-web.models.cron-spec
  (:require [jukebox-web.models.cron :as cron])
  (:use [speclj.core]))

(describe "cron"
  (before (cron/clear!))

  (describe "schedule!"
    (it "starts with nothing scheduled"
      (should= 0 (count @cron/*scheduled-tasks*))
      (should= [] (cron/scheduled-patterns)))

    (it "can schedule functions"
      (cron/schedule! "1 2 * * *" identity)
      (cron/schedule! "3 4 * * *" identity)
      (should= 2 (count @cron/*scheduled-tasks*))
      (should= ["1 2 * * *", "3 4 * * *"] (cron/scheduled-patterns))))

  (describe "clear!"
    (it "clears out scheduled tasks"
      (cron/schedule! "1 2 * * *" identity)
      (cron/clear!)
      (should= 0 (count @cron/*scheduled-tasks*))
      (should= [] (cron/scheduled-patterns)))))
