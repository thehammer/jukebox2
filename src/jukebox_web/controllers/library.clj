(ns jukebox-web.controllers.library
  (:require [jukebox-web.models.library :as lib]))

(defn upload [request]
  (let [{:keys [tempfile filename]} (-> request :params :file)
        user (-> request :params :user)]
    (lib/save-file tempfile user (lib/extension filename)))
  "upload complete")
