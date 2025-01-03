(ns clj-dice-roller.core)

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (->>  (map #(inc (rand-int %)) (filter pos-int? (repeat amount dice)))
        (map #(+ % modifier))))

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

(roll 0 6) ;; () or []
(roll 1 6) ;; [3], 1 a 6a
(roll 3 6) ;; [1 4 6]
(roll 2 6 {:modifier 2})
(roll-keep-highest 3 10)
(roll-keep-lowest 3 6)
(roll 1 0) ;; ()
(roll -1 6) ;; ()
(roll 3 -1) ;; ()
(roll -3 -6) ;; ()
(roll 1 20)
