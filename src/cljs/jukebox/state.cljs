(ns jukebox.state
  (:require [goog.net.XhrIo :as xhr]))

(def current (atom {}))

(defn handle-poll-response [response]
  (let [data (js->clj (.getResponseJson (.-target response)))]
    (if-not (= data @current)
      (reset! current data))))

(defn ^:export poll []
  (xhr/send "/now-playing"
            handle-poll-response
            "GET"
            nil
            (clj->js {"Accept" "application/json"})))

(set! (.-onload js/window) poll)
(js/setInterval poll 2000)
