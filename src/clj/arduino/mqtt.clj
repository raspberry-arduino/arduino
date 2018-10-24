(ns arduino.mqtt
  (:require [clj-time.core :as t]
            [clojurewerkz.machine-head.client :as mh]
   ))


; TOPIC database
; most recent values, as received from the sensors,
; or as it has been set via the rest api

(def topics (atom { :test1 {:valu "13"   :desc "Tiberius Test 1" :is-command false}
                    :test2 {:valu "123"  :desc "Tiberius Test 2" :is-command false}
                    :ph    {:valu "7"    :desc "PH of Water" :is-command false}
                    :light1 {:valu "0"   :desc "Light on=1 off=0" :is-command true}
                    :light2 {:valu "0"   :desc "Light on=1 off=0" :is-command true}
                    }))

(defn get-topics []
  @topics)

(defn set-topic
  "updates the :valu"
  [topic payload]
  (swap! topics assoc-in [topic :valu ] payload)
  ;(swap! topics assoc-in [topic :time ] (t/now))
  )

; MQTT pub/sub

(defn publish [topic payload]
  (let [conn (mh/connect "tcp://debian.hoertlehner.com:1883")]
    (mh/publish conn topic payload)
    (println "published topic:" topic " payload:" payload)
    (mh/disconnect conn)))


(defn subscribe [topic]
  (let [conn (mh/connect "tcp://debian.hoertlehner.com:1883")]
    (println "subscribing: " topic)
    (mh/subscribe conn {topic 0} (fn [^String topic _ ^bytes payload]
                                   (println "RCVD: " topic ": " (String. payload "UTF-8"))
                                   (set-topic (keyword topic) (String. payload "UTF-8") )
                                   ; (mh/disconnect conn)
                                   ;(System/exit 0)
                                   ))))

(defn do-action
  "tell sensor to do certain action"
  [topic payload]
  (println "do-action topic:" topic "payload:" payload)
  (publish topic payload)
  (set-topic topic payload))

(defn subscribe-topics-of-interest []
  ( let [topics (get-topics)]
    (do (println "subscribing to" (count topics) "topics of interest..")
       (run! #(subscribe (name (get % 0))) topics ))))


(comment    ;********************************************************************

   ; Test of Clojure Syntax
  (map #(println "subscribing to: " (name (get % 0))) (get-topics))

   (doall #(println "subscribing to: " (name (get % 0))) (get-topics))

  (println (name :key))
   ; print keys
   (map #(println (get % 0)) (get-topics))
   (map #(println (get % 1)) (get-topics))
   (println "topic count: " (count (get-topics)))
   (set-topic :ph 10)


  (subscribe "test2")
  (publish "test2" "1243456")
  (publish "test1" "100 degrees celsius")
  (get-topics)
  (map println (get-topics))
  (subscribe-topics-of-interest)



  ) ;********************************************************************

