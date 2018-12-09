(defproject arduino "0.1.0-SNAPSHOT"
  :dependencies [
                 ;Common
                 [org.clojure/clojure "1.9.0"]
                 [clojure-future-spec "1.9.0-beta4"]        ; Clojure SPEC (required by compojure routing)
                 [clj-time "0.14.3"]
                 [clj-jgit "0.8.8"]
                 [camel-snake-kebab "0.4.0"]

                 ;Frontend
                 [org.clojure/clojurescript "1.10.238"]
                 ;[cljsjs/react "16.4.0-0"]
                 ;[cljsjs/react-dom "16.4.0-0"]
                 [reagent "0.8.2-SNAPSHOT" :exclusions [cljsjs/react cljsjs/react-dom]] ; reagent has older react references than material-ui
                 [re-frame "0.10.5"]
                 [cljs-ajax "0.7.3"]
                 [cljsjs/material-ui "3.2.0-0"]
                 [secretary "1.2.3"]                        ; client-side routing
                 [metasoarous/oz "1.3.1"]                   ; Vega Charting Wrapper


                 ; Backend
                 ; Logging
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-simple "1.7.5"]
                 ;             [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                 ;                                  javax.jms/jms
                 ;                                   com.sun.jdmk/jmxtools
                 ;                                   com.sun.jmx/jmxri       ]]


                 [mount "0.1.11"]                           ;start/stop component
                 [ring "1.7.0"]
                 [ring/ring-core "1.7.0"]
                 [ring/ring-devel "1.7.0"]
                 [ring/ring-jetty-adapter "1.7.0"]          ; needs to match compojure version
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-codec "1.1.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]                        ; Routing
                 [metosin/compojure-api "1.1.10"]
                 [cheshire "5.8.0"]                         ; JSON encoding
                 [metosin/jsonista "0.2.2"]                 ; metosin json encoding
                 [metosin/muuntaja "0.6.2"]                 ;date encoding for transit

                 ; our project
                 [clojurewerkz/machine_head "1.0.0"]        ; MQTT
                 [capacitor "0.6.0"]                        ; InfluxDB
                 [jarohen/chime "0.2.2"]                    ; Scheduler

                 ]

  :plugins [
            [lein-cljsbuild "1.1.7"]
            [lein-ring "0.9.7"]

            [figwheel-sidecar "0.5.17" :exclusions [org.clojure/tools.nrepl]]
            [lein-figwheel "0.5.17"]
            ]

  :ring {
         :handler arduino.web/app
         :auto-reload? false
         }

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]


  :main arduino.app

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :nrepl-port 7002}

  ;; setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles
  {:dev
   {:dependencies [
                   [binaryage/devtools "0.9.10"]
                   [com.cemerick/piggieback "0.2.1"]
                   [figwheel-sidecar "0.5.17" :exclusions [org.clojure/tools.nrepl]]

                   ]

    :plugins      [[lein-figwheel "0.5.16"]]
    }
   :prod { }
   }

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "arduino.core/mount-root"}
     :compiler     {:main                 arduino.core
                    :npm-deps             false
                    :infer-externs        true
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            arduino.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}


  )
