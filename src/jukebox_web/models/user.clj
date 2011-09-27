(ns jukebox-web.models.user
  (:require [corroborate.core :as co]
            [jukebox-web.models.db :as db]
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

(defn find-by-id [id]
  (first (db/find-by-field *model* "id" id)))

(defn find-by-login [login]
  (first (db/find-by-field *model* "login" login)))

(defn find-all []
  (db/find-all *model*))

(defn validate [user]
  (co/validate user
    :password (co/is-required)
    :avatar (co/is-required)
    :login (co/is-required)))

(defn validate-new-user [user]
  (co/validate user
    :password-confirmation (co/is-confirmed-by :password)
    :login #(if-not (nil? (find-by-login (%2 %1))) "must be unique")))

(defn validate-for-sign-up [user]
  (co/validate-staged user
    validate
    validate-new-user))

(defn sign-up! [user-args]
  (let [errors (validate-for-sign-up user-args)]
    (if (empty? errors)
      [(db/insert *model* (build-user user-args)) errors]
      [nil errors])))

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

(defn update! [user user-args]
  (let [errors (validate (conj user user-args))]
    (if (empty? errors)
      (db/update *model* user-args :id (:id user)))
    errors))

