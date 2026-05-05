# Mini-Projeto: Refatorando o To-Do App com MVVM

🔴 **Avançado** · Módulo 05 · Projeto Prático

Vamos pegar o app de tarefas do Módulo 03 e refatorá-lo para MVVM, mostrando exatamente o que muda e por quê.

---

## Antes vs Depois

=== "❌ Antes (tudo na View)"
    ```swift
    struct ContentView: View {
        @State private var tarefas = ["Estudar", "Academia"]
        @State private var novoTitulo = ""
        @State private var filtro = "Todas"

        var tarefasFiltradas: [String] {
            filtro == "Concluídas" ? [] : tarefas
        }

        func adicionar() {
            guard !novoTitulo.isEmpty else { return }
            tarefas.append(novoTitulo)
            novoTitulo = ""
        }

        var body: some View {
            // View misturada com lógica...
            List { ... }
        }
    }
    // Problemas: não testável, difícil de escalar
    ```

=== "✅ Depois (MVVM)"
    ```swift
    // ViewModel — testável de forma independente
    @Observable
    final class TarefaViewModel { ... }

    // View — só exibe
    struct ContentView: View {
        @State private var vm = TarefaViewModel()

        var body: some View {
            // Só exibição, sem lógica
            TarefaListView(vm: vm)
        }
    }
    ```

---

## ViewModel completo

```swift
import Foundation
import Observation

// MARK: - Modelo

struct Tarefa: Identifiable, Equatable {
    let id = UUID()
    var titulo: String
    var prioridade: Prioridade
    var concluida: Bool = false
}

enum Prioridade: String, CaseIterable, Identifiable {
    case baixa = "Baixa"
    case media = "Média"
    case alta  = "Alta"
    var id: String { rawValue }
}

enum FiltroTarefa: String, CaseIterable {
    case todas = "Todas"
    case pendentes = "Pendentes"
    case concluidas = "Concluídas"
}

// MARK: - ViewModel

@Observable
final class TarefaViewModel {

    // Estado público (a View observa isso)
    private(set) var tarefas: [Tarefa] = []
    var filtroAtual: FiltroTarefa = .todas
    var novoTitulo = ""
    var novaPrioridade = Prioridade.media
    var isLoading = false

    // Computed — derivado do estado
    var tarefasFiltradas: [Tarefa] {
        let base: [Tarefa]
        switch filtroAtual {
        case .todas:      base = tarefas
        case .pendentes:  base = tarefas.filter { !$0.concluida }
        case .concluidas: base = tarefas.filter {  $0.concluida }
        }
        return base.sorted { a, b in
            let ordemPrioridade: [Prioridade] = [.alta, .media, .baixa]
            let ia = ordemPrioridade.firstIndex(of: a.prioridade) ?? 0
            let ib = ordemPrioridade.firstIndex(of: b.prioridade) ?? 0
            if a.concluida != b.concluida { return !a.concluida }
            return ia < ib
        }
    }

    var quantidadePendentes: Int {
        tarefas.filter { !$0.concluida }.count
    }

    var podeAdicionar: Bool {
        !novoTitulo.trimmingCharacters(in: .whitespaces).isEmpty
    }

    // Intenções (ações que a View chama)
    func adicionar() {
        guard podeAdicionar else { return }
        tarefas.append(Tarefa(titulo: novoTitulo.trimmingCharacters(in: .whitespaces),
                               prioridade: novaPrioridade))
        novoTitulo = ""
        novaPrioridade = .media
    }

    func toggleConcluida(_ tarefa: Tarefa) {
        guard let i = tarefas.firstIndex(of: tarefa) else { return }
        tarefas[i].concluida.toggle()
    }

    func deletar(tarefa: Tarefa) {
        tarefas.removeAll { $0.id == tarefa.id }
    }

    func deletar(offsets: IndexSet) {
        let ids = offsets.map { tarefasFiltradas[$0].id }
        tarefas.removeAll { ids.contains($0.id) }
    }
}
```

---

## Views refatoradas

```swift
// View principal — só coordena subviews
struct ContentView: View {
    @State private var vm = TarefaViewModel()

    var body: some View {
        NavigationStack {
            TarefaListView(vm: vm)
                .navigationTitle("Tarefas (\(vm.quantidadePendentes))")
        }
    }
}

// View de lista — só exibe
struct TarefaListView: View {
    var vm: TarefaViewModel
    @State private var mostrarForm = false

    var body: some View {
        VStack(spacing: 0) {
            Picker("Filtro", selection: $vm.filtroAtual) {
                ForEach(FiltroTarefa.allCases, id: \.self) {
                    Text($0.rawValue)
                }
            }
            .pickerStyle(.segmented)
            .padding()

            List {
                ForEach(vm.tarefasFiltradas) { t in
                    TarefaRowView(tarefa: t,
                                  onToggle: { vm.toggleConcluida(t) })
                    .swipeActions {
                        Button("Deletar", role: .destructive) { vm.deletar(tarefa: t) }
                    }
                }
                .onDelete { vm.deletar(offsets: $0) }
            }
        }
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button { mostrarForm = true } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(isPresented: $mostrarForm) {
            FormularioTarefaView(vm: vm)
                .presentationDetents([.medium])
        }
    }
}
```

---

## Testando o ViewModel

```swift
import Testing

@Test func adicionarTarefaValida() {
    let vm = TarefaViewModel()
    vm.novoTitulo = "Estudar Swift"
    vm.novaPrioridade = .alta
    vm.adicionar()

    #expect(vm.tarefas.count == 1)
    #expect(vm.tarefas.first?.titulo == "Estudar Swift")
    #expect(vm.tarefas.first?.prioridade == .alta)
}

@Test func naoAdicionarTarefaVazia() {
    let vm = TarefaViewModel()
    vm.novoTitulo = "   "
    vm.adicionar()
    #expect(vm.tarefas.isEmpty)
}

@Test func filtrarTarefasPendentes() {
    let vm = TarefaViewModel()
    vm.tarefas = [
        Tarefa(titulo: "A", prioridade: .alta, concluida: false),
        Tarefa(titulo: "B", prioridade: .media, concluida: true),
    ]
    vm.filtroAtual = .pendentes
    #expect(vm.tarefasFiltradas.count == 1)
    #expect(vm.tarefasFiltradas.first?.titulo == "A")
}
```

---

## O que mudou

| Antes | Depois |
|---|---|
| Lógica na View | Lógica no ViewModel |
| `@State` para tudo | `@Observable` no ViewModel |
| Não testável | Totalmente testável |
| View reativa ao estado interno | View reativa ao ViewModel |

---

## Checklist do Módulo 05

- [x] Entendo MVC e suas limitações no iOS
- [x] Implemento MVVM com `@Observable`
- [x] Sei injetar dependências via protocolos
- [x] Conheço Clean Architecture e VIPER
- [x] Refatorei o To-Do App com MVVM e testes

**Próximo módulo:** [Persistência →](../06-persistencia/index.md)
