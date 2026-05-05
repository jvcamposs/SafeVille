# Async/Await em Swift

🟡 **Intermediário** · Módulo 07

Swift Concurrency, lançada no Swift 5.5, transformou a forma de escrever código assíncrono. Chega de callback hell — o código assíncrono agora parece síncrono.

---

## O problema das callbacks

```swift
// ❌ Callback hell — difícil de ler e manter
func buscarUsuarioComCallbacks(id: Int,
                               completion: @escaping (Result<Usuario, Error>) -> Void) {
    URLSession.shared.dataTask(with: urlUsuario(id: id)) { data, _, error in
        if let error = error {
            completion(.failure(error))
            return
        }
        guard let data = data,
              let usuario = try? JSONDecoder().decode(Usuario.self, from: data) else {
            completion(.failure(URLError(.cannotParseResponse)))
            return
        }
        // Agora buscar os posts desse usuário...
        URLSession.shared.dataTask(with: urlPosts(userId: usuario.id)) { data, _, error in
            // Mais um nível de indentação... 😱
            if let error = error {
                completion(.failure(error))
                return
            }
            // ...
        }.resume()
    }.resume()
}
```

---

## A solução: async/await

```swift
// ✅ async/await — linear, legível
func buscarUsuario(id: Int) async throws -> Usuario {
    let (data, _) = try await URLSession.shared.data(from: urlUsuario(id: id))
    return try JSONDecoder().decode(Usuario.self, from: data)
}

func buscarPosts(userId: Int) async throws -> [Post] {
    let (data, _) = try await URLSession.shared.data(from: urlPosts(userId: userId))
    return try JSONDecoder().decode([Post].self, from: data)
}

// Uso sequencial — lê de cima para baixo
func carregarTudo(userId: Int) async throws -> (Usuario, [Post]) {
    let usuario = try await buscarUsuario(id: userId)  // (1)
    let posts   = try await buscarPosts(userId: usuario.id)
    return (usuario, posts)
}

// (1) await suspende a execução aqui até terminar
//     a thread NÃO fica bloqueada — outras tarefas podem rodar
```

---

## Task — iniciando trabalho assíncrono

```swift
// Em SwiftUI — .task é a forma recomendada
struct PostsView: View {
    @State private var posts: [Post] = []

    var body: some View {
        List(posts) { post in Text(post.title) }
            .task {                              // (1)
                do {
                    posts = try await buscarPosts()
                } catch {
                    print("Erro: \(error)")
                }
            }
    }
}

// Criando uma Task manualmente
func iniciarDownload() {
    Task {                                       // (2)
        let imagem = try await downloadImagem()
        await MainActor.run {                   // (3)
            self.imagem = imagem
        }
    }
}

// Task detached — não herda o contexto atual
Task.detached(priority: .background) {
    await processarDados()
}

// (1) .task cancela automaticamente quando a View desaparece
// (2) Task herda prioridade e contexto do chamador
// (3) Atualizar UI sempre na MainActor
```

---

## async let — paralelismo

```swift
// ❌ Sequencial: 3 segundos (1+1+1)
func carregarSequencial() async throws {
    let clima     = try await buscarClima()
    let noticias  = try await buscarNoticias()
    let cotacoes  = try await buscarCotacoes()
    return (clima, noticias, cotacoes)
}

// ✅ Paralelo: ~1 segundo
func carregarParalelo() async throws {
    async let clima    = buscarClima()     // (1) Inicia imediatamente
    async let noticias = buscarNoticias()  // (1) Inicia imediatamente
    async let cotacoes = buscarCotacoes()  // (1) Inicia imediatamente

    return try await (clima, noticias, cotacoes)  // (2) Espera todos
}

// (1) async let inicia a task sem esperar
// (2) await na tupla aguarda todas terminarem
```

---

## TaskGroup — paralelismo dinâmico

```swift
func baixarImagens(urls: [URL]) async throws -> [UIImage] {
    try await withThrowingTaskGroup(of: UIImage?.self) { group in
        for url in urls {
            group.addTask {                         // (1)
                let (data, _) = try await URLSession.shared.data(from: url)
                return UIImage(data: data)
            }
        }

        var imagens: [UIImage] = []
        for try await imagem in group {            // (2)
            if let imagem { imagens.append(imagem) }
        }
        return imagens
    }
}

// (1) Adiciona tasks ao grupo — todas rodam em paralelo
// (2) Coleta resultados conforme chegam
```

---

## @MainActor — segurança na UI

```swift
@MainActor
class MinhaViewModel: ObservableObject {
    @Published var texto = ""

    func carregar() async {
        let resultado = try? await buscarDados()    // (1)
        texto = resultado ?? "Erro"                 // (2) seguro — já na MainActor
    }
}

// Função que SEMPRE roda na main thread
@MainActor
func atualizarUI(_ msg: String) {
    label.text = msg
}

// Ir para a main thread de qualquer ponto
await MainActor.run {
    self.tableView.reloadData()
}

// (1) await pode suspender na background thread
// (2) Após retomar, está na MainActor — seguro para UI
```

---

## Cancelação

```swift
func carregarComCancelamento() async {
    for i in 0..<100 {
        try? await Task.sleep(for: .milliseconds(100))

        if Task.isCancelled {           // (1)
            print("Tarefa cancelada em \(i)")
            return
        }

        await processar(item: i)
    }
}

// Verificação automática com checkCancellation
func processarComCheck() async throws {
    for item in listaLonga {
        try Task.checkCancellation()   // (2) Lança CancellationError se cancelado
        await processar(item)
    }
}

// (1) Verificação manual
// (2) Lança erro automaticamente — propaga via throws
```

---

## Convertendo APIs com callbacks

```swift
// API legada com callback:
func buscarUsuarioLegado(id: Int, completion: @escaping (Usuario?, Error?) -> Void) { ... }

// Convertendo para async/await:
func buscarUsuario(id: Int) async throws -> Usuario {
    try await withCheckedThrowingContinuation { continuation in  // (1)
        buscarUsuarioLegado(id: id) { usuario, error in
            if let error {
                continuation.resume(throwing: error)
            } else if let usuario {
                continuation.resume(returning: usuario)
            } else {
                continuation.resume(throwing: URLError(.cannotParseResponse))
            }
        }
    }
}

// (1) withCheckedThrowingContinuation é a "ponte" entre callbacks e async/await
```

---

## Checklist

- [x] Entendo o que `async` e `await` significam
- [x] Crio `Task` para iniciar trabalho assíncrono
- [x] Uso `async let` para paralelismo
- [x] Uso `TaskGroup` para paralelismo dinâmico
- [x] Atualizo a UI com `@MainActor`
- [x] Implemento cancelação de Tasks
- [x] Converto callbacks com `withCheckedContinuation`
