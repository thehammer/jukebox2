(ns jukebox-web.models.user
  (:require [jukebox-web.models.db :as db]
            [jukebox-web.util.crypt :as crypt])
  (:use [clojure.contrib.string :only (blank?)]))

(def *model* :user)

(defn- merge-defaults [user-args]
  (let [defaults {:skip-count 0 :enabled true}]
    (merge defaults user-args)))

(defn- hash-password [{password :password :as user-args}]
  (assoc user-args :password (crypt/hash-password password)))

(defn- build-user [user-args]
  (-> user-args merge-defaults hash-password))

(defn validate [user]
  (conj {}
    (when (blank? (:password user)) [:password "is required"])
    (when (blank? (:avatar user)) [:avatar "is required"])
    (when (blank? (:login user)) [:login "is required"])))

(defn sign-up! [user-args]
  (let [errors (validate user-args)]
    (when (empty? errors)
      (db/insert *model* (build-user user-args)))
    errors))

(defn find-by-login [login]
  (first (db/find-by-field *model* "login" login)))

(defn find-all []
  (db/find-all *model*))

(defn authenticate [login password]
  (let [user (find-by-login login)]
    (if user
      (crypt/matches? password (:password user))
      false)))

(defn increment-skip-count! [login]
  (let [user (find-by-login login)
        skip-count (:skip-count user)]
  (db/update *model* {:skip-count (inc skip-count)} "login" login)))

(defn toggle-enabled! [login]
  (let [enabled (:enabled (find-by-login login))]
    (db/update *model* {:enabled (not enabled)} :login login)))
