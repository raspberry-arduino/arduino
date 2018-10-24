(ns arduino.handlers
  (:require [goog.net.ErrorCode :as errors]
            [ajax.core :refer [GET POST]]
            [re-frame.core :refer [register-handler]]))

(println "Setting up event handlers..")


(register-handler :request-data
  (fn
    [db _]
    ;; kick off the GET, making sure to supply a callback for success and failure
    (println ":request-data..")
    (GET
      "http://localhost:7000/api/topics"
      {:handler       #(re-frame.core/dispatch [:process-data %1])   ;; further dispatch !!
       :error-handler #(re-frame.core/dispatch [:bad-response %1])})     ;; further dispatch !!

    (assoc db :loading? true)))    ;; pure handlers must return a db


(register-handler :process-data
   (fn [db data]
     (println "processing data: " data)
     (assoc db :data (get data 1) )))


(defn xxx []
  (println "...")
  )


(println "Setting up event handlers.. done.")
