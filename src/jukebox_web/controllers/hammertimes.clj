(ns jukebox-web.controllers.hammertimes
  (:require [jukebox-web.views.hammertimes :as view]
            [jukebox-web.models.hammertime :as hammertime]
            [jukebox-web.models.library :as lib]))

(defn index [request]
  (view/index request (hammertime/find-all)))

(defn create-form [request]
  (view/create request {}))

(defn create [request]
  (let [errors (hammertime/create! (:params request))]
    (if (empty? errors)
      (do
        (hammertime/schedule-all!)
        {:status 302 :headers {"Location" "/playlist"}})
      (view/create request errors))))

(defn play [request]
  (hammertime/play! (hammertime/find-by-name (-> request :params :name)))
  {:status 302 :headers {"Location" "/hammertimes"}})

(defn delete [request]
  (hammertime/delete-by-id! (-> request :params :id))
  {:status 302 :headers {"Location" "/hammertimes"} :flash {:success "Hammertime successfully deleted"}})

(defn edit [request]
  (view/edit request (hammertime/find-by-id (-> request :params :id)) nil))

(defn browse-root [request]
  (view/browse request "Hammertimes" (lib/list-directory)))

(defn browse [request]
  (let [path (-> request :params :path)
        files (lib/list-directory path)]
    (view/browse request path files)))

(defn update [request]
  (let [hammertime (hammertime/find-by-id (-> request :params :id))
        errors (hammertime/update! hammertime (:params request))]
    (if (empty? errors)
      (do
        (hammertime/schedule-all!)
        {:status 302 :headers {"Location" "/hammertimes"}})
      (view/edit request hammertime errors))))
