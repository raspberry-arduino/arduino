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


(defn help []
  [:div
   [menu "Help"]
   [:h1 "Help"
    [:p "Arduino based Hydroponics by TB and the gang." ]
    [:p "Sensors and Regulation."]
    [:ol
     [:li "Mao loves Clojure."]
     [:li "Tayron is the best cat!"]
     [:li "TB is an C++ Ninja."]
     ]
    ]
   ])