# UI Tests

🟡 **Intermediário** · Módulo 08

---

## O framework XCUITest

Enquanto os unit tests verificam a lógica isolada, os **UI tests** (ou testes de interface) exercitam o aplicativo como um usuário real faria: tocando botões, digitando texto, navegando entre telas.

O `XCUITest` é construído sobre o framework de Acessibilidade da Apple — o mesmo que o VoiceOver usa para identificar elementos na tela. Isso significa que **UI tests e acessibilidade estão diretamente conectados**.

!!! warning "UI Tests são lentos"
    Cada UI test inicializa o app do zero. Uma suite com 50 testes pode levar 10–15 minutos. Use-os para fluxos críticos, não para cada detalhe da interface.

---

## XCUIApplication

`XCUIApplication` representa seu app em execução. É sempre o ponto de entrada dos UI tests:

```swift
import XCTest

final class LoginUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false // (1)
        app = XCUIApplication()
        app.launch()
    }

    override func tearDown() {
        app.terminate()
        super.tearDown()
    }
}
```

1. `continueAfterFailure = false` faz o teste parar imediatamente ao falhar, evitando erros em cascata.

### Configurando o app com launch arguments

```swift
override func setUp() {
    super.setUp()
    app = XCUIApplication()
    app.launchArguments = ["--uitesting"]      // (1)
    app.launchEnvironment = [
        "BASE_URL": "https://staging.api.com",
        "RESET_STATE": "true"                   // (2)
    ]
    app.launch()
}
```

1. Argumentos de launch são acessíveis com `CommandLine.arguments.contains("--uitesting")`.
2. Use variáveis de ambiente para configurar o app em modo de teste: limpar dados, usar mock server, etc.

No app, detecte o modo de teste:

```swift
// AppDelegate ou @main struct
#if DEBUG
if CommandLine.arguments.contains("--uitesting") {
    // Limpa dados persistidos para testes
    UserDefaults.standard.removePersistentDomain(forName: Bundle.main.bundleIdentifier!)
}
#endif
```

---

## Encontrando elementos

O XCUITest oferece várias formas de localizar elementos na tela:

=== "Por tipo"
    ```swift
    app.buttons.firstMatch              // Primeiro botão
    app.buttons["Entrar"]               // Botão com label "Entrar"
    app.textFields["Email"]             // Campo de texto
    app.secureTextFields["Senha"]       // Campo de senha
    app.labels["Bem-vindo"]             // Label
    app.images["logo"]                  // Imagem
    app.switches["Notificações"]        // Switch
    app.tables.cells.firstMatch         // Célula de tabela
    ```

=== "Por accessibilityIdentifier"
    ```swift
    // No código do app (SwiftUI):
    Button("Entrar") { ... }
        .accessibilityIdentifier("btn_login")

    // No UI test:
    app.buttons["btn_login"].tap()
    ```

=== "Por hierarquia"
    ```swift
    // Elemento dentro de outro elemento
    let form = app.otherElements["loginForm"]
    let emailField = form.textFields.firstMatch

    // Célula específica numa lista
    let cell = app.tables.cells.element(boundBy: 2) // índice 2
    ```

=== "Queries combinadas"
    ```swift
    // Botão que contém "Confirmar" em qualquer parte do texto
    app.buttons.matching(
        NSPredicate(format: "label CONTAINS 'Confirmar'")
    ).firstMatch
    ```

!!! tip "Prefira accessibilityIdentifier"
    Labels podem mudar com localização. `accessibilityIdentifier` é estável e não afeta o que o usuário vê.

---

## Interações com elementos

```swift
// Toque simples
app.buttons["Entrar"].tap()

// Duplo toque
app.buttons["Zoom"].doubleTap()

// Toque longo
app.cells["Favorito"].press(forDuration: 1.5)

// Digitar texto (limpa e digita)
let emailField = app.textFields["Email"]
emailField.tap()
emailField.clearAndEnterText("user@email.com") // extensão personalizada

// Digitar sem limpar
emailField.typeText("texto adicional")

// Swipe
app.swipeUp()
app.swipeDown()
app.swipeLeft()
app.swipeRight()

// Scroll para elemento
app.tables.cells["Item 50"].scrollToElement(app)

// Pinch (zoom)
app.images["mapa"].pinch(withScale: 2.0, velocity: 1.0)
```

### Extensão útil: clearAndEnterText

```swift
extension XCUIElement {
    func clearAndEnterText(_ text: String) {
        guard let currentValue = value as? String else {
            tap()
            typeText(text)
            return
        }

        tap()
        let deleteString = String(repeating: XCUIKeyboardKey.delete.rawValue,
                                  count: currentValue.count)
        typeText(deleteString)
        typeText(text)
    }
}
```

---

## Asserções em UI Tests

```swift
// Verifica se elemento existe na hierarquia
XCTAssertTrue(app.buttons["Entrar"].exists)

// Verifica se elemento está visível (existe E está na tela)
XCTAssertTrue(app.labels["Bem-vindo"].isHittable)

// Aguarda elemento aparecer (com timeout)
let successLabel = app.labels["Login realizado!"]
XCTAssertTrue(
    successLabel.waitForExistence(timeout: 5.0),
    "Label de sucesso não apareceu em 5 segundos"
)

// Verifica texto de um elemento
XCTAssertEqual(app.labels["titulo"].label, "Dashboard")

// Verifica se um switch está ligado/desligado
XCTAssertEqual(app.switches["darkMode"].value as? String, "1") // "1" = on, "0" = off
```

---

## Page Object Pattern

O Page Object Pattern organiza os UI tests criando classes que representam telas do app. Isso evita duplicação e torna os testes mais legíveis.

### Sem Page Object (difícil de manter)

```swift
func testLoginComSucesso() {
    app.textFields["Email"].tap()
    app.textFields["Email"].typeText("user@email.com")
    app.secureTextFields["Senha"].tap()
    app.secureTextFields["Senha"].typeText("senha123")
    app.buttons["Entrar"].tap()
    XCTAssertTrue(app.labels["Dashboard"].waitForExistence(timeout: 5))
}
```

### Com Page Object (limpo e reutilizável)

```swift
// LoginPage.swift
struct LoginPage {
    let app: XCUIApplication

    // Elementos
    var emailField: XCUIElement { app.textFields["Email"] }
    var passwordField: XCUIElement { app.secureTextFields["Senha"] }
    var loginButton: XCUIElement { app.buttons["btn_login"] }
    var errorLabel: XCUIElement { app.labels["errorMessage"] }

    // Ações
    @discardableResult
    func preencherEmail(_ email: String) -> Self {
        emailField.tap()
        emailField.clearAndEnterText(email)
        return self
    }

    @discardableResult
    func preencherSenha(_ senha: String) -> Self {
        passwordField.tap()
        passwordField.clearAndEnterText(senha)
        return self
    }

    func tocarEntrar() -> DashboardPage {
        loginButton.tap()
        return DashboardPage(app: app)
    }

    // Verificações
    func verificarErroVisivel() -> Self {
        XCTAssertTrue(errorLabel.waitForExistence(timeout: 3))
        return self
    }
}

// DashboardPage.swift
struct DashboardPage {
    let app: XCUIApplication

    var title: XCUIElement { app.navigationBars["Dashboard"] }

    func estaVisivel() -> Bool {
        title.waitForExistence(timeout: 5)
    }
}
```

```swift
// LoginUITests.swift — muito mais legível!
final class LoginUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    func testLoginComSucesso() {
        let dashboard = LoginPage(app: app)
            .preencherEmail("user@email.com")
            .preencherSenha("senha123")
            .tocarEntrar()

        XCTAssertTrue(dashboard.estaVisivel())
    }

    func testLoginComSenhaErrada() {
        LoginPage(app: app)
            .preencherEmail("user@email.com")
            .preencherSenha("errada")
            .tocarEntrar()
            .verificarErroVisivel() // Permanece na LoginPage
    }
}
```

---

## Testando fluxos de navegação

```swift
func testFluxoCompletoCadastro() {
    // Tela inicial
    app.buttons["Criar conta"].tap()

    // Tela de cadastro
    let cadastroPage = CadastroPage(app: app)
    cadastroPage
        .preencherNome("Maria Silva")
        .preencherEmail("maria@email.com")
        .preencherSenha("Senha@123")
        .confirmarSenha("Senha@123")
        .tocarCadastrar()

    // Verificar e-mail de confirmação
    XCTAssertTrue(
        app.alerts["Verifique seu e-mail"].waitForExistence(timeout: 5)
    )
    app.alerts.buttons["OK"].tap()

    // Deve voltar para a tela de login
    XCTAssertTrue(LoginPage(app: app).emailField.waitForExistence(timeout: 3))
}
```

---

## Screenshots em caso de falha

O Xcode tira screenshots automáticas quando um teste falha. Você também pode tirar screenshots manualmente:

```swift
func testTelaDetalhe() {
    app.tables.cells.firstMatch.tap()

    // Screenshot manual com nome descritivo
    let screenshot = app.screenshot()
    let attachment = XCTAttachment(screenshot: screenshot)
    attachment.name = "Tela de detalhe após navegação"
    attachment.lifetime = .keepAlways // (1)
    add(attachment)

    XCTAssertTrue(app.navigationBars["Detalhe"].exists)
}
```

1. `.keepAlways` mantém o attachment mesmo quando o teste passa. Use `.deleteOnSuccess` para economizar espaço.

### Screenshot em setUp para debug

```swift
override func tearDown() {
    // Sempre captura screenshot ao final do teste
    let screenshot = app.screenshot()
    let attachment = XCTAttachment(screenshot: screenshot)
    attachment.name = "Estado final — \(name)"
    attachment.lifetime = .deleteOnSuccess
    add(attachment)
    super.tearDown()
}
```

---

## Medição de Performance

O método `measure` roda um bloco várias vezes e mede o tempo médio:

```swift
func testPerformanceLaunch() {
    // Mede quanto tempo o app demora para lançar
    measure(metrics: [XCTApplicationLaunchMetric()]) {
        XCUIApplication().launch()
    }
}

func testPerformanceScrollLista() {
    measure {
        app.swipeUp()
        app.swipeUp()
        app.swipeDown()
        app.swipeDown()
    }
}
```

!!! info "Baseline"
    Na primeira execução, o Xcode estabelece uma **baseline** (valor de referência). Nas execuções seguintes, o teste falha se exceder a baseline por uma margem configurável. Clique em **Set Baseline** no resultado do teste.

---

## Boas práticas para UI Tests

!!! success "Faça isso"
    - Use `accessibilityIdentifier` para identificar elementos críticos
    - Use Page Object Pattern desde o início
    - Resete o estado do app no `setUp` (dados, login, preferências)
    - Escreva testes para fluxos críticos: login, compra, cadastro
    - Mantenha timeouts realistas (5s–10s)

!!! danger "Evite isso"
    - Depender da posição exata dos elementos na tela
    - Usar `Thread.sleep` — prefira `waitForExistence(timeout:)`
    - Testar detalhes visuais (cor, fonte) — isso é melhor para snapshot tests
    - Suite de UI tests maior que a suite de unit tests

---

## Exemplo completo: Teste do app de Clima

```swift
final class WeatherAppUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["--uitesting", "--use-mock-data"]
        app.launch()
    }

    func testBuscaCidadeExibeTemperatura() {
        let searchField = app.searchFields["Buscar cidade"]
        XCTAssertTrue(searchField.waitForExistence(timeout: 3))

        searchField.tap()
        searchField.typeText("São Paulo")
        app.keyboards.buttons["Buscar"].tap()

        // Aguarda resultado aparecer
        let temperatureLabel = app.staticTexts.matching(
            NSPredicate(format: "label MATCHES '\\d+°'")
        ).firstMatch

        XCTAssertTrue(
            temperatureLabel.waitForExistence(timeout: 5),
            "Temperatura não apareceu após busca"
        )
    }

    func testCidadeInvalidaExibeMensagemDeErro() {
        app.searchFields["Buscar cidade"].tap()
        app.searchFields["Buscar cidade"].typeText("xyzxyzxyz")
        app.keyboards.buttons["Buscar"].tap()

        XCTAssertTrue(
            app.alerts["Cidade não encontrada"].waitForExistence(timeout: 5)
        )
    }
}
```

---

## Exercícios

!!! exercise "Pratique"
    1. Crie um UI test para o fluxo de onboarding (telas de boas-vindas com swipe).
    2. Implemente o Page Object Pattern para pelo menos 2 telas do seu app.
    3. Adicione `accessibilityIdentifier` a todos os elementos interativos de uma tela e escreva testes que os usem.

---

[← Unit Tests com XCTest](unit-tests.md){ .md-button }
[TDD →](tdd.md){ .md-button .md-button--primary }
