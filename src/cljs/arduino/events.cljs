(ns arduino.events
  (:require [re-frame.core :as re-frame]
            [re-frame.core :refer [reg-event-db]]

            [goog.net.ErrorCode :as errors]
            [ajax.core :refer [GET POST]]

            [arduino.db :as db]
            [arduino.config :refer [base-url]]
   ))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 :timer-start
 (fn [db data]
    ;; kick off the GET, making sure to supply a callback for success and failure
    (println ":starting-timer..")
    (GET (str "http://" base-url "/api/timer-start" )
         {:params        {:name  (get data 1)
                          :on    (get data 2)
                          :off   (get data 3)}
          :handler       #(re-frame.core/dispatch [:post-success %1]) ;; further dispatch !!
          :error-handler #(re-frame.core/dispatch [:post-failure %1])}) ;; further dispatch !!
    (assoc db :loading? true)))

(reg-event-db
  :request-data
  (fn [db _]
    ;; kick off the GET, making sure to supply a callback for success and failure
    (println ":request-data..")
    (GET (str "http://" base-url "/api/topics" )
         {:response-format :json
          :handler       #(re-frame.core/dispatch [:process-data %1]) ;; further dispatch !!
          :error-handler #(re-frame.core/dispatch [:bad-response %1])}) ;; further dispatch !!
         (assoc db :loading? true)))             ;; pure handlers must return a db


(reg-event-db
  :process-data
  (fn [db data]
   (println "processing data: " data)
   (assoc db :data (get data 1))))


(reg-event-db
  :set-data
  (fn [db data]
     ;; kick off the GET, making sure to supply a callback for success and failure
     (println ":set-data..")
     (GET (str "http://" base-url "/api/action" )
          {:params        {:topic (get data 1)
                           :payload  (get data 2)}
           :handler       #(re-frame.core/dispatch [:post-success %1]) ;; further dispatch !!
           :error-handler #(re-frame.core/dispatch [:post-failure %1])}) ;; further dispatch !!
           (assoc db :loading? true)))

; HISTORY ****************

(reg-event-db
  :request-history
  (fn [db data]
    ;; kick off the GET, making sure to supply a callback for success and failure
    (println ":request-history: "  (get data 1))
    (GET (str "http://" base-url "/api/history?topic=" (get data 1))
         {:handler       #(re-frame.core/dispatch [:process-history (get data 1) %1]) ;; further dispatch !!
          :error-handler #(re-frame.core/dispatch [:bad-response %1])}) ;; further dispatch !!
         (assoc db :loading? true)))


(reg-event-db
  :process-history
  (fn [db data]
    (println "processing history: " data)
    (println "RCVD history: " (get data 1) (get data 2))      ;
    (assoc-in db [:history (keyword (get data 1))]  (get data 2))))

; TIMERS **********************

(reg-event-db
  :request-timer-state
  (fn [db _]
    (println ":request-timer-state..")
    (GET (str "http://" base-url "/api/timer-running" )
         {:handler       #(re-frame.core/dispatch [:process-timer-data %1]) ;; further dispatch !!
          :error-handler #(re-frame.core/dispatch [:bad-response %1])}) ;; further dispatch !!
    (assoc db :loading? true)))             ;; pure handlers must return a db

(reg-event-db
  :process-timer-data
  (fn [db data]
    (println "processing timer-data: " data)
    db
    ;(assoc db :data (get data 1))
    ))


;(reg-event-db :set-data
;
;
;(fn [db data]
; (println "setting data: " data)
;  (assoc db :command (get data 1))
; ) )
