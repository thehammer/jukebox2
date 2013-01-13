(ns jukebox-web.util.encoding
  (:import [java.security MessageDigest]))

(defn sha256
  "Generates a SHA-256 hash of the given input plaintext."
  [input]
  (let [md (MessageDigest/getInstance "SHA-256")]
    (. md update (.getBytes input))
    (let [digest (.digest md)]
      (apply str (map #(Integer/toHexString (bit-and % 0xff)) digest)))))
