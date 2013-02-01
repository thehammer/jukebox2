(ns jukebox.upload
  (:require [domina :as dom]
            [domina.css :as css]
            [domina.events :as ev]
            [dommy.template :as template]
            [goog.debug :as debug]
            [goog.events :as gev]
            [goog.net.EventType :as EventType]
            [goog.net.IframeIo :as IframeIo]
            [goog.net :as gnet]
            [jukebox.gutter-nav :as nav]
            [jukebox.window :as window]))

(defn render-upload []
  (template/node
    [:div#uploader
      [:div#uploader-dropzone
        [:h1 "Drop Files Here"]]
      [:div.in-progress
        [:table.table
          [:thead
            [:tr
              [:th "File"]
              [:th "Progress"]
              [:th "Status"]
              [:th "x"]]]
          [:tbody#uploading-files]]]]))

(defn render-file [file]
  (template/node
    [:tr {:id (.-id file)}
      [:td (.-name file)]
      [:td.percent (.-percent file)]
      [:td (.-status file)]
      [:td "x"]]))

(defn show-upload [event]
  (nav/make-active! (.-parentNode (ev/target event)))
  (dom/replace-children! (dom/by-id "content") (render-upload))
  (attach-events))

(defn files-added [plupload files]
  (doseq [file files]
    (dom/append! (dom/by-id "uploading-files") (render-file file)))
  (.start plupload))

(defn upload-progress [plupload file]
  (dom/set-text! (css/sel (str "#" (.-id file) " td.percent")) (.-percent file)))

(defn upload-complete [plupload file]
  (dom/add-class! (dom/by-id (.-id file)) "success"))

(defn upload-error [plupload error]
  (dom/add-class! (dom/by-id (-> error .-file .-id)) "error")
  (.log js/console file))

(defn init-plupload []
  (let [uploader (js* "Jukebox.Uploader")
        plupload (new uploader (clj->js {"runtimes" "html5"
                                         "url" "/lib/ul"
                                         "max_file_size" "10MB"
                                         "drop_element" "uploader-dropzone"
                                         "autostart" true
                                         "chunk_size" "1MB"
                                         "unique_names" false}))]
    (.init plupload)
    (.bind plupload "FilesAdded" files-added)
    (.bind plupload "Error" upload-error)
    (.bind plupload "UploadProgress" upload-progress)
    (.bind plupload "FileUploaded" upload-complete)))

(defn attach-events []
  (when-let [dropzone (dom/by-id "uploader-dropzone")]
    (init-plupload))
  (ev/listen-once! (dom/by-id "library-upload") :click show-upload))

(window/register-onload! attach-events)
