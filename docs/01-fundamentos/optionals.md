# Optionals e Tratamento de nil

🟢 **Básico** · Módulo 01

Optionals são um dos conceitos mais importantes — e mais exclusivos — do Swift. Eles representam a ausência de valor de forma segura, eliminando toda uma classe de erros que afligem linguagens como Objective-C, Java e JavaScript.

---

## Por que nil é perigoso?

Em muitas linguagens, qualquer variável pode ser `null` a qualquer momento. Isso causa o famoso **Null Pointer Exception** — o erro mais comum no desenvolvimento de software.

```swift
// Em Objective-C: pode ser nil sem avisar
NSString *nome = nil;
NSUInteger tamanho = [nome length]; // retorna 0, sem crash... mas é silencioso!
```

Swift resolve isso de forma diferente: **por padrão, nada pode ser nil**. Se um valor pode ser nil, você precisa declarar isso explicitamente com `?`.

!!! info "Princípio fundamental"
    Em Swift, `nil` só pode existir em tipos opcionais. Um `String` nunca é nil. Um `String?` pode ser nil. O compilador força você a lidar com essa distinção.

---

## Declarando Optionals

```swift
var nome: String = "Ana"        // Nunca pode ser nil
var apelido: String? = "Aninha" // Pode ser nil (1)
var cidade: String? = nil       // Explicitamente nil (2)

// (1) O ? torna o tipo "String ou nil"
// (2) Valor inicial nil — cidade não foi definida
```

Ao imprimir um optional, Swift adiciona `Optional(...)` em torno do valor:

```swift
print(nome)    // Ana
print(apelido) // Optional("Aninha")
print(cidade)  // nil
```

---

## Desembrulhando (Unwrapping) Optionals

Para usar o valor dentro de um optional, você precisa "desembrulhá-lo".

### 1. Force Unwrap (`!`)

```swift
var apelido: String? = "Aninha"
print(apelido!) // "Aninha" — funciona porque não é nil
```

!!! warning "Perigo: Force Unwrap"
    Se o optional for `nil` quando você usar `!`, o app **crasha imediatamente**.
    
    ```swift
    var cidade: String? = nil
    print(cidade!) // 💥 Fatal error: Unexpectedly found nil while unwrapping an Optional value
    ```
    
    Use `!` apenas quando você tem **certeza absoluta** de que o valor não é nil.

### 2. Optional Binding com `if let`

A forma mais comum e segura de desembrulhar:

```swift
var apelido: String? = "Aninha"

if let nomeDesembrulhado = apelido {
    print("Apelido é: \(nomeDesembrulhado)") // (1)
} else {
    print("Sem apelido definido")
}

// (1) Dentro do if, nomeDesembrulhado é um String comum (não optional)
```

**Swift 5.7+: sintaxe abreviada** (shadowing)

```swift
if let apelido {          // (1)
    print("Apelido: \(apelido)")
}

// (1) Equivalente a: if let apelido = apelido
```

### 3. `guard let` — para saída antecipada

Ideal quando um nil significa que a função não pode continuar:

```swift
func saudacao(nome: String?) -> String {
    guard let nome else { // (1)
        return "Olá, visitante!"
    }
    return "Olá, \(nome)!" // (2)
}

// (1) Se nome for nil, executa o bloco e retorna
// (2) Aqui nome é um String não-optional
```

!!! tip "if let vs guard let"
    - Use `if let` quando você quer fazer algo com o valor, mas a função pode continuar sem ele.
    - Use `guard let` quando nil significa que não faz sentido continuar — é como uma pré-condição.

---

## Optional Chaining (`?.`)

Permite acessar propriedades e métodos de um optional sem desembrulhar explicitamente:

```swift
struct Endereco {
    var cidade: String
}

struct Pessoa {
    var nome: String
    var endereco: Endereco?
}

let ana = Pessoa(nome: "Ana", endereco: nil)
let cidade = ana.endereco?.cidade // (1)

print(cidade) // nil — sem crash!

// (1) Se endereco for nil, a expressão toda vira nil
```

O optional chaining pode ser encadeado:

```swift
struct Empresa {
    var sede: Endereco?
}

struct Funcionario {
    var empresa: Empresa?
}

let func1 = Funcionario(empresa: nil)
let cidade = func1.empresa?.sede?.cidade // String?
```

---

## Nil Coalescing (`??`)

Fornece um valor padrão quando o optional é nil:

```swift
var apelido: String? = nil
let nome = apelido ?? "Sem apelido" // (1)
print(nome) // "Sem apelido"

var temperatura: Double? = 22.5
let display = "\(temperatura ?? 0.0)°C" // "22.5°C"

// (1) Se apelido for nil, usa o valor à direita do ??
```

---

## Múltiplos Bindings

Você pode desembrulhar vários optionals ao mesmo tempo:

```swift
var usuario: String? = "joao123"
var senha: String? = "secreta"

if let usuario, let senha { // (1)
    print("Login: \(usuario) / \(senha)")
} else {
    print("Usuário ou senha ausentes")
}

// (1) Ambos precisam ser não-nil para entrar no if
```

Você também pode misturar `let` com condições:

```swift
var idade: Int? = 25

if let idade, idade >= 18 {
    print("Acesso liberado")
}
```

---

## Implicitly Unwrapped Optionals (`!` no tipo)

Declaram um optional que você promete nunca acessar quando nil:

```swift
var label: UILabel!  // Implicitly unwrapped (1)

// Mais tarde, em viewDidLoad:
label = UILabel()
label.text = "Olá" // Não precisa de if let — mas crasha se for nil!

// (1) Comum com IBOutlets no UIKit — o sistema garante que não é nil
```

!!! warning "Use com cautela"
    `!` no tipo é uma promessa ao compilador. Se você quebrar essa promessa, o app crasha. Prefira sempre optional bindings normais.

---

## Comparação de abordagens

=== "❌ Force Unwrap"
    ```swift
    func exibirUsuario(_ nome: String?) {
        print("Nome: \(nome!)") // Crasha se nil
    }
    ```

=== "✅ if let"
    ```swift
    func exibirUsuario(_ nome: String?) {
        if let nome {
            print("Nome: \(nome)")
        } else {
            print("Nome não disponível")
        }
    }
    ```

=== "✅ guard let"
    ```swift
    func exibirUsuario(_ nome: String?) {
        guard let nome else {
            print("Nome não disponível")
            return
        }
        print("Nome: \(nome)")
    }
    ```

=== "✅ nil coalescing"
    ```swift
    func exibirUsuario(_ nome: String?) {
        print("Nome: \(nome ?? "não disponível")")
    }
    ```

---

## Optionals em SwiftUI

SwiftUI usa optionals extensivamente:

```swift
struct PerfilView: View {
    var fotoURL: URL?       // Foto pode não existir
    var bio: String?

    var body: some View {
        VStack {
            if let fotoURL {                    // (1)
                AsyncImage(url: fotoURL)
            } else {
                Image(systemName: "person.circle")
            }

            Text(bio ?? "Sem bio disponível") // (2)
        }
    }

    // (1) Optional binding diretamente no corpo da View
    // (2) Nil coalescing para texto padrão
}
```

---

## Erros comuns

!!! warning "Comparar optional com valor diretamente"
    ```swift
    var numero: Int? = 5
    
    // ❌ Não funciona como esperado em todos os casos
    if numero == 5 { ... }
    
    // ✅ Correto — Swift compara automaticamente aqui,
    // mas prefira ser explícito em lógica complexa
    if let numero, numero == 5 { ... }
    ```

!!! warning "Optional String não é String vazia"
    ```swift
    var nome: String? = nil
    
    // ❌ Confusão comum
    if nome != "" { ... } // Sempre verdadeiro quando nome é nil!
    
    // ✅ Verificar nil primeiro
    if let nome, !nome.isEmpty { ... }
    ```

---

## Checklist do módulo

- [x] Entendo o que é um Optional e por que existe
- [x] Sei declarar variáveis opcionais com `?`
- [x] Consigo usar `if let` para optional binding
- [x] Sei quando usar `guard let` vs `if let`
- [x] Uso `?.` para optional chaining
- [x] Uso `??` para fornecer valores padrão
- [x] Entendo os riscos do force unwrap `!`
- [x] Sei usar optionals em SwiftUI
