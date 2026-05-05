# Estado e Bindings no SwiftUI

🟡 **Intermediário** · Módulo 03

O gerenciamento de estado é o coração do SwiftUI. Entender quando usar cada property wrapper é essencial para construir apps bem estruturados.

---

## @State — estado local da View

Use para dados simples que pertencem a uma única View:

```swift
struct ContadorView: View {
    @State private var contador = 0     // (1)
    @State private var mostrarAlerta = false

    var body: some View {
        VStack(spacing: 20) {
            Text("Contagem: \(contador)")
                .font(.largeTitle)

            HStack {
                Button("-") { contador -= 1 }
                    .buttonStyle(.bordered)
                Button("+") { contador += 1 }
                    .buttonStyle(.borderedProminent)
            }
        }
    }
}

// (1) @State deve ser sempre private — é interno à View
```

!!! tip "Regra de ouro"
    `@State` é para tipos simples (Bool, Int, String, arrays de valores). Para objetos complexos, use `@StateObject` ou `@Observable`.

---

## @Binding — compartilhando estado entre Views

Cria uma referência bidirecional ao estado de outra View:

```swift
struct ViewFilha: View {
    @Binding var isAtivo: Bool      // (1)

    var body: some View {
        Toggle("Ativar", isOn: $isAtivo)
    }
}

struct ViewPai: View {
    @State private var ativado = false

    var body: some View {
        VStack {
            ViewFilha(isAtivo: $ativado)    // (2)
            Text(ativado ? "Ativo ✅" : "Inativo ❌")
        }
    }
}

// (1) @Binding recebe a referência — não possui o valor
// (2) $ converte @State em Binding<Bool>
```

---

## @Observable — Swift 5.9+ (iOS 17+)

O novo padrão recomendado para ViewModels:

```swift
import Observation

@Observable                          // (1)
class TaskViewModel {
    var tasks: [String] = []
    var novaTarefa = ""
    var isLoading = false

    func adicionarTarefa() {
        guard !novaTarefa.isEmpty else { return }
        tasks.append(novaTarefa)
        novaTarefa = ""
    }

    func remover(em offsets: IndexSet) {
        tasks.remove(atOffsets: offsets)
    }
}

struct TaskListView: View {
    @State private var vm = TaskViewModel()  // (2)

    var body: some View {
        List {
            ForEach(vm.tasks, id: \.self) { task in
                Text(task)
            }
            .onDelete(perform: vm.remover)
        }
        .toolbar {
            ToolbarItem {
                TextField("Nova tarefa", text: $vm.novaTarefa)  // (3)
            }
        }
    }
}

// (1) @Observable rastreia automaticamente quais propriedades são acessadas
// (2) Com @Observable, use @State para instanciar o ViewModel
// (3) $ cria um Binding para novaTarefa automaticamente
```

---

## ObservableObject — iOS 14+ (compatibilidade)

Para projetos que precisam suportar iOS 14/15:

```swift
class CarrinhoViewModel: ObservableObject {
    @Published var itens: [String] = []    // (1)
    @Published var total: Double = 0.0

    func adicionar(_ item: String) {
        itens.append(item)
    }
}

// @StateObject: a View cria e possui o objeto
struct CarrinhoView: View {
    @StateObject private var vm = CarrinhoViewModel()  // (2)

    var body: some View {
        List(vm.itens, id: \.self) { Text($0) }
        .onAppear { vm.adicionar("Produto 1") }
    }
}

// @ObservedObject: a View recebe o objeto de fora
struct DetalheCarrinhoView: View {
    @ObservedObject var vm: CarrinhoViewModel  // (3)

    var body: some View {
        Text("Total: R$ \(vm.total, format: .number)")
    }
}

// (1) @Published notifica a View ao mudar
// (2) @StateObject — a View é a "dona" do objeto
// (3) @ObservedObject — recebe o objeto, não o cria
```

---

## @EnvironmentObject — estado global compartilhado

Para dados que precisam estar disponíveis em toda a hierarquia:

```swift
class SessaoUsuario: ObservableObject {
    @Published var usuarioLogado: String?
    @Published var tema: String = "claro"
}

// Na raiz do app:
@main
struct MeuApp: App {
    @StateObject private var sessao = SessaoUsuario()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(sessao)  // (1)
        }
    }
}

// Em qualquer View filha (qualquer nível):
struct PerfilView: View {
    @EnvironmentObject var sessao: SessaoUsuario  // (2)

    var body: some View {
        Text("Olá, \(sessao.usuarioLogado ?? "visitante")")
    }
}

// (1) Injeta o objeto no ambiente
// (2) Extrai automaticamente do ambiente — sem passar manualmente
```

---

## @Environment — valores do sistema

```swift
struct ExemploEnvironment: View {
    @Environment(\.colorScheme) var esquemaDeCor     // (1)
    @Environment(\.locale)      var locale
    @Environment(\.dismiss)     var dismiss          // (2)
    @Environment(\.modelContext) var context         // SwiftData

    var body: some View {
        VStack {
            Text(esquemaDeCor == .dark ? "Modo escuro" : "Modo claro")

            Button("Fechar") { dismiss() }
        }
    }
}

// (1) Acessa propriedades do sistema (tema, tamanho de fonte, etc.)
// (2) dismiss() fecha a sheet/navigationStack atual
```

---

## Comparativo de property wrappers

| Wrapper | Proprietário | Uso |
|---|---|---|
| `@State` | A própria View | Dados simples locais |
| `@Binding` | View pai | Referência bidirecional |
| `@StateObject` | A própria View | ViewModel (ObservableObject) |
| `@ObservedObject` | Passado de fora | ViewModel recebido |
| `@EnvironmentObject` | Injetado no ambiente | Dados globais compartilhados |
| `@Observable` + `@State` | A própria View | ViewModel moderno (iOS 17+) |
| `@Environment` | Sistema | Valores do ambiente |

---

## Checklist

- [x] Uso `@State` para estado local simples
- [x] Uso `@Binding` para compartilhar estado com Views filhas
- [x] Sei a diferença entre `@StateObject` e `@ObservedObject`
- [x] Sei quando usar `@EnvironmentObject`
- [x] Conheço o novo `@Observable` do Swift 5.9+
- [x] Entendo o fluxo de dados unidirecional do SwiftUI
