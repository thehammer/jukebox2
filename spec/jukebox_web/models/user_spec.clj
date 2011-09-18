(ns jukebox-web.models.playlist-spec
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper]))

(describe "sign-up!"
  (around [spec] (with-database-connection spec))

  (it "stores a new user"
    (let [errors (user/sign-up! {:login "hammer" :password "dont hurt em" :avatar "http://gravitar.org/somepic"})
          hammer (user/find-by-login "hammer")]
      (should (empty? errors))
      (should= "http://gravitar.org/somepic" (:avatar hammer))))

  (it "returns errors if the user is not valid"
    (let [errors (user/sign-up! {})]
      (should-not (empty? errors))
      (should= "is required" (:login errors)))))


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
  (around [spec] (with-database-connection spec))

  (it "returns true if credentials are valid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should (user/authenticate "a" "p")))

  (it "returns false if credentials are invalid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should-not (user/authenticate "a" "wrong"))))

(run-specs)