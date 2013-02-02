(ns jukebox-web.controllers.now-playing-test
  (:require [cheshire.core :as json-parser]
            [jukebox-web.controllers.now-playing :as now-playing]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist :as playlist])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each with-database-connection with-test-music-library)

(deftest returns-the-current-status-of-jukebox
  (let [response (now-playing/current {:headers {"accept" "application/json"}})
        json (json-parser/parse-string (:body response))]
    (testing "is a json response"
      (is (= "application/json" (get-in response [:headers "Content-Type"]))))
    (testing "returns the current state of the player"
      (is (= false (get-in json ["player" "playing?"])))
      (is (= 0.0 (get-in json ["player" "current-time"]))))))

(deftest returns-the-current-playlist
  (playlist/reset-state!)
  (playlist/add-song! (library/find-by-id 1))
  (playlist/add-song! (library/find-by-id 2))
  (playlist/next-track "")
  (let [response (now-playing/current {:headers {"accept" "application/json"}})
        json (json-parser/parse-string (:body response))]
    (testing "returns the current song"
      (is (= "artist" (get-in json ["current-song" "artist"])))
      (is (= "album" (get-in json ["current-song" "album"])))
      (is (= "track" (get-in json ["current-song" "title"])))
      (is (= "randomizer" (get-in json ["current-song" "requester"])))
      (is (= "user" (get-in json ["current-song" "owner"])))
      (is (= 0 (get-in json ["current-song" "skip_count"])))
      (is (= 0 (get-in json ["current-song" "play_count"])))
      (is (not (nil? (get-in json ["current-song" "large_image"]))))
      (is (not (nil? (get-in json ["current-song" "xlarge_image"])))))
    (testing "returns the queued songs"
      (is (= 1 (count (get-in json ["queued-songs"]))))
      (is (= "artist" (get-in json ["queued-songs" 0 "artist"])))
      (is (= "album" (get-in json ["queued-songs" 0 "album"])))
      (is (= "track2" (get-in json ["queued-songs" 0 "title"])))
      (is (= "randomizer" (get-in json ["queued-songs" 0 "requester"])))
      (is (= "user" (get-in json ["queued-songs" 0 "owner"])))
      (is (= 0 (get-in json ["queued-songs" 0 "skip_count"])))
      (is (= 0 (get-in json ["queued-songs" 0 "play_count"])))
      (is (not (nil? (get-in json ["queued-songs" 0 "large_image"]))))
      (is (not (nil? (get-in json ["queued-songs" 0 "xlarge_image"])))))))

(deftest returns-the-current-user
  (let [response (now-playing/current {:headers {"accept" "application/json"} :session {:current-user "user"}})
        json (json-parser/parse-string (:body response))]
    (testing "returns the current user"
      (is (= #{"login" "avatar" "id" "skip_count"} (-> (get json "current-user") keys set)))
      (is (= "user" (get-in json ["current-user" "login"])))
      (is (= 0 (get-in json ["current-user" "skip_count"])))
      (is (= "http://example.com/avatar?d=mm&s=35" (get-in json ["current-user" "avatar"]))))))

