# Structs e Classes em Swift

🟡 **Intermediário** · Módulo 02

---

Structs e classes são os dois principais blocos de construção de código Swift. Apesar de superficialmente parecidos, eles têm diferenças fundamentais que afetam o comportamento do seu programa. Dominar essa distinção é essencial para escrever Swift idiomático.

---

## Value Types vs Reference Types

!!! abstract "A diferença fundamental"
    - **Structs** são **value types**: ao atribuir ou passar para uma função, uma **cópia independente** é criada.
    - **Classes** são **reference types**: ao atribuir ou passar para uma função, você está compartilhando a **mesma instância** na memória.

=== "Struct — cópia por valor"

    ```swift
    struct Temperatura {
        var celsius: Double

        var fahrenheit: Double {
            celsius * 9/5 + 32
        }
    }

    var cozinha = Temperatura(celsius: 22)
    var quarto = cozinha       // (1)!

    quarto.celsius = 18

    print(cozinha.celsius)    // 22.0 — inalterado
    print(quarto.celsius)     // 18.0 — cópia independente
    ```

    1. Uma cópia completa de `cozinha` é criada e atribuída a `quarto`. As duas variáveis são completamente independentes.

=== "Class — compartilhamento por referência"

    ```swift
    class ContaBancaria {
        var saldo: Double

        init(saldo: Double) {
            self.saldo = saldo
        }
    }

    let contaA = ContaBancaria(saldo: 1000)
    let contaB = contaA  // (1)!

    contaB.saldo -= 500

    print(contaA.saldo)  // 500.0 — afetado!
    print(contaB.saldo)  // 500.0
    ```

    1. `contaB` aponta para **o mesmo objeto** que `contaA`. Não há cópia — ambos referenciam a mesma instância na memória heap.

---

## Quando usar struct vs class?

!!! tip "Recomendação oficial da Apple"
    A Apple recomenda **começar com `struct`** e mudar para `class` apenas quando necessário.

| Critério | Struct | Class |
|----------|--------|-------|
| Semântica de cópia | ✅ | ❌ |
| Herança | ❌ | ✅ |
| Deinitializer | ❌ | ✅ |
| Contagem de referências (ARC) | ❌ | ✅ |
| Identidade (`===`) | ❌ | ✅ |
| Thread safety | ✅ (mais fácil) | ⚠️ (requer cuidado) |
| Performance | ✅ (stack) | ⚠️ (heap) |

**Use `class` quando:**
- Precisa de herança de comportamento
- A identidade do objeto importa (verificar se é *o mesmo* objeto com `===`)
- Precisa de `deinit` para liberar recursos
- Interopera com Objective-C ou frameworks legados

**Use `struct` nos demais casos** — que é a grande maioria.

---

## Propriedades

### Stored Properties (Propriedades Armazenadas)

```swift
struct Produto {
    var nome: String          // variável — pode ser alterada
    let codigo: String        // constante — imutável após init
    var preco: Double = 0.0   // valor padrão
}

var item = Produto(nome: "Caneta", codigo: "CN-001")
item.nome = "Caneta Azul"   // ✅ permitido
// item.codigo = "XX"       // ❌ erro: let não pode ser alterado
```

### Computed Properties (Propriedades Computadas)

Propriedades computadas **não armazenam valores** — elas calculam e retornam na hora.

```swift
struct Retangulo {
    var largura: Double
    var altura: Double

    // Somente leitura (getter implícito)
    var area: Double {
        largura * altura
    }

    // Leitura e escrita (getter + setter)
    var perimetro: Double {
        get {
            2 * (largura + altura)
        }
        set {
            // newValue é o valor atribuído
            let lado = newValue / 4
            largura = lado
            altura = lado
        }
    }
}

var r = Retangulo(largura: 10, altura: 5)
print(r.area)        // 50.0
print(r.perimetro)   // 30.0

r.perimetro = 40     // define largura e altura para 10
print(r.largura)     // 10.0
```

### Lazy Properties

`lazy` adia a inicialização de uma propriedade até a primeira vez que é acessada — útil para operações custosas.

!!! warning "Lazy só em classes ou structs mutáveis"
    `lazy` só pode ser usado com `var`, nunca com `let`. Em structs, só funciona quando a instância é `var`.

```swift
class AnalisadorDeTexto {
    var texto: String

    // (1)!
    lazy var palavras: [String] = {
        print("Processando texto...")
        return texto.components(separatedBy: " ")
    }()

    init(texto: String) {
        self.texto = texto
        print("AnalisadorDeTexto criado") // lazy ainda não executou
    }
}

let analisador = AnalisadorDeTexto(texto: "Swift é incrível")
// "AnalisadorDeTexto criado"

print(analisador.palavras.count)
// "Processando texto..."
// 3
```

1. A closure só é executada na primeira vez que `palavras` for acessado. Nas chamadas seguintes, o valor já está em cache.

### Property Observers (willSet / didSet)

Observadores permitem executar código **antes** ou **depois** de uma propriedade ser alterada.

```swift
class Progresso {
    var percentual: Int = 0 {
        willSet {
            print("Vai mudar de \(percentual) para \(newValue)")
        }
        didSet {
            print("Mudou de \(oldValue) para \(percentual)")
            if percentual >= 100 {
                print("🎉 Concluído!")
            }
        }
    }
}

let p = Progresso()
p.percentual = 50
// "Vai mudar de 0 para 50"
// "Mudou de 0 para 50"

p.percentual = 100
// "Vai mudar de 50 para 100"
// "Mudou de 50 para 100"
// "🎉 Concluído!"
```

---

## Métodos

### Métodos de instância

```swift
struct Circulo {
    var raio: Double

    func area() -> Double {
        Double.pi * raio * raio
    }

    func descricao() -> String {
        String(format: "Círculo com raio %.1f e área %.2f", raio, area())
    }
}

let c = Circulo(raio: 5)
print(c.descricao())
// "Círculo com raio 5.0 e área 78.54"
```

### Mutating Methods

Em structs, métodos que alteram propriedades precisam ser marcados com `mutating`:

```swift
struct Pilha<T> {
    private var elementos: [T] = []

    mutating func empilhar(_ elemento: T) {  // (1)!
        elementos.append(elemento)
    }

    mutating func desempilhar() -> T? {
        elementos.popLast()
    }

    var topo: T? { elementos.last }
    var estaVazia: Bool { elementos.isEmpty }
}

var pilha = Pilha<Int>()
pilha.empilhar(1)
pilha.empilhar(2)
pilha.empilhar(3)
print(pilha.topo!)          // 3
print(pilha.desempilhar()!) // 3
print(pilha.topo!)          // 2
```

1. `mutating` sinaliza ao compilador que este método pode modificar o estado da struct. Necessário porque structs são value types.

---

## Inicializadores

### Memberwise Initializer (automático em structs)

```swift
struct Endereco {
    var rua: String
    var numero: Int
    var cidade: String
}

// Swift gera automaticamente:
let end = Endereco(rua: "Av. Paulista", numero: 1000, cidade: "São Paulo")
```

### Inicializadores Customizados

```swift
struct Data {
    var dia: Int
    var mes: Int
    var ano: Int

    // Inicializador customizado
    init(timestamp: String) {  // formato: "DD/MM/AAAA"
        let partes = timestamp.split(separator: "/").map { Int($0)! }
        self.dia = partes[0]
        self.mes = partes[1]
        self.ano = partes[2]
    }

    // Inicializador padrão ainda disponível se declarado explicitamente
    init(dia: Int, mes: Int, ano: Int) {
        self.dia = dia
        self.mes = mes
        self.ano = ano
    }

    var formatado: String {
        String(format: "%02d/%02d/%04d", dia, mes, ano)
    }
}

let d1 = Data(dia: 25, mes: 12, ano: 2025)
let d2 = Data(timestamp: "01/01/2026")
print(d1.formatado)  // "25/12/2025"
print(d2.formatado)  // "01/01/2026"
```

### Convenience Initializers (apenas em classes)

```swift
class Usuario {
    var nome: String
    var email: String
    var ativo: Bool

    // Designated initializer — inicializa todas as propriedades
    init(nome: String, email: String, ativo: Bool) {
        self.nome = nome
        self.email = email
        self.ativo = ativo
    }

    // Convenience — chama o designated com valores padrão
    convenience init(nome: String, email: String) {
        self.init(nome: nome, email: email, ativo: true)
    }

    // Convenience — gera email automaticamente
    convenience init(nome: String) {
        let emailGerado = nome.lowercased().replacingOccurrences(of: " ", with: ".") + "@app.com"
        self.init(nome: nome, email: emailGerado)
    }
}

let u1 = Usuario(nome: "Ana")
print(u1.email)  // "ana@app.com"
print(u1.ativo)  // true
```

---

## Herança (somente classes)

```swift
class Veiculo {
    var velocidadeMaxima: Int
    var marca: String

    init(marca: String, velocidadeMaxima: Int) {
        self.marca = marca
        self.velocidadeMaxima = velocidadeMaxima
    }

    func descricao() -> String {
        "\(marca) — máx. \(velocidadeMaxima) km/h"
    }
}

class Carro: Veiculo {
    var numeroDePorras: Int

    init(marca: String, velocidadeMaxima: Int, portas: Int) {
        self.numeroDePorras = portas
        super.init(marca: marca, velocidadeMaxima: velocidadeMaxima)
    }

    // override para customizar comportamento
    override func descricao() -> String {
        super.descricao() + ", \(numeroDePorras) portas"
    }
}

class CarroEletrico: Carro {
    var autonomiaKm: Int

    init(marca: String, velocidadeMaxima: Int, portas: Int, autonomia: Int) {
        self.autonomiaKm = autonomia
        super.init(marca: marca, velocidadeMaxima: velocidadeMaxima, portas: portas)
    }

    override func descricao() -> String {
        super.descricao() + ", autonomia: \(autonomiaKm) km"
    }
}

let tesla = CarroEletrico(marca: "Tesla", velocidadeMaxima: 250, portas: 4, autonomia: 560)
print(tesla.descricao())
// "Tesla — máx. 250 km/h, 4 portas, autonomia: 560 km"
```

!!! note "Impedindo herança com `final`"
    Use `final` para impedir que uma classe seja subclassificada, ou um método seja sobrescrito:

    ```swift
    final class Singleton { ... }  // ninguém pode herdar desta classe

    class Base {
        final func metodoImutavel() { ... }  // não pode ser override
    }
    ```

---

## Deinitializers

Classes podem definir um `deinit` executado antes da instância ser desalocada da memória:

```swift
class ConexaoBancoDeDados {
    let url: String

    init(url: String) {
        self.url = url
        print("✅ Conexão aberta: \(url)")
    }

    deinit {
        print("❌ Conexão fechada: \(url)")
        // liberar recursos, fechar sockets, etc.
    }
}

do {
    let db = ConexaoBancoDeDados(url: "postgres://localhost/app")
    // "✅ Conexão aberta: postgres://localhost/app"
    // usa db...
} // db sai de escopo aqui
// "❌ Conexão fechada: postgres://localhost/app"
```

---

## Extensions

Extensions adicionam funcionalidades a tipos existentes — sem herança e sem acesso ao código-fonte original:

```swift
// Estendendo um tipo da standard library
extension Double {
    var reais: String {
        String(format: "R$ %.2f", self)
    }

    var porcentagem: String {
        String(format: "%.1f%%", self * 100)
    }
}

let preco = 49.90
print(preco.reais)        // "R$ 49.90"
print(0.15.porcentagem)   // "15.0%"

// Estendendo um tipo próprio — adicionar protocolo via extension
struct Produto {
    var nome: String
    var preco: Double
}

extension Produto: CustomStringConvertible {
    var description: String {
        "\(nome) — \(preco.reais)"
    }
}

let p = Produto(nome: "Livro Swift", preco: 89.90)
print(p)  // "Livro Swift — R$ 89.90"
```

---

## Propriedades e Métodos Estáticos

```swift
struct ConfiguracaoApp {
    // Compartilhado por todas as instâncias
    static var versao: String = "1.0.0"
    static let nomeDaApp: String = "MyApp"

    static func atualizar(para versao: String) {
        ConfiguracaoApp.versao = versao
        print("App atualizado para v\(versao)")
    }
}

ConfiguracaoApp.atualizar(para: "2.0.0")
print(ConfiguracaoApp.versao)   // "2.0.0"

// Em classes: 'class' permite override, 'static' não
class Base {
    class var descricao: String { "Base" }        // pode ser sobrescrito
    static var identificador: String { "BASE" }   // não pode ser sobrescrito
}

class Derivada: Base {
    override class var descricao: String { "Derivada" }  // ✅ OK
    // override static var identificador  // ❌ erro
}
```

---

## Identidade de objetos (`===`)

Classes permitem comparar se duas variáveis apontam para **o mesmo objeto**:

```swift
class Sessao {
    var token: String
    init(token: String) { self.token = token }
}

let s1 = Sessao(token: "abc123")
let s2 = s1         // mesma referência
let s3 = Sessao(token: "abc123")  // objeto diferente com mesmo conteúdo

print(s1 === s2)  // true  — mesmo objeto
print(s1 === s3)  // false — objetos diferentes
print(s1 !== s3)  // true
```

---

## Resumo comparativo

```swift
// STRUCT — preferido na maioria dos casos
struct Ponto: Equatable {           // ✅ Equatable grátis com structs simples
    var x, y: Double
    
    mutating func mover(dx: Double, dy: Double) {
        x += dx
        y += dy
    }
}

// CLASS — quando identidade ou herança são necessários
class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
```

!!! success "Boas práticas"
    - ✅ Comece sempre com `struct`
    - ✅ Use `let` sempre que possível (imutabilidade)
    - ✅ Prefira extensions a subclasses para adicionar comportamento
    - ✅ Use `final` em classes que não serão herdadas
    - ⚠️ Evite hierarquias de herança profundas
    - ⚠️ Cuidado com referências circulares entre classes (use `weak`/`unowned`)

---

## Checklist

- [ ] Entendo a diferença entre value type e reference type
- [ ] Sei quando usar struct vs class
- [ ] Sei criar propriedades stored, computed e lazy
- [ ] Uso willSet/didSet para observar mudanças
- [ ] Conheço a diferença entre initializers designated e convenience
- [ ] Entendo herança e como usar `override`
- [ ] Sei criar extensões para tipos existentes

---

Próximo: [Protocolos →](protocolos.md)
