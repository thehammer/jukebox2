(ns jukebox.now-playing-test
  (:use-macros [jukebox.test-macros :only (deftest is)])
  (:require [jukebox.now-playing :as now-playing]
            [goog.testing.jsunit :as jsunit]))

(deftest truth
  (is (= true true)))
