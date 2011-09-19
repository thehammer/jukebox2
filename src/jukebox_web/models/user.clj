(ns jukebox-web.models.user
  (:require [jukebox-web.models.db :as db])
  (:use [clojure.contrib.string :only (blank?)]))

(def *model* :user)

(defn validate [user]
  (conj {}
    (when (blank? (:password user)) [:password "is required"])
    (when (blank? (:avatar user)) [:avatar "is required"])
    (when (blank? (:login user)) [:login "is required"])))

(defn sign-up! [user-args]
  (let [errors (validate user-args)]
    (when (empty? errors)
      (db/insert *model* (merge {:skip-count 0} user-args)))
    errors))

(defn find-by-login [login]
  (first (db/find-by-field *model* "login" login)))

(defn find-all []
  (db/find-all *model*))

(defn authenticate [login password]
  (let [user (find-by-login login)]
    (= password (:password user))))

(defn increment-skip-count! [login]
  (let [user (find-by-login login)
        skip-count (:skip-count user)]
  (db/update *model* {:skip-count (inc skip-count)} "login" login)))
