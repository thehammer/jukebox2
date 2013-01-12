(ns jukebox-web.controllers.player-test
  (:require [jukebox-web.controllers.player :as player-controller]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist :as playlist])
  (:use [clojure.test]
        [jukebox-web.test-helper]))

(use-fixtures :each
              with-database-connection
              with-test-music-library
              (fn [f]
                      (playlist/reset-state!)
                      (playlist/add-song! "user/artist/album/track.mp3")
                      (playlist/next-track nil)
                      (f)))

(deftest skip-tracks
  (let [bob (user/sign-up! (factory/user {:login "bob"}))
        request {:session {:current-user "bob"} :headers {"accept" "text/html"}}
        last-song (playlist/current-song)
        response (player-controller/skip request)]
    (testing "increments skip-count for the current user"
      (is (= 302 (:status response)))
      (is (= 1 (:skip_count (user/find-by-login "bob")))))

    (testing "increments skip-count for the current song"
      (is (= 302 (:status response)))
      (is (= 1 (library/skip-count (:song last-song)))))))
