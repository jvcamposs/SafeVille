# Mini-Projeto: Calculadora no Playground

🟢 **Básico** · Módulo 01 · Projeto Prático

Vamos consolidar tudo que aprendemos no Módulo 01 construindo uma **calculadora funcional** direto no Xcode Playground. O projeto integra variáveis, tipos, controle de fluxo, funções, closures e optionals.

---

## O que vamos construir

Uma calculadora com:

- Operações básicas: `+`, `-`, `×`, `÷`
- Histórico de operações
- Tratamento de divisão por zero
- Formatação elegante de resultados
- Função de memória (M+, MR, MC)

!!! info "Por que Playground?"
    O Playground é perfeito para aprender — você vê os resultados imediatamente, sem precisar criar um projeto completo ou um simulador.

---

## Passo 1 — Criando o Playground

1. Abra o Xcode
2. Menu **File → New → Playground**
3. Escolha **Blank** e salve como `Calculadora.playground`
4. Delete o conteúdo inicial

---

## Passo 2 — Estrutura base

```swift
import Foundation

// MARK: - Tipos

enum OperacaoMatematica {
    case soma, subtracao, multiplicacao, divisao

    var simbolo: String {   // (1)
        switch self {
        case .soma:           return "+"
        case .subtracao:      return "−"
        case .multiplicacao:  return "×"
        case .divisao:        return "÷"
        }
    }
}

enum ErroCalculadora: Error {   // (2)
    case divisaoPorZero
    case operacaoInvalida(String)
}

// (1) Computed property que retorna o símbolo legível
// (2) Enum de erros segue o protocolo Error do Swift
```

---

## Passo 3 — A função de cálculo

```swift
// MARK: - Cálculo principal

func calcular(
    _ a: Double,
    _ operacao: OperacaoMatematica,
    _ b: Double
) throws -> Double {        // (1)

    switch operacao {
    case .soma:
        return a + b
    case .subtracao:
        return a - b
    case .multiplicacao:
        return a * b
    case .divisao:
        guard b != 0 else {  // (2)
            throw ErroCalculadora.divisaoPorZero
        }
        return a / b
    }
}

// (1) throws indica que a função pode lançar erros
// (2) guard garante que não dividimos por zero
```

---

## Passo 4 — Formatação de resultados

```swift
// MARK: - Formatação

func formatarNumero(_ numero: Double) -> String {
    if numero.truncatingRemainder(dividingBy: 1) == 0 {  // (1)
        return String(Int(numero))
    } else {
        return String(format: "%.4g", numero)            // (2)
    }
}

// (1) Se for número inteiro, remove o .0
// (2) Caso contrário, formata com até 4 dígitos significativos
```

---

## Passo 5 — Histórico de operações

```swift
// MARK: - Histórico

struct EntradaHistorico {
    let a: Double
    let operacao: OperacaoMatematica
    let b: Double
    let resultado: Double?      // (1)
    let erro: String?

    var descricao: String {
        let aStr = formatarNumero(a)
        let bStr = formatarNumero(b)
        if let resultado {
            return "\(aStr) \(operacao.simbolo) \(bStr) = \(formatarNumero(resultado))"
        } else {
            return "\(aStr) \(operacao.simbolo) \(bStr) = Erro (\(erro ?? "desconhecido"))"
        }
    }
}

// (1) Optional porque pode ter ocorrido um erro
```

---

## Passo 6 — A Calculadora completa

```swift
// MARK: - Calculadora

class Calculadora {
    private(set) var historico: [EntradaHistorico] = []  // (1)
    private var memoria: Double = 0.0

    func executar(
        _ a: Double,
        _ operacao: OperacaoMatematica,
        _ b: Double
    ) -> Double? {

        do {
            let resultado = try calcular(a, operacao, b)
            let entrada = EntradaHistorico(
                a: a, operacao: operacao, b: b,
                resultado: resultado, erro: nil
            )
            historico.append(entrada)
            return resultado

        } catch ErroCalculadora.divisaoPorZero {
            let entrada = EntradaHistorico(
                a: a, operacao: operacao, b: b,
                resultado: nil, erro: "Divisão por zero"
            )
            historico.append(entrada)
            return nil

        } catch {
            print("Erro inesperado: \(error)")
            return nil
        }
    }

    // MARK: Memória

    func memoriaAdicionar(_ valor: Double) { memoria += valor }
    func memoriaLer() -> Double            { return memoria   }
    func memoriaClear()                    { memoria = 0.0    }

    // MARK: Histórico

    func imprimirHistorico() {
        guard !historico.isEmpty else {         // (2)
            print("📋 Histórico vazio")
            return
        }
        print("📋 Histórico de operações:")
        historico.enumerated().forEach { index, entrada in  // (3)
            print("  \(index + 1). \(entrada.descricao)")
        }
    }
}

// (1) private(set): leitura pública, escrita privada
// (2) guard para saída antecipada
// (3) enumerated() fornece índice + elemento
```

---

## Passo 7 — Testando a calculadora

```swift
// MARK: - Testes

let calc = Calculadora()

// Operações básicas
let r1 = calc.executar(10, .soma,           5)   // 15
let r2 = calc.executar(10, .subtracao,      3)   // 7
let r3 = calc.executar(6,  .multiplicacao,  7)   // 42
let r4 = calc.executar(15, .divisao,        4)   // 3.75
let r5 = calc.executar(9,  .divisao,        0)   // nil (erro)

print("10 + 5  = \(r1.map(formatarNumero) ?? "erro")")
print("10 - 3  = \(r2.map(formatarNumero) ?? "erro")")
print("6  × 7  = \(r3.map(formatarNumero) ?? "erro")")
print("15 ÷ 4  = \(r4.map(formatarNumero) ?? "erro")")
print("9  ÷ 0  = \(r5.map(formatarNumero) ?? "⚠️ Divisão por zero")")

print()
calc.imprimirHistorico()

// Memória
calc.memoriaAdicionar(r1 ?? 0)
calc.memoriaAdicionar(r3 ?? 0)
print("\nMemória: \(calc.memoriaLer())")  // 57.0
calc.memoriaClear()
print("Memória após clear: \(calc.memoriaLer())") // 0.0
```

Saída esperada:

```
10 + 5  = 15
10 - 3  = 7
6  × 7  = 42
15 ÷ 4  = 3.75
9  ÷ 0  = ⚠️ Divisão por zero

📋 Histórico de operações:
  1. 10 + 5 = 15
  2. 10 − 3 = 7
  3. 6 × 7 = 42
  4. 15 ÷ 4 = 3.75
  5. 9 ÷ 0 = Erro (Divisão por zero)

Memória: 57.0
Memória após clear: 0.0
```

---

## Desafios extras

!!! example "Desafio 1 — Potenciação"
    Adicione um caso `.potencia` ao enum `OperacaoMatematica` e implemente a operação usando `pow(_:_:)`.

!!! example "Desafio 2 — Calculadora científica"
    Crie funções para: raiz quadrada, seno, cosseno, logaritmo. Use o módulo `Foundation` (`sqrt`, `sin`, `cos`, `log`).

!!! example "Desafio 3 — Expressões em string"
    Escreva uma função `avaliar(_ expressao: String) -> Double?` que aceita strings como `"3 + 4"` e retorna o resultado (dica: use `components(separatedBy:)`).

!!! example "Desafio 4 — Closure como parâmetro"
    Refatore `executar` para aceitar um closure de callback `onResultado: ((Double) -> Void)?` que é chamado quando a operação tem sucesso.

---

## O que você praticou

| Conceito | Onde aparece |
|---|---|
| `var` / `let` | Declaração de variáveis e constantes |
| `enum` | `OperacaoMatematica`, `ErroCalculadora` |
| `struct` | `EntradaHistorico` |
| `class` | `Calculadora` |
| `switch` | Seleção de operação |
| `guard` | Verificação de divisão por zero |
| `throws` / `do-catch` | Tratamento de erros |
| Optionals | Resultado opcional, `map`, `??` |
| Closures | `forEach`, `enumerated` |
| Computed property | `simbolo`, `descricao` |

---

## Checklist final do Módulo 01

- [x] Configurei o Xcode e sei criar um Playground
- [x] Entendo variáveis (`var`) e constantes (`let`)
- [x] Conheço os tipos básicos do Swift
- [x] Uso `if`, `switch`, `guard` corretamente
- [x] Sei criar e chamar funções com parâmetros
- [x] Entendo closures básicas e trailing closure syntax
- [x] Domino optionals: `if let`, `guard let`, `?.`, `??`
- [x] Construí a calculadora no Playground com sucesso

**Próximo módulo:** [OOP & Protocolos →](../02-oop-protocolos/index.md)
