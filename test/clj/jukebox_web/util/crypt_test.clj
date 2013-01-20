(ns jukebox-web.util.crypt-test
  (:require [jukebox-web.util.crypt :as crypt])
  (:use [clojure.test])
  (:import [org.mindrot.jbcrypt BCrypt]))

(deftest hashes-password
  (testing "hashes the value using bcrypt"
    (is (BCrypt/checkpw "test value" (crypt/hash-password "test value")))))

(deftest matches-hashes-password
  (testing "is true if the raw text matches the hashed text"
    (let [hashed-password (crypt/hash-password "test value")]
      (is (crypt/matches? "test value" hashed-password))))

  (testing "is false if the raw text does not match the hashed text"
    (let [hashed-password (crypt/hash-password "test value")]
      (is (not (crypt/matches? "bad value" hashed-password))))))
