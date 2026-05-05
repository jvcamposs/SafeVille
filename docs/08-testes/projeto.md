# Mini-Projeto: Adicionando Testes ao App de Clima

🟡 **Intermediário** · Módulo 08 · Projeto Prático

Vamos adicionar testes unitários e de UI ao app de clima do Módulo 07, praticando os conceitos de unit tests, mocking e TDD.

---

## Estrutura de testes

```
ClimaAppTests/
├── Unit/
│   ├── ClimaViewModelTests.swift
│   ├── ClimaServiceTests.swift
│   └── WeatherCodeTests.swift
└── UI/
    └── ClimaUITests.swift
```

---

## Mock do ClimaService

```swift
// MockClimaService.swift
final class MockClimaService: ClimaServiceProtocol {
    var cidadesMock:        [Cidade]        = []
    var climaMock:          ClimaResposta?  = nil
    var deveRetornarErro    = false
    var erroLancado: Error  = URLError(.notConnectedToInternet)

    var buscarCidadeChamado = false
    var buscarClimaChamado  = false

    func buscarCidade(nome: String) async throws -> [Cidade] {
        buscarCidadeChamado = true
        if deveRetornarErro { throw erroLancado }
        return cidadesMock
    }

    func buscarClima(lat: Double, lon: Double) async throws -> ClimaResposta {
        buscarClimaChamado = true
        if deveRetornarErro { throw erroLancado }
        guard let clima = climaMock else { throw URLError(.cannotParseResponse) }
        return clima
    }
}
```

---

## Testes do ViewModel

```swift
import Testing
@testable import ClimaApp

@Suite("ClimaViewModel")
struct ClimaViewModelTests {

    @Test("Busca cidades e atualiza a lista")
    func testBuscarCidades() async {
        let mock = MockClimaService()
        mock.cidadesMock = [
            Cidade(id: 1, name: "São Paulo", country: "BR",
                   latitude: -23.5, longitude: -46.6)
        ]
        let vm = ClimaViewModel(service: mock)
        vm.cidadeBuscada = "São Paulo"

        await vm.buscarCidades()

        #expect(mock.buscarCidadeChamado)
        #expect(vm.cidadesEncontradas.count == 1)
        #expect(vm.cidadesEncontradas.first?.name == "São Paulo")
        #expect(!vm.isLoading)
    }

    @Test("Mostra erro quando serviço falha")
    func testErroAoBuscarCidades() async {
        let mock = MockClimaService()
        mock.deveRetornarErro = true
        let vm = ClimaViewModel(service: mock)
        vm.cidadeBuscada = "Qualquer"

        await vm.buscarCidades()

        #expect(vm.errorMessage != nil)
        #expect(vm.cidadesEncontradas.isEmpty)
    }

    @Test("Temperatura formatada corretamente")
    func testTemperaturaFormatada() async {
        let mock = MockClimaService()
        mock.climaMock = ClimaResposta.mockComTemperatura(22.7)
        let vm = ClimaViewModel(service: mock)

        let cidade = Cidade(id: 1, name: "RJ", country: "BR",
                           latitude: -22.9, longitude: -43.2)
        await vm.selecionarCidade(cidade)

        #expect(vm.temperaturaAtual == "22°C")
    }

    @Test("isLoading é true durante carregamento")
    func testIsLoadingDuranteCarregamento() async {
        let mock = MockClimaService()
        // Simular delay
        let vm = ClimaViewModel(service: mock)

        #expect(!vm.isLoading) // Antes: false
        // Durante: seria true (difícil testar sem continuations)
        // Depois:  false (verificado nos outros testes)
    }
}
```

---

## Testes do WeatherCode

```swift
@Suite("WeatherCode")
struct WeatherCodeTests {

    @Test("Código 0 = céu limpo")
    func testCodigoCeuLimpo() {
        #expect(WeatherCode.descricao(for: 0) == "Céu limpo")
        #expect(WeatherCode.icone(for: 0) == "sun.max.fill")
    }

    @Test("Códigos de chuva retornam ícone correto")
    func testCodigosChuva() {
        for codigo in [61, 63, 65] {
            #expect(WeatherCode.icone(for: codigo) == "cloud.heavyrain.fill",
                    "Falhou para código \(codigo)")
        }
    }

    @Test("Código desconhecido retorna fallback")
    func testCodigoDesconhecido() {
        #expect(WeatherCode.icone(for: 999) == "questionmark.circle")
    }
}
```

---

## Testes de UI

```swift
import XCTest

final class ClimaUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["--uitesting"]  // (1)
        app.launch()
    }

    func testBuscaExibeResultados() throws {
        let searchField = app.searchFields["Buscar cidade..."]
        XCTAssertTrue(searchField.exists)

        searchField.tap()
        searchField.typeText("São Paulo")

        // Verificar que sugestões aparecem
        let sugestao = app.buttons["São Paulo, BR"]
        XCTAssertTrue(sugestao.waitForExistence(timeout: 5))

        sugestao.tap()

        // Verificar que o clima é exibido
        let temperaturaLabel = app.staticTexts.matching(
            NSPredicate(format: "label CONTAINS '°C'")
        ).firstMatch
        XCTAssertTrue(temperaturaLabel.waitForExistence(timeout: 10))
    }

    func testEstadoInicialMostraPlaceholder() throws {
        let placeholder = app.staticTexts["Busque uma cidade"]
        XCTAssertTrue(placeholder.exists)
    }
}

// (1) --uitesting pode ser usado para injetar dados mock no app
```

---

## Checklist do Módulo 08

- [x] Escrevo unit tests com XCTest/Swift Testing
- [x] Crio mocks via protocolos para isolar dependências
- [x] Testo ViewModels de forma independente do networking
- [x] Escrevo UI tests com XCUITest
- [x] Conheço o fluxo Red-Green-Refactor do TDD
- [x] Adicionei testes ao app de clima

**Próximo módulo:** [CI/CD & Publicação →](../09-cicd-publicacao/index.md)
