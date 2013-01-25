(ns jukebox.ajax
  (:require [goog.net.XhrIo :as xhr]
            [domina :as dom]))

(defn build-then-replace [dom-id template-fn after-replace-fn]
  (fn [response]
     (dom/replace-children! (dom/by-id dom-id)
                            (-> (.-target response) .getResponseJson js->clj template-fn))
     (after-replace-fn)))

(defn replace-remote
  ([dom-id url template-fn] (replace-remote dom-id url template-fn (fn [])))
  ([dom-id url template-fn after-replace-fn]
   (xhr/send url (build-then-replace dom-id template-fn after-replace-fn))))
