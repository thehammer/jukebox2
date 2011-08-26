(defproject
  jukebox-web "1.0.0-SNAPSHOT"
  :description "Jukebox web server"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.5"]]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler jukebox-web.core/app})
