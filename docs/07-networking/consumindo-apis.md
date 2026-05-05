# Consumindo APIs REST

🟡 **Intermediário** · Módulo 07

Construir uma camada de networking robusta e reutilizável é uma habilidade fundamental. Este capítulo mostra como estruturar um `NetworkService` genérico de nível profissional.

---

## Erros customizados

```swift
enum NetworkError: LocalizedError {
    case invalidURL
    case serverError(statusCode: Int)
    case decodingError(Error)
    case noInternet

    var errorDescription: String? {
        switch self {
        case .invalidURL:              return "URL inválida"
        case .serverError(let code):   return "Erro do servidor: \(code)"
        case .decodingError(let e):    return "Erro ao parsear resposta: \(e)"
        case .noInternet:              return "Sem conexão com a internet"
        }
    }
}
```

---

## Endpoint Pattern

```swift
// Protocolo para todos os endpoints
protocol APIEndpoint {
    var baseURL:    String     { get }
    var path:       String     { get }
    var method:     HTTPMethod { get }
    var headers:    [String: String] { get }
    var parameters: [String: Any]?   { get }
    var body:       Encodable?       { get }
}

enum HTTPMethod: String {
    case GET, POST, PUT, PATCH, DELETE
}

// Extensão com implementações padrão
extension APIEndpoint {
    var baseURL: String { "https://api.exemplo.com" }
    var headers: [String: String] {
        ["Content-Type": "application/json",
         "Accept":       "application/json"]
    }
    var parameters: [String: Any]? { nil }
    var body: Encodable? { nil }

    func urlRequest() throws -> URLRequest {
        guard var components = URLComponents(string: baseURL + path) else {
            throw NetworkError.invalidURL
        }
        if let params = parameters {
            components.queryItems = params.map {
                URLQueryItem(name: $0.key, value: "\($0.value)")
            }
        }
        guard let url = components.url else { throw NetworkError.invalidURL }

        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        headers.forEach { request.setValue($1, forHTTPHeaderField: $0) }

        if let body {
            request.httpBody = try JSONEncoder().encode(body)
        }
        return request
    }
}
```

---

## Endpoints concretos

```swift
// Todos os endpoints da API em um enum organizado
enum PostsEndpoint: APIEndpoint {
    case listar
    case buscar(id: Int)
    case criar(titulo: String, corpo: String)
    case deletar(id: Int)

    var path: String {
        switch self {
        case .listar:         return "/posts"
        case .buscar(let id): return "/posts/\(id)"
        case .criar:          return "/posts"
        case .deletar(let id): return "/posts/\(id)"
        }
    }

    var method: HTTPMethod {
        switch self {
        case .listar, .buscar: return .GET
        case .criar:           return .POST
        case .deletar:         return .DELETE
        }
    }

    var body: Encodable? {
        switch self {
        case .criar(let titulo, let corpo):
            return ["title": titulo, "body": corpo, "userId": 1]
        default: return nil
        }
    }
}
```

---

## NetworkService genérico

```swift
protocol NetworkServiceProtocol {
    func request<T: Decodable>(_ endpoint: APIEndpoint,
                               as type: T.Type) async throws -> T
}

final class NetworkService: NetworkServiceProtocol {

    private let session: URLSession
    private let decoder: JSONDecoder

    init(session: URLSession = .shared,
         decoder: JSONDecoder = {
             let d = JSONDecoder()
             d.keyDecodingStrategy  = .convertFromSnakeCase
             d.dateDecodingStrategy = .iso8601
             return d
         }()) {
        self.session = session
        self.decoder = decoder
    }

    func request<T: Decodable>(_ endpoint: APIEndpoint,
                               as type: T.Type) async throws -> T {
        let urlRequest = try endpoint.urlRequest()

        let (data, response): (Data, URLResponse)
        do {
            (data, response) = try await session.data(for: urlRequest)
        } catch let error as URLError where error.code == .notConnectedToInternet {
            throw NetworkError.noInternet
        }

        guard let http = response as? HTTPURLResponse else {
            throw NetworkError.serverError(statusCode: -1)
        }
        guard (200...299).contains(http.statusCode) else {
            throw NetworkError.serverError(statusCode: http.statusCode)
        }

        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw NetworkError.decodingError(error)
        }
    }
}
```

---

## Repository que usa o NetworkService

```swift
protocol PostRepositoryProtocol {
    func listar() async throws -> [Post]
    func buscar(id: Int) async throws -> Post
}

final class PostRepository: PostRepositoryProtocol {
    private let service: NetworkServiceProtocol

    init(service: NetworkServiceProtocol = NetworkService()) {
        self.service = service
    }

    func listar() async throws -> [Post] {
        try await service.request(PostsEndpoint.listar, as: [Post].self)
    }

    func buscar(id: Int) async throws -> Post {
        try await service.request(PostsEndpoint.buscar(id: id), as: Post.self)
    }
}
```

---

## Paginação

```swift
struct PaginaResposta<T: Decodable>: Decodable {
    let data: [T]
    let page: Int
    let totalPages: Int
    var temMais: Bool { page < totalPages }
}

@Observable
class PostsViewModel {
    var posts: [Post] = []
    var paginaAtual = 1
    var carregandoMais = false
    var temMais = true

    private let repository: PostRepositoryProtocol

    init(repository: PostRepositoryProtocol = PostRepository()) {
        self.repository = repository
    }

    func carregarMais() async {
        guard temMais, !carregandoMais else { return }
        carregandoMais = true
        defer { carregandoMais = false }

        // ... busca a próxima página e adiciona aos posts
    }
}
```

---

## Checklist

- [x] Crio erros customizados com `LocalizedError`
- [x] Uso o Endpoint Pattern para organizar chamadas de API
- [x] Construo um `NetworkService` genérico e reutilizável
- [x] Separo responsabilidades com o Repository Pattern
- [x] Testo com um mock do `NetworkServiceProtocol`
