(ns jukebox-web.views.users
  (:use [hiccup core page-helpers form-helpers]
        [jukebox-web.views.layout :as layout]))

(defn sign-in []
  (layout/main "Sign In"
    (form-to [:post "/users/authenticate"]
      (label "login" "Login")
      (text-field "login")
      (label "password" "Password")
      (password-field "password")
      (submit-button "Sign In"))))
