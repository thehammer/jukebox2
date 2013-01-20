(defproject jukebox "1.0.0-SNAPSHOT"
  :description "Jukebox"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"local" "file:repo"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.cli "0.2.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [clj-http "0.6.3"]
                 [org.jaudiotagger/jaudiotagger "2.0.1"]
                 [org.apache.derby/derby "10.9.1.0"]
                 [cheshire "5.0.1"]
                 [corroborate "0.3.0"]
                 [compojure "1.1.4"]
                 [hiccup "1.0.2"]
                 [ring/ring-jetty-adapter "1.1.5"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [jaad "0.8.3"]
                 [jl "1.0.1"]
                 [jogg "0.0.7"]
                 [jorbis "0.0.15"]
                 [mp3spi "1.9.5"]
                 [tritonus_share "0.3.6"]
                 [vorbisspi "1.0.3"]
                 [fs "1.1.2"]
                 ;; clojurescript deps
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [domina "1.0.0"]
                 [prismatic/dommy "0.0.1"]]
  :plugins [[lein-cljsbuild "0.2.10"]
            [lein-ring "0.8.0"]]
  :source-paths ["src/clj"]
  :test-paths ["test"]
  :main jukebox-web.core
  :ring {:handler jukebox-web.core/app
         :init jukebox-web.core/initialize}
  :profiles {:dev {:dependencies [[ring-mock "0.1.3"]
                                  [peridot "0.0.8"]
                                  [enlive "1.0.1"]]}}
  :cljsbuild {:builds [{:source-path "src/cljs"
                        :compiler {:output-to "resources/public/js/application.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
