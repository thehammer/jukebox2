(ns jukebox.now-playing-test
  (:use-macros [jukebox.test-macros :only (deftest is)])
  (:require [domina :as dom]
            [domina.css :as css]
            [jukebox.now-playing :as now-playing]
            [goog.testing.jsunit :as jsunit]))

(deftest places-the-current-song-first-in-images
  (let [flow (now-playing/cover-flow {"current-song" {"xlarge_image" "xx"
                                                      "artist" "foo"
                                                      "title" "bar"}})
        images (dom/nodes (css/sel flow "img.item"))]
    (is (= "xx" (-> (first images) dom/attrs :src)))
    (is (= "foo - bar" (-> (first images) dom/attrs :title)))))

(deftest rest-of-songs-in-queue-are-in-images
  (let [flow (now-playing/cover-flow {"current-song" {"xlarge_image" "xx"
                                                      "artist" "foo"
                                                      "title" "bar"}
                                      "queued-songs" [{"xlarge_image" "yy"}
                                                      {"xlarge_image" "zz"}]})
        images (dom/nodes (css/sel flow "img.item"))]
    (dom/log (map #(-> % dom/attrs :src) images))
    (is (= ["xx" "yy" "zz"] (map #(-> % dom/attrs :src) images)))))

