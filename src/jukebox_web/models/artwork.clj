(ns jukebox-web.models.artwork
  (:use [ring.util.codec :as codec]
        [clojure.contrib.json :only (read-json)]
        [clojure.contrib.http.agent :only (success? request-uri string http-agent stream)]
        [clojure.java.io :only (reader)]))

(def default-image-path "/img/no_art_lrg.png")

(def base-url "http://ws.audioscrobbler.com/2.0?method=album.getInfo&api_key=809bf298f1f11c57fbb680b1befdf476&format=json&autocorrect=1")


(defn default-image []
  default-image-path)

(defn url [album artist]
  (format "%s&album=%s&artist=%s" base-url (codec/url-encode album) (codec/url-encode artist)))

(defn transform [http-response]
  (let [json-response (read-json (string http-response))
        images (:image (:album json-response))
        artwork (first (filter #(= (:size %) "large") images))]
    (if (or (nil? artwork) (= "" artwork))
      (default-image)
      (val (first artwork)))))

(defn album-cover [album artist]
  (let [http-request (http-agent (url album artist))]
    (await-for 10000 http-request)
    (if (success? http-request)
      (transform http-request)
      (default-image))))

