# Navegação no SwiftUI

🟡 **Intermediário** · Módulo 03

A navegação evoluiu muito no SwiftUI. A partir do iOS 16, `NavigationStack` substituiu `NavigationView` com uma API muito mais poderosa e previsível.

---

## NavigationStack (iOS 16+)

```swift
struct AppPrincipal: View {
    var body: some View {
        NavigationStack {                    // (1)
            List(1...10, id: \.self) { num in
                NavigationLink("Item \(num)",
                               value: num)  // (2)
            }
            .navigationTitle("Itens")
            .navigationDestination(for: Int.self) { num in  // (3)
                DetalheView(numero: num)
            }
        }
    }
}

struct DetalheView: View {
    let numero: Int
    var body: some View {
        Text("Detalhe do item \(numero)")
            .navigationTitle("Item \(numero)")
    }
}

// (1) NavigationStack gerencia toda a pilha de navegação
// (2) value: associa um dado ao link (sem destino hardcoded)
// (3) navigationDestination conecta tipos aos destinos
```

---

## Navegação programática

```swift
struct NavProgramatica: View {
    @State private var path = NavigationPath()  // (1)

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 16) {
                Button("Ir para detalhe 42") {
                    path.append(42)             // (2)
                }
                Button("Ir para perfil") {
                    path.append("perfil")
                }
                Button("Voltar ao início") {
                    path.removeLast(path.count) // (3)
                }
            }
            .navigationDestination(for: Int.self)    { n in DetalheView(numero: n) }
            .navigationDestination(for: String.self) { s in PerfilView(id: s)     }
        }
    }
}

// (1) NavigationPath aceita múltiplos tipos
// (2) Navega programaticamente
// (3) Pop para a raiz
```

---

## Sheet e fullScreenCover

```swift
struct ExemploSheets: View {
    @State private var mostrarSheet       = false
    @State private var mostrarFullScreen  = false
    @State private var itemSelecionado: String? = nil

    var body: some View {
        VStack(spacing: 20) {
            Button("Abrir Sheet") {
                mostrarSheet = true
            }
            Button("Abrir Full Screen") {
                mostrarFullScreen = true
            }
            Button("Sheet com item") {
                itemSelecionado = "Produto selecionado"
            }
        }
        // Sheet simples
        .sheet(isPresented: $mostrarSheet) {
            SheetView()
                .presentationDetents([.medium, .large])  // (1)
        }
        // Full screen
        .fullScreenCover(isPresented: $mostrarFullScreen) {
            FullScreenView()
        }
        // Sheet com item opcional
        .sheet(item: $itemSelecionado.map { ... }) { item in
            Text(item)
        }
    }
}

struct SheetView: View {
    @Environment(\.dismiss) var dismiss  // (1)

    var body: some View {
        NavigationStack {
            Text("Conteúdo da sheet")
                .toolbar {
                    ToolbarItem(placement: .confirmationAction) {
                        Button("OK") { dismiss() }
                    }
                }
        }
    }
}

// (1) .presentationDetents define alturas parciais (iOS 16+)
```

---

## Alerts e ConfirmationDialogs

```swift
struct ExemploAlertas: View {
    @State private var mostrarAlerta    = false
    @State private var mostrarConfirmar = false
    @State private var mensagem = ""

    var body: some View {
        VStack {
            Button("Mostrar Alerta") { mostrarAlerta = true }
            Button("Confirmar ação") { mostrarConfirmar = true }
            Text(mensagem)
        }
        .alert("Título do Alerta", isPresented: $mostrarAlerta) {
            Button("OK")      { mensagem = "OK pressionado" }
            Button("Cancelar", role: .cancel) {}
        } message: {
            Text("Mensagem explicativa do alerta")
        }
        .confirmationDialog("Tem certeza?", isPresented: $mostrarConfirmar,
                            titleVisibility: .visible) {
            Button("Deletar", role: .destructive) { mensagem = "Deletado!" }
            Button("Cancelar", role: .cancel) {}
        }
    }
}
```

---

## TabView

```swift
struct AppComTabs: View {
    @State private var tabSelecionada = 0

    var body: some View {
        TabView(selection: $tabSelecionada) {
            HomeView()
                .tabItem {
                    Label("Início", systemImage: "house.fill")
                }
                .tag(0)

            BuscaView()
                .tabItem {
                    Label("Buscar", systemImage: "magnifyingglass")
                }
                .tag(1)

            PerfilView()
                .tabItem {
                    Label("Perfil", systemImage: "person.circle")
                }
                .tag(2)
        }
    }
}
```

---

## Checklist

- [x] Uso `NavigationStack` com `navigationDestination`
- [x] Implemento navegação programática com `NavigationPath`
- [x] Apresento sheets e full screen covers
- [x] Uso `alert` e `confirmationDialog`
- [x] Construo apps com `TabView`
- [x] Uso `@Environment(\.dismiss)` para fechar views
