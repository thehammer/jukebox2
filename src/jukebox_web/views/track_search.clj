(ns jukebox-web.views.track-search
  (:use [hiccup core page-helpers]))

(defn results [request]
  [:script#track-result-template {:type "text/example"}
    [:li.result
     "<% if(canAdd) { %>"
       [:a.update-playlist {:href "/playlist/add/{{ path }}" :data-remote "true"} "{{ title }}"]
     "<% } else { %>"
       [:p.update-playlist "{{ title }}"]
     "<% } %>"
     [:p.artist "{{ artist }}"]]])

(defn display-search [request]
  [:div#track-search-container
    [:form#search {:action "/library/search" :method "get" :data-remote "true" }
     [:input#query {:type "text" :autocomplete "off" :name "q" :placeholder "Search for Artist, Album or Song"}]
     [:ul#track-search-results]]
    (results request)])
