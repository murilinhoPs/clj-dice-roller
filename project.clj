(defproject clj-dice-roller "0.1.0-SNAPSHOT"
  :description "Rolls dice"
  :url "http://example.com/FIXME"
  :license {:name "Unlicense"
            :url "https://unlicense.org/"}
  :profiles {:dev {:source-paths ["src" "repls"]}}
  :aliases  {"repls-test" ["run" "-m" "repl-runner"]}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.cognitect/transcriptor "0.1.5"]
                 [org.clojure/spec.alpha "0.5.238"]]
  :repl-options {:init-ns clj-dice-roller.core})
