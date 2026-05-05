# MVVM — Model-View-ViewModel

🔴 **Avançado** · Módulo 05

MVVM é o padrão mais popular para apps SwiftUI modernos. Separa a lógica de apresentação em um **ViewModel** testável, mantendo a View simples e declarativa.

---

## Os quatro papéis

```mermaid
graph LR
    M[Model\nDados puros] --> VM[ViewModel\nLógica de apresentação]
    VM -->|@Published / @Observable| V[View\nSó exibe]
    V -->|chamadas de função| VM
    VM --> S[Service / Repository\nAcesso a dados]
```

---

## MVVM com SwiftUI + @Observable

```swift
// MARK: - Model
struct Produto: Codable, Identifiable {
    let id: Int
    let nome: String
    let preco: Double
}

// MARK: - Service (Repository)
protocol ProdutoRepositoryProtocol {
    func buscar() async throws -> [Produto]
}

final class ProdutoRepository: ProdutoRepositoryProtocol {
    func buscar() async throws -> [Produto] {
        let url = URL(string: "https://fakestoreapi.com/products")!
        let (data, _) = try await URLSession.shared.data(from: url)
        return try JSONDecoder().decode([Produto].self, from: data)
    }
}

// MARK: - ViewModel
@Observable
final class ProdutosViewModel {
    var produtos:    [Produto] = []
    var isLoading:   Bool      = false
    var errorMessage: String?  = nil
    var termoBusca:  String    = ""

    var produtosFiltrados: [Produto] {
        termoBusca.isEmpty
            ? produtos
            : produtos.filter { $0.nome.localizedCaseInsensitiveContains(termoBusca) }
    }

    private let repository: ProdutoRepositoryProtocol  // (1)

    init(repository: ProdutoRepositoryProtocol = ProdutoRepository()) {
        self.repository = repository
    }

    @MainActor
    func carregarProdutos() async {
        isLoading    = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            produtos = try await repository.buscar()
        } catch {
            errorMessage = "Erro ao carregar: \(error.localizedDescription)"
        }
    }
}

// (1) Injeção de dependência via protocolo — testável!
```

---

## View limpa

```swift
// MARK: - View
struct ProdutosView: View {
    @State private var vm = ProdutosViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if vm.isLoading {
                    ProgressView("Carregando...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let erro = vm.errorMessage {
                    ContentUnavailableView(erro,
                        systemImage: "wifi.slash")
                } else {
                    listaView
                }
            }
            .navigationTitle("Produtos")
            .searchable(text: $vm.termoBusca)
            .task { await vm.carregarProdutos() }
        }
    }

    private var listaView: some View {
        List(vm.produtosFiltrados) { produto in
            HStack {
                Text(produto.nome)
                Spacer()
                Text(produto.preco, format: .currency(code: "BRL"))
                    .foregroundStyle(.secondary)
            }
        }
    }
}
```

---

## MVVM com UIKit

Para UIKit, o binding é feito com closures ou Combine:

```swift
// ViewModel (sem SwiftUI)
final class ProdutosViewModelUIKit {
    // Closures para binding
    var onProdutosCarregados: (([Produto]) -> Void)?
    var onErro: ((String) -> Void)?
    var onLoading: ((Bool) -> Void)?

    private let repository: ProdutoRepositoryProtocol
    private(set) var produtos: [Produto] = []

    init(repository: ProdutoRepositoryProtocol = ProdutoRepository()) {
        self.repository = repository
    }

    func carregarProdutos() {
        onLoading?(true)
        Task {
            do {
                let produtos = try await repository.buscar()
                await MainActor.run {
                    self.produtos = produtos
                    self.onLoading?(false)
                    self.onProdutosCarregados?(produtos)
                }
            } catch {
                await MainActor.run {
                    self.onLoading?(false)
                    self.onErro?(error.localizedDescription)
                }
            }
        }
    }
}

// ViewController
final class ProdutosUIKitVC: UIViewController {
    private let vm = ProdutosViewModelUIKit()
    private var tableView = UITableView()

    override func viewDidLoad() {
        super.viewDidLoad()
        configurarBindings()
        vm.carregarProdutos()
    }

    private func configurarBindings() {
        vm.onProdutosCarregados = { [weak self] _ in   // (1)
            self?.tableView.reloadData()
        }
        vm.onErro = { [weak self] mensagem in
            self?.mostrarAlerta(mensagem)
        }
    }
}

// (1) [weak self] evita retain cycle
```

---

## MVVM + Repository Pattern

```
View
 └── ViewModel
      └── Repository (Protocol)
           ├── RemoteRepository (URLSession)
           └── MockRepository (para testes)
```

```swift
// Mock para testes
final class MockProdutoRepository: ProdutoRepositoryProtocol {
    var produtosMock: [Produto] = []
    var deveRetornarErro = false

    func buscar() async throws -> [Produto] {
        if deveRetornarErro { throw URLError(.notConnectedToInternet) }
        return produtosMock
    }
}

// Teste
@Test func testCarregarProdutos() async {
    let mock = MockProdutoRepository()
    mock.produtosMock = [Produto(id: 1, nome: "Teste", preco: 9.99)]

    let vm = ProdutosViewModel(repository: mock)
    await vm.carregarProdutos()

    #expect(vm.produtos.count == 1)
    #expect(vm.produtos.first?.nome == "Teste")
}
```

---

## Checklist

- [x] Entendo os papéis de Model, View e ViewModel
- [x] Crio ViewModels com `@Observable`
- [x] Uso injeção de dependência via protocolos
- [x] A View não contém lógica de negócio
- [x] Meu ViewModel é testável de forma independente
