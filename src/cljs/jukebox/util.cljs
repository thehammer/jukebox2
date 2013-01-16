(ns jukebox.util)

(defn map->js-obj
    "makes a javascript map from a clojure one"
    [cljmap]
    (let [out (js-obj)]
          (doall (map #(aset out (name (first %)) (second %)) cljmap))
          out))

(defn mustache [template, data]
  ((.-render js/Mustache) template (map->js-obj data)))
