(ns jukebox-web.util.crypt-spec
  (:require [jukebox-web.util.crypt :as crypt])
  (:use [speclj.core]
        [jukebox-web.spec-helper])
  (:import [org.mindrot.jbcrypt BCrypt]))

(describe "hash-password"
  (it "hashes the value using bcrypt"
    (should
      (BCrypt/checkpw "test value" (crypt/hash-password "test value")))))

(describe "matches?"
  (it "is true if the raw text matches the hashed text"
    (let [hashed-password (crypt/hash-password "test value")]
      (should (crypt/matches? "test value" hashed-password))))

  (it "is false if the raw text does not match the hashed text"
    (let [hashed-password (crypt/hash-password "test value")]
      (should-not (crypt/matches? "bad value" hashed-password)))))
