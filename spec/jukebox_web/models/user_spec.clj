(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.user :as user])
  (:require [jukebox-web.models.factory :as factory])
  (:use [speclj.core]))

(describe "sign-up!"
  (it "stores a new user"
    (user/sign-up! {:login "hammer" :password "dont hurt em" :avatar "http://gravitar.org/somepic"})
    (let [hammer (user/find-by-login "hammer")]
      (should= "http://gravitar.org/somepic" (:avatar hammer)))))


(describe "validate"
  (it "requires a login"
    (let [errors (user/validate {:password "" :avatar "http://foo.com"})]
      (should= "is required" (:login errors))))

  (it "requires an avatar"
    (let [errors (user/validate {:login "foo" :password ""})]
      (should= "is required" (:avatar errors))))

  (it "requires a password"
    (let [errors (user/validate {:login "foo" :avatar "http://foo.com"})]
      (should= "is required" (:password errors)))))

(describe "authenticate"
  (it "returns true if credentials are valid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should (user/authenticate "a" "p")))

  (it "returns false if credentials are invalid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should-not (user/authenticate "a" "wrong"))))

(run-specs)
