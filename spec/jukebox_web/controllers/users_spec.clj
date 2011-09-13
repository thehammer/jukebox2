(ns jukebox-web.controllers.users-spec
  (:require [jukebox-web.controllers.users :as users-controller])
  (:require [jukebox-web.models.user :as user])
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]))

(describe "authenticate"
  (it "redirects to the playlist if credentials are correct"
    (let [bob (user/sign-up! (factory/user {:login "bob" :password "pass"}))
          request {:params {:login "bob" :password "pass"}}
          response (users-controller/authenticate request)]
      (should= 302 (:status response))
      (should= {"Location" "/playlist"} (:headers response))))

  (it "rerenders the sign-in page if the credentials are incorrect"
    (let [bob (user/sign-up! (factory/user {:login "bob" :password "pass"}))
          request {:params {:login "bob" :password "fat-finger"}}
          response (users-controller/authenticate request)]
      (should= nil (:status response))
      (should= nil (:headers response)))))
