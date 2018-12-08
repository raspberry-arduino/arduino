(ns arduino.db
  (:require [mount.core :refer [defstate]]
            [clojure.tools.logging :refer [info]]
            [clj-time.core :as t]))



; TOPIC database
; most recent values, as received from the sensors,
; or as it has been set via the rest api

(defonce topics (atom {

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



(comment

  (get-topics)

  (set-topic :temp 23)                                      ; set temperature to 23 degrees

  )