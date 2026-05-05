# Generics

🟡 **Intermediário** · Módulo 02

---

Generics (tipos genéricos) permitem escrever código **flexível e reutilizável** que funciona com qualquer tipo, sem abrir mão da segurança estática de tipos. Eles estão em toda a standard library do Swift: `Array<Element>`, `Dictionary<Key, Value>`, `Optional<Wrapped>`.

!!! abstract "Por que Generics?"
    Sem generics, você seria forçado a duplicar código para cada tipo ou usar `Any` — perdendo type safety. Generics resolvem isso elegantemente: escreva uma vez, use com qualquer tipo.

---

## O problema que Generics resolvem

```swift
// SEM generics — duplicação de código
func trocaInts(_ a: inout Int, _ b: inout Int) {
    let temp = a; a = b; b = temp
}

func trocaStrings(_ a: inout String, _ b: inout String) {
    let temp = a; a = b; b = temp
}

func trocaDoubles(_ a: inout Double, _ b: inout Double) {
    let temp = a; a = b; b = temp
}

// COM generics — uma única função para todos os tipos
func trocar<T>(_ a: inout T, _ b: inout T) {  // (1)!
    let temp = a; a = b; b = temp
}

var x = 10, y = 20
trocar(&x, &y)
print(x, y)  // 20 10

var s1 = "Olá", s2 = "Mundo"
trocar(&s1, &s2)
print(s1, s2)  // "Mundo Olá"
```

1. `T` é um **type parameter** — um placeholder que Swift substitui pelo tipo real quando a função é chamada. Poderia ser qualquer nome, mas `T` é convenção. Use nomes descritivos (`Element`, `Key`, `Value`) quando o contexto permitir.

---

## Funções genéricas

```swift
// Encontrar o primeiro elemento que satisfaz uma condição
func primeiroQue<T>(_ array: [T], satisfaz condicao: (T) -> Bool) -> T? {
    for elemento in array {
        if condicao(elemento) { return elemento }
    }
    return nil
}

let numeros = [3, 7, 1, 9, 2, 8]
let primeiroPar = primeiroQue(numeros) { $0 % 2 == 0 }
print(primeiroPar!)  // 2

let palavras = ["banana", "uva", "abacate", "manga"]
let primeiraCom5Letras = primeiroQue(palavras) { $0.count == 5 }
print(primeiraCom5Letras!)  // "manga" — wait... "manga" = 5, "banana" = 6
// Resultado real: nil (nenhuma tem exatamente 5 letras no array acima)

// Função com múltiplos type parameters
func combinar<A, B>(_ a: A, com b: B) -> (A, B) {
    (a, b)
}

let par = combinar(42, com: "quarenta e dois")
print(par)  // (42, "quarenta e dois")
```

---

## Tipos genéricos

### Struct genérica

```swift
struct Par<Primeiro, Segundo> {
    var primeiro: Primeiro
    var segundo: Segundo

    func invertido() -> Par<Segundo, Primeiro> {
        Par<Segundo, Primeiro>(primeiro: segundo, segundo: primeiro)
    }
}

let coordenada = Par(primeiro: -23.55, segundo: -46.63)
print(coordenada.primeiro)  // -23.55

let nomeIdade = Par(primeiro: "Ana", segundo: 28)
let invertido = nomeIdade.invertido()
print(invertido.primeiro)  // 28 (Int)
print(invertido.segundo)   // "Ana" (String)
```

### Pilha genérica

```swift
struct Pilha<Elemento> {
    private var itens: [Elemento] = []

    var topo: Elemento? { itens.last }
    var estaVazia: Bool { itens.isEmpty }
    var contagem: Int { itens.count }

    mutating func empilhar(_ item: Elemento) {
        itens.append(item)
    }

    @discardableResult
    mutating func desempilhar() -> Elemento? {
        itens.popLast()
    }
}

// Uso com Int
var pilhaInt = Pilha<Int>()
pilhaInt.empilhar(1)
pilhaInt.empilhar(2)
pilhaInt.empilhar(3)
print(pilhaInt.topo!)         // 3
pilhaInt.desempilhar()
print(pilhaInt.topo!)         // 2

// Uso com String — mesmo código!
var pilhaStr = Pilha<String>()
pilhaStr.empilhar("a")
pilhaStr.empilhar("b")
print(pilhaStr.topo!)         // "b"
```

### Classe genérica

```swift
class Cache<Chave: Hashable, Valor> {
    private var armazenamento: [Chave: Valor] = [:]
    private let capacidadeMaxima: Int

    init(capacidade: Int) {
        self.capacidadeMaxima = capacidade
    }

    func guardar(_ valor: Valor, paraChave chave: Chave) {
        if armazenamento.count >= capacidadeMaxima {
            armazenamento.removeValue(forKey: armazenamento.keys.first!)
        }
        armazenamento[chave] = valor
    }

    func recuperar(chave: Chave) -> Valor? {
        armazenamento[chave]
    }

    func remover(chave: Chave) {
        armazenamento.removeValue(forKey: chave)
    }
}

let cache = Cache<String, Data>(capacidade: 100)
cache.guardar(Data(), paraChave: "imagem-1")
```

---

## Type Constraints (Restrições de tipo)

Às vezes precisamos garantir que o tipo genérico tem certas capacidades:

```swift
// T deve ser Comparable para poder comparar elementos
func maximo<T: Comparable>(_ a: T, _ b: T) -> T {
    a > b ? a : b
}

print(maximo(3, 7))          // 7
print(maximo("banana", "uva"))  // "uva" (ordem alfabética)
// print(maximo(UIView(), UIView()))  // ❌ UIView não é Comparable

// Múltiplas restrições
func buscarEOrdenar<T: Comparable & Hashable>(_ array: [T]) -> [T] {
    Array(Set(array)).sorted()
}

let nums = [3, 1, 4, 1, 5, 9, 2, 6, 5, 3]
print(buscarEOrdenar(nums))  // [1, 2, 3, 4, 5, 6, 9]

// Restrição de protocolo com where
func somarTodos<T>(_ array: [T]) -> T where T: Numeric {
    array.reduce(0, +)
}

print(somarTodos([1, 2, 3, 4, 5]))       // 15
print(somarTodos([1.5, 2.5, 3.0]))       // 7.0
```

---

## Associated Types em protocolos

Associated types tornam protocolos genéricos:

```swift
protocol Colecao {
    associatedtype Item

    var contagem: Int { get }
    func item(em indice: Int) -> Item
    mutating func adicionar(_ item: Item)
}

struct ArrayCustomizado<T>: Colecao {
    typealias Item = T
    private var elementos: [T] = []

    var contagem: Int { elementos.count }

    func item(em indice: Int) -> T {
        elementos[indice]
    }

    mutating func adicionar(_ item: T) {
        elementos.append(item)
    }
}

// Restrições em associated types
protocol Repositorio {
    associatedtype Entidade: Identifiable & Codable

    func buscar(id: Entidade.ID) -> Entidade?
    func salvar(_ entidade: Entidade)
    func listarTodos() -> [Entidade]
}
```

---

## Generic Subscripts

```swift
extension Dictionary {
    // Subscript genérico que converte o tipo na saída
    subscript<T>(chave: Key, como tipo: T.Type) -> T? {
        return self[chave] as? T
    }
}

let config: [String: Any] = [
    "nome": "MyApp",
    "versao": 2,
    "debug": true
]

let nome = config["nome", como: String.self]    // Optional("MyApp")
let versao = config["versao", como: Int.self]   // Optional(2)
let debug = config["debug", como: Bool.self]    // Optional(true)
```

---

## Where Clauses

`where` permite adicionar restrições mais específicas:

```swift
// where em extensão — adiciona método apenas quando Element é Equatable
extension Pilha where Elemento: Equatable {
    func contem(_ item: Elemento) -> Bool {
        // Acesso ao armazenamento interno — em produção use internal/fileprivate
        false // simplificado; acesse itens aqui na prática
    }
}

// where em funções
func saoIguais<T1: Sequence, T2: Sequence>(_ s1: T1, _ s2: T2) -> Bool
    where T1.Element == T2.Element, T1.Element: Equatable
{
    Array(s1) == Array(s2)
}

// where em protocolos com associated types
extension Array where Element: Numeric {
    var soma: Element { reduce(0, +) }
    var media: Double {
        guard !isEmpty else { return 0 }
        return Double(soma as! Int) / Double(count)  // simplificado
    }
}
```

---

## Generics da Standard Library

Os tipos mais usados em Swift são genéricos:

=== "Array<Element>"

    ```swift
    // Array<Element> — o tipo mais usado
    var inteiros: Array<Int> = [1, 2, 3]
    var textos: [String] = ["a", "b"]    // sintaxe abreviada

    // Métodos genéricos de Array
    let dobros = inteiros.map { $0 * 2 }       // [Int] → [Int]
    let textos2 = inteiros.map { "\($0)" }      // [Int] → [String]
    let pares = inteiros.filter { $0 % 2 == 0 }
    let soma = inteiros.reduce(0, +)
    ```

=== "Dictionary<Key, Value>"

    ```swift
    // Dictionary<Key: Hashable, Value>
    var contagem: Dictionary<String, Int> = [:]
    var contagem2: [String: Int] = [:]  // abreviado

    let texto = "banana"
    var freq: [Character: Int] = [:]
    for char in texto {
        freq[char, default: 0] += 1
    }
    // ["b": 1, "a": 3, "n": 2]
    ```

=== "Optional<Wrapped>"

    ```swift
    // Optional<Wrapped> é um enum genérico!
    var nome: Optional<String> = .some("Ana")
    var nome2: String? = "Ana"  // açúcar sintático

    // Equivalent internamente:
    enum Optional<Wrapped> {
        case none
        case some(Wrapped)
    }
    ```

=== "Result<Success, Failure>"

    ```swift
    // Result<Success, Failure: Error>
    enum ErroDeRede: Error {
        case semConexao
        case timeout
        case erroServidor(codigo: Int)
    }

    func buscarDados(url: String) -> Result<Data, ErroDeRede> {
        guard url.hasPrefix("https") else {
            return .failure(.semConexao)
        }
        let dados = Data()  // simulação
        return .success(dados)
    }

    switch buscarDados(url: "https://api.exemplo.com") {
    case .success(let dados):
        print("Recebidos \(dados.count) bytes")
    case .failure(let erro):
        print("Erro: \(erro)")
    }
    ```

---

## Tipos opacos com `some`

O tipo opaco `some` permite que uma função retorne "algum tipo que conforma ao protocolo X" sem expor o tipo concreto:

```swift
protocol Forma {
    func area() -> Double
}

struct Circulo: Forma {
    var raio: Double
    func area() -> Double { Double.pi * raio * raio }
}

struct Quadrado: Forma {
    var lado: Double
    func area() -> Double { lado * lado }
}

// 'some Forma' — o compilador sabe o tipo, mas não é exposto
func criarForma(tipo: String) -> some Forma {  // (1)!
    Circulo(raio: 5)  // deve retornar SEMPRE o mesmo tipo concreto
}

// Muito útil em SwiftUI:
// var body: some View { ... }
```

1. Com `some`, o compilador garante que sempre é retornado o mesmo tipo concreto. Para retornar tipos diferentes em branches, use `any Forma` (existencial) ou `@ViewBuilder` no SwiftUI.

### `some` vs `any`

```swift
// 'some' — tipo opaco, performance máxima, tipo concreto fixo
func criarFormaOtimizada() -> some Forma {
    Circulo(raio: 5)
}

// 'any' — tipo existencial, flexível, leve overhead
func criarFormaFleivel(tipo: String) -> any Forma {
    tipo == "circulo" ? Circulo(raio: 5) : Quadrado(lado: 4)
}

// Em coleções: 'any' é necessário quando os tipos diferem
let formas: [any Forma] = [Circulo(raio: 3), Quadrado(lado: 4), Circulo(raio: 1)]
let areaTotal = formas.map { $0.area() }.reduce(0, +)
```

---

## Exemplo completo: Resultado paginado

```swift
struct Pagina<T> {
    let itens: [T]
    let paginaAtual: Int
    let totalDePaginas: Int
    let itensPorPagina: Int

    var temProxima: Bool { paginaAtual < totalDePaginas - 1 }
    var temAnterior: Bool { paginaAtual > 0 }

    func mapeado<U>(_ transformar: (T) -> U) -> Pagina<U> {
        Pagina<U>(
            itens: itens.map(transformar),
            paginaAtual: paginaAtual,
            totalDePaginas: totalDePaginas,
            itensPorPagina: itensPorPagina
        )
    }
}

struct Produto: Identifiable, Codable {
    let id: UUID
    var nome: String
    var preco: Double
}

func buscarProdutos(pagina: Int) -> Pagina<Produto> {
    // Simulação de API
    let produtos = (1...10).map { i in
        Produto(id: UUID(), nome: "Produto \(pagina * 10 + i)", preco: Double(i) * 9.99)
    }
    return Pagina(itens: produtos, paginaAtual: pagina, totalDePaginas: 5, itensPorPagina: 10)
}

let pagina = buscarProdutos(pagina: 0)
print("Mostrando \(pagina.itens.count) de \(pagina.totalDePaginas * pagina.itensPorPagina) produtos")
print("Tem próxima página:", pagina.temProxima)  // true

// Mapear para exibição
let nomes: Pagina<String> = pagina.mapeado { $0.nome }
print(nomes.itens.prefix(3))  // ["Produto 1", "Produto 2", "Produto 3"]
```

---

## Resumo

| Conceito | Sintaxe | Para quê |
|----------|---------|----------|
| Type parameter | `<T>` | Placeholder de tipo |
| Type constraint | `<T: Protocolo>` | Exigir capacidade do tipo |
| Where clause | `where T == U.Element` | Restrições complexas |
| Associated type | `associatedtype Item` | Tipo genérico em protocolos |
| Opaque type | `some Protocolo` | Retornar tipo concreto oculto |
| Existential | `any Protocolo` | Tipo flexível em runtime |

---

## Checklist

- [ ] Entendo por que generics existem e o problema que resolvem
- [ ] Sei criar funções genéricas com type parameters
- [ ] Sei criar structs e classes genéricas
- [ ] Uso type constraints para restringir capacidades
- [ ] Entendo associated types em protocolos
- [ ] Uso `where` para restrições avançadas
- [ ] Entendo a diferença entre `some` e `any`

---

Próximo: [Mini-Projeto →](projeto.md)
