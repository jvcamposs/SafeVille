# Componentes SwiftUI

🟡 **Intermediário** · Módulo 03

SwiftUI é construído sobre o protocolo `View`. Tudo que aparece na tela é uma View — desde um simples texto até layouts complexos.

---

## O protocolo View

```swift
struct MeuComponente: View {
    var body: some View { // (1)
        Text("Olá, SwiftUI!")
    }
}

// (1) some View é um opaque type — o compilador sabe o tipo exato,
//     mas a API publica apenas "alguma View"
```

---

## Textos e Imagens

```swift
VStack(spacing: 16) {
    // Texto
    Text("Título Principal")
        .font(.largeTitle)
        .fontWeight(.bold)
        .foregroundStyle(.primary)

    Text("Subtítulo com quebra\nde linha")
        .font(.subheadline)
        .multilineTextAlignment(.center)
        .foregroundStyle(.secondary)

    // Imagem SF Symbols
    Image(systemName: "star.fill")         // (1)
        .font(.system(size: 40))
        .foregroundStyle(.yellow)
        .symbolEffect(.bounce)             // iOS 17+

    // Imagem do Assets
    Image("minha-foto")
        .resizable()
        .scaledToFit()
        .frame(width: 100, height: 100)
        .clipShape(Circle())
}

// (1) SF Symbols: +4000 ícones grátis da Apple
```

---

## Botões e Links

```swift
// Botão simples
Button("Clique aqui") {
    print("Botão pressionado!")
}

// Botão com label customizado
Button {
    print("Ação executada")
} label: {
    Label("Salvar", systemImage: "checkmark.circle.fill")
        .padding()
        .background(.blue)
        .foregroundStyle(.white)
        .clipShape(RoundedRectangle(cornerRadius: 10))
}

// Botão com role (destrutivo)
Button("Deletar", role: .destructive) {
    // ação de deleção
}

// Link externo
Link("Documentação Apple",
     destination: URL(string: "https://developer.apple.com")!)
    .font(.caption)
```

---

## Stacks — Layouts de empilhamento

=== "VStack"
    ```swift
    VStack(alignment: .leading, spacing: 8) {
        Text("Item 1")
        Text("Item 2")
        Text("Item 3")
    }
    ```

=== "HStack"
    ```swift
    HStack(alignment: .center, spacing: 16) {
        Image(systemName: "person")
        Text("João Silva")
        Spacer()
        Text("Admin")
            .foregroundStyle(.secondary)
    }
    ```

=== "ZStack"
    ```swift
    ZStack {
        Color.blue.opacity(0.3)
        Text("Sobre o fundo")
            .font(.title)
            .padding()
    }
    .frame(height: 100)
    .clipShape(RoundedRectangle(cornerRadius: 12))
    ```

---

## ScrollView e Lazy Stacks

```swift
// ScrollView vertical (padrão)
ScrollView {
    VStack(spacing: 12) {
        ForEach(0..<50) { i in
            Text("Item \(i)")
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.gray.opacity(0.1))
        }
    }
    .padding()
}

// LazyVStack — só renderiza o que está visível (1)
ScrollView {
    LazyVStack(spacing: 12) {
        ForEach(0..<1000) { i in
            ItemRow(numero: i)
        }
    }
}

// (1) Essencial para listas longas — economiza memória e CPU
```

---

## GeometryReader

Acessa o tamanho e posição do container pai:

```swift
GeometryReader { geo in
    Rectangle()
        .fill(.blue)
        .frame(width: geo.size.width * 0.8)  // 80% da largura disponível
        .frame(maxWidth: .infinity, alignment: .center)
}
```

!!! warning "Use GeometryReader com moderação"
    Ele pode quebrar layouts se usado sem cuidado. Prefira modificadores como `.frame(maxWidth: .infinity)` sempre que possível.

---

## Modificadores essenciais

```swift
Text("Exemplo")
    .padding(.horizontal, 16)           // espaçamento interno
    .background(.blue.opacity(0.1))     // cor de fundo
    .foregroundStyle(.blue)             // cor do texto
    .font(.headline)                    // tipografia
    .fontWeight(.semibold)
    .frame(width: 200, height: 50)      // tamanho fixo
    .frame(maxWidth: .infinity)         // expansão máxima
    .clipShape(Capsule())               // forma de recorte
    .shadow(radius: 4, y: 2)           // sombra
    .opacity(0.9)                       // transparência
    .overlay(                           // camada sobreposta
        RoundedRectangle(cornerRadius: 8)
            .stroke(.blue, lineWidth: 2)
    )
```

---

## Modificadores customizados

```swift
struct CartaoModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(16)
            .background(.background)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(color: .black.opacity(0.1), radius: 8, y: 4)
    }
}

extension View {
    func estiloDCartao() -> some View {
        modifier(CartaoModifier())
    }
}

// Uso:
Text("Texto em cartão")
    .estiloDCartao()
```

---

## SF Symbols

```swift
// Tamanho via font
Image(systemName: "heart.fill")
    .font(.system(size: 32))

// Peso
Image(systemName: "star")
    .fontWeight(.light)

// Multicolor
Image(systemName: "flame.fill")
    .symbolRenderingMode(.multicolor)
    .font(.largeTitle)

// Variantes
Image(systemName: "folder.fill.badge.plus")

// Animação (iOS 17+)
Image(systemName: "wifi")
    .symbolEffect(.variableColor.iterative)
```

!!! tip "Encontrando ícones"
    Baixe o app **SF Symbols** (gratuito da Apple) para explorar e buscar todos os ícones disponíveis com seus nomes exatos.

---

## Checklist

- [x] Crio Views customizadas com o protocolo `View`
- [x] Uso `Text`, `Image`, `Button` corretamente
- [x] Componho layouts com `HStack`, `VStack`, `ZStack`
- [x] Aplico modificadores de estilo
- [x] Crio modificadores customizados com `ViewModifier`
- [x] Uso SF Symbols adequadamente
