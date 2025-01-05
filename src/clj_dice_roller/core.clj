(ns clj-dice-roller.core
  (:require [clojure.string :as str]))

;;TODO: add modifier to multiple rolls
;;TODO: format roll-multiple output like rollem
;;TODO: logic to choose between fn, if it should call roll, roll-multiple, or advantage and disavantage roll

(defn parse-roll
  [roll]
  (->> (str/split roll #"d")
       (mapv #(Integer/parseInt %))))

(defn remove-strings
  [rolls]
  (mapv #(if (coll? %) (remove-strings %)  %)
        (remove string? rolls)))

(defn ^:private sum-multiple-rolls
  [rolls]
  (let [remove-strs  (remove-strings rolls)
        rolls-results (reduce #(apply conj % %2) remove-strs)
        print-rolls (if (-> rolls count (= 1)) (first rolls) rolls)]
    [(reduce + rolls-results) print-rolls]))

(defn get-roll-value 
  "Returns only the result from the roll form this ([15] 1d20)"
  [roll] (first roll))

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount dice & {:keys [modifier]}]
  (let [result (->> (mapv #(inc (rand-int %)) (filter pos-int? (repeat amount dice)))
                    (mapv #(+ % (or modifier 0))))
        print-dice (str amount "d" dice)
        print-mod (when modifier (str "+" modifier))]
    (remove nil? [result print-dice print-mod])))

(defn roll-multiple
  [& args]
  (-> (->> (reduce (fn [acc dice]
                     (let [parsed-dice (parse-roll dice)
                           roll (apply roll parsed-dice)]
                       (conj acc [(get-roll-value roll) dice])))
                   [] args)
           (mapv #(into (first %) [(second %)])))
      (sum-multiple-rolls)))

(defn roll-keep-highest
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (let [roll (roll amount dice {:modifier modifier})
        highest (apply max (get-roll-value roll))]
    [roll highest]))

(defn roll-keep-lowest
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (let [roll (roll amount dice {:modifier modifier})
        lowest (apply min (get-roll-value roll))]
    [roll lowest]))

(roll-keep-highest 3 10) ;; () or []
(roll-keep-lowest 3 6) ;; [3], 1 a 6a
(roll 0 6) ;; [1 4 6]
(roll 1 6)
(roll 3 6)
(roll 2 6 {:modifier 2})
(roll 1 0) ;; ()
(roll -1 6) ;; ()
(roll 3 -1) ;; ()
(roll -3 -6) ;; ()
(roll 1 20)
(roll-multiple "3d4") ;; [6 [1 1 4 "3d4"]]
(roll-multiple "3d4" "1d6") ;; [10 [[3 3 2 "3d4"] [2 "1d6"]]]