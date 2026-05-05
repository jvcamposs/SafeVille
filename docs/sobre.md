# Sobre o Curso

## Filosofia e Abordagem

Este curso foi construído com uma premissa simples: **aprender fazendo**. Em vez de longas explicações teóricas desconectadas da realidade, cada conceito é apresentado com código funcional, exemplos do mundo real e projetos que você pode usar no seu portfólio.

!!! info "Aprendizado progressivo"
    O conteúdo é organizado de forma que cada módulo se apoia no anterior. Você não vai encontrar "saltos" de dificuldade abruptos — a progressão é gradual e cada nova ferramenta é introduzida quando você já tem o contexto necessário para entendê-la.

### Princípios que guiam o curso

=== "Código real"

    Todos os exemplos de código são escritos para funcionar em versões atuais do Xcode e Swift. Não há pseudocódigo — tudo que você vê pode ser copiado e executado imediatamente.

    ```swift
    // Exemplo: código real, funcional, com boas práticas
    struct ContentView: View {
        @State private var contador = 0

        var body: some View {
            VStack(spacing: 20) {
                Text("Contagem: \(contador)")
                    .font(.largeTitle)
                    .bold()

                Button("Incrementar") {
                    contador += 1
                }
                .buttonStyle(.borderedProminent)
            }
            .padding()
        }
    }
    ```

=== "Projetos práticos"

    Cada módulo termina com um mini-projeto que consolida todos os conceitos aprendidos. Os projetos são progressivos — o projeto do módulo 3 usa o que você aprendeu nos módulos 1 e 2, e assim por diante.

=== "Boas práticas desde o início"

    Desde o primeiro módulo, ensinamos Swift idiomático: uso correto de optionals, value types vs reference types, protocol-oriented programming. Você não vai aprender "jeitos ruins" que precisam ser desaprendidos depois.

=== "Conteúdo atualizado"

    O curso cobre Swift 5.9+, SwiftUI, SwiftData e Swift Concurrency — tecnologias que a Apple está ativamente promovendo e que o mercado está adotando. UIKit é coberto porque ainda é essencial, mas como complemento ao SwiftUI.

---

## Pré-requisitos

!!! check "O que você precisa"
    - **Um Mac** com macOS Ventura (13.0) ou superior
    - **Xcode 15+** instalado (gratuito na Mac App Store)
    - Isso é tudo. Sério.

!!! tip "Conhecimento de programação"
    Conhecimento prévio de qualquer linguagem de programação ajuda, mas não é obrigatório. O módulo 01 cobre os fundamentos de lógica e programação aplicados ao Swift do zero.

---

## Como Usar o Curso

### Fluxo recomendado

Siga os módulos em ordem. A estrutura foi planejada para que cada módulo prepare você para o próximo:

1. **Leia a "Visão Geral"** de cada módulo primeiro — ela apresenta o contexto e o que você vai aprender
2. **Leia cada tópico** com o Xcode aberto ao lado, digitando o código junto
3. **Não copie e cole** o código — digitar força seu cérebro a processar cada linha
4. **Complete o mini-projeto** antes de avançar — se travar, as soluções estão disponíveis, mas tente sozinho primeiro
5. **Revise** os conceitos que ficaram confusos antes de passar ao próximo módulo

### Dicas de estudo

!!! tip "Consistência > intensidade"
    Estudar 1 hora por dia, todos os dias, produz resultados melhores do que maratonar 8 horas no fim de semana. A memória de longo prazo precisa de repetição espaçada.

!!! warning "Não pule o código"
    Ler sobre programação sem praticar é como ler sobre natação — você vai entender a teoria, mas não vai saber nadar. Execute cada exemplo, experimente variações, quebre as coisas e conserte.

### Ambiente de estudos

- Configure o Xcode para exibir números de linha: **Xcode → Settings → Text Editing → Line numbers**
- Use o **Canvas** do SwiftUI para visualizar suas views em tempo real
- Use o **simulador** do iOS para testar seus aplicativos
- Crie uma conta gratuita de desenvolvedor Apple em [developer.apple.com](https://developer.apple.com) para testar em dispositivo físico

---

## Tecnologias Cobertas

O curso cobre o ecossistema moderno de desenvolvimento iOS:

| Tecnologia | Versão | Módulo | Para que serve |
|------------|--------|--------|----------------|
| **Swift** | 5.9+ | 01–10 | Linguagem principal de desenvolvimento iOS |
| **SwiftUI** | iOS 16+ | 03, 10 | Framework declarativo para criação de interfaces |
| **UIKit** | iOS 16+ | 04 | Framework imperativo legado, ainda amplamente usado |
| **SwiftData** | iOS 17+ | 06 | Persistência de dados moderna (substitui Core Data) |
| **Core Data** | iOS 15+ | 06 | Persistência de dados — projetos legados |
| **Combine** | iOS 13+ | 10 | Programação reativa e fluxos de dados assíncronos |
| **Swift Concurrency** | iOS 15+ | 07, 10 | Async/await, actors e structured concurrency |
| **WidgetKit** | iOS 14+ | 10 | Criação de widgets para a tela inicial |
| **XCTest** | — | 08 | Framework de testes unitários e de UI |
| **Fastlane** | — | 09 | Automação de build, testes e publicação |
| **GitHub Actions** | — | 09 | CI/CD em nuvem |

!!! note "Foco nas ferramentas certas"
    Não cobrimos **Objective-C** (linguagem legada substituída pelo Swift) nem **React Native / Flutter** (frameworks multiplataforma que sacrificam acesso nativo às APIs da Apple). Este curso é 100% nativo com Swift.

---

## Estrutura de cada módulo

Cada um dos 10 módulos segue a mesma estrutura:

```
Módulo XX
├── Visão Geral      → Contexto, objetivos e pré-requisitos do módulo
├── Tópico 1         → Conceito com exemplos de código
├── Tópico 2         → ...
├── Tópico N         → ...
└── Mini-Projeto     → Projeto prático que aplica todos os conceitos
```

Cada tópico inclui:

- **Explicação conceitual** — o "por quê" antes do "como"
- **Exemplos de código** comentados e funcionais
- **Dicas e armadilhas comuns** em blocos de admonição
- **Exercícios** para praticar

---

## Como Contribuir

Este curso é mantido pela comunidade. Toda contribuição, por menor que seja, é valorizada.

### Tipos de contribuição

=== "Correções"
    Erros de português, código incorreto, links quebrados, informações desatualizadas. Abra uma [Issue](https://github.com/jvcamposs/safeville/issues) ou envie direto um PR.

=== "Melhorias de conteúdo"
    Explicações mais claras, exemplos adicionais, analogias melhores. Se você sentiu dificuldade em algum ponto, outros provavelmente sentiram também.

=== "Novos tópicos"
    Tem um tópico que deveria estar no curso e não está? Proponha via Issue antes de escrever para discutirmos o encaixe na estrutura.

=== "Traduções"
    O curso está em português do Brasil. Quer traduzir para outro idioma? Entre em contato via Issues.

### Guia de estilo

Ao contribuir com conteúdo, siga estas diretrizes:

- Use linguagem clara e direta — evite jargão desnecessário
- Todo código deve compilar no Xcode 15+ com Swift 5.9+
- Use admonitions do Material para destacar dicas, avisos e notas
- Inclua comentários no código explicando o "por quê", não o "o quê"
- Prefira exemplos concretos a abstrações genéricas

```swift
// ✅ Bom: comentário explica o porquê
// Usamos `weak self` para evitar retain cycle no closure
button.addAction(UIAction { [weak self] _ in
    self?.handleButtonTap()
}, for: .touchUpInside)

// ❌ Ruim: comentário apenas descreve o que o código já diz
// Adiciona uma ação ao botão
button.addAction(UIAction { _ in
    handleButtonTap()
}, for: .touchUpInside)
```

---

## Licença

Este projeto está licenciado sob a **MIT License**.

!!! quote "MIT License"
    Copyright © 2025 jvcamposs

    É concedida permissão, gratuitamente, a qualquer pessoa que obtenha uma cópia deste material e dos arquivos associados, para lidar com o material sem restrições, incluindo, sem limitação, os direitos de usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do material.

    O material é fornecido "como está", sem garantia de qualquer tipo.

Isso significa que você pode:

- [x] Usar o conteúdo para aprender e ensinar
- [x] Adaptar e modificar o material
- [x] Distribuir cópias
- [x] Usar comercialmente
- [x] Incorporar em outros projetos

Desde que você:

- Inclua a nota de copyright e licença nas cópias

[Ver licença completa no GitHub :material-open-in-new:](https://github.com/jvcamposs/safeville/blob/main/LICENSE){ .md-button }

---

!!! success "Pronto para começar?"
    Vá para o [Módulo 01 — Fundamentos](01-fundamentos/index.md) e escreva seu primeiro código Swift!
