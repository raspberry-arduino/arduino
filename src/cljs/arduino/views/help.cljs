(ns arduino.views.help
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [arduino.subs :as subs]
            [arduino.mui :as mui]
            [oz.core :as oz]
            [goog.string :as gstring]
            [goog.string.format]

            [arduino.views.menu :refer [menu]]
     ))

(defn timer-component []
  (let [seconds-elapsed (atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div
       "Seconds Elapsed: " @seconds-elapsed])))



(defn help-page []
  (let [name (re-frame/subscribe [::subs/name])]
  [:div
   [menu "Help"]
   [:h1 "Help"]
    [:p "Arduino based Hydroponics by TB and the gang." ]
    [timer-component]
     [:h1 "Hello from " @name]

    [:p "Sensors and Regulation."]
    [:ol
     [:li "Mao loves Clojure."]
     [:li "Tayron is the best cat!"]
     [:li "TB is an C++ Ninja."]
     [:li [mui/icon "arrow_forward_ios"] ]
     [:li [mui/icon "alarm_off"] ]
     [:li [mui/icon "alarm_on"] ]
     ]
   ]))