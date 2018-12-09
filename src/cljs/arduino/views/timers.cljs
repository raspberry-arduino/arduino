(ns arduino.views.timers
  (:require
    [reagent.core :refer [atom]]
    [re-frame.core :as re-frame]
    [oz.core :as oz]
    [goog.string :as gstring]
    [goog.string.format]
    [arduino.subs :as subs]
    [arduino.mui :as mui]
    [arduino.views.menu :refer [menu]]
    ))

(defn timers-table []
  (let [data (re-frame/subscribe [::subs/data])
        ;xx (println "rendering timers-table: " @data)
        ;xx (re-frame.core/dispatch [:request-timer-state])
        ]
    (fn []
      ;(js/setInterval #(re-frame.core/dispatch [:request-timer-state]) 1000)
      [mui/paper
       [mui/typography
        {:variant "title"}
        "Running Timers"]
       [mui/table
        [mui/table-head
         [mui/table-row
          [mui/table-cell "Name"]
          [mui/table-cell "CycleMin"]
          [mui/table-cell "OnForMin"]
          [mui/table-cell "NextOn"]
          [mui/table-cell "NextOff"]
          [mui/table-cell "TimerValue"]
          [mui/table-cell "MQTTValue"]

          ]]
        [mui/table-body (for [topic @data]
                          (let [sensor-name (get topic 0)
                                sensor-data (get topic 1)
                                xx (println sensor-data)
                                ]
                            [mui/table-row {:key sensor-name }
                             [mui/table-cell sensor-name]
                             [mui/table-cell (get-in sensor-data ["timer" "on"])]
                             [mui/table-cell (get-in sensor-data ["timer" "off"])]
                             [mui/table-cell (get-in sensor-data ["timer-status"  "on-next" ])]
                             [mui/table-cell (get-in sensor-data [ "timer-status" "off-next" ])]
                             [mui/table-cell (get-in sensor-data [ "timer-status"  "current-value" ])]
                             [mui/table-cell (get-in sensor-data [ "valu"])]]
                            )



                            )]]
       [:p "#Minutes/Day divided by CycleMin has to be an integer number, otherwise the cycle will drift each day to different on/off times!"]
       ]
      )
    ))


(defn timers-page []
  [:div
    [menu "Timers"]
    [timers-table]
   ]
   )