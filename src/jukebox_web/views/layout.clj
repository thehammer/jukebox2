(ns jukebox-web.views.layout
  (:use [hiccup core page-helpers]))

(defn main [title & content]
  (html5
    [:head
     [:title title]
     [:script {:src "/js/jquery-1.6.4.min.js"}]
     [:script {:src "/js/jquery.plupload.queue.js"}]
     [:script {:src "/js/plupload.full.js"}]
     [:script {:src "/js/upload.js"}]
     (include-css "/css/jquery.plupload.queue.css")
     (include-css "/css/style.css")]
    [:body
      content]))
