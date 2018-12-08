(ns arduino.handlers
  (:require [goog.net.ErrorCode :as errors]
            [ajax.core :refer [GET POST]]
            [re-frame.core :refer [register-handler]]
            [arduino.config :refer [base-url]]))

(println "Setting up event handlers..")


(register-handler :request-data
                  (fn
                    [db _]
                    ;; kick off the GET, making sure to supply a callback for success and failure
                    (println ":request-data..")
                    (GET
                      (str "http://" base-url "/api/topics" )
                      {:handler       #(re-frame.core/dispatch [:process-data %1]) ;; further dispatch !!
                       :error-handler #(re-frame.core/dispatch [:bad-response %1])}) ;; further dispatch !!

                    (assoc db :loading? true)))             ;; pure handlers must return a db


(register-handler :process-data
                  (fn [db data]
                    (println "processing data: " data)
                    (assoc db :data (get data 1))))


(register-handler :set-data

                  (fn
                    [db data]
                    ;; kick off the GET, making sure to supply a callback for success and failure
                    (println ":set-data..")
                    (GET
                      (str "http://" base-url "/api/action" )
                      {:params        {:topic (get data 1)
                                       :payload  (get data 2)}
                       :handler       #(re-frame.core/dispatch [:post-success %1]) ;; further dispatch !!
                       :error-handler #(re-frame.core/dispatch [:post-failure %1])}) ;; further dispatch !!

                    (assoc db :loading? true)))



(register-handler :request-history
                  (fn
                    [db data]
                    ;; kick off the GET, making sure to supply a callback for success and failure
                    (println ":request-history: "  (get data 1))
                    (GET
                      (str "http://" base-url "/api/history?topic=" (get data 1))
                      {:handler       #(re-frame.core/dispatch [:process-history (get data 1) %1]) ;; further dispatch !!
                       :error-handler #(re-frame.core/dispatch [:bad-response %1])}) ;; further dispatch !!

                    (assoc db :loading? true)))


(register-handler :process-history
                (fn [db data]
                     (println "processing history: " data)
                    (println "RCVD history: " (get data 1) (get data 2))      ;
                    (assoc-in db [:history (keyword (get data 1))]  (get data 2))))



;(register-handler :set-data
;
;
;(fn [db data]
  ; (println "setting data: " data)
  ;  (assoc db :command (get data 1))
; ) )


(defn xxx []
  (println "...")
  )


(println "Setting up event handlers.. done.")
