(ns jukebox-web.controllers.users
  (:require [jukebox-web.views.users :as view]
            [jukebox-web.models.user :as user]))

(defn authenticate [request]
  (let [{:keys [login password]} (:params request)]
    (if (user/authenticate login password)
      {:status 302 :headers {"Location" "/playlist"}}
      (view/sign-in))))

(defn sign-in [request]
  (view/sign-in))

(defn sign-up-form [request]
  (view/sign-up {}))

(defn sign-up [request]
  (let [errors (user/sign-up! (:params request))]
    (if (empty? errors)
      {:status 302 :headers {"Location" "/playlist"}}
      (view/sign-up errors))))
