(ns jukebox-web.models.artwork
  (:use [ring.util.codec :as codec]
        [clojure.contrib.json :only (read-json)]
        [clojure.contrib.http.agent :only (success? request-uri string http-agent stream)]
        [clojure.java.io :only (reader)]))

(def default-image-path "/img/no_art_lrg.png")

(def default-images {:large default-image-path :extra-large default-image-path})

(def base-url "http://ws.audioscrobbler.com/2.0?method=album.getInfo&api_key=809bf298f1f11c57fbb680b1befdf476&format=json&autocorrect=1")

(defn url [album artist]
  (format "%s&album=%s&artist=%s" base-url (codec/url-encode album) (codec/url-encode artist)))

(defn image-for-size [images size]
  (get (first (filter #(and (= (:size %) size) (not (empty? (:#text %)))) images)) :#text default-image-path))

(defn transform [http-response]
  (let [json-response (read-json (string http-response))
        images (:image (:album json-response))]
    (if (empty? images)
      default-images
      {:large (image-for-size images "large")
       :extra-large (image-for-size images "mega")})))

(defn album-cover [album artist]
  (let [http-request (http-agent (url album artist))]
    (await-for 10000 http-request)
    (try
      (if (success? http-request)
        (transform http-request)
        default-images)
      (catch java.lang.Exception e default-images))))

