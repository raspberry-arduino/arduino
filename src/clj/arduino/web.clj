(ns arduino.web
  (:require

    [mount.core :refer [defstate]]
    [clojure.tools.logging :refer [info]]

    [schema.core :as s]

    [ring.util.response :as response]
    [ring.util.http-response :refer :all]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.file :refer [wrap-file]]
    [ring.middleware.json :as mid-json]

    [compojure.core :refer :all] ; [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [cheshire.core :refer :all]

    [cognitect.transit :as transit]
    [muuntaja.core :as m]
    [muuntaja.format.transit :as mformats]


    [arduino.config :refer [config]]
    [arduino.db :as db]
    [arduino.mqtt :as mqtt]
    [arduino.influx :as influx]
    [arduino.timer :as timer]
   )
  (:use ring.adapter.jetty))

; https://stackoverflow.com/questions/46859881/clojure-encode-joda-datetime-with-ring-json
; 2018 12 09 fh: for some reason the web server seems not to use cheshire, but transport, so this fix does not work
(extend-protocol cheshire.generate/JSONable
  org.joda.time.DateTime
  (to-json [dt gen]
    (cheshire.generate/write-string gen (str dt))))



(comment

  ; transit workaround
  (def joda-time-writer
    (transit/write-handler
      (constantly "m")
      (fn [v] (-> ^org.joda.time.ReadableInstant v .getMillis))
      (fn [v] (-> ^org.joda.time.ReadableInstant v .getMillis .toString))))

  (def wrap-format-options
    (update
      m/default-options
      :formats
      merge
      {"application/transit+json"
       {:decoder [(partial mformats/make-transit-decoder :json)]
        :encoder [#(mformats/make-transit-encoder
                     :json
                     (merge
                       %
                       {:handlers {org.joda.time.DateTime joda-time-writer}}))]}}))



  )

(defroutes app-routes
           ;(route/resources "/" {:root "public"})

           ;; NOTE: this will deliver your index.html
           (GET "/" [] (-> (response/resource-response "index.html" {:root "public"})
                           (response/content-type "text/html")))

           (sweet/api
             (sweet/context "/api" []
                            :tags ["api"]

                            (sweet/GET "/test" []
                              :query-params []
                              (ring.util.http-response/ok [1 2 3 4 5 6 7]))

                            (sweet/GET "/topics" []
                                       :query-params []
                                       (ring.util.http-response/ok (db/get-topics)))

                            (sweet/GET "/action" []
                                       :query-params [topic :- s/Str ,  {payload :- s/Str nil}]
                                       (ring.util.http-response/ok (mqtt/do-action topic payload)))


                            (sweet/GET "/history" []
                              :query-params [topic :- s/Str ]
                              (ring.util.http-response/ok (influx/history topic)))


                            (sweet/GET "/timer-stop" []
                              :query-params [name :- s/Str ]
                              (ring.util.http-response/ok
                                (do (timer/stop-timer (keyword name))
                                    {:timer name :result "timer is stopped"})))

                            (sweet/GET "/timer-start" []
                              :query-params [name :- s/Str ,  on :- s/Int , off :- s/Int ]
                              (ring.util.http-response/ok
                                (do (timer/start-timer name {:on on :off off} )
                                    {:timer name :result "timer was started"}
                                    ) ))

                            (sweet/GET "/timer-running" []
                              :query-params []
                              (ring.util.http-response/ok (timer/running-timer-info)))

                            ))

           (route/not-found "<h1> Sorry! Page not found! </h1>"))




; (def app (wrap-defaults app-routes site-defaults) )

;(def app (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))


(def app
  (-> app-routes
      (mid-json/wrap-json-response)
      (wrap-defaults site-defaults)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-resource "public")
      (wrap-file "resources/public")
      ;(wrap-content-type)
      ;(wrap-not-modified)
      ))


;; NOTE: wrap reload isn't needed when the clj sources are watched by figwheel
;; but it's very good to know about
;(def dev-app (wrap-reload (wrap-defaults #'app-routes site-defaults)))


(defn- start-server
  ([handler port]
   (run-jetty
     (->
       handler
       ;wrap-swank
       ;wrap-stacktrace
       )
     {:port port, :join? false})))


(defn start-web [port]
  (info "starting web server..")
  (start-server app port)
  )

(defn stop-web [server]
  (info "stopping web server..")
   (.stop server))


(defstate web-server
          :start (start-web (get-in config [:web :port]))
          :stop  (stop-web web-server) )


(comment

  (serve* app 7000)

  (stop-server)

  )





