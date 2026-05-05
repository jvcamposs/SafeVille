# Glossário

Referência rápida dos termos mais usados no desenvolvimento iOS com Swift.

---

## A

**Actor** — Tipo de referência que protege seu estado contra acesso concorrente simultâneo. Todas as operações de um actor são serializadas.

**App Store Connect** — Portal da Apple para gerenciar apps, TestFlight, métricas e publicação na App Store.

**async/await** — Sintaxe Swift para escrever código assíncrono de forma linear e legível, sem callbacks.

**Auto Layout** — Sistema de constraints do UIKit para posicionar views relativamente a outras views e ao container.

## B

**Bundle Identifier** — String única que identifica um app (ex: `com.empresa.meuapp`). Deve ser único no ecossistema Apple.

**Binding** — No SwiftUI, referência bidirecional ao estado de outra view, criada com o prefixo `$`.

## C

**Closure** — Bloco de código que pode ser armazenado e passado como argumento. Captura variáveis do contexto ao redor.

**Codable** — Protocolo composto por `Encodable` e `Decodable`. Permite serializar/deserializar tipos Swift para/de JSON, XML, etc.

**Combine** — Framework Apple para programação reativa com Publishers, Operators e Subscribers.

**Concurrency** — Execução de múltiplas tarefas simultaneamente (ou intercalada). Swift Concurrency é o modelo moderno do Swift.

**ContentView** — Por convenção, a view raiz de um app SwiftUI.

**Core Data** — Framework Apple para persistência de dados com suporte a relacionamentos, migrações e sincronização.

## D

**Decodable** — Protocolo que permite criar uma instância Swift a partir de JSON/dados externos.

**Dependency Injection** — Padrão onde dependências são passadas para um objeto ao invés de criadas por ele. Facilita testes.

**Delegate** — Padrão de design onde um objeto delega comportamentos para outro via protocolo.

## E

**Encodable** — Protocolo que permite serializar um tipo Swift para JSON/dados externos.

**Enum** — Tipo que define um conjunto fechado de casos. No Swift, enums podem ter valores associados e métodos.

**Extension** — Adiciona métodos, propriedades computadas e conformidade a protocolos a tipos existentes.

## F

**Fastlane** — Ferramenta open-source para automação de builds, testes e publicação iOS/Android.

**ForEach** — No SwiftUI, view que gera views dinamicamente a partir de uma coleção.

## G

**Generic** — Código que funciona com qualquer tipo que satisfaça certas constraints. Ex: `Array<T>`.

**guard** — Statement que sai da função/escopo atual se uma condição não for satisfeita. Usado com opcionais.

## H

**Hashable** — Protocolo que permite usar um tipo como chave de dicionário ou em conjuntos (Set).

**HIG** — Human Interface Guidelines. Documentação da Apple com diretrizes de design para apps Apple.

## I

**Identifiable** — Protocolo com propriedade `id: ID`. Obrigatório para usar tipos em `List`, `ForEach`, etc.

**IBOutlet** / **IBAction** — Conexões entre Interface Builder (Storyboard) e código Swift.

## J

**JSONDecoder** — Classe que converte JSON (`Data`) em structs/classes Swift que conformam a `Decodable`.

## L

**Lazy** — Propriedade ou sequência que só é calculada/criada quando acessada pela primeira vez.

## M

**@MainActor** — Annotation que garante execução na main thread. Obrigatório para atualizações de UI em código assíncrono.

**MVC** — Model-View-Controller. Padrão de arquitetura nativo do UIKit.

**MVVM** — Model-View-ViewModel. Padrão popular para SwiftUI com separação de lógica de apresentação.

**ModelContext** — No SwiftData, contexto que gerencia operações CRUD no banco de dados.

## N

**NavigationStack** — Container SwiftUI (iOS 16+) que gerencia a pilha de navegação.

**nil** — Ausência de valor. Em Swift, apenas Optionals podem ser nil.

## O

**@Observable** — Macro Swift 5.9+ para criar classes observáveis sem precisar de `ObservableObject` e `@Published`.

**ObservableObject** — Protocolo para criar objetos observáveis em SwiftUI (versão pré-iOS 17).

**Optional** — Tipo que pode conter um valor ou nil. Declarado com `?` (ex: `String?`).

## P

**@Published** — Property wrapper que notifica observers quando o valor muda. Usado com `ObservableObject`.

**Protocol** — Define um conjunto de requisitos (métodos, propriedades) que tipos podem adotar.

**Protocol Extension** — Adiciona implementações padrão a protocolos.

## R

**Retain Cycle** — Situação onde dois objetos se referem mutuamente, impedindo a liberação de memória.

## S

**@State** — Property wrapper SwiftUI para estado local de uma view.

**@StateObject** — Property wrapper SwiftUI para criar e possuir um ObservableObject.

**Sendable** — Protocolo que garante que um tipo pode ser passado entre isolation domains de forma segura.

**SF Symbols** — Biblioteca com 4000+ ícones da Apple, integrados ao sistema.

**struct** — Tipo por valor (value type). Preferido ao `class` na maioria dos casos no Swift.

**SwiftData** — Framework Apple (iOS 17+) para persistência, substituto moderno do Core Data.

**SwiftUI** — Framework declarativo da Apple para construir interfaces em todas as plataformas Apple.

## T

**Task** — Unidade de trabalho assíncrono no modelo Swift Concurrency.

**TaskGroup** — Permite criar múltiplas Tasks paralelas e coletar seus resultados.

**TestFlight** — Plataforma Apple para distribuição de betas e testes antes da publicação.

**TDD** — Test-Driven Development. Escrever testes antes do código de produção.

## U

**UIKit** — Framework imperativo da Apple para interfaces iOS/tvOS, predecessor do SwiftUI.

**URLSession** — API nativa para networking HTTP no iOS/macOS.

## V

**Value Type** — Tipo copiado na atribuição. Structs e enums são value types.

**VIPER** — Arquitetura com 5 componentes: View, Interactor, Presenter, Entity, Router.

## W

**WidgetKit** — Framework para criar widgets que aparecem na tela inicial e de bloqueio do iOS.

**weak** — Reference fraca que não impede a liberação do objeto. Sempre Optional.
