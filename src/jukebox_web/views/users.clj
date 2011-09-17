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

(defn sign-up []
  (layout/main "Sign Up"
    (form-to [:post "/users/sign-up"]
      [:div
        (label "login" "Login")
        (text-field "login")]
      [:div
        (label "password" "Password")
        (password-field "password")]
      [:div
        (label "password_confirmation" "Confirm Password")
        (password-field "password_confirmation")]
      [:div
        (label "avatar" "Avatar URL")
        (text-field "avatar")]
      [:div (submit-button "Sign Up")])))
