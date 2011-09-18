(ns jukebox-web.controllers.users-spec
  (:require [jukebox-web.controllers.users :as users-controller])
  (:require [jukebox-web.models.user :as user])
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "authenticate"
  (with-database-connection)

  (it "redirects to the playlist if credentials are correct"
    (let [bob (user/sign-up! (factory/user {:login "bob" :password "pass"}))
          request {:params {:login "bob" :password "pass"}}
          response (users-controller/authenticate request)]
      (should= 302 (:status response))
      (should= {"Location" "/playlist"} (:headers response))))

  (it "sets the current user in the session"
    (let [bob (user/sign-up! (factory/user {:login "bob" :password "pass"}))
          request {:params {:login "bob" :password "pass"}}
          response (users-controller/authenticate request)]
      (should= "bob" (-> response :session :current-user))
    ))

  (it "rerenders the sign-in page if the credentials are incorrect"
    (let [bob (user/sign-up! (factory/user {:login "bob" :password "pass"}))
          request {:params {:login "bob" :password "fat-finger"}}
          response (users-controller/authenticate request)]
      (should= nil (:status response))
      (should= nil (:headers response)))))

(describe "sign-up"
  (with-database-connection)

  (it "saves a valid user"
    (let [request {:params (factory/user {:login "test"})}
          response (users-controller/sign-up request)]
      (should-not (nil? (user/find-by-login "test")))))

  (it "redirects valid requests to the playlist"
    (let [request {:params (factory/user {:login "test"})}
          response (users-controller/sign-up request)]
      (should= 302 (:status response))
      (should= {"Location" "/playlist"} (:headers response))))

  (it "rerenders the sign-up page if the user is invalid"
    (let [request {:params (factory/user {:login ""})}
          response (users-controller/sign-up request)]
      (should= nil (:status response))
      (should= nil (:headers response)))))

(describe "sign-out"
  (it "removes the current user from the session"))
