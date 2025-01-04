# Schema and Validation

- Função *"normal"* com o schemas do prismatic

    Compile-time check
    `(:require [schema.core :as s])`

    ``` clojure
    (s/defn func 
        [& args :- [{:nome s/Str 
                     :idade s/Num}]]
        (println args))

    (func4 {:nome "João" :idade 30}
           {:nome "Maria" :idade 25})```

-----

- Criar apenas uma função com validação de quais params o `& args` vai aceitar se for um mapa no caso. **Não está validando o tipo delas**

    Nesse caso só vai ser validado em tempo de execução (`runtime`), só vou encontrar algum erro quando executar essa parte do código
    Estou usando uma `:pre` condition para validar os args antes de passar eles para frente
  
  ```clojure
    (defn func4 [& args]
        {:pre [(every? #(and (:nome %) (:idade %)) args)]}
        (println args))

    (func {:nome "João" :idade 30}
          {:nome "Maria" :idade 25}
          {:nome 13M :idade "dsadad"})```


-----

- Criar uma função que valida quais params e os tipos deles que o `& args` vai aceitar.

  - ddd

    ```clojure
        ;; Usando pre-conditions
    (defn func4 [& args]
        {:pre [(every? #(and (string? (:nome %))
                            (number? (:idade %)))
                        args)]}
     ...)

    (func {:nome "João" :idade 30}
        {:nome "Maria" :idade 25})```

  - Posso também criar uma função auxiliar para fazer essa verificação dos tipos, sem passar in-line

    ```clojure
        
        (defn valid-arg? [arg]
            (and (map? arg)
                (string? (:nome arg))
                (number? (:idade arg))))

    (defn func4 [& args]
        {:pre [(every? valid-arg? args)]}
     ...)

    (func {:nome "João" :idade 30}
        {:nome "Maria" :idade 25})```

  - Ou usando uma verificação explicita na função

    ```clojure
    (defn func4 [& args]
    (when-not (every? #(and (string? (:nome %))
                            (number? (:idade %)))
                    args)
        (throw (IllegalArgumentException. "Cada argumento deve ter :nome como string e :idade como número")))
     ...)
        
        (func {:nome "João" :idade 30}
            {:nome "Maria" :idade 25})``` 
