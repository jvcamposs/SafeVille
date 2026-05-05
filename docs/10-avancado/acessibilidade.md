# Acessibilidade (A11y)

🔴 **Avançado** · Módulo 10

Acessibilidade não é opcional — é um requisito legal em muitos países e atinge ~15% da população mundial. Além disso, apps acessíveis recebem destaque da Apple no App Store.

---

## VoiceOver

VoiceOver é o leitor de tela do iOS. Ele lê em voz alta o conteúdo da tela para usuários com deficiência visual.

**Como testar:** Settings → Accessibility → VoiceOver (ou triple-click do botão lateral)

---

## Modificadores de acessibilidade em SwiftUI

```swift
struct CartaoProduto: View {
    let produto: Produto

    var body: some View {
        VStack {
            Image(systemName: "bag.fill")
                .accessibilityHidden(true)                    // (1)

            Text(produto.nome)
            Text(produto.preco, format: .currency(code: "BRL"))
        }
        .accessibilityElement(children: .combine)             // (2)
        .accessibilityLabel("\(produto.nome), \(produto.preco, format: .currency(code: "BRL"))")
        .accessibilityHint("Toque duas vezes para ver detalhes")
        .accessibilityAddTraits(.isButton)                    // (3)
    }
}

// (1) Ícone decorativo — ocultar do VoiceOver
// (2) Combina os filhos em um único elemento
// (3) Informa que se comporta como botão
```

---

## accessibilityLabel, Hint e Value

```swift
struct SliderView: View {
    @State private var volume = 0.5

    var body: some View {
        Slider(value: $volume)
            .accessibilityLabel("Volume")
            .accessibilityValue("\(Int(volume * 100))%")      // (1)

        Button(action: tocarMusica) {
            Image(systemName: tocando ? "pause.fill" : "play.fill")
        }
        .accessibilityLabel(tocando ? "Pausar" : "Reproduzir")  // (2)
    }
}

// (1) Value é lido quando o valor muda
// (2) Label dinâmico baseado no estado
```

---

## Dynamic Type

Dynamic Type permite ao usuário ajustar o tamanho de texto no sistema:

```swift
// ✅ Use sempre .font() semântico — escala automaticamente
Text("Título")      .font(.title)
Text("Corpo")       .font(.body)
Text("Legenda")     .font(.caption)

// ❌ Evite tamanhos fixos que não escalam
Text("Título")      .font(.system(size: 24))  // Não escala com Dynamic Type

// Para imagens escaláveis com texto
Image(systemName: "star.fill")
    .imageScale(.large)   // Escala com Dynamic Type

// Testar diferentes tamanhos no Preview
#Preview {
    ContentView()
        .environment(\.sizeCategory, .accessibilityExtraExtraLarge)
}
```

---

## Contraste de cores

```swift
// ✅ Use cores semânticas que adaptam automaticamente ao modo claro/escuro
Text("Texto")
    .foregroundStyle(.primary)          // Preto/branco automático
    .foregroundStyle(.secondary)        // Cinza adaptável

// ✅ Definir cores acessíveis no Assets
Color("textoPrimario")  // Definida com variantes claro/escuro no Assets

// ❌ Cores hardcoded que podem ter baixo contraste
Text("Texto")
    .foregroundStyle(Color(hex: "#CCCCCC"))  // Pode ter contraste baixo
```

!!! info "WCAG"
    O padrão internacional de contraste é 4.5:1 para texto normal e 3:1 para texto grande. Use ferramentas como [Colour Contrast Analyser](https://www.tpgi.com/color-contrast-checker/) para verificar.

---

## Accessibility Inspector

Xcode inclui o **Accessibility Inspector** (Xcode → Open Developer Tool → Accessibility Inspector):

1. Conecte o simulador ou dispositivo
2. Abra o Accessibility Inspector
3. Use a inspeção ao vivo para verificar labels, hints e traits
4. Execute a auditoria automática

---

## UIKit — acessibilidade programática

```swift
// UIKit requer configuração manual
let botao = UIButton()
botao.setImage(UIImage(systemName: "trash"), for: .normal)
botao.accessibilityLabel = "Deletar item"
botao.accessibilityHint  = "Toque duas vezes para deletar permanentemente"
botao.accessibilityTraits = .button

// isAccessibilityElement
let containerView = UIView()
containerView.isAccessibilityElement = true    // (1)
containerView.accessibilityLabel = "Cartão do produto iPhone 15"

// (1) Agrupa filhos em um único elemento
```

---

## Checklist

- [x] Uso `accessibilityLabel` em imagens e ícones decorativos
- [x] Uso `accessibilityHidden(true)` para elementos decorativos
- [x] Combino elementos com `accessibilityElement(children: .combine)`
- [x] Suporto Dynamic Type com fontes semânticas
- [x] Verifico contraste de cores com ferramentas externas
- [x] Testo com VoiceOver ligado
- [x] Uso o Accessibility Inspector do Xcode
