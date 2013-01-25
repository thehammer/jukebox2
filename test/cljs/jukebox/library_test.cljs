(ns jukebox.now-playing-test
  (:use-macros [jukebox.test-macros :only (deftest is)])
  (:require [domina :as dom]
            [domina.css :as css]
            [jukebox.library :as library]
            [goog.testing.jsunit :as jsunit]))

;(deftest renders-albums-link
;  (let [artist (library/render-artists [{"artist" "artist 1"}
;                                        {"artist" "artist 2"}])
;        links (dom/nodes (css/sel artist "a.albums"))]
;    (is (= "artist 1" (-> (first links) dom/text)))
;    (is (= "artist%201" (-> (first links) dom/attrs :data-artist)))
;    (is (= "xx" (-> (first images) dom/attrs :src)))
;    (is (= "foo - bar" (-> (first images) dom/attrs :title)))))
