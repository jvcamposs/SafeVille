# Auto Layout

🟡 **Intermediário** · Módulo 04

Auto Layout é o sistema de layout do UIKit baseado em **restrições (constraints)**. Em vez de definir coordenadas absolutas (`x: 100, y: 200`), você define **relacionamentos** entre views — e o sistema calcula as posições automaticamente para qualquer tamanho de tela.

---

## O que é Auto Layout e por que usar?

Antes do Auto Layout (até 2011), os desenvolvedores usavam frames fixos. Com a explosão de tamanhos de tela (iPhone 4, 5, 6, Plus, X, SE, iPad...), isso se tornou insustentável.

```swift
// ❌ Antes: frames fixos (frágil, só funciona em um tamanho de tela)
let button = UIButton(frame: CGRect(x: 100, y: 200, width: 200, height: 44))

// ✅ Hoje: constraints (funciona em qualquer tela)
button.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
button.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
button.widthAnchor.constraint(equalToConstant: 200).isActive = true
button.heightAnchor.constraint(equalToConstant: 44).isActive = true
```

!!! info "Uma view precisa de 4 informações de layout"
    Para que o Auto Layout funcione, toda view precisa ter definidos:
    1. **Posição horizontal** (leading/trailing ou centerX)
    2. **Posição vertical** (top/bottom ou centerY)
    3. **Largura** (width ou leading+trailing)
    4. **Altura** (height ou top+bottom, ou intrínseca como UILabel)
    
    Se faltar alguma, você terá warnings de layout ambíguo.

---

## NSLayoutConstraint — A API de baixo nível

```swift
// Sintaxe completa (verbosa, mas didática)
let constraint = NSLayoutConstraint(
    item: botao,               // View sendo restringida
    attribute: .centerX,       // Atributo da view
    relatedBy: .equal,         // Relação: ==, >=, <=
    toItem: view,              // View âncora
    attribute: .centerX,       // Atributo da view âncora
    multiplier: 1.0,           // Multiplicador
    constant: 0                // Constante adicional
)
constraint.isActive = true

// Ativando múltiplas constraints de uma vez
NSLayoutConstraint.activate([
    constraint1,
    constraint2,
    constraint3
])
```

!!! tip "Na prática: use NSLayoutAnchor"
    A API de `NSLayoutConstraint` direta é muito verbosa. Use `NSLayoutAnchor`, que é mais concisa e type-safe.

---

## NSLayoutAnchor — A API recomendada

`NSLayoutAnchor` é uma API mais limpa introduzida no iOS 9. Cada view expõe propriedades de âncora que representam suas bordas, centro e dimensões.

```swift
// MARK: - Anatomia das âncoras

view.topAnchor           // Borda superior
view.bottomAnchor        // Borda inferior
view.leadingAnchor       // Borda inicial (esquerda em LTR, direita em RTL)
view.trailingAnchor      // Borda final (direita em LTR, esquerda em RTL)
view.centerXAnchor       // Centro horizontal
view.centerYAnchor       // Centro vertical
view.widthAnchor         // Largura
view.heightAnchor        // Altura
view.firstBaselineAnchor // Linha de base do primeiro texto
view.lastBaselineAnchor  // Linha de base do último texto
```

### Exemplos práticos

```swift
import UIKit

class ExemploConstraintsVC: UIViewController {
    
    private let cardView: UIView = {
        let view = UIView()
        view.backgroundColor = .systemBlue
        view.layer.cornerRadius = 16
        view.translatesAutoresizingMaskIntoConstraints = false // (1)
        return view
    }()
    
    private let iconImageView: UIImageView = {
        let iv = UIImageView(image: UIImage(systemName: "star.fill"))
        iv.tintColor = .white
        iv.contentMode = .scaleAspectFit
        iv.translatesAutoresizingMaskIntoConstraints = false
        return iv
    }()
    
    private let titleLabel: UILabel = {
        let label = UILabel()
        label.text = "Título do Card"
        label.font = .systemFont(ofSize: 18, weight: .semibold)
        label.textColor = .white
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        
        view.addSubview(cardView)
        cardView.addSubview(iconImageView)
        cardView.addSubview(titleLabel)
        
        setupConstraints()
    }
    
    private func setupConstraints() {
        NSLayoutConstraint.activate([
            
            // MARK: cardView
            
            // Margem de 24pt das bordas laterais
            cardView.leadingAnchor.constraint(
                equalTo: view.safeAreaLayoutGuide.leadingAnchor, 
                constant: 24  // (2)
            ),
            cardView.trailingAnchor.constraint(
                equalTo: view.safeAreaLayoutGuide.trailingAnchor, 
                constant: -24 // (3)
            ),
            // 100pt abaixo do topo da safe area
            cardView.topAnchor.constraint(
                equalTo: view.safeAreaLayoutGuide.topAnchor, 
                constant: 100
            ),
            // Altura fixa de 120pt
            cardView.heightAnchor.constraint(equalToConstant: 120),
            
            // MARK: iconImageView (dentro do cardView)
            
            iconImageView.leadingAnchor.constraint(
                equalTo: cardView.leadingAnchor, 
                constant: 20
            ),
            iconImageView.centerYAnchor.constraint(
                equalTo: cardView.centerYAnchor
            ),
            iconImageView.widthAnchor.constraint(equalToConstant: 40),
            iconImageView.heightAnchor.constraint(equalToConstant: 40),
            
            // MARK: titleLabel
            
            titleLabel.leadingAnchor.constraint(
                equalTo: iconImageView.trailingAnchor, 
                constant: 16
            ),
            titleLabel.trailingAnchor.constraint(
                equalTo: cardView.trailingAnchor, 
                constant: -20
            ),
            titleLabel.centerYAnchor.constraint(
                equalTo: cardView.centerYAnchor
            )
        ])
    }
}
```

1. `translatesAutoresizingMaskIntoConstraints = false` é **obrigatório** para views criadas programaticamente. Sem isso, o sistema de frames antigo conflita com Auto Layout.
2. Constante positiva: move para dentro (direita para leading).
3. Constante negativa: move para dentro (esquerda para trailing). Pense como "o trailing está a -24 do trailing da superview".

---

## Content Hugging Priority e Compression Resistance

Quando duas views competem por espaço ou precisam esticar, as **prioridades** determinam qual cede.

```swift
// Content Hugging Priority (CHP):
// "O quanto esta view resiste a CRESCER além do seu tamanho intrínseco"
// Maior prioridade = resiste mais a crescer = mais "abraçada" ao conteúdo

// Compression Resistance Priority (CRP):
// "O quanto esta view resiste a ENCOLHER abaixo do seu tamanho intrínseco"
// Maior prioridade = resiste mais a encolher = não comprime

let labelEsquerda = UILabel()
let labelDireita = UILabel()

// Cenário: dois labels lado a lado, label esquerda deve ter tamanho fixo,
// label direita deve pegar o espaço restante.

// Label esquerda: alta CHP = não cresce além do texto
labelEsquerda.setContentHuggingPriority(.required, for: .horizontal)       // 1000
labelEsquerda.setContentCompressionResistancePriority(.required, for: .horizontal)

// Label direita: baixa CHP = cresce para preencher espaço
labelDireita.setContentHuggingPriority(.defaultLow, for: .horizontal)      // 250
```

!!! example "Caso prático: label de chave-valor"
    ```swift
    // "Nome:    João da Silva"
    //  ^chave    ^valor
    // A chave deve ficar pequena, o valor deve expandir
    
    chaveLabel.setContentHuggingPriority(.required, for: .horizontal)
    valorLabel.setContentHuggingPriority(.defaultLow, for: .horizontal)
    
    // Se o valor for muito longo, ele deve ser truncado, não a chave:
    valorLabel.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    chaveLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    ```

---

## UIStackView — Layout simplificado

`UIStackView` é um container que automaticamente aplica Auto Layout aos seus filhos em linha ou coluna. Elimina a necessidade de constraints manuais para a maioria dos layouts.

```swift
// MARK: - Stack View Básico

let stack = UIStackView(arrangedSubviews: [
    iconImageView,
    titleLabel,
    subtitleLabel
])
stack.axis = .vertical          // ou .horizontal
stack.spacing = 8               // espaçamento entre itens
stack.alignment = .leading      // .center, .trailing, .fill
stack.distribution = .fill      // .fillEqually, .equalSpacing, etc.
stack.translatesAutoresizingMaskIntoConstraints = false
view.addSubview(stack)

// Stack precisa apenas das constraints de posição!
NSLayoutConstraint.activate([
    stack.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
    stack.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
    stack.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20)
    // Não precisa de height! O stack calcula automaticamente
])
```

### Stacks aninhados (o segredo dos layouts complexos)

```swift
// Construindo um card complexo com stacks aninhados:
//
// ┌─────────────────────────────┐
// │  [foto]  Nome               │
// │          Cargo              │
// │                             │
// │  [botão esq]  [botão dir]   │
// └─────────────────────────────┘

private func buildCardLayout() -> UIView {
    
    // Stack horizontal: foto + info vertical
    let infoStack = UIStackView(arrangedSubviews: [
        makeInfoStack() // nome + cargo empilhados verticalmente
    ])
    
    let headerStack = UIStackView(arrangedSubviews: [
        fotoImageView,
        makeInfoStack()
    ])
    headerStack.axis = .horizontal
    headerStack.spacing = 12
    headerStack.alignment = .center
    
    // Stack horizontal: botões
    let botoesStack = UIStackView(arrangedSubviews: [
        seguirButton,
        mensagemButton
    ])
    botoesStack.axis = .horizontal
    botoesStack.spacing = 12
    botoesStack.distribution = .fillEqually
    
    // Stack principal: header + botões
    let mainStack = UIStackView(arrangedSubviews: [
        headerStack,
        botoesStack
    ])
    mainStack.axis = .vertical
    mainStack.spacing = 20
    mainStack.translatesAutoresizingMaskIntoConstraints = false
    
    return mainStack
}

private func makeInfoStack() -> UIStackView {
    let stack = UIStackView(arrangedSubviews: [nomeLabel, cargoLabel])
    stack.axis = .vertical
    stack.spacing = 4
    return stack
}
```

!!! tip "Adicionar e remover views de um StackView"
    ```swift
    // Adicionar com animação
    UIView.animate(withDuration: 0.3) {
        stackView.addArrangedSubview(novaView)
        stackView.layoutIfNeeded()
    }
    
    // Remover (hide, não remove da memória)
    UIView.animate(withDuration: 0.3) {
        viewParaEsconder.isHidden = true // (1)
    }
    ```
    1. `isHidden = true` em um arranged subview de UIStackView remove o espaço visualmente. A view ainda existe na memória mas não ocupa espaço no layout.

---

## Safe Area

A Safe Area é a região da tela **garantidamente visível** ao usuário, respeitando notch, Dynamic Island, home indicator e barras do sistema.

```swift
// Sempre prefira safeAreaLayoutGuide para conteúdo crítico
view.topAnchor              // Borda real da tela (pode estar embaixo da status bar)
view.safeAreaLayoutGuide.topAnchor    // Abaixo da status bar/notch (seguro)

view.bottomAnchor           // Borda real (pode estar embaixo do home indicator)
view.safeAreaLayoutGuide.bottomAnchor // Acima do home indicator (seguro)

// Exemplo:
NSLayoutConstraint.activate([
    tableView.topAnchor.constraint(
        equalTo: view.safeAreaLayoutGuide.topAnchor
    ),
    tableView.bottomAnchor.constraint(
        equalTo: view.safeAreaLayoutGuide.bottomAnchor
    ),
    tableView.leadingAnchor.constraint(
        equalTo: view.safeAreaLayoutGuide.leadingAnchor
    ),
    tableView.trailingAnchor.constraint(
        equalTo: view.safeAreaLayoutGuide.trailingAnchor
    )
])
```

!!! warning "Background vs Conteúdo"
    - **Background** (cor, imagem de fundo): use as bordas reais da `view` para que a cor vá até as bordas
    - **Conteúdo** (botões, textos, listas): use sempre `safeAreaLayoutGuide`

---

## Dynamic Type — Suporte a Tamanhos de Fonte

```swift
// Use estilos de texto escaláveis (não tamanhos fixos)
label.font = UIFont.preferredFont(forTextStyle: .body)       // Automático
label.adjustsFontForContentSizeCategory = true               // (1)

// Com fonte customizada:
label.font = UIFontMetrics(forTextStyle: .headline)
    .scaledFont(for: UIFont(name: "Avenir-Bold", size: 17)!)
label.adjustsFontForContentSizeCategory = true
```

1. Esta linha garante que a label re-calcule o tamanho quando o usuário muda o tamanho de fonte nas configurações.

```swift
// Adaptar layout para acessibilidade (texto muito grande)
override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
    super.traitCollectionDidChange(previousTraitCollection)
    
    if traitCollection.preferredContentSizeCategory.isAccessibilityCategory {
        // Usuário tem tamanho de fonte de acessibilidade
        stackView.axis = .vertical // Muda layout para vertical
    } else {
        stackView.axis = .horizontal
    }
}
```

---

## Padrões comuns de layout

=== "Centralizado na tela"

    ```swift
    // Centralizando um botão no meio da tela
    NSLayoutConstraint.activate([
        button.centerXAnchor.constraint(equalTo: view.centerXAnchor),
        button.centerYAnchor.constraint(equalTo: view.centerYAnchor),
        button.widthAnchor.constraint(equalToConstant: 200),
        button.heightAnchor.constraint(equalToConstant: 50)
    ])
    ```

=== "Preenchendo a tela"

    ```swift
    // View que ocupa toda a safe area
    NSLayoutConstraint.activate([
        contentView.topAnchor.constraint(
            equalTo: view.safeAreaLayoutGuide.topAnchor
        ),
        contentView.leadingAnchor.constraint(
            equalTo: view.safeAreaLayoutGuide.leadingAnchor
        ),
        contentView.trailingAnchor.constraint(
            equalTo: view.safeAreaLayoutGuide.trailingAnchor
        ),
        contentView.bottomAnchor.constraint(
            equalTo: view.safeAreaLayoutGuide.bottomAnchor
        )
    ])
    
    // Atalho com helper:
    func pinToSafeArea(_ childView: UIView) {
        NSLayoutConstraint.activate([
            childView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            childView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            childView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            childView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
        ])
    }
    ```

=== "Aspect Ratio"

    ```swift
    // Manter proporção 16:9 (video player)
    NSLayoutConstraint.activate([
        playerView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
        playerView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        playerView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
        playerView.heightAnchor.constraint(
            equalTo: playerView.widthAnchor, 
            multiplier: 9.0/16.0  // (1)
        )
    ])
    ```

    1. O multiplicador define a proporção: `altura = largura * (9/16)`.

=== "Bottom pinned button"

    ```swift
    // Botão fixo na parte inferior (padrão de checkout/confirmar)
    NSLayoutConstraint.activate([
        confirmButton.leadingAnchor.constraint(
            equalTo: view.leadingAnchor, constant: 20
        ),
        confirmButton.trailingAnchor.constraint(
            equalTo: view.trailingAnchor, constant: -20
        ),
        confirmButton.bottomAnchor.constraint(
            equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20
        ),
        confirmButton.heightAnchor.constraint(equalToConstant: 52)
    ])
    ```

---

## UIScrollView com Auto Layout

ScrollViews têm um comportamento especial com Auto Layout — exigem um `contentView` interno.

```swift
class ScrollViewController: UIViewController {
    
    private let scrollView: UIScrollView = {
        let sv = UIScrollView()
        sv.translatesAutoresizingMaskIntoConstraints = false
        return sv
    }()
    
    private let contentView: UIView = { // (1)
        let v = UIView()
        v.translatesAutoresizingMaskIntoConstraints = false
        return v
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(scrollView)
        scrollView.addSubview(contentView)
        
        NSLayoutConstraint.activate([
            // ScrollView preenche a view
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            // ContentView preenche o scrollView
            contentView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            
            // (2) ContentView tem a mesma largura da frame (scroll apenas vertical)
            contentView.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor)
            // Para scroll horizontal, use heightAnchor em vez de widthAnchor
        ])
    }
}
```

1. O `contentView` é um container para todo o conteúdo rolável. Nunca adicione subviews diretamente ao `scrollView`.
2. Fixar a largura ao `frameLayoutGuide` garante que o scroll seja apenas vertical. O `contentLayoutGuide` define o tamanho do conteúdo, o `frameLayoutGuide` define o tamanho da janela de visualização.

---

## SwiftUI dentro de UIKit — UIHostingController

Quando você tem um componente SwiftUI que quer usar dentro de um app UIKit:

```swift
import SwiftUI

// Sua view SwiftUI
struct BadgeView: View {
    let count: Int
    
    var body: some View {
        Text("\(count)")
            .font(.caption2.bold())
            .foregroundStyle(.white)
            .padding(6)
            .background(.red, in: Circle())
    }
}

// Usando em UIKit
class PerfilViewController: UIViewController {
    
    private var badgeCount = 3
    
    override func viewDidLoad() {
        super.viewDidLoad()
        adicionarBadge()
    }
    
    private func adicionarBadge() {
        let badgeView = BadgeView(count: badgeCount)
        let hostingVC = UIHostingController(rootView: badgeView) // (1)
        
        addChild(hostingVC)
        view.addSubview(hostingVC.view)
        hostingVC.didMove(toParent: self)
        
        hostingVC.view.translatesAutoresizingMaskIntoConstraints = false
        hostingVC.view.backgroundColor = .clear
        
        NSLayoutConstraint.activate([
            hostingVC.view.topAnchor.constraint(equalTo: avatarView.topAnchor, constant: -4),
            hostingVC.view.trailingAnchor.constraint(equalTo: avatarView.trailingAnchor, constant: 4)
        ])
    }
    
    // Atualizar a view SwiftUI
    private func atualizarBadge(count: Int) {
        // Re-criar o hosting controller com novos dados
        children.first(where: { $0 is UIHostingController<BadgeView> })
            .map { $0 as? UIHostingController<BadgeView> }?
            .map { $0?.rootView = BadgeView(count: count) }
    }
}
```

1. `UIHostingController` é a ponte entre UIKit e SwiftUI. Ele é um `UIViewController` que hospeda uma view SwiftUI.

---

## Debugando problemas de layout

!!! warning "Warnings comuns e como resolver"

    | Warning | Causa | Solução |
    |---|---|---|
    | "Ambiguous layout" | Faltam constraints | Adicionar constraints de posição ou tamanho faltantes |
    | "Unsatisfiable constraints" | Constraints conflitantes | Remover constraints redundantes ou usar prioridades |
    | "Unable to simultaneously satisfy constraints" | Duas constraints incompatíveis são obrigatórias | Usar prioridades (`priority = .defaultHigh`) |

```swift
// Ativar "constraint debugger" visual no simulador:
// Xcode > Debug > View Hierarchy

// Nomear constraints para debug mais fácil:
let topConstraint = cardView.topAnchor.constraint(
    equalTo: view.safeAreaLayoutGuide.topAnchor, 
    constant: 20
)
topConstraint.identifier = "cardView.top" // (1)
topConstraint.isActive = true
```

1. Identificadores aparecem no console quando há conflitos, facilitando muito a identificação do problema.

```swift
// Adicionar cor temporária para visualizar bounds
view.subviews.forEach { $0.backgroundColor = UIColor.random } // debug

// Ativar o Autolayout Debugger (não para produção!)
UserDefaults.standard.set(true, forKey: "UIViewLayoutFeedbackLoopDebuggingEnabled")
```

---

## Checklist

- [ ] Você entende por que `translatesAutoresizingMaskIntoConstraints = false` é necessário
- [ ] Você sabe usar `NSLayoutAnchor` para criar constraints
- [ ] Você entende a diferença entre `safeAreaLayoutGuide` e os anchors diretos da view
- [ ] Você sabe usar `UIStackView` para layouts de coluna e linha
- [ ] Você entende Content Hugging Priority e Compression Resistance
- [ ] Você sabe criar um `UIScrollView` com Auto Layout
- [ ] Você sabe usar `UIHostingController` para integrar views SwiftUI

---

[:octicons-arrow-right-24: Próximo: Table Views](tableviews.md){ .md-button .md-button--primary }
[:octicons-arrow-left-24: Voltar: View Controllers](viewcontrollers.md){ .md-button }
