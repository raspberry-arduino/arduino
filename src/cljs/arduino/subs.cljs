(ns arduino.subs
  (:require
   [re-frame.core :as re-frame]))


; Sensor List (configuration data)
(re-frame/reg-sub
  ::data
  (fn [db]
    (:data db)))


; Sensor data Timeseries history (from influx)
(re-frame/reg-sub
  ::history
  (fn [db]
    (:history db)))


(re-frame/reg-sub
  ::saying
  (fn [db]
    (:saying db)))


(re-frame/reg-sub
  ::name
  (fn [db]
    (:name db)))
