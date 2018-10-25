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
                 ;[cljsjs/material-ui "3.1.1-0"]
                 [cljsjs/material-ui "3.2.0-0"]
                 [secretary "1.2.3"]                        ; client-side routing

                 ; Backend
                 [ring "1.7.0"]
                 [ring/ring-core "1.7.0"]
                 [ring/ring-devel "1.7.0"]
                 [ring/ring-jetty-adapter "1.7.0"]; needs to match compojure version
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-codec "1.1.1"]
                 [compojure "1.6.1"] ; Routing
                 [metosin/compojure-api "1.1.10"]
                 [cheshire "5.8.0"] ; JSON encoding
                 [clojurewerkz/machine_head "1.0.0"]        ; MQTT



]

  :plugins [
            [lein-cljsbuild "1.1.7"]
            [lein-ring "0.9.7"]
            ]

  :ring {
         :handler arduino.web/app
         :auto-reload? false
         }

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]]

    :plugins      [[lein-figwheel "0.5.16"]]}
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
