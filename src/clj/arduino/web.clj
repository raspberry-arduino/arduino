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


    [compojure.core :refer :all] ; [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [compojure.api.sweet :as sweet]
    [cheshire.core :refer :all]

    [arduino.config :refer [config]]
    [arduino.db :as db]
    [arduino.mqtt :as mqtt]
    [arduino.influx :as influx]
   )
  (:use ring.adapter.jetty))


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


                            ))

           (route/not-found "<h1> Sorry! Page not found.</h1>"))




; (def app (wrap-defaults app-routes site-defaults) )

;(def app (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))


(def app
  (-> app-routes
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





