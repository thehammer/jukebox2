(ns jukebox-web.models.user
  (:use [clojure.contrib.string :only (blank?)]))

(def *users* (atom []))

(defn validate [user]
  (conj {}
    (when (blank? (:password user)) [:password "is required"])
    (when (blank? (:avatar user)) [:avatar "is required"])
    (when (blank? (:login user)) [:login "is required"])))

(defn sign-up! [user-args]
  (swap! *users* conj user-args))

(defn find-by-login [login]
  (first (filter #(= login (:login %)) @*users*)))

(defn authenticate [login password]
  (let [user (find-by-login login)]
    (= password (:password user))))
