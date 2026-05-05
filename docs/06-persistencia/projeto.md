# Mini-Projeto: Diário Pessoal com SwiftData

🟡 **Intermediário** · Módulo 06 · Projeto Prático

Vamos construir um diário pessoal completo usando SwiftData + SwiftUI + MVVM, com entradas que persistem entre execuções do app.

---

## Funcionalidades

- ✅ Criar entradas do diário com título, texto e humor
- ✅ Listar entradas ordenadas por data
- ✅ Editar e deletar entradas
- ✅ Filtrar por humor
- ✅ Persistência local automática com SwiftData

---

## Modelo SwiftData

```swift
import SwiftData
import Foundation

enum Humor: String, Codable, CaseIterable {
    case otimo    = "Ótimo 😄"
    case bom      = "Bom 🙂"
    case neutro   = "Neutro 😐"
    case ruim     = "Ruim 😞"
    case pessimo  = "Péssimo 😔"

    var emoji: String { rawValue.components(separatedBy: " ").last ?? "" }
    var cor: String {
        switch self {
        case .otimo:   return "green"
        case .bom:     return "teal"
        case .neutro:  return "gray"
        case .ruim:    return "orange"
        case .pessimo: return "red"
        }
    }
}

@Model
final class EntradaDiario {
    var titulo:    String
    var conteudo:  String
    var humor:     Humor
    var data:      Date
    var favorita:  Bool

    init(titulo: String = "",
         conteudo: String = "",
         humor: Humor = .neutro) {
        self.titulo   = titulo
        self.conteudo = conteudo
        self.humor    = humor
        self.data     = Date()
        self.favorita = false
    }
}
```

---

## Configurando o ModelContainer

```swift
// DiarioApp.swift
import SwiftUI
import SwiftData

@main
struct DiarioApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(for: EntradaDiario.self)  // (1)
    }
}

// (1) SwiftData cria e gerencia o banco automaticamente
```

---

## ViewModel

```swift
import SwiftData
import Observation

@Observable
final class DiarioViewModel {
    var filtroHumor: Humor? = nil
    var busca = ""
    var novoTitulo   = ""
    var novoConteudo = ""
    var novoHumor    = Humor.bom

    func resetFormulario() {
        novoTitulo   = ""
        novoConteudo = ""
        novoHumor    = .bom
    }

    func criarEntrada(context: ModelContext) {
        guard !novoTitulo.isEmpty else { return }
        let entrada = EntradaDiario(
            titulo: novoTitulo,
            conteudo: novoConteudo,
            humor: novoHumor
        )
        context.insert(entrada)
        resetFormulario()
    }

    func deletar(_ entrada: EntradaDiario, context: ModelContext) {
        context.delete(entrada)
    }

    func toggleFavorita(_ entrada: EntradaDiario) {
        entrada.favorita.toggle()
    }
}
```

---

## View Principal

```swift
struct ContentView: View {
    @Environment(\.modelContext) private var context
    @State private var vm = DiarioViewModel()
    @State private var mostrarForm = false

    @Query(sort: \EntradaDiario.data, order: .reverse)
    private var entradas: [EntradaDiario]

    var entradasFiltradas: [EntradaDiario] {
        entradas.filter { entrada in
            let passaHumor  = vm.filtroHumor == nil || entrada.humor == vm.filtroHumor
            let passaBusca  = vm.busca.isEmpty ||
                              entrada.titulo.localizedCaseInsensitiveContains(vm.busca)
            return passaHumor && passaBusca
        }
    }

    var body: some View {
        NavigationStack {
            List {
                ForEach(entradasFiltradas) { entrada in
                    NavigationLink {
                        DetalheEntradaView(entrada: entrada, vm: vm)
                    } label: {
                        EntradaRow(entrada: entrada)
                    }
                    .swipeActions(edge: .trailing) {
                        Button("Deletar", role: .destructive) {
                            vm.deletar(entrada, context: context)
                        }
                    }
                    .swipeActions(edge: .leading) {
                        Button {
                            vm.toggleFavorita(entrada)
                        } label: {
                            Label(entrada.favorita ? "Desfavoritar" : "Favoritar",
                                  systemImage: entrada.favorita ? "star.slash" : "star.fill")
                        }
                        .tint(.yellow)
                    }
                }
            }
            .navigationTitle("Meu Diário")
            .searchable(text: $vm.busca)
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button { mostrarForm = true } label: {
                        Image(systemName: "square.and.pencil")
                    }
                }
            }
            .sheet(isPresented: $mostrarForm) {
                NovaEntradaView(vm: vm)
                    .presentationDetents([.large])
            }
        }
    }
}

struct EntradaRow: View {
    let entrada: EntradaDiario

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(entrada.humor.emoji)
                Text(entrada.titulo)
                    .font(.headline)
                Spacer()
                if entrada.favorita {
                    Image(systemName: "star.fill")
                        .foregroundStyle(.yellow)
                        .font(.caption)
                }
            }
            Text(entrada.data, format: .dateTime.day().month(.wide).year())
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }
}
```

---

## Checklist do Módulo 06

- [x] Sei usar UserDefaults para dados simples com `@AppStorage`
- [x] Entendo Core Data (NSPersistentContainer, NSManagedObject)
- [x] Uso SwiftData com `@Model`, `@Query`, `ModelContext`
- [x] Sei a diferença entre UserDefaults, Core Data e SwiftData
- [x] Construí o Diário Pessoal com SwiftData

**Próximo módulo:** [Networking →](../07-networking/index.md)
