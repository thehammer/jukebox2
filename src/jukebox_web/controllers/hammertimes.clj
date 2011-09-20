(ns jukebox-web.controllers.hammertimes
  (:require [jukebox-web.views.hammertimes :as view]
            [jukebox-web.models.hammertime :as hammertime]))

(defn create-form [request]
  (view/create {}))

(defn create [request]
  (let [errors (hammertime/create! (:params request))]
    (if (empty? errors)
      {:status 302 :headers {"Location" "/playlist"}}
      (view/create errors))))
