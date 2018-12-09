(ns arduino.timer
  (:require [mount.core :refer [defstate]]
                               [clojure.tools.logging :refer [info]]
                               [clj-time.core :as t]
                               [clojurewerkz.machine-head.client :as mh]
                               [arduino.config :refer [config]]
                               [arduino.scheduler :as scheduler]
                               [arduino.db :as db]
                               [arduino.mqtt :as mqtt]

                               ))

; STATE OF RUNNING TIMERS

; currently running timers
(defonce timers-db (atom { }))

(defn save-timer
  "saves a timer finish function"
  [topic timer-function]
  (swap! timers-db assoc-in [topic ] timer-function)
  ;(swap! topics assoc-in [topic :time ] (t/now))
  )

(defn remove-timer [name]
  (swap! timers-db dissoc name))



; SCHEDULER ON/OFF cycles for mqtt variables.

(defn timer-action [time name data]
  "when timer fires on/off - forward new state to mqtt"
  (info time "timer-action " name data)
  (if data (mqtt/do-action name "1") (mqtt/do-action name "0")))


(defn stop-timer-if-running [name]
  "we can re-start already running timers, in which case we need to stop the old timer"
  (let [ running-timer (get-in @timers-db [name])]
    (if-not (nil? running-timer)
      (do (info "stopping timer (because of new start) " name " .. ")
          (scheduler/stop-cycle running-timer)
          (remove-timer name)))))


(defn start-timer [name timer-data]
  (if-not (nil? timer-data)
          (do (stop-timer-if-running (keyword name) )
              (info "starting timer " name timer-data)
              (save-timer (keyword name)  (scheduler/start-cycle name (:on timer-data) (:off timer-data) timer-action)))))


(defn stop-timer [name]
  (let [ running-timer (get-in @timers-db [name])]
    (if (nil? running-timer)
      (info "cannot stop timer " name "because it is not running")
      (do (info "stopping timer " name " .. ")
          (scheduler/stop-cycle running-timer)
           (remove-timer name)))))


(defn start-timers []
  (let [topics (db/get-topics)]
    (info "starting cycle timers..")
    (run! #(start-timer  (name (get % 0))  (:timer (get % 1))  ) topics)))


(defn stop-running-timers []
  (let [running-timers @timers-db
        xx (println "stopping " (count running-timers) "running timers..")]
   (map #(stop-timer (get % 0) ) running-timers  )

    ))


; SERVICE


(defstate timers
          :start (start-timers)
          :stop (stop-running-timers))

(comment

  ; test the removing of stopped timers
  (remove-timer :status)


  (start-timer "tyron" {:on 10 :off 5} )

  (start-timers)
  (println @timers-db)
  (stop-timer :status)
  (stop-running-timers)

  (stop-timer :esteban)

  )