(ns jukebox-web.views.users
  (:use [hiccup core page-helpers form-helpers]))

(defn sign-in []
  (html5
    [:head
     [:title ""]
     (include-css "/css/style.css")]
    [:body
     (form-to [:post "/users/authenticate"]
       (label "login" "Login")
       (text-field "login")
       (label "password" "Password")
       (password-field "password")
       (submit-button "Login")) ]))
