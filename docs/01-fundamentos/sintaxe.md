# Sintaxe Básica do Swift

🟢 **Básico** · Módulo 01

O Swift foi projetado para ser **legível e expressivo**. Ao contrário de linguagens mais verbosas, o Swift permite escrever código conciso sem sacrificar clareza. Nesta seção, você aprenderá o vocabulário fundamental da linguagem.

---

## Variáveis e Constantes

Em Swift, há uma distinção clara e importante entre valores que **podem mudar** e valores que **não devem mudar**.

### `var` — Variáveis (valores mutáveis)

```swift
var pontuacao = 0         // (1)
pontuacao = 10            // (2)
pontuacao += 5            // (3)
print(pontuacao)          // Imprime: 15
```

1. Declara uma variável chamada `pontuacao` com valor inicial `0`
2. Reatribui um novo valor — isso é permitido com `var`
3. Incrementa o valor existente em 5

### `let` — Constantes (valores imutáveis)

```swift
let pi = 3.14159          // (1)
let nomeDoApp = "SafeVille"
// pi = 3.0              // (2) — ERRO de compilação!
```

1. Declara uma constante — o valor não pode ser alterado depois
2. Tentar reatribuir uma constante causa erro em tempo de compilação, não em tempo de execução

!!! tip "Regra de ouro: prefira `let`"
    Use `let` por padrão. Só mude para `var` quando souber que o valor precisará mudar. Isso torna seu código mais seguro, mais fácil de raciocinar e ajuda o compilador a otimizá-lo melhor.

!!! warning "Erro clássico de iniciante"
    ```swift
    let contador = 0
    contador = contador + 1  // ❌ Error: cannot assign to value: 'contador' is a 'let' constant
    ```
    Se você precisa de um contador, use `var contador = 0`.

---

## Tipos básicos

Swift é uma linguagem **fortemente tipada** — cada valor tem um tipo específico. O compilador garante que você não misture tipos incompatíveis.

### Tipos numéricos

=== "Int"
    ```swift
    let anoDeNascimento: Int = 1998   // (1)
    let quantidade = 42               // (2) tipo inferido como Int
    let populacao: Int = 215_000_000  // (3) underscores para legibilidade

    print(type(of: quantidade))       // Imprime: Int
    ```
    1. Declaração explícita do tipo `Int` (número inteiro)
    2. O Swift infere o tipo automaticamente — `42` é `Int` por padrão
    3. Underscores (`_`) podem ser usados em literais numéricos para melhorar a legibilidade

=== "Double e Float"
    ```swift
    let altura: Double = 1.75        // (1)
    let peso: Float = 68.5           // (2)
    let imc = 68.5 / (1.75 * 1.75)  // (3) Double inferido

    print("IMC: \(imc)")
    // Double tem 64 bits (15-17 dígitos de precisão)
    // Float tem 32 bits (6-9 dígitos de precisão)
    ```
    1. `Double` é o tipo de ponto flutuante padrão no Swift — 64 bits
    2. `Float` tem menos precisão — use quando memória for crítica
    3. Operações com literais decimais resultam em `Double` por padrão

=== "Bool"
    ```swift
    let aprovado: Bool = true
    let reprovado = false             // (1) tipo Bool inferido
    let maiorDeIdade = (18 >= 18)     // (2) expressão booleana

    if aprovado {
        print("Parabéns!")
    }
    ```
    1. `true` ou `false` são os únicos valores possíveis para `Bool`
    2. Expressões de comparação retornam `Bool`

### Tipos de texto

=== "String"
    ```swift
    let saudacao = "Olá, mundo!"           // (1)
    let multilinhas = """
    Esta é uma string
    que ocupa várias
    linhas do código.
    """                                      // (2)

    var mensagem = "Curso de "
    mensagem += "Swift"                      // (3)
    print(mensagem.count)                    // (4) Imprime: 12
    print(mensagem.isEmpty)                  // (5) Imprime: false
    print(mensagem.uppercased())             // (6) Imprime: CURSO DE SWIFT
    ```
    1. Strings são delimitadas por aspas duplas `"`
    2. Strings multilinha usam três aspas `"""` — preservam quebras de linha
    3. O operador `+=` concatena strings quando usadas com `var`
    4. `.count` retorna o número de caracteres
    5. `.isEmpty` verifica se a string está vazia
    6. `.uppercased()` retorna a string em maiúsculas

=== "Character"
    ```swift
    let inicial: Character = "M"    // (1)
    let emoji: Character = "🦊"    // (2)

    // Iterando sobre caracteres de uma String
    for char in "Swift" {           // (3)
        print(char)
    }
    // Imprime: S, w, i, f, t (um por linha)
    ```
    1. `Character` representa um único caractere Unicode
    2. Emojis são caracteres válidos em Swift!
    3. Uma `String` pode ser iterada como uma sequência de `Character`

---

## Interpolação de Strings

A interpolação é a forma de incorporar valores dentro de uma String usando `\()`.

```swift
let nome = "Carlos"
let idade = 30
let altura = 1.80

// Interpolação básica
let apresentacao = "Meu nome é \(nome)."                        // (1)

// Interpolação com expressões
let proxAniversario = "Ano que vem terei \(idade + 1) anos."    // (2)

// Interpolação com formatação
let descricao = "Tenho \(String(format: "%.2f", altura))m."     // (3)

print(apresentacao)      // Meu nome é Carlos.
print(proxAniversario)   // Ano que vem terei 31 anos.
print(descricao)         // Tenho 1.80m.
```

1. O valor da variável `nome` é inserido diretamente na string
2. Expressões inteiras podem ser colocadas dentro de `\()`
3. Use `String(format:)` para formatar números com precisão específica

!!! info "Interpolação vs Concatenação"
    Prefira interpolação a concatenação — é mais legível e eficiente:
    ```swift
    // ❌ Concatenação (verboso)
    let msg1 = "Olá, " + nome + "! Você tem " + String(idade) + " anos."

    // ✅ Interpolação (claro e direto)
    let msg2 = "Olá, \(nome)! Você tem \(idade) anos."
    ```

---

## Inferência de tipo vs Tipagem explícita

O Swift pode **inferir** o tipo de uma variável a partir do seu valor inicial.

```swift
// Inferência de tipo
let numero = 42          // Swift infere: Int
let preco = 9.99         // Swift infere: Double
let ativo = true         // Swift infere: Bool
let cidade = "Recife"    // Swift infere: String

// Tipagem explícita
let numero2: Int = 42
let preco2: Double = 9.99
let ativo2: Bool = true
let cidade2: String = "Recife"
```

!!! tip "Quando usar tipagem explícita?"
    - Quando o tipo inferido não é o que você quer:
      ```swift
      let nota: Float = 9.5   // sem isso, seria Double
      ```
    - Quando declara a variável sem valor inicial:
      ```swift
      var resultado: Int   // ainda não tem valor, mas o tipo deve ser declarado
      resultado = calcular()
      ```
    - Para documentar a intenção e melhorar a legibilidade em tipos complexos

---

## Controle de fluxo

### `if` / `else if` / `else`

```swift
let temperatura = 28

if temperatura > 35 {          // (1)
    print("Muito quente! 🔥")
} else if temperatura > 25 {   // (2)
    print("Agradável! 😊")
} else if temperatura > 15 {
    print("Fresco. 🌤️")
} else {                       // (3)
    print("Frio! 🥶")
}
// Imprime: Agradável! 😊
```

1. Condição principal — avaliada primeiro
2. Condições alternativas — avaliadas em ordem se a anterior for falsa
3. Bloco padrão — executado se nenhuma condição anterior for verdadeira

### `switch` — Muito mais poderoso que em outras linguagens

O `switch` no Swift é **exaustivo** (deve cobrir todos os casos) e **não tem fall-through por padrão**.

```swift
let diaDaSemana = 3

switch diaDaSemana {          // (1)
case 1:
    print("Segunda-feira")
case 2:
    print("Terça-feira")
case 3:
    print("Quarta-feira")    // Este caso será executado
case 4:
    print("Quinta-feira")
case 5:
    print("Sexta-feira")
case 6, 7:                   // (2) múltiplos valores no mesmo case
    print("Fim de semana!")
default:                     // (3) obrigatório se nem todos os casos forem cobertos
    print("Dia inválido")
}
```

1. O `switch` avalia o valor e executa o primeiro `case` correspondente
2. Vários valores podem ser combinados em um único `case` com vírgula
3. `default` é o caso "pega-tudo" — obrigatório quando os casos não são exaustivos

```swift
// switch com intervalos
let nota = 8.5

switch nota {
case 9.0...10.0:              // (1)
    print("Excelente! ⭐")
case 7.0..<9.0:               // (2)
    print("Bom!")
case 5.0..<7.0:
    print("Regular")
case 0.0..<5.0:
    print("Reprovado")
default:
    print("Nota inválida")
}
// Imprime: Bom!
```

1. `...` é o operador de intervalo fechado (inclui o 10.0)
2. `..<` é o operador de intervalo semiaberto (não inclui o 9.0)

### `guard` — Saída antecipada (early return)

O `guard` é usado para verificar condições que **devem ser verdadeiras** para que o código continue. Se a condição for falsa, o código dentro do bloco `else` é executado (geralmente um `return`, `break`, `continue` ou `throw`).

```swift
func processarIdade(_ idadeString: String) {
    guard let idade = Int(idadeString) else {   // (1)
        print("Erro: '\(idadeString)' não é um número válido.")
        return                                   // (2)
    }

    guard idade >= 0 && idade <= 150 else {      // (3)
        print("Erro: idade \(idade) está fora do intervalo.")
        return
    }

    // Aqui temos certeza que 'idade' é válida
    print("Idade válida: \(idade) anos")         // (4)
}

processarIdade("25")    // Idade válida: 25 anos
processarIdade("abc")   // Erro: 'abc' não é um número válido.
processarIdade("200")   // Erro: idade 200 está fora do intervalo.
```

1. Tenta converter a String para Int — se falhar, entra no `else`
2. O `else` do `guard` **deve** sair do escopo atual (return, break, etc.)
3. Um segundo `guard` verifica a faixa válida
4. A variável `idade` está disponível aqui — fora do bloco `guard`

!!! tip "guard vs if"
    `guard` promove o **caminho feliz** (happy path) — o código principal fica sem indentação excessiva. `if let` aninhou o código de sucesso; `guard let` deixa o código principal no nível base.

    ```swift
    // Com if let — caminho feliz indentado
    if let valor = opcional {
        // código principal aninhado
        print(valor)
    }

    // Com guard let — caminho feliz no nível base
    guard let valor = opcional else { return }
    print(valor)  // código principal no nível base
    ```

---

## Loops

### `for-in` — Para coleções e intervalos

```swift
// Iterando um intervalo
for numero in 1...5 {           // (1)
    print(numero)
}
// Imprime: 1, 2, 3, 4, 5

// Iterando um array
let frutas = ["maçã", "banana", "laranja"]
for fruta in frutas {           // (2)
    print("Fruta: \(fruta)")
}

// Quando o índice não importa
for _ in 1...3 {                // (3)
    print("Repetição!")
}

// Com índice usando enumerated()
for (indice, fruta) in frutas.enumerated() { // (4)
    print("\(indice + 1). \(fruta)")
}
// Imprime: 1. maçã, 2. banana, 3. laranja
```

1. Itera de 1 a 5 incluindo o 5 (`...` = intervalo fechado)
2. Itera sobre cada elemento do array
3. `_` (underscore) descarta o valor do iterador quando não é necessário
4. `.enumerated()` fornece pares (índice, elemento)

### `while` — Enquanto a condição for verdadeira

```swift
var tentativas = 0
var senhaCorreta = false

while !senhaCorreta {                    // (1)
    tentativas += 1
    print("Tentativa \(tentativas)")

    if tentativas >= 3 {                 // (2)
        senhaCorreta = true
        print("Acesso liberado!")
    }
}
// Imprime:
// Tentativa 1
// Tentativa 2
// Tentativa 3
// Acesso liberado!
```

1. Continua enquanto `senhaCorreta` for `false`
2. Simula a senha sendo aceita na terceira tentativa

### `repeat-while` — Executa pelo menos uma vez

```swift
var numero = 0

repeat {                                 // (1)
    numero += 1
    print("Número: \(numero)")
} while numero < 3                       // (2)

// Imprime:
// Número: 1
// Número: 2
// Número: 3
```

1. O bloco é executado **antes** da condição ser verificada
2. A condição é avaliada **após** cada execução — equivalente ao `do-while` de outras linguagens

!!! warning "Loop infinito — cuidado!"
    ```swift
    // ❌ Loop infinito — o programa trava!
    var x = 0
    while x < 10 {
        print(x)
        // Esquecemos de incrementar x!
    }

    // ✅ Correto
    var x = 0
    while x < 10 {
        print(x)
        x += 1   // incremento necessário
    }
    ```

---

## Operadores de intervalo

| Operador | Nome | Exemplo | Valores incluídos |
|---|---|---|---|
| `...` | Fechado | `1...5` | 1, 2, 3, 4, **5** |
| `..<` | Semiaberto | `1..<5` | 1, 2, 3, **4** (sem o 5) |
| `...5` | Parcial (até) | `...5` | tudo até e incluindo 5 |
| `1...` | Parcial (de) | `1...` | 1 em diante |

```swift
let dias = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"]

// Primeiros 5 dias (índices 0 a 4)
for dia in dias[0..<5] {               // (1)
    print(dia)
}

// A partir da quarta posição
for dia in dias[3...] {                // (2)
    print(dia)
}

// Verificando se um valor está num intervalo
let hora = 14
let expediente = 8...18
if expediente.contains(hora) {         // (3)
    print("Dentro do expediente")
}
```

1. `0..<5` = índices 0, 1, 2, 3, 4 (sem o 5)
2. `3...` = do índice 3 até o final
3. Intervalos têm o método `.contains()` para verificação de pertencimento

---

## Operadores

### Aritméticos

```swift
let a = 10
let b = 3

print(a + b)    // 13  — Soma
print(a - b)    // 7   — Subtração
print(a * b)    // 30  — Multiplicação
print(a / b)    // 3   — Divisão inteira (resultado é Int!)
print(a % b)    // 1   — Resto da divisão (módulo)

// Para divisão com decimais, use Double:
let resultado = Double(a) / Double(b)  // 3.3333...
```

!!! warning "Divisão inteira"
    ```swift
    let resultado = 10 / 3   // resultado = 3 (não 3.333!)
    ```
    Em Swift, dividir dois `Int` resulta em `Int`, truncando a parte decimal. Para obter um resultado decimal, converta para `Double` primeiro.

### Comparação e Lógicos

```swift
// Comparação — retornam Bool
5 == 5    // true  (igual)
5 != 3    // true  (diferente)
5 > 3     // true  (maior que)
5 < 3     // false (menor que)
5 >= 5    // true  (maior ou igual)
5 <= 4    // false (menor ou igual)

// Lógicos
true && false   // false (E lógico — ambos devem ser true)
true || false   // true  (OU lógico — pelo menos um deve ser true)
!true           // false (NÃO lógico — inverte)
```

---

## Comentários

```swift
// Comentário de uma linha

/* Comentário
   de múltiplas
   linhas */

/// Documentação de uma função ou tipo
/// - Parameter nome: O nome a ser saudado
/// - Returns: Uma string com a saudação
func saudar(_ nome: String) -> String {
    return "Olá, \(nome)!"
}
```

!!! tip "Comentários `///` para documentação"
    Use três barras `///` antes de funções, tipos e propriedades. O Xcode usa esses comentários para gerar documentação automática. Ao digitar o nome da função em outro lugar e passar o mouse, você verá a documentação.

---

## Armadilhas comuns

!!! warning "Conversão de tipos não é implícita"
    ```swift
    let inteiro = 5
    let decimal = 2.5

    // ❌ Erro: cannot convert value of type 'Double' to expected argument type 'Int'
    let soma = inteiro + decimal

    // ✅ Correto — converter explicitamente
    let soma = Double(inteiro) + decimal   // 7.5
    // ou
    let soma2 = inteiro + Int(decimal)     // 7 (trunca o decimal)
    ```

!!! warning "== vs = (comparação vs atribuição)"
    ```swift
    var x = 5

    // ❌ Isso é uma atribuição, não uma comparação!
    // (Em Swift, isso não compila em condições — diferente de C/JS)
    if x = 10 { }   // Erro de compilação

    // ✅ Use == para comparação
    if x == 10 { }
    ```

!!! warning "String não é uma coleção de índices inteiros"
    ```swift
    let texto = "Olá"

    // ❌ Não funciona em Swift (diferente de outras linguagens)
    // let primeiro = texto[0]

    // ✅ Use startIndex ou prefix()
    let primeiro = texto[texto.startIndex]   // "O"
    let doisPrimeiros = texto.prefix(2)      // "Ol"
    ```

---

## Checklist da seção

Antes de avançar, certifique-se de que consegue:

- [ ] Declarar variáveis com `var` e constantes com `let`
- [ ] Identificar e usar os tipos básicos: `Int`, `Double`, `Bool`, `String`, `Character`
- [ ] Usar interpolação de strings com `\()`
- [ ] Escrever condicionais com `if/else if/else`
- [ ] Usar `switch` com múltiplos casos, valores combinados e intervalos
- [ ] Aplicar `guard` para saídas antecipadas
- [ ] Escrever loops com `for-in`, `while` e `repeat-while`
- [ ] Usar os operadores de intervalo `...` e `..<`
- [ ] Realizar conversões de tipo explícitas quando necessário

---

[:octicons-arrow-right-24: Próximo: Funções e Closures](funcoes.md){ .md-button .md-button--primary }
[:octicons-arrow-left-24: Voltar: Configurando o Ambiente](ambiente.md){ .md-button }
