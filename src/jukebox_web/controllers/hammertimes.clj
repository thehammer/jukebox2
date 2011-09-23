(ns jukebox-web.controllers.hammertimes
  (:require [jukebox-player.core :as player]
            [jukebox-web.views.hammertimes :as view]
            [jukebox-web.models.hammertime :as hammertime]))

(defn index [request]
  (view/index request (hammertime/find-all)))

(defn create-form [request]
  (view/create request {}))

(defn create [request]
  (let [errors (hammertime/create! (:params request))]
    (if (empty? errors)
      {:status 302 :headers {"Location" "/playlist"}}
      (view/create request errors))))

(defn play [request]
  (let [{:keys [file start end]} (hammertime/find-by-name (-> request :params :name))]
    (player/hammertime! file start end))
    {:status 302 :headers {"Location" "/hammertimes"}})
