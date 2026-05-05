# UserDefaults

🟡 **Intermediário** · Módulo 06

`UserDefaults` é o mecanismo mais simples de persistência do iOS. Pense nele como um dicionário persistente — você armazena pares chave/valor que sobrevivem ao fechamento do app. É ideal para preferências do usuário, configurações e pequenas quantidades de dados.

---

## O que é UserDefaults?

`UserDefaults` é uma interface para um banco de dados de propriedades (`.plist`) armazenado no sandbox do app. O sistema gerencia automaticamente a leitura e escrita no disco.

!!! info "Onde os dados ficam?"
    O arquivo `.plist` fica em:
    ```
    ~/Library/Containers/<bundle-id>/Data/Library/Preferences/<bundle-id>.plist
    ```
    No simulador, você pode inspecionar esse arquivo diretamente.

!!! warning "Não use UserDefaults para:"
    - Grandes volumes de dados (imagens, arquivos, listas longas)
    - Dados sensíveis como senhas ou tokens (use o **Keychain**)
    - Dados relacionais complexos (use **Core Data** ou **SwiftData**)
    - Listas de objetos que precisam ser filtradas ou ordenadas eficientemente

---

## Lendo e escrevendo tipos básicos

`UserDefaults` suporta nativamente: `Bool`, `Int`, `Double`, `Float`, `String`, `Data`, `Date`, `URL`, `Array`, e `Dictionary`.

```swift
import Foundation

// Obtendo a instância padrão
let defaults = UserDefaults.standard

// --- ESCRITA ---
defaults.set(true, forKey: "notificacoesAtivadas")    // (1)
defaults.set(42, forKey: "idadeUsuario")
defaults.set("Ana", forKey: "nomeUsuario")
defaults.set(3.14, forKey: "valorPrecisao")
defaults.set(Date(), forKey: "ultimoAcesso")
defaults.set(["swift", "ios"], forKey: "interesses")  // (2)

// --- LEITURA ---
let notificacoes = defaults.bool(forKey: "notificacoesAtivadas") // true
let idade = defaults.integer(forKey: "idadeUsuario")             // 42
let nome = defaults.string(forKey: "nomeUsuario") ?? "Visitante" // (3)
let precisao = defaults.double(forKey: "valorPrecisao")
let interesses = defaults.stringArray(forKey: "interesses") ?? []

// --- REMOÇÃO ---
defaults.removeObject(forKey: "idadeUsuario") // (4)
```

1. Não é necessário chamar `synchronize()` — o iOS sincroniza automaticamente.
2. Arrays e Dicionários são suportados, mas todos os elementos devem ser tipos compatíveis.
3. `string(forKey:)` retorna `String?` — sempre trate o optional.
4. Remove a chave completamente do store.

!!! tip "Valores padrão"
    Para tipos não-opcionais como `Bool` e `Int`, `UserDefaults` retorna `false` e `0` quando a chave não existe. Use `object(forKey:)` se precisar distinguir entre "não definido" e o valor padrão.

    ```swift
    // Verificar se a chave existe
    if defaults.object(forKey: "modoEscuro") == nil {
        print("Preferência ainda não foi definida")
    }
    ```

---

## Objetos customizados com Codable

Para salvar structs ou classes customizadas, use `Codable` + `JSONEncoder`/`JSONDecoder`:

```swift
// 1. Defina seu modelo com Codable
struct ConfiguracaoUsuario: Codable {
    var tema: String
    var tamanhoFonte: Int
    var notificacoesAtivadas: Bool
    var idioma: String
}

// 2. Salvar
func salvarConfiguracao(_ config: ConfiguracaoUsuario) {
    do {
        let data = try JSONEncoder().encode(config) // (1)
        UserDefaults.standard.set(data, forKey: "configuracaoUsuario")
    } catch {
        print("Erro ao salvar configuração: \(error)")
    }
}

// 3. Carregar
func carregarConfiguracao() -> ConfiguracaoUsuario? {
    guard let data = UserDefaults.standard.data(forKey: "configuracaoUsuario") else {
        return nil // (2)
    }
    do {
        return try JSONDecoder().decode(ConfiguracaoUsuario.self, from: data)
    } catch {
        print("Erro ao carregar configuração: \(error)")
        return nil
    }
}

// Uso
let config = ConfiguracaoUsuario(
    tema: "escuro",
    tamanhoFonte: 16,
    notificacoesAtivadas: true,
    idioma: "pt-BR"
)
salvarConfiguracao(config)

if let configuracao = carregarConfiguracao() {
    print("Tema: \(configuracao.tema)") // "escuro"
}
```

1. `JSONEncoder` transforma o objeto em `Data`, que `UserDefaults` sabe armazenar.
2. Retorna `nil` se a chave não existir — primeira execução do app, por exemplo.

---

## Property Wrappers com @AppStorage (SwiftUI)

O SwiftUI oferece `@AppStorage`, um property wrapper que conecta diretamente uma propriedade ao `UserDefaults`, provocando atualização da view automaticamente quando o valor muda.

```swift
import SwiftUI

struct ConfiguracoesView: View {
    // (1) Liga diretamente ao UserDefaults.standard
    @AppStorage("modoEscuro") private var modoEscuro = false
    @AppStorage("tamanhoFonte") private var tamanhoFonte = 14
    @AppStorage("nomeUsuario") private var nomeUsuario = ""

    var body: some View {
        Form {
            Section("Aparência") {
                Toggle("Modo Escuro", isOn: $modoEscuro) // (2)

                Stepper("Fonte: \(tamanhoFonte)pt",
                        value: $tamanhoFonte,
                        in: 10...24)
            }

            Section("Perfil") {
                TextField("Seu nome", text: $nomeUsuario)
            }

            Section("Valores atuais") {
                Text("Modo escuro: \(modoEscuro ? "Ativado" : "Desativado")")
                Text("Fonte: \(tamanhoFonte)pt")
                    .font(.system(size: CGFloat(tamanhoFonte)))
            }
        }
        .navigationTitle("Configurações")
    }
}
```

1. O primeiro parâmetro é a chave no `UserDefaults`. O segundo argumento é o valor padrão.
2. Quando o `Toggle` muda, `modoEscuro` é atualizado no `UserDefaults` automaticamente — sem chamar `set(_:forKey:)` manualmente.

!!! tip "Tipos suportados por @AppStorage"
    `@AppStorage` suporta: `Bool`, `Int`, `Double`, `String`, `URL` e `Data`. Para enums, conforme-os ao protocolo `RawRepresentable` com `RawValue` sendo um tipo suportado.

    ```swift
    enum Tema: String {
        case claro, escuro, sistema
    }

    extension Tema: RawRepresentable {} // já é, pois tem RawValue: String

    @AppStorage("tema") private var tema: Tema = .sistema // ✅ funciona!
    ```

---

## @AppStorage na prática — App com tema persistente

```swift
// Chaves centralizadas (evita typos)
enum UserDefaultsKeys {
    static let tema = "tema"
    static let nomeUsuario = "nomeUsuario"
    static let onboardingConcluido = "onboardingConcluido"
}

// ViewModel que lê/escreve UserDefaults
class ConfiguracoesViewModel: ObservableObject {
    @AppStorage(UserDefaultsKeys.tema) var temaEscuro = false
    @AppStorage(UserDefaultsKeys.nomeUsuario) var nomeUsuario = ""
    @AppStorage(UserDefaultsKeys.onboardingConcluido) var onboardingConcluido = false

    func resetarPreferencias() {
        temaEscuro = false
        nomeUsuario = ""
        // onboardingConcluido permanece — não queremos mostrar onboarding novamente
    }
}

// View principal
struct ContentView: View {
    @StateObject private var vm = ConfiguracoesViewModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("Olá, \(vm.nomeUsuario.isEmpty ? "visitante" : vm.nomeUsuario)!")
                    .font(.title)

                NavigationLink("⚙️ Configurações") {
                    ConfiguracoesView()
                        .environmentObject(vm)
                }
            }
        }
        .preferredColorScheme(vm.temaEscuro ? .dark : .light) // (1)
    }
}
```

1. `preferredColorScheme` lê o valor em tempo real — quando o usuário muda o tema em configurações, a view raiz se atualiza imediatamente.

---

## Limitações e quando NÃO usar

!!! danger "Limites do UserDefaults"

    **Tamanho**: Embora não haja um limite documentado, a Apple recomenda manter os dados pequenos. O arquivo `.plist` inteiro é carregado em memória na inicialização do app.

    **Segurança**: Os dados são armazenados em texto simples (não criptografado). Nunca armazene senhas, tokens de autenticação ou dados sensíveis no `UserDefaults`.

    **Consultas**: Não há como filtrar ou fazer queries. Você carrega todos os dados ou nada.

    **Concorrência**: `UserDefaults` é thread-safe para leitura/escrita, mas operações em batch podem ter comportamentos inesperados.

=== "✅ Use UserDefaults para"

    - Preferência de idioma selecionado pelo usuário
    - Estado do onboarding (`onboardingConcluido: Bool`)
    - Última aba visitada
    - Configurações de notificação
    - Contador de lançamentos do app
    - Valor de filtro padrão (ex: "ordenar por data")

=== "❌ NÃO use UserDefaults para"

    - Lista de tarefas, contatos, produtos (use SwiftData/Core Data)
    - Imagens ou arquivos binários (use FileManager)
    - Senhas ou tokens (use Keychain)
    - Dados que precisam de busca eficiente
    - Dados compartilhados entre usuários (use servidor remoto)

---

## UserDefaults Suites (compartilhamento entre app e extensão)

Para compartilhar dados entre o app principal e uma extensão (widget, share extension), use um **App Group**:

```swift
// 1. No Xcode: Signing & Capabilities → App Groups → adicionar grupo
// Nome do grupo: group.com.suaempresa.seuapp

// 2. Use a suite compartilhada
let suiteName = "group.com.suaempresa.seuapp"
let sharedDefaults = UserDefaults(suiteName: suiteName)! // (1)

// App principal escreve
sharedDefaults.set("Comprar leite", forKey: "ultimaTarefa")

// Widget lê (mesmo suiteName)
let tarefa = sharedDefaults.string(forKey: "ultimaTarefa")
```

1. `UserDefaults(suiteName:)` retorna `Optional` — pode falhar se o App Group não estiver configurado. Em produção, trate esse erro adequadamente.

!!! info "App Groups"
    Para usar suites compartilhadas você precisa:

    1. Ativar a capability **App Groups** no target principal
    2. Ativar a mesma capability na extensão
    3. Selecionar o mesmo grupo em ambos
    4. Usar o mesmo `suiteName` em ambos os lados

---

## Unit Testing com UserDefaults

Nunca use `UserDefaults.standard` diretamente em código testável. Injete a dependência para poder usar uma instância isolada nos testes:

```swift
// Protocolo para abstração (permite mock)
protocol PreferencesStorage {
    func set(_ value: Any?, forKey key: String)
    func bool(forKey key: String) -> Bool
    func string(forKey key: String) -> String?
    func removeObject(forKey key: String)
}

// UserDefaults conforma ao protocolo
extension UserDefaults: PreferencesStorage {}

// ViewModel com injeção de dependência
class OnboardingViewModel: ObservableObject {
    private let storage: PreferencesStorage
    @Published var concluido: Bool = false

    init(storage: PreferencesStorage = UserDefaults.standard) { // (1)
        self.storage = storage
        self.concluido = storage.bool(forKey: "onboardingConcluido")
    }

    func concluirOnboarding() {
        storage.set(true, forKey: "onboardingConcluido")
        concluido = true
    }
}
```

1. O valor padrão garante que o app real usa `UserDefaults.standard`, mas os testes podem passar uma implementação diferente.

```swift
// Nos testes unitários
import XCTest

class OnboardingViewModelTests: XCTestCase {

    // UserDefaults com suite de teste isolada
    var testDefaults: UserDefaults!

    override func setUp() {
        super.setUp()
        // Suite única por teste — evita contaminação entre testes
        testDefaults = UserDefaults(suiteName: "TestSuite-\(UUID())")!
    }

    override func tearDown() {
        // Limpa tudo após cada teste
        testDefaults.removePersistentDomain(forName: testDefaults.description)
        super.tearDown()
    }

    func testConcluirOnboarding() {
        let vm = OnboardingViewModel(storage: testDefaults)
        XCTAssertFalse(vm.concluido)

        vm.concluirOnboarding()

        XCTAssertTrue(vm.concluido)
        XCTAssertTrue(testDefaults.bool(forKey: "onboardingConcluido"))
    }

    func testEstadoInicialSemDadosSalvos() {
        let vm = OnboardingViewModel(storage: testDefaults)
        XCTAssertFalse(vm.concluido, "Onboarding não deve estar concluído na primeira execução")
    }
}
```

!!! success "Boas práticas de teste com UserDefaults"
    - Use uma suite com UUID único por teste para isolamento total
    - Remova o domínio no `tearDown` para não vazar estado entre testes
    - Injete `PreferencesStorage` pelo `init` em vez de usar `UserDefaults.standard` diretamente
    - Nunca use `UserDefaults.standard` nos testes — pode afetar o app real no simulador

---

## Resumo

!!! abstract "O que aprendemos"
    - `UserDefaults` é um dicionário persistente para dados simples
    - Suporta tipos primitivos nativamente; use `Codable` para objetos complexos
    - `@AppStorage` no SwiftUI conecta propriedades diretamente ao `UserDefaults`
    - Para dados sensíveis, use o Keychain; para listas e relacionamentos, use SwiftData
    - App Groups permitem compartilhar dados entre o app e suas extensões
    - Injete `UserDefaults` como dependência para facilitar testes unitários

---

[:octicons-arrow-right-24: Próximo: Core Data](coredata.md){ .md-button .md-button--primary }
