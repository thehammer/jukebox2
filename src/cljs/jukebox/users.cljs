(ns jukebox.users
  (:require [goog.dom.forms :as form]
            [goog.net.XhrIo :as xhr]
            [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [jukebox.core :as jukebox]
            [jukebox.window :as window]))

(defn render-user [current-user]
  (if current-user
    (template/node
      [:ul.nav.nav-pills
        [:li.dropdown
          [:a.dropdown-toggle {:data-toggle "dropdown" :href "#"}
            [:img.avatar {:src (get current-user "avatar")}]
            (get current-user "login")
            [:b.caret]]]])
    (template/node
      [:ul.nav.nav-pills
        [:li [:a.#sign-in {:href "#"} "Sign In"]]
        [:li [:a.#sign-up {:href "#sign-up-modal" :data-toggle "modal"} "Sign Up"]]])))

(defn render-sign-up-modal []
  (template/node
    [:div#sign-up-modal.modal.hide.fade {:role "dialog" :tabindex "-1" :aria-labelledby "sign-up-modal-label" :aria-hidden "true"}
      [:div.modal-header
        [:button.close {:type "button" :data-dismiss "modal" :aria-hidden "true"} "×"]
        [:h3#sign-up-modal-label "Sign Up"]]
      [:form#sign-up-form.form-horizontal {:action "/users/sign-up-api" :method "POST"}
        [:div.modal-body
          [:div.login.control-group
            [:label.control-label "Login"]
            [:div.controls
              [:input {:type "text" :name "login"}]
              [:div.errors]]]
          [:div..password.control-group
            [:label.control-label "Password"]
            [:div.controls
              [:input {:type "password" :name "password"}]
              [:div.errors]]]
          [:div.password-confirmation.control-group
            [:label.control-label "Confirm Password"]
            [:div.controls
              [:input {:type "password" :name "password-confirmation"}]
              [:div.errors]]]
          [:div.avatar.control-group
            [:label.control-label "Gravitar URL"]
            [:div.controls
              [:input {:type "text" :name "avatar"}]
              [:div.errors]]]]
        [:div.modal-footer
          [:button.btn {:data-dismiss "modal" :aria-hidden "true"} "Close"]
          [:button#sign-up-button.btn.btn-primary "Sign Up"]]]]))

(defn clear-form [form-id]
  (dom/remove-class! (css/sel (str "#" form-id " div.control-group")) "error")
  (dom/set-value! (css/sel (str "#" form-id " input[type=text]")) "")
  (dom/set-value! (css/sel (str "#" form-id " input[type=password]")) "")
  (dom/destroy-children! (css/sel (str "#" form-id " div.errors"))))

(defn render-errors [field errors]
  (dom/add-class! (css/sel (str "div.control-group." field)) "error")
  (dom/replace-children! (css/sel (str "div." field " div.errors"))
                         (template/node
                           [:ul.unstyled
                             (doall (map (fn [msg] [:li [:p.text-error msg]]) errors))])))

(defn show-user [current-user]
  (dom/replace-children! (dom/by-id "user") (render-user current-user)))

(defn sign-up-response [response]
  (if (.isSuccess (.-target response))
    (do
      (swap! jukebox/current assoc "current-user" (-> response .-target .getResponseJson js->clj)) 
      (.modal (js/jQuery "#sign-up-modal") "hide")
      (show-user))
    (doseq [[field errors] (-> response .-target .getResponseJson js->clj)]
      (render-errors field errors))))

(defn sign-up [event]
  (ev/prevent-default event)
  (.log js/console (dom/by-id "sign-up-form"))
  (xhr/send "/users/sign-up-api"
            sign-up-response
            "POST"
            (form/getFormDataString (dom/by-id "sign-up-form")))
  (dom/log "signup"))

(defn show-sign-in [])

(defn stage-modals []
  (dom/append! (dom/by-id "modal-zone") (render-sign-up-modal)))

(defn attach-events []
  (ev/listen! (dom/by-id "sign-up-button") :click sign-up)
  (ev/listen! (dom/by-id "sign-up") :click (partial clear-form "sign-up-form"))
  (ev/listen! (dom/by-id "sign-in") :click show-sign-in))

(add-watch jukebox/current-user-state :current-user (fn [_ _ _ state] (show-user state)))

(window/register-onload! (fn []
                           (stage-modals)
                           (attach-events)))
