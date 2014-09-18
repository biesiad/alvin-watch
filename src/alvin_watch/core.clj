(ns alvin-watch.core
  (:require [clj-http.client :as client]
            [watchtower.core])

  (:use [watchtower.core]
        [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful]))


(def moisture-file "/home/pi/projects/RF24/RPi/RF24/examples/moisture.txt")
; (def moisture-file "./data/moisture.txt")

(load "oauth-keys")

(def oauth-creds (make-oauth-creds consumer-key
                                   consumer-secret
                                   oauth-token
                                   oauth-token-secret))


(defn delta [old, new]
  (Math/abs (- old new)))


(defn moisture []
  (let
    [data (slurp moisture-file)]

    (cond
      (= data "") 0
      :else (Integer. (read-string (slurp moisture-file))))))


(def old-moisture (atom (moisture)))


(defn send-message [value]
  (println (str "Moisture: " value "%"))
  (statuses-update :oauth-creds oauth-creds :params { :status (str "Moisture: " value "%") }))


(defn on-moisture-change [file]
  (if (> (delta @old-moisture (moisture)) 10)
    (do
      (send-message (moisture))
      (reset! old-moisture (moisture)))))


(defn -main [& args]
  (println "Watching for moisture changes...")

  (watcher [moisture-file]
    (rate 100)
    (on-change on-moisture-change)))
