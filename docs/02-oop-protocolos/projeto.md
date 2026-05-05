# Mini-Projeto: Biblioteca de Mídia

🟡 **Intermediário** · Módulo 02 · Projeto Prático

Vamos construir uma **biblioteca de mídia** que gerencia livros, filmes e músicas usando structs, classes, protocolos e generics — todos os conceitos do Módulo 02 em ação.

---

## O que vamos construir

Um sistema que:

- Representa diferentes tipos de mídia (livro, filme, música)
- Compartilha comportamentos via protocolos
- Usa generics para uma coleção flexível e tipada
- Permite busca, filtro e ordenação

---

## Passo 1 — Protocolos base

```swift
import Foundation

// MARK: - Protocolos

protocol Midia: Identifiable, CustomStringConvertible {
    var id: UUID          { get }
    var titulo: String    { get }
    var ano: Int          { get }
    var genero: String    { get }
    var avaliacao: Double { get set } // (1)
}

protocol Reproducivel {
    var duracao: TimeInterval { get }   // em segundos
    func reproduzir()
}

protocol Pesquisavel {
    var termosIndexados: [String] { get }
    func corresponde(a busca: String) -> Bool
}

// (1) avaliacao é var porque o usuário pode alterar
```

---

## Passo 2 — Extensão padrão para Pesquisavel

```swift
extension Pesquisavel {
    func corresponde(a busca: String) -> Bool {
        let buscaLower = busca.lowercased()
        return termosIndexados.contains { termo in
            termo.lowercased().contains(buscaLower)
        }
    }
}
```

---

## Passo 3 — Tipos de mídia

```swift
// MARK: - Livro

struct Livro: Midia, Pesquisavel {
    let id       = UUID()
    let titulo:  String
    let autor:   String
    let ano:     Int
    let genero:  String
    let paginas: Int
    var avaliacao: Double

    var description: String {
        "📚 \(titulo) — \(autor) (\(ano)) [\(paginas) pgs]"
    }

    var termosIndexados: [String] { [titulo, autor, genero] }
}

// MARK: - Filme

struct Filme: Midia, Reproducivel, Pesquisavel {
    let id        = UUID()
    let titulo:   String
    let diretor:  String
    let ano:      Int
    let genero:   String
    let duracao:  TimeInterval     // segundos
    var avaliacao: Double

    var description: String {
        let min = Int(duracao / 60)
        return "🎬 \(titulo) — \(diretor) (\(ano)) [\(min) min]"
    }

    var termosIndexados: [String] { [titulo, diretor, genero] }

    func reproduzir() {
        print("▶️  Reproduzindo filme: \(titulo)")
    }
}

// MARK: - Música

struct Musica: Midia, Reproducivel, Pesquisavel {
    let id       = UUID()
    let titulo:  String
    let artista: String
    let album:   String
    let ano:     Int
    let genero:  String
    let duracao: TimeInterval
    var avaliacao: Double

    var description: String {
        "🎵 \(titulo) — \(artista) (\(album))"
    }

    var termosIndexados: [String] { [titulo, artista, album, genero] }

    func reproduzir() {
        print("▶️  Reproduzindo: \(titulo) — \(artista)")
    }
}
```

---

## Passo 4 — Biblioteca genérica

```swift
// MARK: - Biblioteca Genérica

class Biblioteca<T: Midia & Pesquisavel> { // (1)
    private(set) var itens: [T] = []

    func adicionar(_ item: T) {
        itens.append(item)
        print("✅ Adicionado: \(item.titulo)")
    }

    func remover(id: UUID) {
        itens.removeAll { $0.id == id }
    }

    func buscar(_ termo: String) -> [T] {
        itens.filter { $0.corresponde(a: termo) }
    }

    func ordenarPorAvaliacao() -> [T] {
        itens.sorted { $0.avaliacao > $1.avaliacao }
    }

    func melhorAvaliado() -> T? {
        itens.max { $0.avaliacao < $1.avaliacao }
    }

    func estatisticas() {
        guard !itens.isEmpty else {
            print("Biblioteca vazia")
            return
        }
        let total   = itens.count
        let media   = itens.map(\.avaliacao).reduce(0, +) / Double(total)
        print("📊 Total: \(total) | Média: \(String(format: "%.1f", media))★")
    }
}

// (1) T deve ser Midia E Pesquisavel
```

---

## Passo 5 — Testando tudo

```swift
// MARK: - Uso

// Bibliotecas separadas por tipo
var livros  = Biblioteca<Livro>()
var filmes  = Biblioteca<Filme>()
var musicas = Biblioteca<Musica>()

// Adicionando itens
livros.adicionar(Livro(titulo: "O Guia do Mochileiro das Galáxias",
                       autor: "Douglas Adams", ano: 1979,
                       genero: "Ficção Científica", paginas: 224,
                       avaliacao: 4.8))

livros.adicionar(Livro(titulo: "Clean Code", autor: "Robert C. Martin",
                       ano: 2008, genero: "Tecnologia", paginas: 431,
                       avaliacao: 4.5))

filmes.adicionar(Filme(titulo: "Inception", diretor: "Christopher Nolan",
                       ano: 2010, genero: "Ficção", duracao: 8880,
                       avaliacao: 4.9))

filmes.adicionar(Filme(titulo: "Interstellar", diretor: "Christopher Nolan",
                       ano: 2014, genero: "Ficção", duracao: 10140,
                       avaliacao: 4.7))

musicas.adicionar(Musica(titulo: "Bohemian Rhapsody", artista: "Queen",
                         album: "A Night at the Opera", ano: 1975,
                         genero: "Rock", duracao: 354, avaliacao: 5.0))

// Buscas
print("\n🔍 Busca por 'Nolan':")
filmes.buscar("Nolan").forEach { print("  \($0)") }

print("\n⭐ Filmes por avaliação:")
filmes.ordenarPorAvaliacao().forEach { print("  \($0.titulo): \($0.avaliacao)★") }

print("\n🏆 Melhor filme:")
if let melhor = filmes.melhorAvaliado() {
    print("  \(melhor)")
}

livros.estatisticas()
filmes.estatisticas()
```

---

## Conceitos praticados

| Conceito | Onde aparece |
|---|---|
| Protocolos | `Midia`, `Reproducivel`, `Pesquisavel` |
| Protocol extensions | Implementação padrão de `corresponde(a:)` |
| Protocol composition | `T: Midia & Pesquisavel` |
| Structs | `Livro`, `Filme`, `Musica` |
| Generics | `Biblioteca<T>` |
| Type constraints | `where T: Midia & Pesquisavel` |
| Computed properties | `description`, `termosIndexados` |
| `Identifiable` | `id: UUID` |
| `CustomStringConvertible` | `description` |

---

## Checklist do Módulo 02

- [x] Sei a diferença entre struct e class (value vs reference)
- [x] Sei quando usar struct (preferir por padrão)
- [x] Crio e adoto protocolos
- [x] Uso protocol extensions para implementações padrão
- [x] Sei usar generics com type constraints
- [x] Conheço `Equatable`, `Hashable`, `Codable`, `Identifiable`
- [x] Construí a Biblioteca de Mídia com sucesso

**Próximo módulo:** [SwiftUI →](../03-swiftui/index.md)
