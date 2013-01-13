(ns jukebox-web.util.url
  (:require [clojure.string :as str]))

(defn map-to-query-string [m]
    (str/join "&" (map (fn [[k v]] (str (name k) "=" v)) m)))
