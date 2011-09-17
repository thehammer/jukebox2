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

(defn- labeled-field [field-type id label-text errors]
  [:div
    (label id label-text)
    (field-type id)
    [:span (id errors)]])

(defn sign-up [errors]
  (layout/main "Sign Up"
    (form-to [:post "/users/sign-up"]
      (labeled-field text-field :login "Login" errors)
      (labeled-field password-field :password "Password" errors)
      (labeled-field password-field :password_confirmation "Password Confirmation" errors)
      (labeled-field text-field :avatar "Avatar" errors)
      [:div (submit-button "Sign Up")])))
