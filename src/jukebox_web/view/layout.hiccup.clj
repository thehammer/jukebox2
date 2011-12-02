(doctype :html5)
[:html
 [:head
  [:meta {:http-equiv "Content-Type" :content "text/html" :charset "iso-8859-1"}]
  [:title "jukebox_web"]
  (include-css "/stylesheets/jukebox_web.css")
  (include-js "/javascript/jukebox_web.js")]
 [:body
  (eval (:template-body joodo.views/*view-context*))
]]