(ns jukebox-web.util.crypt
  (:import [org.mindrot.jbcrypt BCrypt]))

(defn hash-password [value]
  (BCrypt/hashpw value (BCrypt/gensalt)))

(defn matches? [value hashed-value]
  (BCrypt/checkpw value hashed-value))
