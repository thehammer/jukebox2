(defproject
  jukebox "1.0.0-SNAPSHOT"
  :description "Jukebox"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.jaudiotagger/jaudiotagger "2.0.1"]
                 [fleetdb "0.3.1"]
                 [corroborate "0.1.0"]
                 [compojure "0.6.5"]
                 [ring-json-params "0.1.3"]
                 [clj-json "0.4.3"]
                 [org.mindrot/jbcrypt "0.3m"]]
  :dev-dependencies [[lein-ring "0.4.5"]
                     [lein-javac "1.2.1-SNAPSHOT"]
                     [speclj "1.5.2"]
                     [speclj-growl "1.0.0-SNAPSHOT"]]
  :java-source-path [["src/jukebox_player"]]
  :main jukebox-web.core
  :ring {:handler jukebox-web.core/app}
  :test-path "spec/")
