(ns jukebox-web.models.factory)

(defn user [overrides]
  (let [defaults {:login "billy" :password "secret" :avatar "http://example.com/avatar"}]
    (merge defaults overrides)))
