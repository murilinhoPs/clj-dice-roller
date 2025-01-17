(ns clj-dice-roller.core
  (:require [clojure.string :as str]))

;;TODO: logic to choose between fn, if it should call roll, roll-multiple, or advantage and disavantage roll

(defn parse-roll
  [roll]
  (let [parse-fn #(if (str/starts-with? roll "d")
                    (-> (str/replace % #"d" "1d") (str/split #"d"))
                    (str/split % #"d"))]
    (->> (parse-fn roll)
         (mapv #(Integer/parseInt %)))))

(defn remove-strings
  [rolls]
  (mapv #(if (coll? %) (remove-strings %)  %)
        (remove string? rolls)))

(defn ^:private  get-roll-value
  "Returns only the result from the roll form this ([15] 1d20)"
  [roll] (first roll))

(defn ^:private  get-modifier-value
  "Returns only the value (number) from modifier (+ 3), gets 3"
  [modifier] (second modifier))

(defn ^:private roll-operation
  [roll modifier]
  (when modifier
    (let [modfier-value (-> modifier (subs 1) str Integer/parseInt)
          math-operation ((-> modifier first str symbol resolve) roll modfier-value)]
      (println modfier-value)
      (print math-operation)
      [math-operation modifier])))

(defn ^:private sum-multiple-rolls
  [rolls & {:keys [modifier]}]
  (let [result (->> (reduce #(apply conj % %2) (remove-strings rolls)) (reduce +))
        result+mod (when modifier (+ modifier result))
        print-mod (when modifier (str "+" modifier)) ;;TODO: change modifier to string
        print-dice (if (-> rolls count (= 1)) (first rolls) rolls)
        print-result (if modifier result (str result " <-"))
        print-result+mod (when modifier (str result+mod " <-"))]
    (remove nil? [print-result+mod print-result print-dice print-mod])))

(defn ^:private roll ;;TODO: add parser for other arithmetic functions (- * /)
  "Rolls some dice, like (roll 3 6) would be three d6."
  [roll & {:keys [modifier]}]
  (let [[amount dice] (parse-roll roll)
        roll-result (mapv #(inc (rand-int %)) (filter pos-int? (repeat amount dice)))
       ; [result+mod print-mod] (roll-operation roll-result modifier)
        [result+mod print-mod] (when modifier (->> roll-result (mapv #(roll-operation % modifier)))) ;;TODO: criar uma função para a operação
        ;print-mod (when modifier (str "+" modifier)) ;;TODO: mudar string de acordo com a operação
        print-dice (str amount "d" dice)]
    (println print-mod)
    (println result+mod)
    (remove nil? [result+mod roll-result print-dice print-mod])))

(defn roll-multiple
  [& args]
  (let [optional? (-> args last map?)
        optional-args (when optional? (last args))
        regular-args (if optional? (butlast args) args)
        {:keys [modifier]} optional-args]
    (-> (->> (reduce #(conj % [(-> %2 roll get-roll-value) %2])
                     [] regular-args)
             (mapv #(into (first %) [(second %)])))
        (sum-multiple-rolls {:modifier modifier}))))

(defn roll-keep-highest
  [args & {:keys [modifier]}]
  (let [roll (roll args {:modifier modifier})
        highest (apply max (get-roll-value roll))]
    [highest roll]))

(defn roll-keep-lowest
  [args & {:keys [modifier]}]
  (let [roll (roll args {:modifier modifier})
        lowest (apply min (get-roll-value roll))]
    [lowest roll]))

;; (roll-keep-highest 3 10) ;; () or []
;; (roll-keep-lowest 3 6) ;; [3], 1 a 6a
;; (roll 0 6) ;; [1 4 6]
;; (roll 1 6)
;; (roll 3 6)
;; (roll 2 6 {:modifier 2})
;; (roll 1 0) ;; ()
;; (roll -1 6) ;; ()
;; (roll 3 -1) ;; ()
;; (roll -3 -6) ;; ()
;; (roll 1 20)
;; (roll-multiple "3d4") ;; [6 [1 1 4 "3d4"]]
;; (roll-multiple "3d4" "1d6") ;; [10 [[3 3 2 "3d4"] [2 "1d6"]]]