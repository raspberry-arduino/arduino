(ns arduino.core
  (:require [clojurewerkz.machine-head.client :as mh]
            ;[com.stuartsierra.component :as component]
            ))

(defn publish [topic payload]
  (let [conn (mh/connect "tcp://debian.hoertlehner.com:1883")]
    (mh/publish conn topic payload)
    (println "published topic:" topic " payload:" payload)
    (mh/disconnect conn)))


(defn subscribe [topic]
  (let [conn (mh/connect "tcp://debian.hoertlehner.com:1883")]
    (mh/subscribe conn {topic 0} (fn [^String topic _ ^bytes payload]
                                     (println "RCVD: " (String. payload "UTF-8"))
                                   ; (mh/disconnect conn)
                                   ;(System/exit 0)
                                   ))))


(comment    ;********************************************************************


  (subscribe "tb1")

  (publish "tb1" "12434")



  ) ;********************************************************************





