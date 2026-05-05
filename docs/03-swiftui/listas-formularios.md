# Listas e Formulários no SwiftUI

🟡 **Intermediário** · Módulo 03

`List` e `Form` são os componentes de layout baseados em tabela do SwiftUI. Simples de usar e poderosos o suficiente para a maioria dos apps.

---

## List básica

```swift
struct Produto: Identifiable {   // (1)
    let id = UUID()
    var nome: String
    var preco: Double
}

struct ListaProdutos: View {
    let produtos = [
        Produto(nome: "MacBook Pro",  preco: 12_999),
        Produto(nome: "iPhone 15",    preco: 7_499),
        Produto(nome: "AirPods Pro",  preco: 2_099),
    ]

    var body: some View {
        List(produtos) { produto in          // (2)
            HStack {
                Image(systemName: "laptopcomputer")
                    .foregroundStyle(.blue)
                Text(produto.nome)
                Spacer()
                Text(produto.preco, format: .currency(code: "BRL"))
                    .foregroundStyle(.secondary)
            }
        }
        .listStyle(.insetGrouped)            // (3)
    }
}

// (1) Identifiable fornece id único para cada item
// (2) List aceita coleção diretamente se for Identifiable
// (3) Estilos: .plain, .grouped, .insetGrouped, .sidebar
```

---

## Swipe actions e deleção

```swift
struct TarefasView: View {
    @State private var tarefas = ["Comprar leite", "Academia", "Estudar Swift"]

    var body: some View {
        List {
            ForEach(tarefas, id: \.self) { tarefa in
                Text(tarefa)
                    .swipeActions(edge: .trailing) {              // (1)
                        Button("Deletar", role: .destructive) {
                            tarefas.removeAll { $0 == tarefa }
                        }
                    }
                    .swipeActions(edge: .leading) {
                        Button("Concluir") {
                            // marcar como concluída
                        }
                        .tint(.green)
                    }
            }
            .onDelete { offsets in                                // (2)
                tarefas.remove(atOffsets: offsets)
            }
            .onMove { from, to in                                 // (3)
                tarefas.move(fromOffsets: from, toOffset: to)
            }
        }
        .toolbar {
            EditButton()                                          // (4)
        }
    }
}

// (1) Ações de swipe customizadas
// (2) onDelete habilita o swipe padrão de deletar
// (3) onMove habilita reordenação
// (4) EditButton alterna o modo de edição
```

---

## Sections e Headers

```swift
struct AgendaView: View {
    let hoje     = ["Reunião 10h", "Almoço 12h"]
    let amanha   = ["Workshop 9h", "Call 15h"]
    let semana   = ["Dentista Quarta", "Viagem Sexta"]

    var body: some View {
        List {
            Section("Hoje") {
                ForEach(hoje, id: \.self) { Text($0) }
            }
            Section("Amanhã") {
                ForEach(amanha, id: \.self) { Text($0) }
            }
            Section {
                ForEach(semana, id: \.self) { Text($0) }
            } header: {
                Label("Esta semana", systemImage: "calendar")
            } footer: {
                Text("Horários sujeitos a alteração")
                    .font(.caption)
            }
        }
    }
}
```

---

## Form

Ideal para configurações e entrada de dados:

```swift
struct CadastroView: View {
    @State private var nome        = ""
    @State private var email       = ""
    @State private var idade       = 18
    @State private var notificacoes = true
    @State private var tema        = "Claro"
    @State private var nascimento  = Date()

    let temas = ["Claro", "Escuro", "Sistema"]

    var body: some View {
        Form {
            Section("Dados pessoais") {
                TextField("Nome completo", text: $nome)
                    .textContentType(.name)
                TextField("E-mail", text: $email)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                Stepper("Idade: \(idade)", value: $idade, in: 1...120)
                DatePicker("Nascimento", selection: $nascimento,
                           displayedComponents: .date)
            }

            Section("Preferências") {
                Toggle("Receber notificações", isOn: $notificacoes)
                Picker("Tema", selection: $tema) {
                    ForEach(temas, id: \.self) { Text($0) }
                }
            }

            Section {
                Button("Salvar") { salvar() }
                    .frame(maxWidth: .infinity)
                    .disabled(nome.isEmpty || email.isEmpty) // (1)
            }
        }
        .navigationTitle("Cadastro")
    }

    func salvar() {
        print("Salvando: \(nome), \(email)")
    }
}

// (1) .disabled() desativa o botão quando a condição for true
```

---

## Searchable

Adiciona uma barra de busca integrada à List:

```swift
struct BuscaView: View {
    @State private var busca    = ""
    let frutas = ["Maçã", "Banana", "Laranja", "Uva", "Morango", "Abacaxi"]

    var frutasFiltradas: [String] {           // (1)
        busca.isEmpty ? frutas : frutas.filter { $0.localizedCaseInsensitiveContains(busca) }
    }

    var body: some View {
        NavigationStack {
            List(frutasFiltradas, id: \.self) { Text($0) }
                .navigationTitle("Frutas")
                .searchable(text: $busca,          // (2)
                            prompt: "Buscar fruta")
        }
    }
}

// (1) Filtragem computada — reage automaticamente a mudanças em 'busca'
// (2) .searchable adiciona a barra de busca nativa
```

---

## Checklist

- [x] Crio listas dinâmicas com `List` e `ForEach`
- [x] Implemento swipe actions e deleção
- [x] Uso `Section` para agrupar itens
- [x] Crio formulários com `Form` e todos os controles
- [x] Adiciono busca com `.searchable`
- [x] Habilito reordenação com `.onMove`
