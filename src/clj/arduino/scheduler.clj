(ns arduino.scheduler
  (:require [clojure.tools.logging :refer [info]]
            [chime :refer  [chime-at]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            )
  (:import [org.joda.time DateTimeZone])
  )

(defn run-every [minutes]
  "lazy sequence of on-times"
  (->> (periodic-seq (.. (.plusDays (t/now) -1 )
                         (withZone (DateTimeZone/forID "America/New_York"))
                         (withTime 20 0 0 0))
                     (-> minutes t/minutes))
  ))

(defn run-for [time-seq minutes]
  "lazy sequence of off-times"
  (map #(t/plus % (t/minutes minutes)) time-seq)
  )

(defn run-on-off [run-every-minutes run-for-minutes]
  "lazy sequence of on/off times"
  "not used, because since chime skipps past times, we don't know weather it is on or off"
  (let [on-seq (run-every run-every-minutes)
        off-seq (run-for on-seq run-for-minutes)
        combined (interleave on-seq off-seq)]
    combined
    ))

(defn start-cycle [name run-every-minutes run-for-minutes f]
  "returns a zero argument function that can be called to cancel the timer"
  (let [on-seq (run-every run-every-minutes)
        off-seq (run-for on-seq run-for-minutes)
        on-action (chime-at on-seq (fn [time] (f time name true)))
        off-action (chime-at off-seq (fn [time] (f time name false)))
        end (fn []  (do  ;(info "stopping timer " name " - on-action")
                         (on-action)
                         ;(info "stopping timer " name " - off-action")
                         (off-action)
                         ;(info "stopping timer " name " - done")
                         nil
                         ) )
        xx (println name " on times: " (take 10 on-seq))
        xx (println name " off times: " (take 10 off-seq)) ]
    {:name name
     :on-seq  "on"                                          ;on-seq
     :off-seq "off"                                         ;off-seq
     :finish end
     }))

(defn stop-cycle [running-timer]
  (let [name (:name running-timer)
        finish (get-in running-timer [:finish])]
    (info "stop-cycle: " name)
    (finish)))


(comment ;********************************************************

  (chime-at [(-> 2 t/seconds t/from-now)
             (-> 4 t/seconds t/from-now)]
            (fn [time]
              (println "Chiming at" time)))

  ; lazy sequence of times, each for 20:00 NY Time, one for each day
  (->> (periodic-seq (.. (t/now)
                         (withZone (DateTimeZone/forID "America/New_York"))
                         (withTime 20 0 0 0))
                     (-> 1 t/days)))


  (take 50 (run-every 240) )
  (take 10 (run-for (run-every 60) 7 ))
  (take 10 (run-on-off 60 7) )


  (defn demo [time name val]
    (println time " setting " name " to " val ))

  (def running-timer (start-cycle "light55" 60 6 demo) )
  (stop-cycle running-timer)

  );********************************************************
