(ns arduino.mqtt
  (:require [clj-time.core :as t]
            [clojurewerkz.machine-head.client :as mh]
            [arduino.scheduler :as scheduler]
   ))


; TOPIC database
; most recent values, as received from the sensors,
; or as it has been set via the rest api

(def topics (atom {

                   :temp  {:valu 0 :format "%.1f" :desc "Temperature" :is-command false}
                   :ph     {:valu 0 :format "%.3f" :desc "PH of Water" :is-command false}
                   :ppm     {:valu 0 :format "%.2f" :desc "PPM of nutrients" :is-command false}
                   ; light cycle: once a day on for 10 hours
                   :light {:valu 0 :format "%.0f" :desc "Light on=1 off=0" :is-command true :timer {:on 1440 :off 600}}
                   ; ozone cycle: every 6 hours on for 10 minutes.
                   :ozone {:valu 0 :format "%.0f" :desc "Ozone generator on=1 off=0" :is-command true :timer {:on 360 :off 10}}
                   :status  {:valu 13 :format "%.0f" :desc "Tiberius Test 1" :is-command false :timer {:on 15 :off 2}}
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
                                   (let [payload-str (String. payload "UTF-8")]
                                     (println "RCVD: " topic ": " payload-str)
                                     (set-topic (keyword topic) payload-str )
                                     ; (mh/disconnect conn)
                                     ;(System/exit 0)
                                     )

                                   ))))

(defn do-action
  "tell sensor to do certain action"
  [topic payload]
  (println "do-action topic:" topic "payload:" payload)
  (publish topic payload)
  ;(set-topic topic payload)
  )

(defn subscribe-topics-of-interest []
  ( let [topics (get-topics)]
    (do (println "subscribing to" (count topics) "topics of interest..")
       (run! #(subscribe (name (get % 0))) topics ))))


; SCHEDULER ON/OFF cycles for mqtt variables.


(defn timer-action [time name data]
  (println time "timer-action " name data)
  (if data (do-action name "1") (do-action name "0")))


(defn start-timer [name timer-data]
  (if-not (nil? timer-data) (do (println "starting timer " name timer-data)
                                (scheduler/start-cycle name (:on timer-data) (:off timer-data) timer-action)
                              )))



(defn start-timers []
  (let [topics (get-topics)]
    (run! #(start-timer  (name (get % 0))  (:timer (get % 1))  ) topics)))



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


  (subscribe "test3")
  (publish "test3" "1243456")
  (publish "test3" 123.456)                                ; crashes
  (publish "test3" "100 degrees celsius")
  (get-topics)
  (map println (get-topics))
  (subscribe-topics-of-interest)


  (start-timers)

  ) ;********************************************************************

