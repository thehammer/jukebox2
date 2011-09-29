(defproject
  jukebox "1.0.0-SNAPSHOT"
  :description "Jukebox"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.jaudiotagger/jaudiotagger "2.0.1"]
                 [fleetdb "0.3.1"]
                 [corroborate "0.1.0"]
                 [aleph "0.2.0-beta2-SNAPSHOT"]
                 [compojure "0.6.5"]
                 [org.mindrot/jbcrypt "0.3m"]]
  :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]
                     [speclj "1.2.0"]]
  :java-source-path [["src/jukebox_player"]]
  :main jukebox-web.core
  :test-path "spec/")
