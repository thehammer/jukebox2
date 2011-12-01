(ns jukebox-web.util.json
  (:require [clj-json.core :as json]))

(defn request? [accept]
  (when nil? accept false)
  (let [matches (re-find #"json" accept)]
    (not (nil? matches))))

(defn response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})
