(ns clj-dice-roller.core
  (:require [clojure.string :as str]))

(defn parse-rolls
  [& args]
  (mapv (fn [roll]
          (->> (str/split roll #"d")
               (mapv #(Integer/parseInt %))))
        args))

(defn remove-strings
  [rolls]
  (mapv #(if (coll? %) (remove-strings %)  %)
        (remove string? rolls)))

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (->>  (mapv #(inc (rand-int %)) (filter pos-int? (repeat amount dice)))
        (mapv #(+ % modifier))))

(defn roll-multiple
  [& args]
  (->> (reduce (fn [acc dice]
                 (let [parsed-dice (-> dice parse-rolls first)
                       roll (apply roll parsed-dice)]
                   (conj acc [roll dice])))
               [] args)
       (mapv #(into (first %) [(second %)]))))

(defn sum-multiple-rolls
  [rolls]
  (let [remove-strs  (remove-strings rolls)
        rolls-results (reduce #(apply conj % %2) remove-strs)
        print-rolls (if (-> rolls count (= 1)) (first rolls) rolls)] 
    [(reduce + rolls-results) print-rolls]))

(defn roll-keep-highest
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (let [roll (roll amount dice {:modifier modifier})
        highest (apply max roll)]
    [roll highest]))

(defn roll-keep-lowest
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (let [roll (roll amount dice {:modifier modifier})
        lowest (apply min roll)]
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
(-> (roll-multiple "3d4") (sum-multiple-rolls)) ;; [6 [1 1 4 "3d4"]]
(-> (roll-multiple "3d4" "1d6") (sum-multiple-rolls)) ;; [10 [[3 3 2 "3d4"] [2 "1d6"]]]