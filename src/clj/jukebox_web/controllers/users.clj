(ns jukebox-web.controllers.users
  (:require [jukebox-web.views.users :as view]
            [jukebox-web.models.user :as user]))

(defn authenticate [request]
  (let [{:keys [login password]} (:params request)]
    (if (user/authenticate login password)
      {:status 302 :headers {"Location" "/playlist"} :session {:current-user login}}
      {:status 302 :headers {"Location" "/playlist"}})))

(defn edit [request]
  (let [user (user/find-by-id (-> request :params :id))]
    (view/edit request user nil)))

(defn update [request]
  (let [user (user/find-by-id (-> request :params :id))
        errors (user/update! user {:avatar (-> request :params :avatar)})]
    (if (empty? errors)
      {:status 302 :headers {"Location" "/users"}}
      (view/edit request user errors))))

(defn sign-out [request]
  {:status 302 :headers {"Location" "/playlist"} :session {:current-user nil}})

(defn sign-up-form [request]
  (view/sign-up request {}))

(defn sign-up [request]
  (let [[user errors] (user/sign-up! (:params request))]
    (if (empty? errors)
      {:status 302 :headers {"Location" "/playlist"} :session {:current-user (-> request :params :login)}}
      (view/sign-up request errors))))

(defn index [request]
  (view/index request (user/find-all)))

(defn toggle-enabled [request]
  (user/toggle-enabled! (-> request :params :login))
  {:status 302 :headers {"Location" "/users"}})
