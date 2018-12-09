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
  (swap! timers-db assoc-in [topic ] timer-function))

(defn remove-timer [name]
  (swap! timers-db dissoc name))

(defn set-current-value
  "saves the current-value of a timer"
  [topic current-value]
  (if-not (nil? topic)
     (swap! timers-db assoc-in [topic :current-value ] current-value))
  )


; SCHEDULER ON/OFF cycles for mqtt variables.

(defn send-mqtt [name data]
  (set-current-value (keyword name) data)
  (mqtt/do-action name data)
  )


(defn timer-action [time name data]
  "when timer fires on/off - forward new state to mqtt"
  (info time "timer-action " name data)
  (if data (send-mqtt name "1") (send-mqtt name "0")))


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
              (db/save-timer-settings (keyword name) timer-data)
              (save-timer (keyword name)  (scheduler/start-cycle name (:on timer-data) (:off timer-data) timer-action)))))


(defn stop-timer [name]
  (let [ running-timer (get-in @timers-db [name])]
    (if (nil? running-timer)
      (info "cannot stop timer " name "because it is not running")
      (do (info "stopping timer " name " .. ")
          (scheduler/stop-cycle running-timer)
           (remove-timer name)))))


(defn start-timers []
  "starts all timers that we have in the db"
  (let [topics (db/get-topics)]
    (info "starting cycle timers..")
    (run! #(start-timer  (name (get % 0))  (:timer (get % 1))  ) topics)))


(defn stop-running-timers []
  "stop all running timers"
  (let [running-timers @timers-db
        xx (println "stopping " (count running-timers) "running timers..")]
   (map #(stop-timer (get % 0) ) running-timers  )

    ))


(defn timer-summary [timer-data]
  (assoc {} :name (:name timer-data)
            :current-value (:current-value timer-data)
            :on-next (scheduler/next-upcoming (:on-seq timer-data))
            :off-next (scheduler/next-upcoming (:off-seq timer-data))
            ))


(defn running-timer-info []
  "get information on running timers (for frontend display)"
  (let [running-timers @timers-db
        timer-array (map #(get % 1) running-timers)
        ;short-array (map #(select-keys % [:name :current-value]) timer-array)
        short-array (map timer-summary timer-array)
        ]
    short-array
     ))

; SERVICE


(defstate timers
          :start (start-timers)
          :stop (stop-running-timers))

(comment

  ; timer-db tests
  ; removing stopped timers
  (remove-timer :status)
  ; set current value
  (set-current-value :alice 5)

  ; this will block the repl because of infinite sequences
  (println @timers-db)

  ; info for the web client
  (println "running timer info: " (running-timer-info))

  (running-timer-info)

  ; start/stop one timer
  (start-timer "tyron" {:on 10 :off 5} )
  (stop-timer :tyron)

  (start-timers)

  (stop-timer :status)
  (stop-running-timers)



  )