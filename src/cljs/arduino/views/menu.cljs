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
              :style  {:background-color mui/secondary
                       ;; :text-align       :center
                       :padding          "1em 2em 1em 2em"}}

   [mui/grid {:container   true
              :align-items :center
              :spacing     16
              :xs 12}

    [mui/grid {:item true :xs 6}
     [mui/typography {:variant :display2
                      :style   {:color :white}}
      "Hydroponics 4 U"]]

    [mui/grid {:item true :xs 6}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      name]]


    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/"} "dashboard"]]]

    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/charts"} "charts"]]]

    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/timers"} "timers"]]]


    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/help"} "help"]]]

    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/settings"} "settings"]]]

    [mui/grid {:item true :xs 2}
     [mui/typography {:variant :title
                      :style   {:color   :white}}
      [:a {:href "#/simulator"} "simulator"]]]



    ]
   ])