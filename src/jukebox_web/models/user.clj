(ns jukebox-web.models.user
  (:use [clojure.contrib.string :only (blank?)]
        [jukebox-web.models.db :as db]))

(defn validate [user]
  (conj {}
    (when (blank? (:password user)) [:password "is required"])
    (when (blank? (:avatar user)) [:avatar "is required"])
    (when (blank? (:login user)) [:login "is required"])))

(defn sign-up! [user-args]
  (db/insert "user" user-args))

(defn find-by-login [login]
  (first (db/find-by-field "user" "login" login)))

(defn authenticate [login password]
  (let [user (find-by-login login)]
    (= password (:password user))))
