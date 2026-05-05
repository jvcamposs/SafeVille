# Projeto Final: NewsHub — App Completo

🔴 **Avançado** · Módulo 10 · Projeto Final

O projeto final combina **todos** os conceitos do curso em um app de notícias completo, com arquitetura profissional.

---

## Visão geral do app

**NewsHub** é um agregador de notícias que:

- Busca notícias de uma API real (NewsAPI.org — plano gratuito)
- Salva artigos favoritos localmente com SwiftData
- Suporta modo offline com cache
- Tem widget mostrando a última notícia
- É totalmente acessível (VoiceOver, Dynamic Type)
- Tem pipeline CI/CD configurado

---

## Arquitetura

```
NewsHub/
├── App/
│   └── NewsHubApp.swift
├── Features/
│   ├── Headlines/
│   │   ├── HeadlinesView.swift
│   │   ├── HeadlinesViewModel.swift
│   │   └── HeadlineRow.swift
│   ├── Detail/
│   │   ├── ArticleDetailView.swift
│   │   └── ArticleDetailViewModel.swift
│   └── Favorites/
│       ├── FavoritesView.swift
│       └── FavoritesViewModel.swift
├── Data/
│   ├── Models/
│   │   ├── Article.swift         ← Codable (API)
│   │   └── SavedArticle.swift    ← @Model (SwiftData)
│   ├── Network/
│   │   ├── NewsEndpoint.swift
│   │   └── NetworkService.swift
│   └── Repository/
│       └── ArticleRepository.swift
├── Shared/
│   └── Views/
│       ├── AsyncImageView.swift
│       └── ErrorView.swift
└── NewsHubWidget/
    └── NewsHubWidget.swift
```

---

## Modelos

```swift
// Article.swift — modelo da API (Codable)
struct Article: Codable, Identifiable {
    let source:      Source
    let author:      String?
    let title:       String
    let description: String?
    let url:         String
    let urlToImage:  String?
    let publishedAt: Date

    var id: String { url }

    struct Source: Codable {
        let name: String
    }
}

// SavedArticle.swift — persistência (SwiftData)
@Model
final class SavedArticle {
    @Attribute(.unique) var url: String
    var title:       String
    var description: String
    var sourceName:  String
    var urlToImage:  String?
    var publishedAt: Date
    var savedAt:     Date

    init(from article: Article) {
        self.url         = article.url
        self.title       = article.title
        self.description = article.description ?? ""
        self.sourceName  = article.source.name
        self.urlToImage  = article.urlToImage
        self.publishedAt = article.publishedAt
        self.savedAt     = Date()
    }
}
```

---

## Repository (unificando network + cache)

```swift
protocol ArticleRepositoryProtocol {
    func buscarHeadlines(categoria: String) async throws -> [Article]
    func salvarFavorito(_ article: Article, context: ModelContext)
    func removerFavorito(_ saved: SavedArticle, context: ModelContext)
}

final class ArticleRepository: ArticleRepositoryProtocol {

    private let networkService: NetworkServiceProtocol

    init(networkService: NetworkServiceProtocol = NetworkService()) {
        self.networkService = networkService
    }

    func buscarHeadlines(categoria: String) async throws -> [Article] {
        let endpoint = NewsEndpoint.headlines(categoria: categoria, country: "br")
        let resposta = try await networkService.request(endpoint, as: NewsResponse.self)
        return resposta.articles
    }

    func salvarFavorito(_ article: Article, context: ModelContext) {
        let saved = SavedArticle(from: article)
        context.insert(saved)
    }

    func removerFavorito(_ saved: SavedArticle, context: ModelContext) {
        context.delete(saved)
    }
}
```

---

## ViewModel principal

```swift
@Observable
@MainActor
final class HeadlinesViewModel {

    var artigos:      [Article]       = []
    var categorias    = ["geral", "tecnologia", "esportes", "saúde", "ciência"]
    var categoriaAtual = "geral"
    var isLoading     = false
    var errorMessage: String?
    var busca         = ""

    var artigosFiltrados: [Article] {
        busca.isEmpty ? artigos
                      : artigos.filter { $0.title.localizedCaseInsensitiveContains(busca) }
    }

    private let repository: ArticleRepositoryProtocol

    init(repository: ArticleRepositoryProtocol = ArticleRepository()) {
        self.repository = repository
    }

    func carregarArtigos() async {
        isLoading    = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            artigos = try await repository.buscarHeadlines(categoria: categoriaAtual)
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func mudarCategoria(_ cat: String) async {
        categoriaAtual = cat
        await carregarArtigos()
    }
}
```

---

## View principal

```swift
struct HeadlinesView: View {
    @Environment(\.modelContext) private var ctx
    @State private var vm = HeadlinesViewModel()
    @Query private var favoritos: [SavedArticle]

    var body: some View {
        NavigationStack {
            Group {
                if vm.isLoading {
                    ProgressView("Carregando notícias...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let erro = vm.errorMessage {
                    ErrorView(mensagem: erro) { Task { await vm.carregarArtigos() } }
                } else if vm.artigosFiltrados.isEmpty {
                    ContentUnavailableView.search(text: vm.busca)
                } else {
                    listaArtigos
                }
            }
            .navigationTitle("Notícias")
            .toolbar { categoriaPicker }
            .searchable(text: $vm.busca, prompt: "Buscar notícias")
            .task { await vm.carregarArtigos() }
        }
    }

    var listaArtigos: some View {
        List(vm.artigosFiltrados) { artigo in
            NavigationLink {
                ArticleDetailView(artigo: artigo, favoritos: favoritos,
                                  onFavoritar: { fav in
                    if fav { vm.repository.salvarFavorito(artigo, context: ctx) }
                })
            } label: {
                ArticleRow(artigo: artigo,
                           eFavorito: favoritos.contains { $0.url == artigo.url })
            }
        }
        .listStyle(.plain)
        .refreshable { await vm.carregarArtigos() }
    }

    var categoriaPicker: some ToolbarContent {
        ToolbarItem(placement: .bottomBar) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack {
                    ForEach(vm.categorias, id: \.self) { cat in
                        Button(cat.capitalized) {
                            Task { await vm.mudarCategoria(cat) }
                        }
                        .buttonStyle(.bordered)
                        .tint(cat == vm.categoriaAtual ? .accentColor : .secondary)
                    }
                }
                .padding(.horizontal)
            }
        }
    }
}
```

---

## Checklist do Módulo 10 e do Curso

- [x] Uso Actors para proteger estado compartilhado
- [x] Trabalho com AsyncStream para eventos contínuos
- [x] Entendo Combine e sei quando usá-lo vs async/await
- [x] Profile apps com Instruments (Time Profiler, Leaks)
- [x] Corrijo retain cycles com `[weak self]`
- [x] Implemento acessibilidade completa (VoiceOver, Dynamic Type)
- [x] Crio Widgets com WidgetKit
- [x] Construí um app completo integrando todos os módulos

---

## Parabéns! 🎉

Você concluiu o **Swift iOS — Do Zero ao Avançado**!

**O que você aprendeu:**

| Módulo | Competência |
|---|---|
| 01 — Fundamentos | Swift 5.9+: tipos, funções, optionals |
| 02 — OOP | Structs, classes, protocolos, generics |
| 03 — SwiftUI | Views, estado, navegação, listas |
| 04 — UIKit | ViewControllers, AutoLayout, TableViews |
| 05 — Arquitetura | MVC, MVVM, Clean Architecture |
| 06 — Persistência | UserDefaults, Core Data, SwiftData |
| 07 — Networking | URLSession, async/await, REST APIs |
| 08 — Testes | XCTest, UI Tests, TDD |
| 09 — CI/CD | Fastlane, GitHub Actions, TestFlight |
| 10 — Avançado | Concurrency, Combine, Performance, A11y, Widgets |

**Próximos passos:**

- Publique seu primeiro app na App Store
- Contribua com projetos open-source Swift
- Participe do [Swift Forums](https://forums.swift.org)
- Assista às sessões do [WWDC](https://developer.apple.com/wwdc/)
- Explore frameworks avançados: RealityKit, Vision, Core ML

**[Ver recursos adicionais →](../recursos/links.md)**
