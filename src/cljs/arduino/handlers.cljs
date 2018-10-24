(ns arduino.handlers

  (:require [ajax.core :refer [GET POST]]
            [re-frame.core :refer [register-handler]))

(println "Setting up event handlers..")

(register-handler  :request-data
  (fn
    [db _]
    ;; kick off the GET, making sure to supply a callback for success and failure
    (println "getting data..")
    (GET
      "http://127.0.0.1:7000/api/test"
      {:handler       #(dispatch [:process-response %1])   ;; further dispatch !!
       :error-handler #(dispatch [:bad-response %1])})     ;; further dispatch !!

    (assoc db :loading? true)))    ;; pure handlers must return a db