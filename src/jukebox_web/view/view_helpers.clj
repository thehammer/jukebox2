(ns jukebox-web.view.view-helpers
  "Put helper functions for views in this namespace."
  (:use
    [joodo.views :only (render-partial *view-context*)]
    [hiccup.page-helpers]
    [hiccup.form-helpers])
  (:require
    [jukebox-player.core :as player]
    [jukebox-web.models.library :as library]))
