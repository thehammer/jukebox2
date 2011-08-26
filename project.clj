(defproject
  jukebox "1.0.0-SNAPSHOT"
  :description "Jukebox"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.5"]]
  :dev-dependencies [[lein-ring "0.4.5"]
                     [lein-javac "1.2.1-SNAPSHOT"]]
  :java-source-path [["src/jukebox_player"]]
  :ring {:handler jukebox-web.core/app})
