# Funções e Closures

🟢 **Básico** · Módulo 01

Funções são os blocos de construção fundamentais de qualquer programa Swift. Elas permitem nomear e reutilizar trechos de código, tornando programas mais organizados, legíveis e fáceis de manter. Closures, por sua vez, são funções anônimas que podem ser tratadas como valores — uma das características mais poderosas do Swift.

---

## Sintaxe básica de funções

```swift
func nomeDaFuncao(parametro: Tipo) -> TipoDeRetorno {
    // corpo da função
    return valor
}
```

Vamos ver na prática:

```swift
// Função sem parâmetros e sem retorno
func dizerOla() {                           // (1)
    print("Olá, mundo!")
}

dizerOla()   // Chamada da função

// Função com parâmetro e sem retorno
func saudar(nome: String) {                 // (2)
    print("Olá, \(nome)!")
}

saudar(nome: "Ana")   // Chamada com rótulo do parâmetro

// Função com parâmetro e com retorno
func quadrado(de numero: Int) -> Int {      // (3)
    return numero * numero
}

let resultado = quadrado(de: 5)             // resultado = 25
print(resultado)
```

1. Função sem parâmetros — parênteses vazios são obrigatórios
2. Parâmetro `nome` do tipo `String` — ao chamar, o rótulo é obrigatório por padrão
3. `-> Int` indica que a função retorna um valor do tipo `Int`

---

## Parâmetros e tipos de retorno

### Múltiplos parâmetros

```swift
func calcularArea(largura: Double, altura: Double) -> Double { // (1)
    return largura * altura
}

let area = calcularArea(largura: 5.0, altura: 3.0)             // (2)
print("Área: \(area) m²")   // Área: 15.0 m²
```

1. Múltiplos parâmetros separados por vírgula
2. Cada argumento é passado com seu rótulo

### Retorno de múltiplos valores com Tuplas

```swift
func minMax(array: [Int]) -> (min: Int, max: Int) {   // (1)
    var minAtual = array[0]
    var maxAtual = array[0]

    for valor in array {
        if valor < minAtual { minAtual = valor }
        if valor > maxAtual { maxAtual = valor }
    }

    return (minAtual, maxAtual)                         // (2)
}

let numeros = [3, 7, 1, 9, 4, 6]
let resultado = minMax(array: numeros)
print("Mínimo: \(resultado.min)")   // (3) Mínimo: 1
print("Máximo: \(resultado.max)")   // (3) Máximo: 9
```

1. A função retorna uma tupla nomeada com dois `Int`
2. Retorna os dois valores como uma tupla
3. Acessa os valores pelo nome definido na assinatura da tupla

!!! note "O que é uma Tupla?"
    Uma tupla agrupa múltiplos valores em um único composto. É diferente de um array — cada posição pode ter um tipo diferente e pode ser nomeada:
    ```swift
    let pessoa: (nome: String, idade: Int) = ("João", 30)
    print(pessoa.nome)   // João
    print(pessoa.idade)  // 30
    ```

---

## Rótulos de parâmetros: interno vs externo

Uma das características mais elegantes do Swift é a separação entre o **rótulo externo** (usado na chamada) e o **nome interno** (usado no corpo da função).

```swift
//      rótulo externo ↓  nome interno ↓
func mover(de origem: String, para destino: String) {  // (1)
    print("Movendo de \(origem) para \(destino)")
    //                    ↑ nome interno
}

mover(de: "São Paulo", para: "Rio de Janeiro")          // (2)
//    ↑ rótulo externo
```

1. `de` é o rótulo externo (aparece na chamada); `origem` é o nome interno (usado no corpo)
2. A chamada lê quase como uma frase em inglês: "mover de... para..."

### Omitindo o rótulo externo com `_`

```swift
func multiplicar(_ a: Int, _ b: Int) -> Int {   // (1)
    return a * b
}

let resultado = multiplicar(4, 5)               // (2) sem rótulos
print(resultado)  // 20
```

1. `_` como rótulo externo significa "sem rótulo" na chamada
2. Os argumentos são passados diretamente, sem rótulos — mais próximo de outras linguagens

!!! tip "Quando omitir rótulos?"
    Omita rótulos quando o contexto for óbvio pelo nome da função:
    ```swift
    print("olá")          // sem rótulo — claro o suficiente
    sqrt(16.0)            // sem rótulo — convenção matemática
    max(3, 7)             // sem rótulo — convenção matemática
    ```
    Mantenha rótulos quando eles adicionam clareza:
    ```swift
    mover(de: "A", para: "B")   // rótulos deixam o código expressivo
    ```

---

## Parâmetros com valores padrão

```swift
func criarCafe(tipo: String = "Expresso",       // (1)
               tamanho: String = "Médio",
               comLeite: Bool = false) -> String {
    var descricao = "\(tamanho) \(tipo)"
    if comLeite { descricao += " com leite" }
    return descricao
}

// Chamadas válidas — todos os parâmetros têm padrão
print(criarCafe())                              // Médio Expresso
print(criarCafe(tipo: "Cappuccino"))            // (2) Médio Cappuccino
print(criarCafe(tamanho: "Grande", comLeite: true)) // (3) Grande Expresso com leite
print(criarCafe(tipo: "Latte", tamanho: "Pequeno", comLeite: true))
```

1. `= "Expresso"` define o valor padrão — usado quando o argumento não é fornecido
2. Você pode fornecer apenas os parâmetros que diferem do padrão
3. Parâmetros com padrão podem ser fornecidos em qualquer combinação

---

## Parâmetros variádicos

Permitem passar um número variável de argumentos do mesmo tipo.

```swift
func somar(_ numeros: Int...) -> Int {   // (1)
    var total = 0
    for numero in numeros {              // (2)
        total += numero
    }
    return total
}

print(somar(1, 2, 3))           // 6
print(somar(10, 20, 30, 40))    // 100
print(somar(5))                 // 5
```

1. `Int...` indica parâmetro variádico — dentro da função, `numeros` é tratado como `[Int]`
2. Itera normalmente como se fosse um array

---

## Parâmetros `inout` — Modificando valores externos

Por padrão, parâmetros de função são **constantes** dentro da função. Para modificar o valor original da variável passada, use `inout`.

```swift
func dobrar(_ numero: inout Int) {       // (1)
    numero = numero * 2
}

var meuNumero = 10
dobrar(&meuNumero)                       // (2)
print(meuNumero)   // 20
```

1. `inout` indica que a função pode modificar o valor original
2. `&` antes do argumento indica que está passando uma referência — é obrigatório

!!! warning "Quando usar `inout` com cuidado"
    `inout` aumenta o acoplamento entre função e código chamador. Prefira retornar o novo valor quando possível:
    ```swift
    // ❌ inout desnecessário
    func dobrar(_ n: inout Int) { n *= 2 }

    // ✅ Mais limpo — retorna o novo valor
    func dobrado(_ n: Int) -> Int { return n * 2 }
    var x = 10
    x = dobrado(x)   // 20
    ```

---

## Tipos de função

Em Swift, funções são **cidadãs de primeira classe** — podem ser atribuídas a variáveis, passadas como parâmetros e retornadas de outras funções.

```swift
func somar(a: Int, b: Int) -> Int { return a + b }
func subtrair(a: Int, b: Int) -> Int { return a - b }

// O tipo desta função é (Int, Int) -> Int
var operacao: (Int, Int) -> Int = somar      // (1)
print(operacao(5, 3))   // 8

operacao = subtrair                          // (2)
print(operacao(5, 3))   // 2
```

1. `operacao` é uma variável do tipo função `(Int, Int) -> Int`
2. Pode ser reatribuída para qualquer função com a mesma assinatura

---

## Funções de ordem superior (Higher-order functions)

Funções que recebem ou retornam outras funções.

```swift
// Função que recebe outra função como parâmetro
func aplicarOperacao(_ a: Int, _ b: Int, operacao: (Int, Int) -> Int) -> Int { // (1)
    return operacao(a, b)
}

func multiplicar(_ x: Int, _ y: Int) -> Int { return x * y }

let resultado = aplicarOperacao(4, 5, operacao: multiplicar)  // 20    // (2)

// Passando uma closure diretamente
let resultado2 = aplicarOperacao(4, 5, operacao: { x, y in x + y })   // 9 // (3)
```

1. O parâmetro `operacao` é do tipo função
2. Passa a função `multiplicar` como argumento
3. Passa uma closure anônima diretamente

---

## Closures — Funções anônimas

Uma closure é um bloco de código que pode ser armazenado e passado como valor.

### Sintaxe completa

```swift
let saudacao = { (nome: String) -> String in   // (1)
    return "Olá, \(nome)!"
}

print(saudacao("Pedro"))   // Olá, Pedro!
```

1. `{ (parametros) -> TipoDeRetorno in corpo }` — sintaxe completa de uma closure

### Sintaxe simplificada — Swift infere o tipo

```swift
// Versão completa
let quadrado1 = { (n: Int) -> Int in return n * n }

// Swift infere os tipos do contexto
let quadrado2 = { (n: Int) -> Int in n * n }      // (1)

// Swift infere tudo, incluindo tipos de parâmetro
let quadrado3: (Int) -> Int = { n in n * n }       // (2)

// Usando nomes de argumento abreviados ($0, $1, ...)
let quadrado4: (Int) -> Int = { $0 * $0 }          // (3)
```

1. `return` pode ser omitido em expressões de uma única linha
2. Quando o tipo da variável é declarado, os tipos dos parâmetros podem ser omitidos
3. `$0`, `$1`, etc. referenciam o primeiro, segundo parâmetro sem nomeá-los

### Trailing closure syntax

Quando o último parâmetro é uma closure, ela pode ser escrita fora dos parênteses:

```swift
func repetir(_ vezes: Int, acao: () -> Void) {     // (1)
    for _ in 1...vezes {
        acao()
    }
}

// Sem trailing closure syntax
repetir(3, acao: { print("Repetindo!") })

// Com trailing closure syntax
repetir(3) {                                        // (2)
    print("Repetindo!")
}
```

1. `acao: () -> Void` — parâmetro do tipo closure que não retorna nada
2. A closure é colocada após os parênteses da chamada — mais legível

---

## Capturando valores

Closures **capturam** as variáveis do escopo em que foram criadas.

```swift
func criarContador() -> () -> Int {             // (1)
    var contagem = 0                            // (2)

    let incrementar = {
        contagem += 1                           // (3)
        return contagem
    }

    return incrementar
}

let contador = criarContador()
print(contador())   // 1
print(contador())   // 2
print(contador())   // 3

let outroCont = criarContador()                 // (4)
print(outroCont())  // 1 (contagem independente)
```

1. A função retorna uma closure do tipo `() -> Int`
2. `contagem` é uma variável local que será **capturada** pela closure
3. A closure captura e modifica `contagem` mesmo após `criarContador()` ter retornado
4. Cada chamada de `criarContador()` cria uma nova captura independente

---

## `@escaping` closures

Uma closure é **escaping** quando ela pode ser chamada **após** a função que a recebeu retornar.

```swift
var callbacksArmazenados: [() -> Void] = []

func armazenarCallback(_ callback: @escaping () -> Void) {  // (1)
    callbacksArmazenados.append(callback)                    // (2)
}

armazenarCallback {
    print("Callback 1 executado!")
}

armazenarCallback {
    print("Callback 2 executado!")
}

// Executando mais tarde
for callback in callbacksArmazenados {
    callback()
}
// Callback 1 executado!
// Callback 2 executado!
```

1. `@escaping` é obrigatório quando a closure pode ser chamada depois da função retornar
2. Aqui, a closure "escapa" para o array `callbacksArmazenados` que persiste além da função

!!! info "Onde `@escaping` é comum?"
    - **Callbacks assíncronos**: quando você faz uma requisição de rede e fornece uma closure para ser chamada quando a resposta chegar
    - **Completions handlers**: padrão antigo de APIs da Apple
    - **SwiftUI**: alguns modificadores usam `@escaping` internamente

---

## Métodos funcionais em coleções

Swift tem métodos embutidos que usam closures para transformar coleções de forma elegante.

### `map` — Transforma cada elemento

```swift
let numeros = [1, 2, 3, 4, 5]

// Eleva cada número ao quadrado
let quadrados = numeros.map { $0 * $0 }              // (1)
print(quadrados)   // [1, 4, 9, 16, 25]

// Converte para String
let strings = numeros.map { "Número \($0)" }
print(strings)     // ["Número 1", "Número 2", ...]

// Com nomes
let nomes = ["ana", "bruno", "carla"]
let nomesMaiusculos = nomes.map { $0.capitalized }   // (2)
print(nomesMaiusculos)   // ["Ana", "Bruno", "Carla"]
```

1. `map` aplica a closure a cada elemento e retorna um novo array com os resultados
2. `.capitalized` é uma propriedade de `String` que coloca a primeira letra em maiúscula

### `filter` — Filtra elementos

```swift
let idades = [15, 22, 17, 30, 12, 25]

// Apenas maiores de 18
let adultos = idades.filter { $0 >= 18 }             // (1)
print(adultos)   // [22, 30, 25]

let palavras = ["swift", "java", "python", "swiftui", "kotlin"]
let palavrasSwift = palavras.filter { $0.contains("swift") }  // (2)
print(palavrasSwift)   // ["swift", "swiftui"]
```

1. `filter` retorna um novo array com apenas os elementos para os quais a closure retornou `true`
2. Filtra strings que contêm "swift"

### `reduce` — Acumula em um único valor

```swift
let notas = [7.5, 8.0, 9.5, 6.0, 8.5]

// Soma de todas as notas
let soma = notas.reduce(0, { acumulador, nota in acumulador + nota }) // (1)

// Versão compacta
let soma2 = notas.reduce(0, +)                        // (2)

// Calculando a média
let media = notas.reduce(0, +) / Double(notas.count)  // (3)
print("Média: \(media)")   // Média: 7.9
```

1. `reduce(valorInicial, combinador)` — aplica a closure acumulando resultado
2. `+` é um operador que funciona como closure `(Double, Double) -> Double`
3. Combina `reduce` para somar e depois divide pelo total

### `compactMap` — map que remove nils

```swift
let stringsNumericas = ["1", "dois", "3", "quatro", "5"]

// Tenta converter para Int — retorna nil para falhas
let apenasNumeros = stringsNumericas.compactMap { Int($0) }  // (1)
print(apenasNumeros)   // [1, 3, 5]

// Versus map que manteria os nils
let comNils = stringsNumericas.map { Int($0) }               // (2)
print(comNils)   // [Optional(1), nil, Optional(3), nil, Optional(5)]
```

1. `compactMap` aplica a transformação e **descarta** os valores `nil` resultantes
2. `map` normal manteria os `Optional(Int)` e `nil` — array de optionals

### Encadeando métodos

```swift
let estudantes = [
    ("Alice", 8.5),
    ("Bob", 5.0),
    ("Carol", 9.0),
    ("Dave", 6.5),
    ("Eve", 7.5)
]

// Pegar nomes dos estudantes aprovados (nota >= 7.0), em ordem alfabética
let aprovados = estudantes
    .filter { $0.1 >= 7.0 }                     // (1) filtra por nota
    .map { $0.0 }                               // (2) extrai apenas o nome
    .sorted()                                   // (3) ordena alfabeticamente

print(aprovados)   // ["Alice", "Carol", "Eve"]
```

1. Filtra apenas tuplas cuja nota (segundo elemento) é maior ou igual a 7.0
2. Extrai o nome (primeiro elemento) de cada tupla
3. Ordena o array de strings alfabeticamente

!!! tip "Métodos funcionais vs loops imperativoss"
    ```swift
    let numeros = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

    // ❌ Imperativo — verbose
    var pares: [Int] = []
    for n in numeros {
        if n % 2 == 0 {
            pares.append(n)
        }
    }

    // ✅ Funcional — conciso e expressivo
    let pares = numeros.filter { $0 % 2 == 0 }
    ```

---

## Exemplo integrado

```swift
// Sistema simples de processamento de pedidos
struct Pedido {
    let id: Int
    let produto: String
    let valor: Double
    let entregue: Bool
}

let pedidos = [
    Pedido(id: 1, produto: "Camiseta", valor: 49.90, entregue: true),
    Pedido(id: 2, produto: "Calça", valor: 89.90, entregue: false),
    Pedido(id: 3, produto: "Tênis", valor: 199.90, entregue: false),
    Pedido(id: 4, produto: "Boné", valor: 29.90, entregue: true),
]

// Total dos pedidos ainda não entregues
let totalPendente = pedidos
    .filter { !$0.entregue }              // (1)
    .map { $0.valor }                     // (2)
    .reduce(0, +)                         // (3)

print("Total pendente: R$ \(String(format: "%.2f", totalPendente))")
// Total pendente: R$ 289.80

// Descrição dos pedidos pendentes
let descricoesPendentes = pedidos
    .filter { !$0.entregue }
    .map { "Pedido #\($0.id): \($0.produto) — R$ \(String(format: "%.2f", $0.valor))" }

descricoesPendentes.forEach { print($0) }
// Pedido #2: Calça — R$ 89.90
// Pedido #3: Tênis — R$ 199.90
```

1. Filtra apenas pedidos não entregues
2. Extrai apenas o valor de cada pedido
3. Soma todos os valores

---

## Checklist da seção

- [ ] Escrever funções com e sem parâmetros
- [ ] Usar rótulos externos e nomes internos de parâmetros
- [ ] Omitir rótulos externos com `_` quando apropriado
- [ ] Definir valores padrão para parâmetros
- [ ] Usar parâmetros variádicos com `...`
- [ ] Usar `inout` para modificar variáveis externas (e saber quando evitar)
- [ ] Atribuir funções a variáveis como tipos de primeira classe
- [ ] Escrever closures com sintaxe completa e simplificada
- [ ] Usar trailing closure syntax
- [ ] Entender captura de valores em closures
- [ ] Saber quando usar `@escaping`
- [ ] Aplicar `map`, `filter`, `reduce` e `compactMap` em arrays

---

[:octicons-arrow-right-24: Próximo: Optionals e Tratamento de nil](optionals.md){ .md-button .md-button--primary }
[:octicons-arrow-left-24: Voltar: Sintaxe Básica](sintaxe.md){ .md-button }
