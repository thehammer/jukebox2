(ns jukebox-web.models.factory)

(defn user [overrides]
  (let [defaults {:login "billy" :password "secret"
                  :password-confirmation "secret" :avatar "http://example.com/avatar"}]
    (merge defaults overrides)))

(defn hammertime
  ([] (hammertime {}))
  ([overrides]
   (let [defaults {:name "butts" :file "holdon.mp3" :start 1 :end 5 :schedule "* * * * *"}]
     (merge defaults overrides))))
