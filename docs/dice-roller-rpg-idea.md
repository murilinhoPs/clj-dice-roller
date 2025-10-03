# Ideia de Projeto: Rolador de Dados para RPG

Este documento descreve a concepção de um projeto de rolador de dados para RPG de mesa, utilizando um backend em Clojure para a lógica e um frontend em ClojureScript para a interação com o usuário.

## Conceito Base: Backend como Motor de Rolagem

A ideia inicial é criar um serviço de backend robusto que possa interpretar e calcular expressões de dados complexas, comuns em jogos de RPG.

* **Gameplay:** O usuário digita uma expressão de dados (ex: "4d6kh3") em um campo de texto, e o sistema calcula o resultado.
* **Backend (CLJ):**
  * Criar uma rota de API, por exemplo `POST /api/evaluate-roll`, que recebe uma string de expressão como `{"expression": "4d6kh3"}`.
  * O backend contém a lógica para **interpretar** essa string:
    * `4d6`: Rolar 4 dados de 6 lados.
    * `kh3`: "Keep Highest 3" (manter os 3 maiores resultados).
    * `+5`: Adicionar um bônus.
  * A API executa a rolagem, aplica as regras e retorna um resultado detalhado em JSON. Exemplo de resposta para `"4d6kh3+5"`:
  * Uma rota que salva as rolagens anteriores para mostar um histórico (tem um limite de rolagens salvas)

    ```json
    {
      "expression": "4d6kh3+5",
      "rolls": [6, 5, 2, 1],
      "kept": [6, 5, 2],
      "bonus": 5,
      "total": 18
    }
    ```

* **Frontend (CLJS) - Versão Simples:**
  * Um campo de texto para o usuário digitar a expressão.
  * Um botão "Rolar" que envia a string para a API do backend.
  * Uma área para exibir de forma clara o resultado detalhado que o backend retornou.

---

## Evoluindo o Projeto: Um Frontend Detalhado (UI-Driven)

Com o motor do backend pronto, o frontend pode ser muito enriquecido para oferecer uma experiência mais visual e amigável, sem precisar de um campo de texto para expressões.

### 1. Construtor de Rolagem Visual

Em vez de texto, o usuário usa controles visuais para montar a rolagem.

* **Interface:**
  * **Quantidade:** Um campo numérico ou botões `[+]` e `[-]` para definir quantos dados rolar.
  * **Tipo de Dado:** Botões grandes e clicáveis com `d4`, `d6`, `d8`, `d10`, `d12`, `d20`, `d100`.
  * **Modificadores:** Um campo numérico para bônus ou penalidades (ex: `+5`, `-2`).
  * **Regras Especiais:** Botões para "Vantagem" e "Desvantagem". Ao clicar em "Vantagem", o frontend montaria a expressão "2d20kh1" por baixo dos panos.
* **Como funciona:** O frontend usa os inputs do usuário para **construir a string de expressão** internamente. Ao clicar em "Rolar", ele envia a string montada (ex: `"3d8+4"`) para a mesma API do backend. A experiência do usuário melhora drasticamente sem alterar o backend.

### 2. Animação e Feedback Visual

Mostre os dados rolando para uma experiência mais tátil.

* **Interface:**
  * Ao clicar em "Rolar", uma animação de dados rolando é exibida.
  * Quando a resposta da API chega, a animação para e os "dados" na tela mostram os números exatos que foram retornados pelo backend.
* **Como funciona:** Use componentes visuais (divs estilizadas com CSS) para representar os dados. A animação pode ser feita com transições ou keyframes de CSS. Adicionar um efeito sonoro de dados rolando pode enriquecer ainda mais a experiência.

### 3. Relação Detalhada de Resultados

Mostre *como* o total foi calculado, não apenas o número final.

* **Interface:**
  * Exiba cada dado individualmente após a rolagem.
  * Se uma regra como "manter os 3 maiores" foi usada, os dados descartados podem ser mostrados em cinza ou riscados.
  * Mostre a matemática de forma explícita: `(6 + 5 + 2) + 5 = 18`.
* **Como funciona:** O frontend utiliza a resposta JSON detalhada do backend para construir essa visualização rica e informativa.

### 4. Rolagens Salvas (Presets)

Permita que o usuário salve suas rolagens mais frequentes para acesso rápido.

* **Interface:**
  * Uma seção "Rolagens Salvas" na tela.
  * Um botão "Salvar Rolagem Atual" que pede um nome (ex: "Ataque de Espada", "Dano de Bola de Fogo").
  * Uma lista de rolagens salvas, cada uma com um botão "Rolar" ao lado.
* **Como funciona:** O frontend pode salvar essas predefinições no **`localStorage` do navegador**. Isso adiciona uma funcionalidade de persistência útil sem a necessidade de um sistema de contas/login no backend, mantendo o projeto mais simples.

---

## Expandindo o Backend: Mais Ações e Conteúdo

O backend pode evoluir de um simples calculador para o cérebro de uma aplicação de RPG muito mais completa.

### 1. Contas de Usuário e Persistência de Dados

Transforme a ferramenta de anônima para uma plataforma personalizada, onde os dados do usuário são salvos no servidor e acessíveis de qualquer lugar.

* **Ações a serem feitas:**
  * **Autenticação:** Implementar rotas para registro (`/api/auth/register`) e login (`/api/auth/login`) de usuários, gerenciando senhas com hashing e sessões com tokens (JWT).
  * **Dados do Usuário:** Criar rotas que exigem autenticação para salvar e buscar predefinições de rolagens (`/api/user/rolls`) e históricos de rolagens (`/api/user/history`) associados a um usuário específico.
* **Complexidade Adicionada:** Exige um banco de dados (ex: SQLite), e bibliotecas para hashing e tokens, aprofundando o conhecimento em segurança e persistência de dados.

### 2. Salas de Jogo em Tempo Real (com WebSockets)

Permita que um grupo de jogadores veja as rolagens uns dos outros em tempo real, simulando uma mesa de jogo virtual.

* **Ações a serem feitas:**
  * **Gerenciamento de Salas:** Uma rota para criar uma nova "sala de jogo" (`/api/rooms`) que retorna um ID único.
  * **Comunicação Real-time:** Implementar um endpoint de **WebSocket** (`/ws/room/:roomId`). Quando um usuário na sala faz uma rolagem, o backend calcula e **transmite (broadcasts)** o resultado para todos os outros participantes da sala.
* **Complexidade Adicionada:** Introduz o conceito de WebSockets para comunicação bidirecional, essencial para aplicações colaborativas e em tempo real.

### 3. Integração com Fichas de Personagem Simplificadas

Faça com que o backend entenda o contexto do jogo, armazenando fichas de personagem e fazendo rolagens baseadas em seus atributos.

* **Ações a serem feitas:**
  * **Gerenciamento de Personagens:** Rotas para criar e buscar personagens de um usuário, com seus atributos (ex: Força, Destreza).
  * **Rolagens Contextuais:** Uma rota (`/api/roll/character-skill`) que recebe um ID de personagem e uma perícia (ex: "teste de força"). O backend busca os atributos do personagem, calcula o bônus relevante (ex: Força 18 -> +4), e executa a rolagem (ex: "1d20+4") automaticamente.
* **Complexidade Adicionada:** O backend passa a conter lógica específica das regras do sistema de RPG, tornando-se mais "inteligente".

### 4. Expansão do Motor de Expressões

Torne o próprio interpretador de dados mais poderoso para suportar sistemas de regras mais variados.

* **Ações a serem feitas:**
  * **Dados Explosivos:** Adicionar suporte para sintaxe de "exploding dice" (ex: `3d6!`), onde rolagens de valor máximo são roladas novamente e somadas.
  * **Contagem de Sucessos:** Suportar expressões que contam resultados acima de um limiar (ex: `10d8>6`) em vez de somar os valores.
* **Complexidade Adicionada:** Aprofunda o trabalho na lógica de parsing e avaliação de expressões, um desafio de programação interessante por si só.
