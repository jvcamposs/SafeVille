# Combine Framework

🔴 **Avançado** · Módulo 10

Combine é o framework de programação reativa da Apple. Embora async/await seja a escolha para novo código, Combine ainda é amplamente usado e necessário para trabalhar com apps existentes.

---

## Conceitos fundamentais

```mermaid
graph LR
    P[Publisher\nEmite valores] --> O[Operator\nTransforma] --> S[Subscriber\nConsome]
```

| Conceito | Papel |
|---|---|
| **Publisher** | Fonte de valores ao longo do tempo |
| **Operator** | Transforma, filtra, combina publishers |
| **Subscriber** | Consome os valores finais |
| **AnyCancellable** | Token que cancela a subscription |

---

## Publishers básicos

```swift
import Combine

// Just — emite um único valor e encerra
let just = Just(42)
    .sink { print($0) }   // Imprime: 42

// Future — execução assíncrona única
let future = Future<String, Error> { promise in
    DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
        promise(.success("Resultado!"))
    }
}

// Subjects — publishers "manuais"
let passthrough = PassthroughSubject<String, Never>()
let current     = CurrentValueSubject<Int, Never>(0)   // (1)

passthrough.send("Olá")     // Emite valor
current.send(42)            // Emite e guarda o valor atual
print(current.value)        // Acesso ao valor atual: 42

// (1) CurrentValueSubject guarda o último valor
```

---

## Operators essenciais

```swift
let numeros = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].publisher

// map — transformação
numeros
    .map { $0 * 2 }
    .sink { print($0) }   // 2, 4, 6, 8...

// filter
numeros
    .filter { $0 % 2 == 0 }
    .sink { print($0) }   // 2, 4, 6, 8, 10

// flatMap — publisher dentro de publisher
numeros
    .flatMap { n in
        Just(n * n)
    }
    .sink { print($0) }

// combineLatest — combina dois publishers
let $nome  = CurrentValueSubject<String, Never>("")
let $email = CurrentValueSubject<String, Never>("")

Publishers.CombineLatest($nome, $email)
    .map { nome, email in !nome.isEmpty && email.contains("@") }
    .sink { formValido in
        print("Formulário válido: \(formValido)")
    }

// debounce — aguarda silêncio antes de emitir (ideal para busca)
let searchSubject = PassthroughSubject<String, Never>()
searchSubject
    .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
    .removeDuplicates()
    .sink { texto in buscar(texto) }
```

---

## Gerenciamento de memória

```swift
class ViewModel: ObservableObject {
    @Published var resultado = ""

    private var cancellables = Set<AnyCancellable>()   // (1)

    init() {
        $resultado
            .sink { print("Resultado: \($0)") }
            .store(in: &cancellables)                  // (2)
    }

    deinit {
        cancellables.removeAll()  // Libera automaticamente
    }
}

// (1) Set que guarda todos os tokens de subscription
// (2) store(in:) adiciona o token ao Set
```

---

## Combine + URLSession

```swift
struct PostsService {
    let url = URL(string: "https://jsonplaceholder.typicode.com/posts")!

    func buscarPosts() -> AnyPublisher<[Post], Error> {   // (1)
        URLSession.shared
            .dataTaskPublisher(for: url)                  // (2)
            .map(\.data)
            .decode(type: [Post].self, decoder: JSONDecoder())
            .receive(on: DispatchQueue.main)              // (3)
            .eraseToAnyPublisher()
    }
}

// No ViewModel
class PostsViewModel: ObservableObject {
    @Published var posts: [Post] = []
    private var cancellables = Set<AnyCancellable>()
    private let service = PostsService()

    func carregar() {
        service.buscarPosts()
            .catch { _ in Just([]) }
            .assign(to: &$posts)                         // (4)
    }
}

// (1) AnyPublisher apaga o tipo concreto — boa API pública
// (2) dataTaskPublisher é o URLSession publisher
// (3) receive(on:) muda para a main thread
// (4) assign(to:) conecta diretamente a @Published
```

---

## Combine vs async/await

| Cenário | Recomendação |
|---|---|
| Chamada de rede simples | ✅ async/await |
| Stream de eventos contínuos | ✅ Combine ou AsyncStream |
| Combinar múltiplos publishers | ✅ Combine (combineLatest, merge) |
| Debounce em campo de busca | ✅ Combine |
| App legado UIKit | ✅ Combine (já existente) |
| Novo código SwiftUI | ✅ async/await + @Observable |

---

## Checklist

- [x] Entendo Publisher, Operator e Subscriber
- [x] Crio PassthroughSubject e CurrentValueSubject
- [x] Uso operators: map, filter, flatMap, combineLatest, debounce
- [x] Gerencio subscriptions com `Set<AnyCancellable>`
- [x] Sei quando usar Combine vs async/await
