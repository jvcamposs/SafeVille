# Performance e Otimização

🔴 **Avançado** · Módulo 10

Um app lento ou que consome muita bateria recebe reviews negativos. Este capítulo ensina a identificar e resolver problemas de performance com Instruments.

---

## Instruments — Visão geral

Instruments é a ferramenta de profiling da Apple, acessível via **Xcode → Product → Profile** (⌘I).

| Template | Uso |
|---|---|
| **Time Profiler** | Encontrar código lento (CPU) |
| **Allocations** | Monitorar uso de memória |
| **Leaks** | Detectar memory leaks |
| **Core Animation** | Performance de renderização/FPS |
| **Energy Log** | Consumo de bateria |

---

## Memory Leaks e Retain Cycles

```swift
// ❌ Retain cycle — ViewModel retém a View, View retém o ViewModel
class ViewModel {
    var onUpdate: (() -> Void)?   // Strong reference para closure
}

class ViewController {
    let vm = ViewModel()

    func configurar() {
        vm.onUpdate = {
            self.atualizar()      // self capturado fortemente = leak!
        }
    }
}

// ✅ Correto — weak self quebra o ciclo
class ViewController {
    let vm = ViewModel()

    func configurar() {
        vm.onUpdate = { [weak self] in        // (1)
            self?.atualizar()
        }
    }
}

// ✅ unowned — quando você tem CERTEZA que self nunca é nil
class ViewController {
    let vm = ViewModel()

    func configurar() {
        vm.onUpdate = { [unowned self] in     // (2)
            self.atualizar()
        }
    }
}

// (1) weak — self pode se tornar nil, sempre Optional
// (2) unowned — self nunca nil durante a vida do closure (crash se nil)
```

---

## Lazy Loading

```swift
// ❌ Inicializado imediatamente (mesmo que nunca usado)
class PesadaOperacao {
    let processador = GrandeProcessador()  // Caro para criar

    var dados: [DadosComplexos] = carregarTudo()  // Bloqueia a init
}

// ✅ Lazy — só cria quando acessado pela primeira vez
class PesadaOperacao {
    lazy var processador = GrandeProcessador()  // (1)

    lazy var dados: [DadosComplexos] = {        // (2)
        return carregarTudo()
    }()
}

// (1) lazy var — inicializa na primeira leitura
// (2) Closure lazy para lógica complexa
```

---

## Performance em SwiftUI

```swift
// ❌ Recalcula toda vez que qualquer @State muda
struct ListaProblematica: View {
    @State private var contador = 0
    let itens: [Item]  // Array grande

    var body: some View {
        List(itens) { item in
            ItemView(item: item)   // Re-renderiza TODOS quando contador muda
        }
        Button("Incrementar") { contador += 1 }
    }
}

// ✅ Extrai subviews para isolar re-renderizações
struct ListaOtimizada: View {
    @State private var contador = 0
    let itens: [Item]

    var body: some View {
        VStack {
            ContadorView(valor: contador)   // (1) Isolado
            ItemListView(itens: itens)      // (2) Não re-renderiza com contador
        }
        Button("Incrementar") { contador += 1 }
    }
}

struct ItemListView: View {
    let itens: [Item]
    var body: some View {
        List(itens) { item in ItemView(item: item) }
    }
}

// (1) Só este componente re-renderiza quando contador muda
// (2) Não é afetado pela mudança de contador
```

---

## Imagens e cache

```swift
// ❌ Carrega imagem gigante e redimensiona na UI
Image("foto-4k")
    .resizable()
    .frame(width: 50, height: 50)   // Processamento desnecessário

// ✅ AsyncImage com cache automático
AsyncImage(url: URL(string: urlString)) { phase in
    switch phase {
    case .empty:
        ProgressView()
    case .success(let image):
        image.resizable().scaledToFill()
    case .failure:
        Image(systemName: "photo")
    @unknown default:
        EmptyView()
    }
}
.frame(width: 50, height: 50)
.clipShape(Circle())

// Para controle avançado, use SDWebImageSwiftUI ou Kingfisher
```

---

## Checklist

- [x] Uso Instruments para encontrar problemas de performance
- [x] Identifico e corrijo retain cycles com `[weak self]`
- [x] Uso `lazy` para propriedades pesadas
- [x] Extraio subviews para otimizar re-renderizações no SwiftUI
- [x] Carrego imagens de forma assíncrona com cache
