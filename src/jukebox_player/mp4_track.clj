(ns jukebox-player.mp4-track
  (:use [jukebox-player.playable])
  (:import [javax.sound.sampled AudioFormat]
           [net.sourceforge.jaad.aac Decoder SampleBuffer]
           [net.sourceforge.jaad.mp4 MP4Container]
           [net.sourceforge.jaad.mp4.api AudioTrack$AudioCodec Frame Movie Track]
           [java.io ByteArrayInputStream ByteArrayOutputStream RandomAccessFile]))

(defrecord MP4Track [track-data])

(defn- read-track-data [track-data]
  (let [decoder (Decoder. (.getDecoderSpecificInfo track-data))
       buffer (SampleBuffer.)
       output (ByteArrayOutputStream.)]
    (loop [frame (.readNextFrame track-data)]
      (when-not (nil? frame)
        (.decodeFrame decoder (.getData frame) buffer)
        (.write output (.getData buffer) 0 (alength (.getData buffer)))
        (recur (.readNextFrame track-data))))
    (.toByteArray output)))

(defn load-mp4-track [file]
  (let [container (MP4Container. (RandomAccessFile. file "r"))
        tracks (.getTracks (.getMovie container) AudioTrack$AudioCodec/AAC)]
    (MP4Track. (first tracks))))

(extend-type MP4Track
  Playable
    (in-stream [mp4-track]
      (ByteArrayInputStream. (read-track-data (:track-data mp4-track))))

     (out-format [mp4-track]
       (let [track-data (:track-data mp4-track)]
         (AudioFormat. (.getSampleRate track-data)
                       (.getSampleSize track-data)
                       (.getChannelCount track-data)
                       true
                       true))))


