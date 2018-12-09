(ns arduino.views.settings
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

(defn sensor-table []
  (let [data (re-frame/subscribe [::subs/data])
        xx (println "rendering data: " @data)]
    (fn []
      (js/setInterval #(re-frame.core/dispatch [:request-data]) 1000)
      [:div
       [:ul (for [topic @data]
              [:li {:key (get topic 0) } (get topic 0)
               [:span {:style {:color "orange"}}
                [:b ":   " (:valu (get topic 1))]  ]  ] )]
       ]
      )
    ))


(defn settings-page []
  (let [saying (re-frame/subscribe [::subs/saying])
        bongo "bongo"]
    [:div
     [menu "Settings"]
     [mui/tooltip {:title @saying
                   :on-open (fn [] ( println "tooltip onOpen"))
                   :on-close (fn [] (println "tooltip onClose")) }
      [:h1 "Edit Sensor Settings here"]]
      [sensor-table]
     ]


    ))