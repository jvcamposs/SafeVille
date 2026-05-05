# TDD — Test Driven Development

🟡 **Intermediário** · Módulo 08

---

## O que é TDD?

**Test Driven Development** (Desenvolvimento Orientado a Testes) é uma disciplina de desenvolvimento onde você escreve o teste *antes* do código de produção. Não é sobre testar — é sobre **design**.

TDD foi popularizado por Kent Beck no contexto do Extreme Programming (XP) no final dos anos 90. A ideia central é simples, mas transforma profundamente a forma como você escreve código.

!!! quote "Kent Beck"
    "TDD is not about testing. TDD is about design. It's about writing code that is easy to test."

---

## O ciclo Red-Green-Refactor

O TDD funciona em ciclos curtos de três passos:

```
     ┌─────────────┐
     │   🔴 RED    │  Escreva um teste que FALHA
     └──────┬──────┘
            │
            ▼
     ┌─────────────┐
     │  🟢 GREEN   │  Escreva o MÍNIMO de código para passar
     └──────┬──────┘
            │
            ▼
     ┌─────────────┐
     │  🔵 REFACTOR│  Melhore o código sem quebrar os testes
     └──────┬──────┘
            │
            └──────── repita ──────────────────────┐
```

### Regras do ciclo

- **Red**: Escreva exatamente um teste que falha — nem mais, nem menos
- **Green**: Faça o teste passar com o **código mais simples possível** (mesmo que "errado")
- **Refactor**: Melhore a estrutura interna sem alterar o comportamento externo

!!! warning "A armadilha do Green"
    No passo Green, você tem permissão para ser "trapaceiro". Se o teste espera que `soma(2,3)` retorne `5`, você pode retornar literalmente `return 5`. O próximo teste vai forçar você a implementar de verdade. Isso ensina a escrever código mínimo.

---

## TDD na prática: Calculadora de IMC

Vamos construir uma calculadora de IMC seguindo TDD passo a passo.

### Iteração 1 — Primeiro teste (Red)

```swift
// IMCCalculatorTests.swift
import XCTest
@testable import MeuApp

final class IMCCalculatorTests: XCTestCase {

    func testCalculaIMCBasico() {
        // Este código não compila ainda — IMCCalculator não existe
        let calc = IMCCalculator()
        let imc = calc.calcular(peso: 70, altura: 1.75)
        XCTAssertEqual(imc, 22.86, accuracy: 0.01)
    }
}
```

Execute: **Erro de compilação** (Red ✓ — esperado!)

### Iteração 1 — Código mínimo (Green)

```swift
// IMCCalculator.swift
struct IMCCalculator {
    func calcular(peso: Double, altura: Double) -> Double {
        return peso / (altura * altura)
    }
}
```

Execute: **Passa** (Green ✓)

Refactor: código já está limpo, nada a melhorar por enquanto.

### Iteração 2 — Classificação (Red)

```swift
func testClassificacaoAbaixoDoPeso() {
    let calc = IMCCalculator()
    let classificacao = calc.classificar(imc: 17.5)
    XCTAssertEqual(classificacao, .abaixoDoPeso)
}
```

Execute: **Erro de compilação** — `classificar` e `.abaixoDoPeso` não existem (Red ✓)

### Iteração 2 — Código mínimo (Green)

```swift
enum ClassificacaoIMC {
    case abaixoDoPeso
    case pesoNormal
    case sobrepeso
    case obesidade
}

struct IMCCalculator {
    func calcular(peso: Double, altura: Double) -> Double {
        return peso / (altura * altura)
    }

    func classificar(imc: Double) -> ClassificacaoIMC {
        return .abaixoDoPeso // (1)
    }
}
```

1. Código "trapaceiro" — retorna sempre o mesmo valor para fazer o teste passar. O próximo teste vai forçar a implementação real.

Execute: **Passa** (Green ✓)

### Iteração 3 — Forçando a implementação real (Red)

```swift
func testClassificacaoPesoNormal() {
    let calc = IMCCalculator()
    XCTAssertEqual(calc.classificar(imc: 22.0), .pesoNormal)
}

func testClassificacaoSobrepeso() {
    let calc = IMCCalculator()
    XCTAssertEqual(calc.classificar(imc: 27.0), .sobrepeso)
}
```

Execute: **Falha** — `classificar` retorna sempre `.abaixoDoPeso` (Red ✓)

### Iteração 3 — Implementação real (Green)

```swift
func classificar(imc: Double) -> ClassificacaoIMC {
    switch imc {
    case ..<18.5: return .abaixoDoPeso
    case 18.5..<25: return .pesoNormal
    case 25..<30: return .sobrepeso
    default: return .obesidade
    }
}
```

Execute: **Passa** (Green ✓)

Refactor: podemos extrair as constantes para melhorar legibilidade.

```swift
// Refactored — mesma lógica, mais legível
struct IMCCalculator {
    private enum Threshold {
        static let abaixoDoPeso: Double = 18.5
        static let pesoNormal: Double = 25.0
        static let sobrepeso: Double = 30.0
    }

    func calcular(peso: Double, altura: Double) -> Double {
        guard altura > 0 else { return 0 } // (1)
        return peso / (altura * altura)
    }

    func classificar(imc: Double) -> ClassificacaoIMC {
        switch imc {
        case ..<Threshold.abaixoDoPeso: return .abaixoDoPeso
        case Threshold.abaixoDoPeso..<Threshold.pesoNormal: return .pesoNormal
        case Threshold.pesoNormal..<Threshold.sobrepeso: return .sobrepeso
        default: return .obesidade
        }
    }
}
```

1. Adicionamos proteção contra divisão por zero — que imediatamente nos diz: precisamos de um teste para isso!

---

## TDD com ViewModels

ViewModels são perfeitos para TDD porque têm lógica bem definida e não dependem de UI:

### Construindo o CarrinhoViewModel com TDD

```swift
// Passo 1: Teste para adicionar item
func testAdicionarItemAumentaQuantidade() {
    let sut = CarrinhoViewModel()
    let produto = Produto(nome: "Camiseta", preco: 59.90)

    sut.adicionar(produto)

    XCTAssertEqual(sut.itens.count, 1)
}
```

```swift
// Implementação mínima
final class CarrinhoViewModel: ObservableObject {
    @Published var itens: [Produto] = []

    func adicionar(_ produto: Produto) {
        itens.append(produto)
    }
}
```

```swift
// Passo 2: Teste para calcular total
func testTotalComUmItem() {
    let sut = CarrinhoViewModel()
    sut.adicionar(Produto(nome: "Camiseta", preco: 59.90))

    XCTAssertEqual(sut.total, 59.90, accuracy: 0.01)
}

func testTotalComVariosItens() {
    let sut = CarrinhoViewModel()
    sut.adicionar(Produto(nome: "Camiseta", preco: 59.90))
    sut.adicionar(Produto(nome: "Calça", preco: 129.90))

    XCTAssertEqual(sut.total, 189.80, accuracy: 0.01)
}
```

```swift
// Implementação do total
var total: Double {
    itens.reduce(0) { $0 + $1.preco }
}
```

```swift
// Passo 3: Teste para remover item
func testRemoverItemDiminuiQuantidade() {
    let sut = CarrinhoViewModel()
    let produto = Produto(id: "abc", nome: "Camiseta", preco: 59.90)
    sut.adicionar(produto)

    sut.remover(produto)

    XCTAssertEqual(sut.itens.count, 0)
}

func testRemoverItemNaoExistenteNaoAlteraCarrinho() {
    let sut = CarrinhoViewModel()
    let camiseta = Produto(id: "1", nome: "Camiseta", preco: 59.90)
    let calca = Produto(id: "2", nome: "Calça", preco: 129.90)
    sut.adicionar(camiseta)

    sut.remover(calca) // produto que não está no carrinho

    XCTAssertEqual(sut.itens.count, 1)
}
```

```swift
// Implementação do remover
func remover(_ produto: Produto) {
    itens.removeAll { $0.id == produto.id }
}
```

```swift
// Passo 4: Teste para cupom de desconto
func testAplicarCupomReduzTotal() {
    let sut = CarrinhoViewModel()
    sut.adicionar(Produto(nome: "Camiseta", preco: 100.00))

    let aplicado = sut.aplicarCupom("DESCONTO10") // 10% de desconto

    XCTAssertTrue(aplicado)
    XCTAssertEqual(sut.total, 90.00, accuracy: 0.01)
}

func testCupomInvalidoNaoAlteraTotal() {
    let sut = CarrinhoViewModel()
    sut.adicionar(Produto(nome: "Camiseta", preco: 100.00))

    let aplicado = sut.aplicarCupom("INVALIDO")

    XCTAssertFalse(aplicado)
    XCTAssertEqual(sut.total, 100.00, accuracy: 0.01)
}
```

```swift
// Implementação do cupom
private let cuponsValidos: [String: Double] = [
    "DESCONTO10": 0.10,
    "DESCONTO20": 0.20
]

private var desconto: Double = 0

@discardableResult
func aplicarCupom(_ codigo: String) -> Bool {
    guard let percentual = cuponsValidos[codigo] else {
        return false
    }
    desconto = percentual
    return true
}

var total: Double {
    let subtotal = itens.reduce(0) { $0 + $1.preco }
    return subtotal * (1 - desconto)
}
```

---

## Benefícios do TDD

| Benefício | Como TDD ajuda |
|-----------|---------------|
| **Design** | Código escrito para ser testável é, por natureza, mais desacoplado |
| **Documentação** | Os testes documentam o comportamento esperado |
| **Confiança** | Refatoração sem medo — os testes pegam regressões |
| **Feedback rápido** | Erros descobertos em segundos, não em horas |
| **Escopo** | Força você a definir claramente o que uma função deve fazer |

---

## Desafios do TDD

!!! warning "Seja honesto sobre as dificuldades"
    - **Curva de aprendizado**: leva semanas para ganhar fluidez
    - **Código legado**: difícil de aplicar retroativamente sem refatoração
    - **Testes difíceis**: UI, animações, integrações externas resistem ao TDD puro
    - **Pressão de tempo**: no início, TDD parece mais lento (mas economiza tempo no longo prazo)
    - **Testes frágeis**: se os testes são mal escritos, o TDD vira um fardo

---

## Quando usar TDD

!!! success "Excelente para TDD"
    - Algoritmos e lógica de negócio
    - ViewModels e presenters
    - Parsers e transformadores de dados
    - Utilitários e extensões
    - Bugs — escreva um teste que reproduz o bug antes de corrigir

!!! info "TDD adaptado (escreva testes logo depois)"
    - Código de integração com SDKs de terceiros
    - Configurações de UI em SwiftUI
    - Animações e transições

!!! danger "Difícil ou desnecessário"
    - Código gerado automaticamente
    - Configurações de projeto
    - Código que vai ser deletado em breve

---

## Objeções comuns ao TDD

**"Demora mais para escrever código"**
> No início, sim. Depois de alguns meses praticando, você escreve código na mesma velocidade — mas com muito menos tempo gasto debugando.

**"Eu já sei o que meu código faz, não preciso de testes"**
> Você sabe o que o código faz *agora*. Daqui a 6 meses, depois de 50 mudanças, você ainda vai saber?

**"Meu gerente não deixa"**
> TDD é uma técnica de desenvolvimento, não um entregável. Os testes fazem parte do código. Comunique como "escrevendo código com qualidade" em vez de "escrevendo testes".

**"O código de teste é mais difícil que o código de produção"**
> Isso é um sinal de design ruim no código de produção. Se é difícil de testar, provavelmente está acoplado demais.

**"Vou escrever os testes depois"**
> "Depois" raramente chega. Com pressão de prazo, os testes são sempre a primeira coisa cortada.

---

## Exercício completo: Carrinho de compras

!!! exercise "Construa um carrinho com TDD"
    Seguindo estritamente o ciclo Red-Green-Refactor, implemente:

    1. `Produto(id: String, nome: String, preco: Double, quantidade: Int)`
    2. `Carrinho` com:
        - `adicionar(_ produto: Produto)`
        - `remover(_ produto: Produto)`
        - `aumentarQuantidade(produtoId: String)`
        - `diminuirQuantidade(produtoId: String)` — remove se chegar a 0
        - `total: Double` — considera quantidade de cada item
        - `quantidadeTotal: Int` — total de unidades
        - `estaVazio: Bool`
        - `aplicarCupom(_ codigo: String) -> Bool`
        - `limpar()`

    **Regra**: escreva um teste, veja-o falhar, então escreva o código mínimo para passá-lo.

---

[← UI Tests](ui-tests.md){ .md-button }
[Projeto do Módulo →](projeto.md){ .md-button .md-button--primary }
