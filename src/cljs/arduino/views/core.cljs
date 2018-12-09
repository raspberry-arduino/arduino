(ns arduino.views.core

  (:require
    [reagent.core :refer [atom]]
    [re-frame.core :as re-frame]
    [arduino.subs :as subs]
    [arduino.mui :as mui]
    [oz.core :as oz]

    [goog.string :as gstring]
    [goog.string.format]

    [arduino.views.menu :refer [menu]]
    [arduino.views.sensor-cards :refer [sensor-cards]]
    [arduino.views.chart :refer [sensor-plot line-plot]]

    ))


(defn main-panel []
    [mui/grid {:container true}
     [mui/mui-theme-provider {:theme mui/jyu-theme-dark}
      [:div
       [sensor-cards]
       ]]])


(defn dashboard-page []
  [:div
   [menu "Sensors"]
   [main-panel]
   ])




