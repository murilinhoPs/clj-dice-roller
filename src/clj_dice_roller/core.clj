(ns clj-dice-roller.core
  (:require
   [clojure.string :as str]))

(defn parse-rolls
  [& args]
  (mapv (fn [roll]
          (->> (str/split roll #"d")
               (mapv #(Integer/parseInt %))))
        args))

(defn remove-strings
  [rolls]
  (mapv #(if (coll? %) (remove-strings %)  %)
        (remove string? rolls))) ;;* get the collection, se não tem nada para remover retorna a collection normal do jeito que ta

(defn roll
  "Rolls some dice, like (roll 3 6) would be three d6."
  [amount dice & {:keys [modifier] :or {modifier 0}}]
  (->>  (mapv #(inc (rand-int %)) (filter pos-int? (repeat amount dice)))
        (mapv #(+ % modifier))))

(defn ^:deprecated roll-multiple
  [& args]
  (print args)
  (->> (apply parse-rolls args) ;;* -> [[1 4] [2 6]]
       (reduce (fn [acc v] ;;* -> acc == [] (valor inicial)
                 (let [roll (apply roll v)]
                   (println (str "value: " v))
                   (print "vector: ")
                   (println acc)
                   (println roll)
                   (apply conj acc roll)))
               []))) ;;* -> return only the values, without the dice like "1d4"

(defn roll-multiple2
  [& args]
  (->> (reduce (fn [acc dice] ;;* -> acc (accumulator) == [] (valor inicial) valor atual da iteração e o dice é o próximo, nesse caso começa em []
                 (let [value (-> dice parse-rolls first)
                       roll (apply roll value)]
                   (conj acc [roll dice])))
               [] args) ;;* ->  [[[2 4 3] "3d4"] [[5] "1d6"]]
       (mapv #(into (first %) [(second %)])))) ;;*  junta os resultados de cada dado em um único array -> [[2 4 3 "3d4"] [5 "1d6"]]

(defn sum-multiple-rolls
  [rolls]
  (let [remove-strs  (remove-strings rolls)
        parsed-rolls (reduce #(apply conj % %2) remove-strs) ;;? com lambda % pega o primeiro argumento da funcao, e %2 pega o segundo elemento da funcao
        print-rolls (if (-> rolls count (= 1)) (first rolls) rolls)] 
    [(reduce + parsed-rolls) print-rolls]))

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
(-> (roll-multiple2 "3d4") (sum-multiple-rolls)) ;; [6 [1 1 4 "3d4"]]
(-> (roll-multiple2 "3d4" "1d6") (sum-multiple-rolls)) ;; [10 [[3 3 2 "3d4"] [2 "1d6"]]]