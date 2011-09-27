(ns jukebox-web.controllers.hammertimes
  (:require [jukebox-player.core :as player]
            [jukebox-web.views.hammertimes :as view]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.library :as lib]))

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
    (player/hammertime! (lib/file-on-disk file) (read-string start) (read-string end)))
    {:status 302 :headers {"Location" "/hammertimes"}})

(defn delete [request]
  (hammertime/delete-by-id! (-> request :params :id))
  {:status 302 :headers {"Location" "/hammertimes"}})

(defn browse-root [request]
  (view/browse request "Hammertimes" (lib/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (lib/list-directory path)]
    (view/browse request path files)))
