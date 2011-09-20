(ns jukebox-web.models.user-spec
  (:require [jukebox-web.models.user :as user]
            [jukebox-web.models.factory :as factory])
  (:use [speclj.core]
        [jukebox-web.spec-helper])
  (:import [org.mindrot.jbcrypt BCrypt]))

(describe "sign-up!"
  (with-database-connection)

  (it "stores a new user"
    (let [errors (user/sign-up! {:login "hammer" :password "dont hurt em" :avatar "http://gravitar.org/somepic"})
          hammer (user/find-by-login "hammer")]
      (should (empty? errors))
      (should= "http://gravitar.org/somepic" (:avatar hammer))))

  (it "encrypts the password before storing"
    (let [errors (user/sign-up! {:login "hammer" :password "dont hurt em" :avatar "http://gravitar.org/somepic"})
          hammer (user/find-by-login "hammer")
          hashed-password (:password hammer)]
      (should (BCrypt/checkpw "dont hurt em" hashed-password))))

  (it "sets the users skip-count to zero"
    (user/sign-up! (factory/user {:login "test"}))
    (should= 0 (:skip-count (user/find-by-login "test"))))

  (it "enables the user"
    (user/sign-up! (factory/user {:login "test"}))
    (should (:enabled (user/find-by-login "test"))))

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
  (with-database-connection)

  (it "returns true if credentials are valid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should (user/authenticate "a" "p")))

  (it "returns false if credentials are invalid"
    (user/sign-up! (factory/user {:login "a" :password "p"}))
    (should-not (user/authenticate "a" "wrong"))))

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

(describe "find-all"
  (with-database-connection)

  (it "returns all the users"
    (user/sign-up! (factory/user {}))
    (user/sign-up! (factory/user {}))
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
