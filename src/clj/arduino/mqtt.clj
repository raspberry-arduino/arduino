(ns arduino.mqtt
  (:require [mount.core :refer [defstate]]
            [clojure.tools.logging :refer [info]]
            [clj-time.core :as t]
            [clojurewerkz.machine-head.client :as mh]
            [arduino.scheduler :as scheduler]
            [arduino.db :as db]
            [arduino.config :refer [config]]
   ))



(defn connect-mqtt []
  (let [url (get-in config [:mqtt :server])]
    (info "mqtt connecting to " url)
    (mh/connect url )))

(defn disconnect-mqtt [conn]
    (info "mqtt disconnecting..")
    (mh/disconnect conn ))

(defstate mqtt-conn
          :start (connect-mqtt)
          :stop  (disconnect-mqtt mqtt-conn) )





; MQTT pub/sub

(defn publish [topic payload]
    (mh/publish mqtt-conn topic payload)
    (info "published topic:" topic " payload:" payload))


(defn subscribe [topic]
    (info "mqtt subscribing: " topic)
    (mh/subscribe mqtt-conn {topic 0} (fn [^String topic _ ^bytes payload]
                                   (let [payload-str (String. payload "UTF-8")]
                                     (info "MQTT RCVD " topic "=" payload-str)
                                     (db/set-topic (keyword topic) payload-str )
                                     )
                                   )))

(defn do-action
  "tell sensor to do certain action"
  [topic payload]
  (info "do-action topic:" topic "payload:" payload)
  (publish topic payload)
  ;(db/set-topic topic payload)
  )

(defn subscribe-topics-of-interest []
  ( let [topics (db/get-topics)]
    (do (info "subscribing to" (count topics) "topics of interest..")
       (run! #(subscribe (name (get % 0))) topics ))))



; SERVICE

(defn start-mqtt-subscriptions []
  (info "starting mqtt subscriptions ..")
  (subscribe-topics-of-interest))

(defstate mqtt-actions
          :start (start-mqtt-subscriptions))



(comment    ;********************************************************************

  ;dont use connect-mqtt; mqtt connection is managed by mount.
  (connect-mqtt)

   ; Test of Clojure Syntax
  (map #(println "subscribing to: " (name (get % 0))) (db/get-topics))

   (doall #(println "subscribing to: " (name (get % 0))) (db/get-topics))

  (println (name :key))
   ; print keys
   (map #(println (get % 0)) (db/get-topics))
   (map #(println (get % 1)) (db/get-topics))
   (println "topic count: " (count (db/get-topics)))
   (db/set-topic :ph 10)


  (subscribe "test3")
  (publish "test3" "1243456")
  (publish "test3" 123.456)                                ; crashes
  (publish "test3" "100 degrees celsius")
  (db/get-topics)
  (map println (db/get-topics))
  (subscribe-topics-of-interest)


  (start-timers)

  ) ;********************************************************************

