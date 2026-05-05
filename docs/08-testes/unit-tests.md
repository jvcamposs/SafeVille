# Unit Tests com XCTest

🟡 **Intermediário** · Módulo 08

---

## O framework XCTest

`XCTest` é o framework oficial da Apple para testes automatizados em Swift e Objective-C. Ele está integrado ao Xcode e oferece:

- Testes unitários (`XCTestCase`)
- Testes de UI (`XCUITestCase`)
- Medição de performance (`measure {}`)
- Suporte a `async/await`

!!! info "Versão mínima"
    `XCTest` com `async/await` requer **iOS 13+** e **Xcode 13+**. Para projetos mais antigos, use `XCTestExpectation`.

---

## Configurando o Test Target

Ao criar um novo projeto no Xcode, marque a opção **Include Tests** para criar automaticamente dois targets:

- `NomeProjeto` — o app em si
- `NomeProjetoTests` — testes unitários
- `NomeProjetoUITests` — testes de UI

### Adicionando um test target a um projeto existente

Se você não marcou a opção na criação:

1. **File → New → Target**
2. Selecione **Unit Testing Bundle**
3. Nomeie como `NomeProjetoTests`
4. Em **Target to be Tested**, selecione seu app

!!! tip "Estrutura de pastas recomendada"
    ```
    MeuApp/
    ├── MeuApp/
    │   ├── Models/
    │   ├── ViewModels/
    │   └── Views/
    └── MeuAppTests/
        ├── Models/
        │   └── UserModelTests.swift
        └── ViewModels/
            └── LoginViewModelTests.swift
    ```
    Espelhe a estrutura do app para facilitar a navegação.

---

## A classe XCTestCase

Toda classe de testes herda de `XCTestCase`. Cada método que começa com `test` é executado automaticamente:

```swift
import XCTest
@testable import MeuApp // (1)

final class CalculadoraTests: XCTestCase {

    func testSomaPositivos() {
        let calc = Calculadora()
        XCTAssertEqual(calc.soma(2, 3), 5)
    }

    func testSomaNegativos() {
        let calc = Calculadora()
        XCTAssertEqual(calc.soma(-1, -4), -5)
    }
}
```

1. `@testable import` expõe membros `internal` do módulo para os testes. Membros `private` continuam inacessíveis.

---

## setUp e tearDown

Use esses métodos para configurar e limpar o estado antes/depois de cada teste:

```swift
final class ProdutoRepositoryTests: XCTestCase {

    var sut: ProdutoRepository!  // (1)
    var mockStorage: MockStorage!

    // Executa antes de CADA teste
    override func setUp() {
        super.setUp()
        mockStorage = MockStorage()
        sut = ProdutoRepository(storage: mockStorage)
    }

    // Executa depois de CADA teste
    override func tearDown() {
        sut = nil
        mockStorage = nil
        super.tearDown()
    }

    func testBuscaProdutoPorId() {
        // sut já está configurado e limpo
        let produto = sut.buscar(id: "abc123")
        XCTAssertNotNil(produto)
    }
}
```

1. `sut` significa **System Under Test** — convenção amplamente usada para nomear o objeto sendo testado.

### setUp e tearDown assíncronos (Xcode 13+)

```swift
override func setUp() async throws {
    try await super.setUp()
    sut = await ProdutoRepository.criarAsync()
}

override func tearDown() async throws {
    await sut.limpar()
    sut = nil
    try await super.tearDown()
}
```

---

## Funções XCTAssert

O XCTest oferece uma família de funções de asserção:

=== "Igualdade"
    ```swift
    XCTAssertEqual(a, b)               // a == b
    XCTAssertNotEqual(a, b)            // a != b
    XCTAssertEqual(a, b, accuracy: 0.001) // floats com precisão
    ```

=== "Booleanos"
    ```swift
    XCTAssertTrue(condicao)
    XCTAssertFalse(condicao)
    ```

=== "Nil"
    ```swift
    XCTAssertNil(valor)
    XCTAssertNotNil(valor)
    ```

=== "Erros"
    ```swift
    XCTAssertThrowsError(try funcaoQueLancaErro()) { error in
        XCTAssertEqual(error as? AppError, .notFound)
    }
    XCTAssertNoThrow(try funcaoSegura())
    ```

=== "Comparação"
    ```swift
    XCTAssertGreaterThan(a, b)         // a > b
    XCTAssertGreaterThanOrEqual(a, b)  // a >= b
    XCTAssertLessThan(a, b)            // a < b
    XCTAssertLessThanOrEqual(a, b)     // a <= b
    ```

### Mensagens customizadas

Todas as funções aceitam uma mensagem opcional que aparece no log de falha:

```swift
XCTAssertEqual(
    usuario.nome,
    "João",
    "O nome do usuário deveria ser João após o parse do JSON"
)
```

---

## Testando código assíncrono

### Com async/await (recomendado para iOS 13+/Xcode 13+)

```swift
final class WeatherServiceTests: XCTestCase {

    func testFetchWeatherRetornaTemperatura() async throws {
        // Arrange
        let service = WeatherService(apiKey: "test-key")

        // Act
        let weather = try await service.fetchWeather(city: "São Paulo")

        // Assert
        XCTAssertNotNil(weather)
        XCTAssertGreaterThan(weather.temperature, -100)
        XCTAssertLessThan(weather.temperature, 100)
    }

    func testFetchWeatherLancaErroComCidadeInvalida() async throws {
        let service = WeatherService(apiKey: "test-key")

        await XCTAssertThrowsErrorAsync(
            try await service.fetchWeather(city: "")
        ) { error in
            XCTAssertEqual(error as? WeatherError, .invalidCity)
        }
    }
}
```

### Com XCTestExpectation (legado, iOS 12 e abaixo)

```swift
func testFetchCompletion() {
    let expectation = expectation(description: "Fetch weather concluído")

    service.fetchWeather(city: "Rio de Janeiro") { result in
        switch result {
        case .success(let weather):
            XCTAssertNotNil(weather)
        case .failure(let error):
            XCTFail("Não deveria falhar: \(error)")
        }
        expectation.fulfill()
    }

    waitForExpectations(timeout: 5.0) // (1)
}
```

1. O teste falha se `fulfill()` não for chamado em 5 segundos. Sempre defina um timeout razoável.

---

## Testando com dependências: Protocolos e Mocks

O maior obstáculo para testes unitários são as dependências externas: rede, banco de dados, hardware. A solução é **injeção de dependência** via protocolos.

### Passo 1: Defina um protocolo para a dependência

```swift
// Protocolo que abstrai a chamada de rede
protocol WeatherAPIProtocol {
    func fetchWeather(city: String) async throws -> WeatherResponse
}

// Implementação real (usada no app)
struct WeatherAPI: WeatherAPIProtocol {
    func fetchWeather(city: String) async throws -> WeatherResponse {
        // Faz chamada de rede real
        let url = URL(string: "https://api.weather.com/v1/\(city)")!
        let (data, _) = try await URLSession.shared.data(from: url)
        return try JSONDecoder().decode(WeatherResponse.self, from: data)
    }
}
```

### Passo 2: Injete a dependência no ViewModel

```swift
final class WeatherViewModel: ObservableObject {
    @Published var weather: WeatherModel?
    @Published var errorMessage: String?
    @Published var isLoading = false

    private let api: WeatherAPIProtocol

    // Injeção via inicializador
    init(api: WeatherAPIProtocol = WeatherAPI()) { // (1)
        self.api = api
    }

    @MainActor
    func loadWeather(city: String) async {
        isLoading = true
        defer { isLoading = false }
        do {
            let response = try await api.fetchWeather(city: city)
            weather = WeatherModel(from: response)
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
```

1. O valor padrão `WeatherAPI()` é usado no app de produção. Nos testes, passamos o mock.

### Passo 3: Crie um Mock para os testes

```swift
// Mock que controla o comportamento nos testes
final class MockWeatherAPI: WeatherAPIProtocol {
    // Você controla o resultado
    var resultToReturn: Result<WeatherResponse, Error> = .success(
        WeatherResponse(temperature: 25.0, description: "Ensolarado")
    )

    // Espião: registra chamadas
    var fetchCalled = false
    var lastCityRequested: String?

    func fetchWeather(city: String) async throws -> WeatherResponse {
        fetchCalled = true
        lastCityRequested = city
        return try resultToReturn.get()
    }
}
```

### Passo 4: Escreva os testes

```swift
final class WeatherViewModelTests: XCTestCase {

    var sut: WeatherViewModel!
    var mockAPI: MockWeatherAPI!

    override func setUp() {
        super.setUp()
        mockAPI = MockWeatherAPI()
        sut = WeatherViewModel(api: mockAPI)
    }

    override func tearDown() {
        sut = nil
        mockAPI = nil
        super.tearDown()
    }

    func testLoadWeatherAtualizaPropriedade() async {
        // Arrange
        mockAPI.resultToReturn = .success(
            WeatherResponse(temperature: 30.0, description: "Quente")
        )

        // Act
        await sut.loadWeather(city: "Recife")

        // Assert
        XCTAssertNotNil(sut.weather)
        XCTAssertEqual(sut.weather?.temperature, 30.0)
        XCTAssertNil(sut.errorMessage)
        XCTAssertFalse(sut.isLoading)
    }

    func testLoadWeatherComErroDefineErrorMessage() async {
        // Arrange
        mockAPI.resultToReturn = .failure(WeatherError.networkError)

        // Act
        await sut.loadWeather(city: "Manaus")

        // Assert
        XCTAssertNil(sut.weather)
        XCTAssertNotNil(sut.errorMessage)
        XCTAssertFalse(sut.isLoading)
    }

    func testLoadWeatherPassaCidadeCorreta() async {
        // Act
        await sut.loadWeather(city: "Fortaleza")

        // Assert
        XCTAssertTrue(mockAPI.fetchCalled)
        XCTAssertEqual(mockAPI.lastCityRequested, "Fortaleza")
    }

    func testIsLoadingDuranteRequisicao() async {
        // Arrange: API lenta
        let slowAPI = SlowMockWeatherAPI()
        let viewModel = WeatherViewModel(api: slowAPI)

        // Act
        let task = Task {
            await viewModel.loadWeather(city: "Brasília")
        }

        // Dá tempo para a task começar
        await Task.yield()

        // Assert: deve estar carregando
        XCTAssertTrue(viewModel.isLoading)

        await task.value
        XCTAssertFalse(viewModel.isLoading)
    }
}
```

---

## Testando ViewModels completos

Aqui está um exemplo completo de ViewModel com testes abrangentes:

```swift
// ViewModel de login
final class LoginViewModel: ObservableObject {
    @Published var email = ""
    @Published var password = ""
    @Published var isLoggedIn = false
    @Published var errorMessage: String?
    @Published var isLoading = false

    private let authService: AuthServiceProtocol

    init(authService: AuthServiceProtocol = AuthService()) {
        self.authService = authService
    }

    var isFormValid: Bool {
        !email.isEmpty && password.count >= 6 && email.contains("@")
    }

    @MainActor
    func login() async {
        guard isFormValid else {
            errorMessage = "Preencha todos os campos corretamente."
            return
        }

        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            try await authService.login(email: email, password: password)
            isLoggedIn = true
        } catch AuthError.invalidCredentials {
            errorMessage = "Email ou senha incorretos."
        } catch {
            errorMessage = "Erro ao conectar. Tente novamente."
        }
    }
}
```

```swift
// Testes do LoginViewModel
final class LoginViewModelTests: XCTestCase {

    var sut: LoginViewModel!
    var mockAuth: MockAuthService!

    override func setUp() {
        super.setUp()
        mockAuth = MockAuthService()
        sut = LoginViewModel(authService: mockAuth)
    }

    override func tearDown() {
        sut = nil
        mockAuth = nil
        super.tearDown()
    }

    // MARK: - isFormValid

    func testFormInvalidoComEmailVazio() {
        sut.email = ""
        sut.password = "123456"
        XCTAssertFalse(sut.isFormValid)
    }

    func testFormInvalidoSemArroba() {
        sut.email = "emailsemarroba.com"
        sut.password = "123456"
        XCTAssertFalse(sut.isFormValid)
    }

    func testFormInvalidoSenhaCurta() {
        sut.email = "user@email.com"
        sut.password = "12345" // menos de 6 caracteres
        XCTAssertFalse(sut.isFormValid)
    }

    func testFormValidoComDadosCorretos() {
        sut.email = "user@email.com"
        sut.password = "senha123"
        XCTAssertTrue(sut.isFormValid)
    }

    // MARK: - login()

    func testLoginComSucessoDefineIsLoggedIn() async {
        sut.email = "user@email.com"
        sut.password = "senha123"
        mockAuth.shouldSucceed = true

        await sut.login()

        XCTAssertTrue(sut.isLoggedIn)
        XCTAssertNil(sut.errorMessage)
    }

    func testLoginComCredenciaisInvalidasDefineErro() async {
        sut.email = "user@email.com"
        sut.password = "errada123"
        mockAuth.errorToThrow = AuthError.invalidCredentials

        await sut.login()

        XCTAssertFalse(sut.isLoggedIn)
        XCTAssertEqual(sut.errorMessage, "Email ou senha incorretos.")
    }

    func testLoginComFormInvalidoNaoChamaService() async {
        sut.email = ""
        sut.password = ""

        await sut.login()

        XCTAssertFalse(mockAuth.loginCalled)
        XCTAssertNotNil(sut.errorMessage)
    }
}
```

---

## Code Coverage no Xcode

A cobertura de código mostra quais linhas foram executadas durante os testes.

### Ativando Code Coverage

1. **Product → Scheme → Edit Scheme**
2. Selecione **Test** na barra lateral
3. Marque **Gather coverage for: All targets** (ou selecione targets específicos)

### Visualizando a cobertura

Após rodar os testes (**Cmd+U**):

1. Abra o **Report Navigator** (ícone de documento na barra lateral)
2. Selecione o último run de testes
3. Clique na aba **Coverage**

!!! tip "Cobertura realista"
    Não busque 100% de cobertura — isso é impraticável e pode levar a testes sem valor. Foque em:

    - **ViewModels**: 80–90%
    - **Models/Parsers**: 90–100%
    - **Views**: 30–50% (teste a lógica, não a UI)
    - **Utilitários**: 90–100%

### Cobertura por linha no editor

Com Coverage ativado, o Xcode mostra uma coluna verde/vermelha no editor:

- **Verde**: linha executada pelos testes
- **Vermelho**: linha nunca executada

---

## Boas práticas

!!! success "Faça isso"
    - Nomeie testes de forma descritiva: `testLoginComEmailInvalidoRetornaErro`
    - Um assert por teste (quando possível)
    - Use `setUp`/`tearDown` para não repetir código
    - Testes devem ser independentes — nunca dependam da ordem de execução

!!! danger "Evite isso"
    - Testes que dependem de estado global
    - Chamadas de rede reais nos unit tests
    - Testes que dormem (`sleep()`) — use `async/await` ou `XCTestExpectation`
    - `XCTAssert` sem mensagem explicativa em casos complexos

---

## Exercícios

!!! exercise "Pratique"
    1. Crie um model `Produto(nome: String, preco: Double, estoque: Int)` e escreva pelo menos 5 testes para métodos como `aplicarDesconto(percentual:)` e `estaDisponivel`.
    2. Adicione injeção de dependência ao `WeatherViewModel` do Módulo 07 e crie um mock para a camada de rede.
    3. Meça a cobertura de código do seu projeto e identifique as áreas com menos cobertura.

---

[← Visão Geral do Módulo](index.md){ .md-button }
[UI Tests →](ui-tests.md){ .md-button .md-button--primary }
