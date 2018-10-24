(ns arduino.mqtt
  (:require [clj-time.core :as t]
            [clojurewerkz.machine-head.client :as mh]
   ))


; TOPIC database
; most recent values, as received from the sensors,
; or as it has been set via the rest api

(def topics (atom { :test1 {:valu 13  :desc "Tiberius Test 1"}
                    :test2 {:valu 123 :desc "Tiberius Test 2"}
                    :ph    {:valu 7   :desc "PH of Water"}
                    :light1 {:valu 0  :desc "Light on=1 off=0"}
                    :light2 {:valu 0  :desc "Light on=1 off=0"}
                    }))

(defn get-topics []
  @topics)

(defn set-topic
  "updates the :valu"
  [topic payload]
  (swap! topics assoc-in [topic :valu ] payload)
  (swap! topics assoc-in [topic :time ] (t/now))
  )

; MQTT pub/sub

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

(defn do-action
  "tell sensor to do certain action"
  [topic payload]
  (publish topic payload)
  (set-topic topic payload))


(comment    ;********************************************************************


  (subscribe "tb1")

  (publish "tb1" "12434")

  (get-topics)

  (set-topic :ph 10)


  ) ;********************************************************************

