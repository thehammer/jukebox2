(ns jukebox-web.views.layout
  (:use [hiccup core page-helpers]))

(defn main [title & content]
  (html5
    [:head
     [:title title]
     (include-css "/css/style.css")]
    [:body
      content]))
