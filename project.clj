(defproject
  jukebox "1.0.0-SNAPSHOT"
  :description "Jukebox"
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.cli "0.2.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [clj-http "0.6.3"]
                 [org.jaudiotagger/jaudiotagger "2.0.1"]
                 [org.apache.derby/derby "10.9.1.0"]
                 [cheshire "5.0.1"]
                 [corroborate "0.2.0"]
                 [compojure "1.1.3"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [jaad "0.8.3"]
                 [jl "1.0.1"]
                 [jogg "0.0.7"]
                 [jorbis "0.0.15"]
                 [mp3spi "1.9.5"]
                 [tritonus_share "0.3.6"]
                 [vorbisspi "1.0.3"]
                 [fs "1.1.2"]]
  :dev-dependencies [[lein-ring "0.4.5"]
                     [lein-javac "1.2.1-SNAPSHOT"]]
  :main jukebox-web.core
  :ring {:handler jukebox-web.core/app}
  :test-path "test/")
