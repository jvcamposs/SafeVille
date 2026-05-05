# Protocolos em Swift

🟡 **Intermediário** · Módulo 02

---

Protocolos são o coração da Swift moderna. Eles definem **contratos** — um conjunto de propriedades e métodos que um tipo deve implementar. Ao contrário da herança de classes, protocolos permitem compor comportamento de forma flexível e sem as restrições de hierarquias rígidas.

!!! abstract "Protocol-Oriented Programming"
    Swift foi desenhada do zero para ser uma linguagem orientada a protocolos. A própria standard library — `Array`, `String`, `Dictionary` — é construída quase inteiramente sobre protocolos. Entender protocolos é entender Swift.

---

## Definindo e adotando um protocolo

```swift
// Definição: o "contrato"
protocol Descritivel {
    var descricao: String { get }  // (1)!
    func resumo() -> String
}

// Adoção por uma struct
struct Livro: Descritivel {
    var titulo: String
    var autor: String

    var descricao: String {
        "\"\(titulo)\" por \(autor)"
    }

    func resumo() -> String {
        "Livro: \(descricao)"
    }
}

// Adoção por uma class
class Podcast: Descritivel {
    var titulo: String
    var apresentador: String

    init(titulo: String, apresentador: String) {
        self.titulo = titulo
        self.apresentador = apresentador
    }

    var descricao: String { "\(titulo) com \(apresentador)" }
    func resumo() -> String { "Podcast: \(descricao)" }
}

let itens: [any Descritivel] = [
    Livro(titulo: "Swift in Depth", autor: "Tjeerd in 't Veen"),
    Podcast(titulo: "Swift by Sundell", apresentador: "John Sundell")
]

for item in itens {
    print(item.resumo())
}
// "Livro: "Swift in Depth" por Tjeerd in 't Veen"
// "Podcast: Swift by Sundell com John Sundell"
```

1. `{ get }` significa que o protocolo exige que a propriedade seja legível. O tipo que adota pode implementá-la como `var` ou `let`. Se fosse `{ get set }`, deveria ser `var`.

---

## Requisitos de protocolo

### Propriedades

```swift
protocol Configuravel {
    var id: UUID { get }           // somente leitura
    var nome: String { get set }   // leitura e escrita
    static var versao: String { get }  // propriedade estática
}
```

### Métodos

```swift
protocol Calculavel {
    func calcular() -> Double
    mutating func resetar()        // 'mutating' necessário para structs
    static func criar() -> Self    // Self = tipo que adota o protocolo
}
```

### Inicializadores

```swift
protocol Inicializavel {
    init(nome: String)
}

class Produto: Inicializavel {
    var nome: String
    required init(nome: String) {  // (1)!
        self.nome = nome
    }
}
```

1. Em classes, inicializadores de protocolo devem ser marcados com `required` para garantir que subclasses também os implementem.

---

## Protocol Extensions (Implementações padrão)

Protocol extensions permitem fornecer **implementações padrão** — o tipo pode usar o padrão ou sobrescrever com sua própria versão.

```swift
protocol Saudavel {
    var nome: String { get }
    func cumprimentar() -> String
    func despedir() -> String
}

// Extensão com implementação padrão
extension Saudavel {
    func cumprimentar() -> String {
        "Olá, eu sou \(nome)!"
    }

    func despedir() -> String {
        "Até logo!"
    }
}

struct Pessoa: Saudavel {
    var nome: String
    // cumprimentar() e despedir() herdados da extensão
}

struct Robo: Saudavel {
    var nome: String

    // Sobrescreve apenas o que precisa
    func cumprimentar() -> String {
        "BEEP BOOP. IDENTIFICAÇÃO: \(nome.uppercased())"
    }
}

let p = Pessoa(nome: "Ana")
let r = Robo(nome: "R2D2")

print(p.cumprimentar())  // "Olá, eu sou Ana!"
print(p.despedir())      // "Até logo!"
print(r.cumprimentar())  // "BEEP BOOP. IDENTIFICAÇÃO: R2D2"
print(r.despedir())      // "Até logo!"
```

---

## Protocol Composition

Um tipo pode adotar múltiplos protocolos, e funções podem exigir múltiplos protocolos de uma só vez:

```swift
protocol Nomeavel {
    var nome: String { get }
}

protocol Avaliavel {
    var nota: Double { get }
    var aprovado: Bool { get }
}

// Composição com &
func exibirResultado(_ item: Nomeavel & Avaliavel) {
    let status = item.aprovado ? "✅ Aprovado" : "❌ Reprovado"
    print("\(item.nome): \(item.nota) — \(status)")
}

struct Aluno: Nomeavel, Avaliavel {
    var nome: String
    var nota: Double
    var aprovado: Bool { nota >= 6.0 }
}

let aluno = Aluno(nome: "Carlos", nota: 7.5)
exibirResultado(aluno)  // "Carlos: 7.5 — ✅ Aprovado"

// Typealias para composições frequentes
typealias Candidato = Nomeavel & Avaliavel
```

---

## Protocolos da Standard Library

### Equatable

Permite comparar instâncias com `==`:

```swift
struct Coordenada: Equatable {
    var latitude: Double
    var longitude: Double
    // Swift sintetiza == automaticamente para structs simples! (1)!
}

let sp = Coordenada(latitude: -23.55, longitude: -46.63)
let rj = Coordenada(latitude: -22.91, longitude: -43.17)
print(sp == rj)  // false
print(sp == sp)  // true

// Para classes ou lógica customizada:
struct Produto: Equatable {
    var codigo: String
    var nome: String

    static func == (lhs: Produto, rhs: Produto) -> Bool {
        lhs.codigo == rhs.codigo  // apenas o código define igualdade
    }
}
```

1. Structs cuja todas as propriedades são `Equatable` ganham conformidade automática — não é preciso implementar `==`.

### Hashable

Permite usar o tipo como chave de `Dictionary` ou elemento de `Set`:

```swift
struct Tag: Hashable {
    var nome: String
    // Swift sintetiza hash(into:) automaticamente
}

var tagSet: Set<Tag> = [Tag(nome: "swift"), Tag(nome: "ios")]
tagSet.insert(Tag(nome: "swiftui"))
print(tagSet.count)  // 3

var tagCount: [Tag: Int] = [Tag(nome: "swift"): 42]
```

### Comparable

Permite ordenar com `<`, `>`, `<=`, `>=`:

```swift
struct Versao: Comparable {
    var major: Int
    var minor: Int
    var patch: Int

    static func < (lhs: Versao, rhs: Versao) -> Bool {
        if lhs.major != rhs.major { return lhs.major < rhs.major }
        if lhs.minor != rhs.minor { return lhs.minor < rhs.minor }
        return lhs.patch < rhs.patch
    }
}

let versoes: [Versao] = [
    Versao(major: 2, minor: 0, patch: 0),
    Versao(major: 1, minor: 5, patch: 3),
    Versao(major: 1, minor: 5, patch: 10),
]

let ordenadas = versoes.sorted()
// [1.5.3, 1.5.10, 2.0.0]
```

### Codable (Encodable + Decodable)

Permite serializar/deserializar para JSON e outros formatos:

```swift
struct Usuario: Codable {
    var id: Int
    var nome: String
    var email: String
    var ativo: Bool

    // Renomear chave no JSON
    enum CodingKeys: String, CodingKey {
        case id
        case nome = "name"       // JSON usa "name", Swift usa "nome"
        case email
        case ativo = "is_active"
    }
}

// Encode (Swift → JSON)
let user = Usuario(id: 1, nome: "Ana Silva", email: "ana@exemplo.com", ativo: true)
let encoder = JSONEncoder()
encoder.outputFormatting = .prettyPrinted

if let jsonData = try? encoder.encode(user),
   let jsonString = String(data: jsonData, encoding: .utf8) {
    print(jsonString)
    // {
    //   "id": 1,
    //   "name": "Ana Silva",
    //   "email": "ana@exemplo.com",
    //   "is_active": true
    // }
}

// Decode (JSON → Swift)
let json = """
{"id": 2, "name": "João", "email": "joao@exemplo.com", "is_active": false}
""".data(using: .utf8)!

if let usuario = try? JSONDecoder().decode(Usuario.self, from: json) {
    print(usuario.nome)  // "João"
}
```

### Identifiable (SwiftUI)

Essencial para listas no SwiftUI — garante que cada item tenha um identificador único:

```swift
import Foundation

struct Tarefa: Identifiable {
    let id: UUID             // (1)!
    var titulo: String
    var concluida: Bool

    init(titulo: String, concluida: Bool = false) {
        self.id = UUID()
        self.titulo = titulo
        self.concluida = concluida
    }
}

// No SwiftUI:
// List(tarefas) { tarefa in ... }  ← funciona porque Tarefa: Identifiable
```

1. `id` pode ser qualquer tipo `Hashable`. `UUID` é a escolha mais comum para garantir unicidade global.

---

## Programação Orientada a Protocolos (POP)

!!! info "O paradigma Swift"
    Em vez de criar hierarquias profundas de classes, Swift incentiva compor comportamento usando protocolos e suas extensões. Isso resulta em código mais flexível e testável.

```swift
// Abordagem OOP clássica (evitar)
class Animal {
    func comer() { print("comendo...") }
}
class Nadador: Animal {
    func nadar() { print("nadando...") }
}
class PeixeVolador: Nadador {  // ❌ e se precisar voar também?
    func voar() { print("voando...") }
}

// Abordagem POP (preferir)
protocol Comedor { func comer() }
protocol Nadador { func nadar() }
protocol Voador  { func voar() }

extension Comedor {
    func comer() { print("\(Self.self) comendo...") }
}
extension Nadador {
    func nadar() { print("\(Self.self) nadando...") }
}
extension Voador {
    func voar() { print("\(Self.self) voando...") }
}

struct Peixe:       Comedor, Nadador { }
struct Pato:        Comedor, Nadador, Voador { }
struct PeixeVolador: Comedor, Nadador, Voador { }  // ✅ sem herança complicada

let pato = Pato()
pato.comer()  // "Pato comendo..."
pato.nadar()  // "Pato nadando..."
pato.voar()   // "Pato voando..."
```

---

## Associated Types

Associated types permitem criar protocolos **genéricos** — o tipo concreto é definido por quem adota o protocolo:

```swift
protocol Repositorio {
    associatedtype Entidade  // (1)!

    func buscarTodos() -> [Entidade]
    func buscarPorId(_ id: Int) -> Entidade?
    func salvar(_ entidade: Entidade)
    func deletar(id: Int)
}

struct Livro: Identifiable {
    let id: Int
    var titulo: String
}

class RepositorioDeLivros: Repositorio {
    typealias Entidade = Livro  // concretiza o associated type

    private var livros: [Livro] = []

    func buscarTodos() -> [Livro] { livros }

    func buscarPorId(_ id: Int) -> Livro? {
        livros.first { $0.id == id }
    }

    func salvar(_ livro: Livro) {
        if let idx = livros.firstIndex(where: { $0.id == livro.id }) {
            livros[idx] = livro
        } else {
            livros.append(livro)
        }
    }

    func deletar(id: Int) {
        livros.removeAll { $0.id == id }
    }
}
```

1. `associatedtype` é um placeholder. Cada tipo que adota o protocolo define o tipo concreto que `Entidade` representa.

---

## @objc protocols

Necessários quando o protocolo precisa interoperar com Objective-C (delegates do UIKit, por exemplo):

```swift
@objc protocol AcoesDoCelula {
    func celulaFoiTocada(em indice: Int)

    @objc optional func celulaFoiSelecionadaLongamente(em indice: Int)  // (1)!
}

class MeuViewController: UIViewController, AcoesDoCelula {
    func celulaFoiTocada(em indice: Int) {
        print("Célula \(indice) tocada")
    }
    // celulaFoiSelecionadaLongamente é opcional — não precisa implementar
}
```

1. Métodos `optional` só existem em `@objc protocols`. Em Swift puro, use protocol extensions com implementações padrão.

---

## Caso de uso real: sistema de notificações

```swift
protocol Notificavel {
    var destinatario: String { get }
    func enviar(mensagem: String) async throws
}

protocol Logavel {
    func registrar(evento: String)
}

struct EmailService: Notificavel, Logavel {
    var destinatario: String

    func enviar(mensagem: String) async throws {
        // simulação — em produção chamaria uma API real
        registrar(evento: "Email enviado para \(destinatario)")
        print("📧 Email para \(destinatario): \(mensagem)")
    }

    func registrar(evento: String) {
        print("[LOG \(Date())] \(evento)")
    }
}

struct PushService: Notificavel {
    var destinatario: String  // device token

    func enviar(mensagem: String) async throws {
        print("📱 Push para \(destinatario): \(mensagem)")
    }
}

// Função que aceita qualquer Notificavel
func notificarUsuario(_ servico: any Notificavel, mensagem: String) async {
    do {
        try await servico.enviar(mensagem: mensagem)
    } catch {
        print("Erro ao notificar: \(error)")
    }
}
```

---

## Resumo rápido

| Conceito | O que faz |
|----------|-----------|
| `protocol` | Define um contrato de propriedades e métodos |
| `extension protocol` | Fornece implementações padrão |
| `A & B` | Composição — exige ambos os protocolos |
| `associatedtype` | Placeholder de tipo dentro de protocolos |
| `Equatable` | Permite `==` e `!=` |
| `Hashable` | Permite uso em `Set` e como chave de `Dictionary` |
| `Comparable` | Permite `<`, `>`, `sorted()` |
| `Codable` | Serialização/deserialização automática |
| `Identifiable` | `id` único — essencial para listas SwiftUI |

---

## Checklist

- [ ] Sei definir e adotar protocolos
- [ ] Uso protocol extensions para implementações padrão
- [ ] Sei compor múltiplos protocolos com `&`
- [ ] Entendo e uso `Equatable`, `Hashable`, `Comparable`, `Codable`
- [ ] Sei usar `Identifiable` em structs para SwiftUI
- [ ] Entendo associated types
- [ ] Prefiro POP a hierarquias de herança

---

Próximo: [Generics →](generics.md)
