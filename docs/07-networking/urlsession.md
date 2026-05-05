# URLSession

🟡 **Intermediário** · Módulo 07

`URLSession` é a API nativa do iOS para networking. Desde o Swift 5.5, ela se integra perfeitamente com async/await, tornando o código de rede tão legível quanto código síncrono.

---

## Request básico

```swift
import Foundation

// Struct Codable que mapeia o JSON
struct Post: Codable, Identifiable {
    let id:     Int
    let userId: Int
    let title:  String
    let body:   String
}

// Buscar um post (async/await)
func buscarPost(id: Int) async throws -> Post {
    let url = URL(string: "https://jsonplaceholder.typicode.com/posts/\(id)")!
    let (data, response) = try await URLSession.shared.data(from: url) // (1)

    guard let http = response as? HTTPURLResponse,
          (200...299).contains(http.statusCode) else {           // (2)
        throw URLError(.badServerResponse)
    }

    return try JSONDecoder().decode(Post.self, from: data)       // (3)
}

// Uso em SwiftUI
struct PostView: View {
    @State private var post: Post?

    var body: some View {
        VStack {
            if let post {
                Text(post.title).font(.headline)
                Text(post.body).font(.body)
            }
        }
        .task { post = try? await buscarPost(id: 1) }
    }
}

// (1) data(from:) suspende a Task até receber resposta
// (2) Sempre verifique o statusCode!
// (3) JSONDecoder mapeia o JSON para a struct Swift
```

---

## URLRequest — configuração completa

```swift
func criarPost(titulo: String, corpo: String) async throws -> Post {
    let url = URL(string: "https://jsonplaceholder.typicode.com/posts")!

    var request = URLRequest(url: url)
    request.httpMethod = "POST"                                            // (1)
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue("Bearer meu-token-aqui", forHTTPHeaderField: "Authorization")
    request.timeoutInterval = 30

    let body = ["title": titulo, "body": corpo, "userId": 1] as [String: Any]
    request.httpBody = try JSONSerialization.data(withJSONObject: body)    // (2)

    let (data, _) = try await URLSession.shared.data(for: request)
    return try JSONDecoder().decode(Post.self, from: data)
}

// (1) GET, POST, PUT, PATCH, DELETE
// (2) Serializar o body para JSON
```

---

## Configurações da URLSession

```swift
// Configuração customizada
let config = URLSessionConfiguration.default
config.timeoutIntervalForRequest  = 30  // timeout por request
config.timeoutIntervalForResource = 60  // timeout total
config.requestCachePolicy = .reloadIgnoringLocalCacheData

let session = URLSession(configuration: config)

// Ephemeral — sem cookies, cache ou credenciais salvas
let sessionPrivada = URLSession(configuration: .ephemeral)

// Background — continua mesmo com app em background
let bgConfig = URLSessionConfiguration.background(withIdentifier: "meu.app.download")
let sessionBG = URLSession(configuration: bgConfig)
```

---

## Download de arquivos

```swift
func downloadImagem(url: URL) async throws -> Data {
    let (localURL, response) = try await URLSession.shared.download(from: url) // (1)

    guard let http = response as? HTTPURLResponse,
          http.statusCode == 200 else {
        throw URLError(.badServerResponse)
    }

    return try Data(contentsOf: localURL)
}

// (1) download(from:) salva em arquivo temporário — bom para arquivos grandes
```

---

## Upload

```swift
func uploadImagem(_ imageData: Data, para url: URL) async throws {
    var request = URLRequest(url: url)
    request.httpMethod = "PUT"
    request.setValue("image/jpeg", forHTTPHeaderField: "Content-Type")

    let (_, response) = try await URLSession.shared.upload(
        for: request,
        from: imageData   // (1)
    )

    guard let http = response as? HTTPURLResponse,
          (200...299).contains(http.statusCode) else {
        throw URLError(.badServerResponse)
    }
}

// (1) upload(for:from:) para dados em memória
```

---

## JSONDecoder — datas e chaves

```swift
// JSON com snake_case → Swift camelCase
struct Usuario: Codable {
    let id:         Int
    let nomeCompleto: String  // JSON: "nome_completo"
    let criadoEm:  Date       // JSON: "criado_em"
}

let decoder = JSONDecoder()
decoder.keyDecodingStrategy  = .convertFromSnakeCase  // (1)
decoder.dateDecodingStrategy = .iso8601               // (2)

let usuario = try decoder.decode(Usuario.self, from: data)

// (1) Converte nome_completo → nomeCompleto automaticamente
// (2) Parseia datas ISO 8601 (ex: "2024-01-15T10:30:00Z")
```

---

## Checklist

- [x] Faço requests com `URLSession.shared.data(from:)`
- [x] Configuro `URLRequest` com método, headers e body
- [x] Verifico o `statusCode` da resposta
- [x] Decodifico JSON com `JSONDecoder` e `Codable`
- [x] Configuro `keyDecodingStrategy` e `dateDecodingStrategy`
