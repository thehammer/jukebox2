(ns jukebox-web.views.layout
  (:use [hiccup core page-helpers]))

(defn main [title & content]
  (html5
    [:head
     [:title title]
     [:script {:src "/js/jquery-1.6.4.min.js"}]
     [:script {:src "/js/jquery.multifile-1.47.min.js"}]
     (include-css "/css/style.css")]
    [:body
      content]))
