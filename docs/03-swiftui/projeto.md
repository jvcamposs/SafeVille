# Mini-Projeto: App de Lista de Tarefas

🟡 **Intermediário** · Módulo 03 · Projeto Prático

Vamos construir um app completo de To-Do usando SwiftUI com todos os conceitos do módulo: componentes, estado, navegação, listas e formulários.

---

## Funcionalidades

- ✅ Adicionar tarefas com título e prioridade
- ✅ Marcar tarefas como concluídas
- ✅ Deletar tarefas com swipe
- ✅ Filtrar por: Todas, Pendentes, Concluídas
- ✅ Ordenar por prioridade
- ✅ Contagem de tarefas pendentes na aba

---

## Modelo de dados

```swift
import SwiftUI

// MARK: - Modelo

enum Prioridade: String, CaseIterable, Identifiable, Codable {
    case baixa  = "Baixa"
    case media  = "Média"
    case alta   = "Alta"

    var id: String { rawValue }

    var cor: Color {
        switch self {
        case .baixa: return .green
        case .media: return .orange
        case .alta:  return .red
        }
    }

    var icone: String {
        switch self {
        case .baixa: return "arrow.down.circle"
        case .media: return "minus.circle"
        case .alta:  return "exclamationmark.circle.fill"
        }
    }
}

struct Tarefa: Identifiable, Equatable {
    let id = UUID()
    var titulo: String
    var prioridade: Prioridade
    var concluida: Bool = false
    var dataCriacao = Date()
}
```

---

## ViewModel

```swift
// MARK: - ViewModel

enum FiltroTarefa: String, CaseIterable {
    case todas     = "Todas"
    case pendentes = "Pendentes"
    case concluidas = "Concluídas"
}

@Observable
class TarefaViewModel {
    var tarefas: [Tarefa] = [
        Tarefa(titulo: "Estudar SwiftUI",     prioridade: .alta),
        Tarefa(titulo: "Fazer exercícios",    prioridade: .media),
        Tarefa(titulo: "Ler documentação",    prioridade: .baixa),
    ]
    var filtro: FiltroTarefa = .todas
    var novoTitulo     = ""
    var novaPrioridade = Prioridade.media

    var tarefasFiltradas: [Tarefa] {
        let base: [Tarefa]
        switch filtro {
        case .todas:      base = tarefas
        case .pendentes:  base = tarefas.filter { !$0.concluida }
        case .concluidas: base = tarefas.filter { $0.concluida  }
        }
        return base.sorted { a, b in
            if a.concluida != b.concluida { return !a.concluida }
            let order: [Prioridade] = [.alta, .media, .baixa]
            return (order.firstIndex(of: a.prioridade) ?? 0) <
                   (order.firstIndex(of: b.prioridade) ?? 0)
        }
    }

    var quantidadePendentes: Int {
        tarefas.filter { !$0.concluida }.count
    }

    func adicionar() {
        guard !novoTitulo.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        tarefas.append(Tarefa(titulo: novoTitulo, prioridade: novaPrioridade))
        novoTitulo = ""
    }

    func toggleConcluida(_ tarefa: Tarefa) {
        guard let index = tarefas.firstIndex(of: tarefa) else { return }
        tarefas[index].concluida.toggle()
    }

    func deletar(em offsets: IndexSet) {
        let ids = offsets.map { tarefasFiltradas[$0].id }
        tarefas.removeAll { ids.contains($0.id) }
    }
}
```

---

## View principal

```swift
// MARK: - Views

struct ContentView: View {
    @State private var vm = TarefaViewModel()
    @State private var mostrarFormulario = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Filtro
                Picker("Filtro", selection: $vm.filtro) {
                    ForEach(FiltroTarefa.allCases, id: \.self) {
                        Text($0.rawValue)
                    }
                }
                .pickerStyle(.segmented)
                .padding()

                // Lista
                List {
                    ForEach(vm.tarefasFiltradas) { tarefa in
                        TarefaRow(tarefa: tarefa) {
                            vm.toggleConcluida(tarefa)
                        }
                        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                            Button("Deletar", role: .destructive) {
                                vm.tarefas.removeAll { $0.id == tarefa.id }
                            }
                        }
                    }

                    if vm.tarefasFiltradas.isEmpty {
                        ContentUnavailableView(
                            "Nenhuma tarefa",
                            systemImage: "checkmark.circle",
                            description: Text("Adicione tarefas usando o botão +")
                        )
                        .listRowBackground(Color.clear)
                    }
                }
                .listStyle(.plain)
            }
            .navigationTitle("Tarefas")
            .badge(vm.quantidadePendentes)
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        mostrarFormulario = true
                    } label: {
                        Image(systemName: "plus.circle.fill")
                            .font(.title2)
                    }
                }
            }
            .sheet(isPresented: $mostrarFormulario) {
                FormularioTarefaView(vm: vm)
                    .presentationDetents([.medium])
            }
        }
    }
}
```

---

## Componentes de suporte

```swift
// Row individual da lista
struct TarefaRow: View {
    let tarefa: Tarefa
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            Button(action: onToggle) {
                Image(systemName: tarefa.concluida
                      ? "checkmark.circle.fill"
                      : "circle")
                    .font(.title2)
                    .foregroundStyle(tarefa.concluida ? .green : .secondary)
            }
            .buttonStyle(.plain)

            VStack(alignment: .leading, spacing: 4) {
                Text(tarefa.titulo)
                    .strikethrough(tarefa.concluida)
                    .foregroundStyle(tarefa.concluida ? .secondary : .primary)

                Label(tarefa.prioridade.rawValue,
                      systemImage: tarefa.prioridade.icone)
                    .font(.caption)
                    .foregroundStyle(tarefa.prioridade.cor)
            }

            Spacer()
        }
        .padding(.vertical, 4)
    }
}

// Formulário de nova tarefa
struct FormularioTarefaView: View {
    var vm: TarefaViewModel
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationStack {
            Form {
                Section("Nova Tarefa") {
                    TextField("Título da tarefa", text: $vm.novoTitulo)

                    Picker("Prioridade", selection: $vm.novaPrioridade) {
                        ForEach(Prioridade.allCases) { p in
                            Label(p.rawValue, systemImage: p.icone)
                                .foregroundStyle(p.cor)
                                .tag(p)
                        }
                    }
                }
            }
            .navigationTitle("Nova Tarefa")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Adicionar") {
                        vm.adicionar()
                        dismiss()
                    }
                    .disabled(vm.novoTitulo.isEmpty)
                }
            }
        }
    }
}
```

---

## Conceitos praticados

| Conceito | Onde aparece |
|---|---|
| `@Observable` | `TarefaViewModel` |
| `@State` | Estado local das Views |
| `@Binding` | Não necessário (ViewModel compartilhado) |
| `@Environment(\.dismiss)` | Fechar formulário |
| `List` + `ForEach` | Lista de tarefas |
| `.swipeActions` | Deletar com swipe |
| `Picker` segmented | Filtros |
| `Form` | Formulário de criação |
| `NavigationStack` + `.sheet` | Navegação e modal |
| `ContentUnavailableView` | Estado vazio (iOS 17+) |

---

## Checklist do Módulo 03

- [x] Crio Views customizadas com composição
- [x] Gerencio estado com `@State` e `@Observable`
- [x] Compartilho estado entre Views com Bindings
- [x] Navego entre telas com `NavigationStack`
- [x] Apresento Sheets e Formulários
- [x] Construo listas dinâmicas com ações de swipe
- [x] Construí o To-Do App completo

**Próximo módulo:** [UIKit →](../04-uikit/index.md)
