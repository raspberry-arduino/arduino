(defproject arduino "0.1.0-SNAPSHOT"
  :dependencies [
                 ;Clojure Essentials
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [clj-time "0.11.0"]
                 [clj-jgit "0.8.8"]
                 [clojure-future-spec "1.9.0-beta4"]        ; Clojure SPEC (required by compojure routing)

                 ; Domain Libraries
                 [clojurewerkz/machine_head "1.0.0"]        ; MQTT

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

                 ;Frontend
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [cljs-ajax "0.7.3"]

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
