# Swift Concurrency Avançada

🔴 **Avançado** · Módulo 10

Além do async/await básico, Swift Concurrency oferece ferramentas poderosas para segurança em concorrência: Actors, Sendable, e async sequences.

---

## Actors — Proteção de estado compartilhado

```swift
// ❌ Sem actor — race condition possível
class ContadorInseguro {
    var valor = 0
    func incrementar() { valor += 1 }  // Perigoso em múltiplas threads!
}

// ✅ Com actor — acesso serializado automaticamente
actor Contador {
    private(set) var valor = 0

    func incrementar() {
        valor += 1    // (1) Apenas uma Task acessa por vez
    }

    func reset() { valor = 0 }
}

// Uso
let contador = Contador()

await withTaskGroup(of: Void.self) { group in
    for _ in 0..<1000 {
        group.addTask {
            await contador.incrementar()  // (2) await necessário fora do actor
        }
    }
}
print(await contador.valor) // Sempre 1000 — nunca race condition
```

---

## @MainActor em classes inteiras

```swift
@MainActor
class MinhaViewModel: ObservableObject {
    @Published var titulo = ""
    @Published var isLoading = false

    // Todos os métodos rodam na main thread automaticamente
    func carregar() async {
        isLoading = true
        let dados = await Task.detached(priority: .background) {
            await processarDadosHeavy()  // Background thread
        }.value
        titulo = dados                   // Main thread — seguro
        isLoading = false
    }
}
```

---

## Sendable — Segurança de tipos em concorrência

```swift
// Sendable: seguro para passar entre concurrency domains
struct Ponto: Sendable {   // (1) Structs com tipos Sendable são Sendable
    let x: Double
    let y: Double
}

// Classe não-Sendable (estado mutável compartilhável)
final class Cache: @unchecked Sendable {  // (2) Você garante a segurança
    private let lock = NSLock()
    private var store: [String: Any] = [:]

    func set(_ value: Any, for key: String) {
        lock.lock()
        store[key] = value
        lock.unlock()
    }
}

// @Sendable em closures
func executarBackground(_ trabalho: @Sendable () async -> Void) async {
    await Task.detached { await trabalho() }.value
}

// (1) Struct é Sendable se todos os campos forem Sendable
// (2) @unchecked Sendable — você assume a responsabilidade
```

---

## AsyncStream — Sequências assíncronas

```swift
// Criando um stream de eventos
func monitorarLocalizacao() -> AsyncStream<CLLocation> {
    AsyncStream { continuation in              // (1)
        let manager = CLLocationManager()
        let delegate = LocationDelegate { location in
            continuation.yield(location)       // (2) Emite um valor
        }
        manager.delegate = delegate
        manager.startUpdatingLocation()

        continuation.onTermination = { _ in    // (3) Cleanup
            manager.stopUpdatingLocation()
        }
    }
}

// Consumindo o stream
for await localizacao in monitorarLocalizacao() {  // (4)
    print("Nova localização: \(localizacao)")
    if distanciaChegou(localizacao) { break }
}

// (1) AsyncStream é a "fonte" do stream
// (2) yield emite valores para quem está consumindo
// (3) Chamado quando o stream é cancelado
// (4) for await consome valores conforme chegam
```

---

## AsyncThrowingStream

```swift
func buscarAtualizacoes() -> AsyncThrowingStream<Atualizacao, Error> {
    AsyncThrowingStream { continuation in
        let conexao = WebSocketConexao(url: wsURL)
        conexao.onMensagem = { msg in
            do {
                let update = try JSONDecoder().decode(Atualizacao.self, from: msg)
                continuation.yield(update)
            } catch {
                continuation.finish(throwing: error)  // Encerra com erro
            }
        }
        conexao.onConexaoFechada = {
            continuation.finish()  // Encerra normalmente
        }
    }
}

// Consumo
do {
    for try await atualizacao in buscarAtualizacoes() {
        processar(atualizacao)
    }
} catch {
    print("Stream encerrado com erro: \(error)")
}
```

---

## Task Local Values

```swift
// Valores propagados automaticamente dentro de uma Task
@TaskLocal static var requestID: String? = nil

func processarRequest() async {
    await $requestID.withValue(UUID().uuidString) {    // (1)
        await etapa1()   // requestID disponível aqui
        await etapa2()   // e aqui
    }
}

func etapa1() async {
    if let id = requestID {
        print("Request \(id): etapa 1")
    }
}

// (1) O valor é propagado para todas as Tasks filhas automaticamente
```

---

## Checklist

- [x] Crio `actor` para proteger estado compartilhado
- [x] Uso `@MainActor` para segurança na thread de UI
- [x] Entendo `Sendable` e quando usar `@unchecked Sendable`
- [x] Crio e consumo `AsyncStream`
- [x] Uso `@TaskLocal` para contexto propagado
