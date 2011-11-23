(ns jukebox-web.controllers.stats-spec
  (:require [jukebox-web.controllers.stats :as stats-controller]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.user :as user]
            [clojure.contrib.string :as string]
            [clj-json.core :as json])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "stats"
  (with-database-connection)
  (with-test-music-library)

  (describe "index"
    (it "renders successfully"
      (let [response (stats-controller/index nil)]
        (should (string/substring? "Stats" response))
        (should (string/substring? "Most Played Tracks" response))
        (should (string/substring? "Most Popular Artists" response)))))

  (describe "song-counts"
    (it "returns an array of users and song counts"
      (user/sign-up! (factory/user {:login "user"}))
      (user/sign-up! (factory/user {:login "user2"}))
      (user/sign-up! (factory/user {:login "user3"}))
      (let [response (stats-controller/song-counts nil)]
        (should= [["user" 3] ["user2" 1] ["user3" 0]] (json/parse-string (:body response)))))))
