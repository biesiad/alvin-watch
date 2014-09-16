(ns alvin-watch.core
  (:require [clj-http.client :as client]
            [watchtower.core])
  (:use [watchtower.core]))


(defn moisture []
  (let
    [data (slurp "./data/moisture.txt")]

    (cond
      (= data "") 0
      :else (Integer. (read-string (slurp "./data/moisture.txt"))))))


(def old-moisture (moisture))


(defn delta [old, new]
  (Math/abs (- old new)))


(defn send-message [value]
  (println "changed" value))


(defn on-moisture-change [file]
  (send-message (moisture)))
  ; (cond
    ; (> (delta old-moisture (moisture)) 10) (send-message)
    ; :else ""))


(defn -main [& args]
  (println "Watching for moisture changes...")

  (watcher ["./data/moisture.txt"]
    (rate 100)
    (on-change on-moisture-change)))
