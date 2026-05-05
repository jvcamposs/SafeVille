# Módulo 08 — Testes em iOS

🟡 **Intermediário** · Módulo 08

---

## Por que testar?

Testes automatizados são a rede de segurança do seu código. Sem eles, cada mudança se torna uma aposta: você modifica uma função e torce para que nada mais quebre. Com eles, você refatora com confiança, dorme tranquilo após um deploy e colabora em equipe sem medo de regressões.

!!! quote "Kent Beck — criador do TDD"
    "I'm not a great programmer; I'm a good programmer with great habits."

No contexto iOS, testar é ainda mais importante porque:

- **O ciclo de release é lento**: aprovação na App Store leva dias; um bug em produção é caro.
- **Dispositivos fragmentados**: iPads, iPhones de tamanhos diferentes, versões de iOS.
- **Integrações externas**: APIs, banco de dados local, câmera, localização — tudo pode falhar.

---

## A Pirâmide de Testes

A pirâmide de testes é um modelo que orienta a proporção ideal de cada tipo de teste em um projeto:

```
          /\
         /  \
        / UI \          ← Poucos, lentos, frágeis
       /------\
      /  Integ. \       ← Moderados
     /------------\
    /  Unit Tests  \    ← Muitos, rápidos, confiáveis
   /________________\
```

| Tipo | Velocidade | Custo | Quantidade Ideal |
|------|-----------|-------|-----------------|
| **Unit Tests** | Milissegundos | Baixo | 70–80% |
| **Integration Tests** | Segundos | Médio | 15–20% |
| **UI Tests** | Dezenas de segundos | Alto | 5–10% |

!!! tip "Regra prática"
    Se você está começando, foque nos **unit tests** primeiro. Eles oferecem o maior retorno sobre o investimento.

---

## O que você vai aprender

=== "Unit Tests"
    - Framework XCTest
    - Configuração do test target
    - `setUp` e `tearDown`
    - Funções `XCTAssert*`
    - Testes assíncronos com `async/await`
    - Mocks e injeção de dependência
    - Cobertura de código

=== "UI Tests"
    - Framework XCUITest
    - `XCUIApplication`
    - Localizar e interagir com elementos
    - Page Object Pattern
    - Screenshots em falhas
    - Medição de performance

=== "TDD"
    - Ciclo Red-Green-Refactor
    - TDD na prática com ViewModels
    - Construindo um carrinho de compras com TDD
    - Quando usar (e quando não usar)

=== "Projeto"
    - Adicionar testes ao app de clima do Módulo 07
    - Cobertura real de código
    - Testes de UI para fluxos principais

---

## Pré-requisitos

!!! warning "Antes de continuar"
    Este módulo assume que você completou os Módulos 01–07, especialmente:

    - [x] Módulo 05 — Arquitetura MVVM
    - [x] Módulo 07 — Networking e APIs
    - [x] Conhecimento básico de protocolos Swift

---

## Conceitos-chave do módulo

### O que é um "bom teste"?

Um bom teste deve ser **FIRST**:

- **F**ast — executa em milissegundos
- **I**solated — não depende de outros testes
- **R**epeatable — mesmo resultado sempre
- **S**elf-validating — passa ou falha, sem interpretação manual
- **T**imely — escrito antes ou junto ao código de produção

### Anatomia de um teste (AAA)

```swift
func testSomaTwoNumbers() {
    // Arrange — configura o estado inicial
    let calculadora = Calculadora()
    
    // Act — executa a ação que você quer testar
    let resultado = calculadora.soma(2, 3)
    
    // Assert — verifica o resultado
    XCTAssertEqual(resultado, 5)
}
```

---

## Estimativa de tempo

| Aula | Conteúdo | Tempo estimado |
|------|----------|---------------|
| 08.1 | Unit Tests com XCTest | 2h 30min |
| 08.2 | UI Tests | 2h |
| 08.3 | TDD | 2h |
| 08.4 | Projeto: Testes no App de Clima | 1h 30min |
| **Total** | | **≈ 8 horas** |

---

## Ferramentas do módulo

!!! info "O que você vai usar"
    - **Xcode** — IDE com suporte nativo a XCTest
    - **XCTest** — framework da Apple para testes unitários e de UI
    - **XCUITest** — framework para testes de interface
    - **Instruments** — profiling e análise de cobertura (abordado no Módulo 10)

---

Pronto para começar? Vamos para a primeira aula! 👇

[Aula 08.1 — Unit Tests com XCTest →](unit-tests.md){ .md-button .md-button--primary }
