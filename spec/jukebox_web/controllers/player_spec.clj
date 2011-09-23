(ns jukebox-web.controllers.player-spec
  (:require [jukebox-web.controllers.player :as player-controller])
  (:require [jukebox-web.models.user :as user])
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "with-database-connection"
  (with-database-connection)

  (describe "skip"
    (it "increments skip-count for the current user"
      (let [bob (user/sign-up! (factory/user {:login "bob"}))
            request {:session {:current-user "bob"}}
            response (player-controller/skip request)]
        (should= 302 (:status response))
        (should= 1 (:skip-count (user/find-by-login "bob")))))

    (it "does nothing unless you're logged in"
      (let [request {:session {}}
            response (player-controller/skip request)]
        (should= 302 (:status response))))))
