(ns clj-dice-roller.core)

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (let [rolls (map #(inc (rand-int %)) (repeat amount dice))
        rolls+mod (map #(+ % modifier) rolls)]
   (print rolls)
    rolls+mod))

(roll 0 6) ;; () or []
(roll 1 6) ;; [3], 1 a 6
(roll 3 6) ;; [1 4 6]
(roll 2 6 {:modifier 2})
(roll 1 0) ;; ERROR or 0
(roll -1 6) ;; ???
(roll 3 -1) ;; ???
(roll 1 20)
