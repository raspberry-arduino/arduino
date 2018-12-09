(ns arduino.views.chart

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


;; define a function for generating some dummy data
(defn group-data [& names]
  (apply concat
         (for [n names]
           (map-indexed (fn [i x] {:x i :y x :col n}) (take 20 (repeatedly #(rand-int 100)))))))



(def line-plot
  {:data {:values (group-data "tiberius" "tayron" "esteban" "mao")}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})



(defn lineplot-convert [name history]
  (map-indexed (fn [idx itm] (assoc {} :x idx :y itm :col name)) history))

(defn line-plot-settings [name history]
  {:data {:values (lineplot-convert name history)}
   :encoding {:x {:field "x"}
              :y {:field "y"}
              :color {:field "col" :type "nominal"}}
   :mark "line"})


(defn sensor-plot [name]
  (let [ xx (re-frame.core/dispatch [:request-history name])     ; request history once at first time
        history (re-frame/subscribe [::subs/history])]      ; get the data from the re-frame db
    (fn []
      ( let [plot-data (line-plot-settings name ((keyword name) @history)) ; create vega plot from array-data
             xx (println "lineplot data: " plot-data)]
        [oz.core/vega-lite plot-data])
      )))


(defn chart-page []
  [:div
   [menu "Charts"]
   [:h1 "Charts"]
   [mui/grid {:container true}
    [mui/mui-theme-provider {:theme mui/jyu-theme-dark}
      [:p1 "demo plot:"]
      [oz.core/vega-lite line-plot]
      [mui/divider]
      [:p1 "real plots:"]
      [sensor-plot "temp"]
      [sensor-plot "ph"]
      [sensor-plot "ppm"]
      [sensor-plot "light"]

     ]]

   ])