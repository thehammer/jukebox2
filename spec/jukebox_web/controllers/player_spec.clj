(ns jukebox-web.controllers.player-spec
  (:require [jukebox-web.controllers.player :as player-controller]
            [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory]
            [jukebox-web.models.library :as library]
            [jukebox-web.models.playlist :as playlist])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "with-database-connection"
  (with-test-music-library)
  (with-database-connection)

  (before
    (playlist/reset-state!)
    (playlist/add-song! "user/artist/album/track.mp3")
    (playlist/next-track nil))

  (describe "skip"
    (it "increments skip-count for the current user"
      (let [bob (user/sign-up! (factory/user {:login "bob"}))
            request {:session {:current-user "bob"} :headers {"accept" "text/html"}}
            response (player-controller/skip request)]
        (should= 302 (:status response))
        (should= 1 (:skip-count (user/find-by-login "bob")))))

    (it "increments skip-count for the current song"
      (let [bob (user/sign-up! (factory/user {:login "bob"}))
            request {:session {:current-user "bob"} :headers {"accept" "text/html"}}
            last-song (playlist/current-song)
            response (player-controller/skip request)]
        (should= 302 (:status response))
        (should= 1 (library/skip-count (:song last-song)))))
