# SwiftData (Swift 5.9+)

🟡 **Intermediário** · Módulo 06

**SwiftData** é o framework moderno de persistência da Apple, introduzido na WWDC23. Ele substitui o Core Data com uma API nativa em Swift, usando macros para eliminar o boilerplate e integração total com SwiftUI. É o caminho recomendado para novos projetos no iOS 17+.

!!! info "Requisitos"
    - **iOS 17+** / macOS 14+
    - **Xcode 15+**
    - **Swift 5.9+**

---

## O que é SwiftData?

SwiftData permite descrever seu modelo de dados diretamente em Swift, sem precisar de arquivos `.xcdatamodeld` ou subclasses de `NSManagedObject`. A macro `@Model` transforma uma classe Swift comum em um modelo persistível.

```swift
// Core Data (antes)
@objc(Tarefa)
class Tarefa: NSManagedObject {
    @NSManaged var titulo: String?
    @NSManaged var criadoEm: Date?
    @NSManaged var concluida: Bool
}

// SwiftData (agora) ✨
import SwiftData

@Model
class Tarefa {
    var titulo: String
    var criadoEm: Date
    var concluida: Bool

    init(titulo: String) {
        self.titulo = titulo
        self.criadoEm = Date()
        self.concluida = false
    }
}
```

!!! success "Vantagens do SwiftData"
    - Sem arquivos `.xcdatamodeld` — o schema vive no código Swift
    - Sem `NSManagedObject` — use classes Swift normais
    - Type-safe por padrão — sem opcionais desnecessários
    - Integração nativa com SwiftUI via `@Query`
    - Suporte nativo a `async/await`
    - Migração automática para mudanças simples

---

## A macro @Model

`@Model` é o coração do SwiftData. Ela transforma uma classe Swift em um modelo persistível:

```swift
import SwiftData

@Model // (1)
final class Produto {
    var nome: String
    var preco: Double
    var quantidade: Int
    var dataCriacao: Date
    var categoria: String?
    var ativo: Bool

    // (2) Unique constraint
    @Attribute(.unique) var codigoBarras: String

    // (3) Dados grandes — armazenados separadamente
    @Attribute(.externalStorage) var imagemData: Data?

    // (4) Não persistido
    @Transient var precoFormatado: String {
        String(format: "R$ %.2f", preco)
    }

    init(nome: String, preco: Double, codigoBarras: String) {
        self.nome = nome
        self.preco = preco
        self.quantidade = 0
        self.dataCriacao = Date()
        self.codigoBarras = codigoBarras
        self.ativo = true
    }
}
```

1. `@Model` gera conformidade com `PersistentModel` e adiciona rastreamento de mudanças.
2. `@Attribute(.unique)` cria uma constraint única — inserir um produto com o mesmo código de barras lançará um erro.
3. `@Attribute(.externalStorage)` armazena `Data` grande em um arquivo separado, mantendo o banco principal leve.
4. `@Transient` marca a propriedade como não persistida — apenas computada em memória.

### Atributos disponíveis

```swift
@Model
final class Exemplo {
    @Attribute(.unique) var id: String           // Valor único
    @Attribute(.externalStorage) var foto: Data? // Arquivo externo
    @Attribute(.spotlight) var titulo: String    // Indexado no Spotlight
    @Transient var cache: String = ""            // Não persistido
}
```

---

## ModelContainer e ModelContext

### ModelContainer

`ModelContainer` é o equivalente ao `NSPersistentContainer` — ele configura o banco de dados:

```swift
import SwiftData
import SwiftUI

// Configuração básica
let container = try! ModelContainer(for: Tarefa.self)

// Configuração avançada
let schema = Schema([Tarefa.self, Categoria.self, Produto.self])
let config = ModelConfiguration(
    schema: schema,
    isStoredInMemoryOnly: false,  // (1)
    allowsSave: true
)
let container = try! ModelContainer(for: schema, configurations: config)
```

1. `isStoredInMemoryOnly: true` é ideal para testes e previews SwiftUI.

### Integração no App

```swift
@main
struct MeuApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(for: [Tarefa.self, Categoria.self]) // (2)
    }
}
```

2. `.modelContainer(for:)` cria e injeta o container em toda a hierarquia de views automaticamente.

### ModelContext

`ModelContext` é onde você realiza operações — equivalente ao `NSManagedObjectContext`:

```swift
// Em uma View SwiftUI
struct MinhaView: View {
    @Environment(\.modelContext) private var contexto // (3)

    func criarTarefa() {
        let tarefa = Tarefa(titulo: "Nova tarefa")
        contexto.insert(tarefa) // (4)
        // SwiftData faz autosave — não precisa chamar save() manualmente
    }
}
```

3. `@Environment(\.modelContext)` fornece o contexto injetado pelo `modelContainer`.
4. `insert(_:)` registra o objeto no contexto para persistência.

---

## CRUD com SwiftData

### Criar

```swift
func criarTarefa(titulo: String, descricao: String? = nil) {
    let tarefa = Tarefa(titulo: titulo)
    tarefa.descricao = descricao
    contexto.insert(tarefa) // SwiftData persiste automaticamente
}
```

!!! tip "Autosave"
    SwiftData salva automaticamente as mudanças quando necessário. Você pode salvar explicitamente com `try contexto.save()`, mas raramente é necessário.

### Ler

```swift
// Busca manual no ModelContext
let descriptor = FetchDescriptor<Tarefa>(
    predicate: #Predicate { $0.concluida == false }, // (1)
    sortBy: [SortDescriptor(\.criadoEm, order: .reverse)]
)

let tarefas = try contexto.fetch(descriptor)
```

1. `#Predicate` é type-safe e verificado em tempo de compilação — diferente do `NSPredicate` com strings.

### Atualizar

```swift
func concluirTarefa(_ tarefa: Tarefa) {
    tarefa.concluida = true // (2)
    // SwiftData rastreia a mudança automaticamente
}
```

2. Simplesmente modifique a propriedade — sem `set` manual, sem `save()` obrigatório.

### Deletar

```swift
func deletarTarefa(_ tarefa: Tarefa) {
    contexto.delete(tarefa)
}

// Deletar em batch
func deletarTarefasConcluidas() throws {
    try contexto.delete(model: Tarefa.self,
                        where: #Predicate { $0.concluida == true }) // (3)
}
```

3. Delete em batch com predicado — muito mais eficiente que buscar e deletar um a um.

---

## @Query no SwiftUI

`@Query` é o equivalente ao `@FetchRequest` — busca e observa dados automaticamente:

```swift
struct ListaTarefasView: View {
    // (1) Busca todas as tarefas, ordenadas por data
    @Query(sort: \Tarefa.criadoEm, order: .reverse)
    private var tarefas: [Tarefa]

    // Com filtro
    @Query(filter: #Predicate<Tarefa> { !$0.concluida },
           sort: \Tarefa.criadoEm,
           order: .reverse)
    private var tarefasPendentes: [Tarefa]

    @Environment(\.modelContext) private var contexto

    var body: some View {
        NavigationStack {
            List {
                ForEach(tarefas) { tarefa in
                    TarefaRow(tarefa: tarefa)
                        .swipeActions {
                            Button(role: .destructive) {
                                contexto.delete(tarefa)
                            } label: {
                                Label("Deletar", systemImage: "trash")
                            }
                        }
                }
            }
            .navigationTitle("Tarefas (\(tarefas.count))")
            .toolbar {
                Button("Adicionar", systemImage: "plus") {
                    let nova = Tarefa(titulo: "Nova tarefa")
                    contexto.insert(nova)
                }
            }
        }
    }
}
```

1. `@Query` atualiza a view automaticamente quando os dados mudam no banco.

### @Query dinâmico

Para filtros baseados em estado da UI, inicialize `@Query` no `init`:

```swift
struct TarefasFiltradas: View {
    @Query private var tarefas: [Tarefa]

    init(mostraConcluidas: Bool, categoria: String?) {
        let predicate = #Predicate<Tarefa> { tarefa in
            if let cat = categoria {
                return tarefa.categoria == cat
            }
            return true
        }

        _tarefas = Query( // (2)
            filter: predicate,
            sort: \Tarefa.criadoEm,
            order: .reverse
        )
    }

    var body: some View {
        List(tarefas) { tarefa in
            Text(tarefa.titulo)
        }
    }
}
```

2. `_tarefas` acessa o `Query` subjacente para inicializá-lo com parâmetros dinâmicos.

---

## Relacionamentos em SwiftData

```swift
@Model
final class Categoria {
    var nome: String
    var cor: String

    @Relationship(deleteRule: .cascade)  // (1)
    var tarefas: [Tarefa] = []

    init(nome: String, cor: String = "azul") {
        self.nome = nome
        self.cor = cor
    }
}

@Model
final class Tarefa {
    var titulo: String
    var criadoEm: Date
    var concluida: Bool

    var categoria: Categoria? // (2) Inverso do relacionamento

    init(titulo: String) {
        self.titulo = titulo
        self.criadoEm = Date()
        self.concluida = false
    }
}
```

1. `deleteRule: .cascade` — deletar a categoria deleta todas as suas tarefas.
2. SwiftData infere automaticamente o relacionamento inverso. O `@Relationship` é necessário apenas quando você precisa de configurações especiais.

```swift
// Usando relacionamentos
let trabalho = Categoria(nome: "Trabalho", cor: "azul")
let pessoal = Categoria(nome: "Pessoal", cor: "verde")
contexto.insert(trabalho)
contexto.insert(pessoal)

let reuniao = Tarefa(titulo: "Reunião às 14h")
reuniao.categoria = trabalho // (3)
contexto.insert(reuniao)

// Acessar tarefas da categoria
let tarefasDeTrabalho = trabalho.tarefas // [Tarefa]
```

3. Basta atribuir a referência — o SwiftData gerencia o relacionamento bidirecional.

---

## Migração de Core Data para SwiftData

=== "Passo 1 — Modelo paralelo"

    Crie os modelos SwiftData correspondentes às suas entidades Core Data:

    ```swift
    // Core Data (existente)
    // Entidade: Note
    // Atributos: id (UUID), text (String), date (Date)

    // SwiftData (novo)
    @Model
    final class Note {
        var id: UUID
        var text: String
        var date: Date

        init(text: String) {
            self.id = UUID()
            self.text = text
            self.date = Date()
        }
    }
    ```

=== "Passo 2 — Migrar dados"

    ```swift
    func migrarDoCoreData(containerCD: NSPersistentContainer,
                          contextoSD: ModelContext) {
        let request = NSFetchRequest<NSManagedObject>(entityName: "Note")
        guard let notesCD = try? containerCD.viewContext.fetch(request) else { return }

        for noteCD in notesCD {
            let note = Note(text: noteCD.value(forKey: "text") as? String ?? "")
            note.id = noteCD.value(forKey: "id") as? UUID ?? UUID()
            note.date = noteCD.value(forKey: "date") as? Date ?? Date()
            contextoSD.insert(note)
        }
        try? contextoSD.save()
    }
    ```

=== "Passo 3 — Verificar e limpar"

    Após confirmar que a migração funcionou, remova o stack Core Data do projeto.

### Migração de schema no SwiftData

Para mudanças evolutivas no modelo SwiftData:

```swift
// Versão 1 (original)
@Model
final class Tarefa {
    var titulo: String
    var criadoEm: Date
}

// Versão 2 (adicionando campo)
// Para lightweight migration, apenas adicione a propriedade com valor padrão:
@Model
final class Tarefa {
    var titulo: String
    var criadoEm: Date
    var prioridade: Int = 0  // (1) Novo campo com valor padrão
}
```

1. Adicionar uma propriedade com valor padrão é uma lightweight migration automática — o SwiftData lida sem configuração extra.

---

## Filtrando e Ordenando com #Predicate

`#Predicate` é completamente type-safe — o compilador verifica os predicados em tempo de compilação:

```swift
// Busca type-safe
let descriptor = FetchDescriptor<Tarefa>(
    predicate: #Predicate<Tarefa> {
        $0.titulo.localizedStandardContains("reunião") // (1)
        && !$0.concluida
    },
    sortBy: [
        SortDescriptor(\.prioridade, order: .reverse),
        SortDescriptor(\.criadoEm, order: .reverse)
    ]
)

// Múltiplos critérios de sort
let descriptor2 = FetchDescriptor<Produto>(
    sortBy: [
        SortDescriptor(\.categoria),
        SortDescriptor(\.nome)
    ]
)

// Limitar resultados
var descriptor3 = FetchDescriptor<Tarefa>()
descriptor3.fetchLimit = 10
descriptor3.fetchOffset = 0
```

1. `localizedStandardContains` faz busca case/diacritic insensitive — equivalente a `CONTAINS[cd]` no NSPredicate.

!!! warning "Limitações do #Predicate"
    `#Predicate` é poderoso mas tem algumas limitações — nem todas as operações Swift são suportadas. Se encontrar um erro de compilação, simplifique o predicado ou use uma abordagem de filtragem em duas etapas (busca geral + filtro em memória).

---

## SwiftData + MVVM

```swift
// ViewModel independente da View
@Observable // (1)
class TarefaViewModel {
    var tarefas: [Tarefa] = []
    var erro: String?

    private var contexto: ModelContext

    init(contexto: ModelContext) {
        self.contexto = contexto
        carregarTarefas()
    }

    func carregarTarefas() {
        do {
            let descriptor = FetchDescriptor<Tarefa>(
                sortBy: [SortDescriptor(\.criadoEm, order: .reverse)]
            )
            tarefas = try contexto.fetch(descriptor)
        } catch {
            self.erro = error.localizedDescription
        }
    }

    func adicionarTarefa(titulo: String) {
        let tarefa = Tarefa(titulo: titulo)
        contexto.insert(tarefa)
        carregarTarefas()
    }

    func deletarTarefa(_ tarefa: Tarefa) {
        contexto.delete(tarefa)
        carregarTarefas()
    }

    func concluirTarefa(_ tarefa: Tarefa) {
        tarefa.concluida = true
        // SwiftData rastreia automaticamente
    }
}

// View que usa o ViewModel
struct TarefasViewMVVM: View {
    @Environment(\.modelContext) private var contexto
    @State private var vm: TarefaViewModel?
    @State private var novoTitulo = ""

    var body: some View {
        List {
            ForEach(vm?.tarefas ?? []) { tarefa in
                HStack {
                    Image(systemName: tarefa.concluida ? "checkmark.circle.fill" : "circle")
                        .onTapGesture { vm?.concluirTarefa(tarefa) }
                    Text(tarefa.titulo)
                        .strikethrough(tarefa.concluida)
                }
            }
            .onDelete { offsets in
                offsets.forEach { vm?.deletarTarefa(vm!.tarefas[$0]) }
            }
        }
        .onAppear {
            if vm == nil {
                vm = TarefaViewModel(contexto: contexto)
            }
        }
    }
}
```

1. `@Observable` (Swift 5.9) substitui `ObservableObject` com rastreamento automático de propriedades. Não precisa de `@Published`.

---

## Tabela Comparativa

| Característica | UserDefaults | Core Data | SwiftData |
|---|---|---|---|
| **Complexidade** | ⭐ Simples | ⭐⭐⭐ Complexo | ⭐⭐ Moderado |
| **iOS mínimo** | iOS 2+ | iOS 3+ | iOS 17+ |
| **Boilerplate** | Nenhum | Alto | Baixo |
| **Type safety** | Baixa | Média | Alta |
| **Relacionamentos** | Não | Sim | Sim |
| **Predicados** | Não | NSPredicate (string) | #Predicate (type-safe) |
| **SwiftUI nativo** | @AppStorage | @FetchRequest | @Query |
| **Concorrência** | Básica | NSManagedObjectContext | ModelActor |
| **Migration** | N/A | Manual/automática | Lightweight automática |
| **Melhor para** | Preferências | Projetos iOS 16 e abaixo | Projetos novos iOS 17+ |

---

## Resumo

!!! abstract "O que aprendemos"
    - SwiftData é a solução moderna de persistência da Apple para iOS 17+
    - `@Model` transforma classes Swift em modelos persistíveis com zero boilerplate
    - `ModelContainer` configura o banco; `ModelContext` realiza operações CRUD
    - `@Query` é o `@FetchRequest` moderno — type-safe e integrado com SwiftUI
    - `#Predicate` oferece predicados verificados em tempo de compilação
    - Lightweight migration funciona automaticamente para mudanças simples
    - Para projetos novos, SwiftData é a escolha recomendada pela Apple

---

[:octicons-arrow-right-24: Próximo: Mini-Projeto — Diário Pessoal](projeto.md){ .md-button .md-button--primary }
