(ns arduino.views.menu
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [arduino.subs :as subs]
            [arduino.mui :as mui]
            [oz.core :as oz]
            [goog.string :as gstring]
            [goog.string.format]

                        ))


(defn menu [name]

  [mui/paper {:square true
              :style  {:padding "1em 2em 1em 2em"
                      :margin-bottom "2em"
                      :a {
                        :color "white !important"
                        :text-decoration "none !important"
                      }
                }}
    [mui/app-bar
      [mui/tool-bar
        [mui/typography {:variant :display2
                         :style   {:color :white}}
         "Hydroponics 4 U"]

        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/"} "dashboard"]]
        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/charts"} "charts"]]

        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/help"} "help"]]

        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/timers"} "timers"]]

        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/settings"} "settings"]]

        [mui/button {:variant :title
                        :style   {:color   :white}}
          [:a {:href "#/simulator"} "simulator"]]
      ]
    ]
  ])
