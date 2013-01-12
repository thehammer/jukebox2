(ns jukebox-web.models.user
  (:require [corroborate.core :as co]
            [jukebox-web.models.db :as db]
            [jukebox-web.models.library :as library]
            [jukebox-web.util.crypt :as crypt]
            [jukebox-web.util.reserved-names :as reserved-names]
            [jukebox-web.util.url :as url])
  (:use [clojure.contrib.string :only (blank?)]))

(def *model* :users)

(defn- merge-defaults [user-args]
  (let [defaults {:skip_count 0 :enabled true}]
    (merge defaults user-args)))

(defn- hash-password [{password :password :as user-args}]
  (assoc user-args :password (crypt/hash-password password)))

(defn- build-user [user-args]
  (dissoc (-> user-args merge-defaults hash-password) :password-confirmation))

(defn find-by-id [id]
  (first (db/find-by-field *model* "id" id)))

(defn find-by-login [login]
  (first (db/find-by-field *model* "login" login)))

(defn find-all []
  (db/find-all ["SELECT * FROM users"]))

(defn find-random []
  (rand-nth (find-all)))

(defn find-enabled []
  (db/find-by-field *model* "enabled" true))

(defn canAdd? [user]
  (not (nil? user)))

(defn isRequester? [track user]
  (let [requester (:login (:requester track))]
    (if (contains? #{nil "(randomizer)" "(guest)"}  requester)
      (not (nil? user))
      (= requester (:login user)))))

(co/defvalidator validate
  :password (co/is-required)
  :login (co/is-required))

(co/defvalidator validate-new-user
  :password-confirmation (co/is-confirmed-by :password)
  :login #(if-not (nil? (find-by-login (%2 %1))) "must be unique")
  :login (co/is-excluded-from (reserved-names/restricted) "can't be a reserved name"))

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

(defn base-avatar-url [user]
  (if (empty? (:avatar user))
    "http://www.gravatar.com/avatar/no-avatar"
    (:avatar user)))

(defn avatar-url
  ([user] (avatar-url user {}))
  ([user params]
    (let [default-params {:s 35 :d "mm"}
          query-params (merge default-params params)
          base-url (base-avatar-url user)]
      (str base-url "?" (url/map-to-query-string query-params)))))

(defn increment-skip-count! [login]
  (let [user (find-by-login login)
        skip-count (:skip_count user)]
  (db/update *model* {:skip_count (inc skip-count)} "login" login)))

(defn toggle-enabled! [login]
  (let [{:keys [id enabled]} (find-by-login login)]
    (db/update *model* {:enabled (not (.booleanValue enabled))} :id id)))

(defn update! [user user-args]
  (let [errors (validate (conj user user-args))]
    (if (empty? errors)
      (db/update *model* user-args :id (:id user)))
    errors))

(defn count-songs [user]
  (count (library/all-tracks (:login user))))

(defn enabled? [login]
  (if-let [user (find-by-login login)]
     (.booleanValue (:enabled user))))

(defn delete! [user]
  (db/delete *model* (:id user)))
