(ns jukebox-web.models.user-spec
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper])
  (:import [org.mindrot.jbcrypt BCrypt]))

(describe "sign-up!"
  (with-database-connection)

  (it "stores a new user"
    (let [[user errors] (user/sign-up! (factory/user {:login "hammer" :avatar "http://gravitar.org/somepic"}))
          hammer (user/find-by-login "hammer")]
      (should (empty? errors))
      (should= "http://gravitar.org/somepic" (:avatar hammer))))

  (it "encrypts the password before storing"
    (let [[hammer errors] (user/sign-up! (factory/user {:password "pass" :password-confirmation "pass"}))
          hashed-password (:password hammer)]
      (should (BCrypt/checkpw "pass" hashed-password))))

  (it "sets the users skip-count to zero"
    (user/sign-up! (factory/user {:login "test"}))
    (should= 0 (:skip-count (user/find-by-login "test"))))

  (it "enables the user"
    (user/sign-up! (factory/user {:login "test"}))
    (should (:enabled (user/find-by-login "test"))))

  (it "returns errors if the user is not valid"
    (let [[user errors] (user/sign-up! {})]
      (should-not (empty? errors))
      (should= ["is required"] (:login errors)))))

(describe "validate"
  (with-database-connection)

  (it "requires a login"
    (let [errors (user/validate {:password "" :avatar "http://foo.com"})]
      (should= ["is required"] (:login errors))))

  (it "requires an avatar"
    (let [errors (user/validate {:login "foo" :password ""})]
      (should= ["is required"] (:avatar errors))))

  (it "requires a password"
    (let [errors (user/validate {:login "foo" :avatar "http://foo.com"})]
      (should= ["is required"] (:password errors)))))

(describe "validate-for-sign-up"
  (with-database-connection)

  (it "requires the password and confirmation to match"
    (let [errors (user/validate-for-sign-up (factory/user {:password-confirmation "different"}))]
      (should= ["does not match"] (:password-confirmation errors))))

  (it "requires unique login on sign up"
    (user/sign-up! (factory/user {}))
    (let [errors (user/validate-for-sign-up (factory/user {}))]
      (should= ["must be unique"] (:login errors)))))

(describe "authenticate"
  (with-database-connection)

  (it "returns true if credentials are valid"
    (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmation "p"}))
    (should (user/authenticate "a" "p")))

  (it "returns false if credentials are invalid"
    (user/sign-up! (factory/user {:login "a" :password "p" :password-confirmatoin "p"}))
    (should-not (user/authenticate "a" "wrong")))

  (it "returns false if login is invalid"
    (should-not (user/authenticate "bad_login" "wrong"))))

(describe "increment-skip-count"
  (with-database-connection)

  (it "increments the skip count for the given user"
    (user/sign-up! (factory/user {:login "test"}))
    (should= 0 (:skip-count (user/find-by-login "test")))
    (user/increment-skip-count! "test")
    (should= 1 (:skip-count (user/find-by-login "test"))))

  (it "increments multiple skips"
    (user/sign-up! (factory/user {:login "test"}))
    (user/increment-skip-count! "test")
    (user/increment-skip-count! "test")
    (should= 2 (:skip-count (user/find-by-login "test")))))

(describe "update!"
  (with-database-connection)

  (it "updates the user"
    (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
    (let [user (user/find-by-login "avatar-test")]
      (user/update! user {:avatar "new-avatar.png"})
      (should= "new-avatar.png" (:avatar (user/find-by-login "avatar-test")))))

  (it "merges user-args with current attributes when running validations"
    (user/sign-up! (factory/user {:login "avatar-test" :avatar "old-avatar.png"}))
    (let [user (user/find-by-login "avatar-test")]
      (user/update! user {:avatar "new-avatar.png"})
      (should= "new-avatar.png" (:avatar (user/find-by-login "avatar-test")))))

  (it "returns errors if validations fail"
    (let [errors (user/update! (factory/user {}) {:avatar ""})]
      (should= ["is required"] (:avatar errors)))))

(describe "find-all"
  (with-database-connection)

  (it "returns all the users"
    (user/sign-up! (factory/user {}))
    (user/sign-up! (factory/user {:login "kenny"}))
    (should= 2 (count (user/find-all)))))

(describe "toggle-enabled"
  (with-database-connection)

  (it "enables a disabled user"
    (user/sign-up! (factory/user {:login "test" :enabled false}))
    (user/toggle-enabled! "test")
    (should (:enabled (user/find-by-login "test"))))

  (it "disables an enabled user"
    (user/sign-up! (factory/user {:login "test" :enabled true}))
    (user/toggle-enabled! "test")
    (should-not (:enabled (user/find-by-login "test")))))

(run-specs)
