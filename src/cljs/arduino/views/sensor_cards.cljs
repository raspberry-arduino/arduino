(ns arduino.views.sensor-cards
  (:require
    [reagent.core :refer [atom]]
    [re-frame.core :as re-frame]
    [arduino.subs :as subs]
    [arduino.mui :as mui]
    [oz.core :as oz]

    [goog.string :as gstring]
    [goog.string.format]

    [arduino.views.menu :refer [menu]]

    ))










(defn on-off-icon [name data]
  [mui/typography {:variant :title
                   :style   {:color   :yellow}}
   (if (= data "1")
     [mui/icon {:on-click #(re-frame.core/dispatch [:set-data name "0"])}  "alarm_on"]
     (if (= data "0")
       [mui/icon {:on-click #(re-frame.core/dispatch [:set-data name "1"])}  "alarm_off"]) )
   ]
  )

(defn sensor-card [name sensor-value]
  [mui/grid {:item true}
   [mui/card
    [mui/card-header {:title name }]
    [mui/card-content
     [mui/typography {:variant :title
                      :style   {:color :white}}
      sensor-value]
     [on-off-icon name sensor-value]
     ]]])


(defn format-digits [value format-string]
  (println "formatting value: " value " format:" format-string)
  (if (or (nil? value) (nil? format-string)) value (gstring/format format-string value) )
  )



(defn sensor-cards []
  (let [data (re-frame/subscribe [::subs/data])]
    (fn []
      [mui/grid {:container   true
                 :align-items :center
                 :spacing     16}

       (for [topic @data]
         [sensor-card (get topic 0) ( format-digits (get-in (get topic 1) ["valu"]) (get-in  (get topic 1) ["format"])  )]) ; topic 1 is the data strucutre, topic 0 is the name of the data

       ;[sensor-card "mao" 1.55]
       ;[sensor-card "tb" "sir"]
       ;[sensor-card "cat" "miao"]

       ])))