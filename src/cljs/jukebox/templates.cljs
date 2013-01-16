(ns jukebox.templates
  (:require [domina :as dom]
            [jukebox.util :as util]))

(defn mustache [template, data]
  (dom/log (util/map->js-obj data))
  ((.-render js/Mustache) template (util/map->js-obj data)))

(defn replace-with-template [dom-id template-id data]
  (dom/replace-children! (dom/by-id dom-id)
                         (mustache (dom/html (dom/by-id template-id)) data)))
