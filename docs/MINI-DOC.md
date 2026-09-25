# clj-dice-roller — mini doc

Lib Clojure (Leiningen) para rolar dados no terminal. Projeto de **estudo**: nasceu do template Lein e foi evoluindo no REPL. Não tem Shadow-CLJS, npm nem frontend.

Repo: https://github.com/murilinhoPs/clj-dice-roller

---

## Como foi criado

1. **2025-01-02** — scaffold Leiningen (`defproject`, `CHANGELOG` ainda com texto de template “widgets”).
2. Funções de rolagem + modificador; depois vantagem/desvantagem.
3. Testes com **Cognitect Transcriptor** (`repls/*.repl`).
4. `roll-multiple`, soma de várias expressões, formatação de saída estilo “rollem”.
5. Modifiers com `+ - * /`.
6. Anotações em `docs/` (`data-structure.md`, `validation.md`).
7. **2025-10** — visão de app RPG em `docs/dice-roller-rpg-idea.md` (ainda não implementada).

---

## Estrutura

| Caminho | Papel |
|---|---|
| `project.clj` | Lein, Clojure 1.11.1, `spec.alpha`, Transcriptor; REPL inicia em `clj-dice-roller.core`; profile `:dev` inclui `repls/` no classpath |
| `src/clj_dice_roller/core.clj` | API: parse, roll, multiple, keep high/low |
| `repls/rolls_repl.repl` + `repls/repl_runner.clj` | Suíte real de testes (Transcriptor) |
| `test/clj_dice_roller/core_test.clj` | Placeholder do template — ainda falha (`(= 0 1)`) |
| `docs/*` | Caderno de estudo + ideia futura de produto RPG |

---

## API (visão rápida)

Namespace: `clj-dice-roller.core`

- Parse de strings tipo `"3d6"` (e `"d4"` como um dado).
- Rolagem com `rand-int` e modifiers.
- Várias expressões somadas (`roll-multiple`).
- Manter maior / menor resultado (`roll-keep-highest` / `roll-keep-lowest`).

Há um TODO no `core` para unificar a escolha entre roll / multiple / vantagem. Prefira as formas públicas (`roll-multiple`, `roll-keep-highest`, `roll-keep-lowest`) e os exemplos abaixo; `repls/rolls_repl.repl` ainda exercita o helper `roll` com aridade numérica (marcado `^:private`). Alguns comentários antigos no código ainda mostram assinaturas experimentais.

---

## Como rodar

Requisitos: **JDK** + **Leiningen** no PATH.

```bash
git clone https://github.com/murilinhoPs/clj-dice-roller.git
cd clj-dice-roller
lein repl
```

O REPL abre em `clj-dice-roller.core`. Exemplos:

```clojure
(require '[clj-dice-roller.core :as dice])

(dice/roll-multiple "1d20")
(dice/roll-multiple "3d4" "1d6")
(dice/roll-multiple "2d6" {:modifier "+3"})

(dice/roll-keep-highest "3d6")
(dice/roll-keep-lowest "3d6")
```

Não há app web nem `lein run` de servidor: o uso é lib + REPL.

---

## Como testar

### Caminho real — Transcriptor

```bash
lein repls-test
```

Alias em `project.clj`: `"repls-test"` → `["run" "-m" "repl-runner"]`. O runner (`repls/repl_runner.clj`) chama `(xr/run ...)` em cada `*.repl` de `repls/`; as checagens ficam nos arquivos via `(xr/check! ...)`.

Arquivo principal hoje: `repls/rolls_repl.repl`.

### `clojure.test` (não use como gate ainda)

```bash
lein test
```

Falha no placeholder `FIXME, I fail.` — resto do template Lein, não cobre a lib.

### Spec

`org.clojure/spec.alpha` está nas deps e aparece no `.repl`; não há suite `s/fdef` completa no `core`.

---

## Relação com os outros projetos

Fora das famílias **Static/SPA** e **SSR híbrido** do frontend ClojureScript. Serve como referência de Clojure JVM / Leiningen antes de entrar em Shadow, Helix/UIx ou o template SSR.

A ideia em `docs/dice-roller-rpg-idea.md` (API de expressões + UI CLJS) seria um produto separado; este repo continua sendo só o motor/estudo em terminal.

---

## Dependências

- `org.clojure/clojure` 1.11.1  
- `org.clojure/spec.alpha` 0.5.238  
- `com.cognitect/transcriptor` 0.1.5  

Licença declarada no `project.clj`: Unlicense.
