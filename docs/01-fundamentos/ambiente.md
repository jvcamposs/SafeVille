# Configurando o Ambiente de Desenvolvimento

🟢 **Básico** · Módulo 01

Antes de escrever a primeira linha de Swift, precisamos preparar nosso ambiente. Um ambiente bem configurado faz toda a diferença na produtividade — menos erros misteriosos, menos frustração, mais código funcionando.

---

## Requisitos do sistema

| Item | Mínimo | Recomendado |
|---|---|---|
| Sistema operacional | macOS 13 Ventura | macOS 14 Sonoma ou superior |
| Processador | Apple Silicon ou Intel | Apple Silicon (M1/M2/M3) |
| RAM | 8 GB | 16 GB ou mais |
| Espaço em disco | 15 GB livres | 30 GB+ livres |
| Conexão | Banda larga | Banda larga (download ~7 GB) |

!!! info "Apple Silicon vs Intel"
    Se você tem um Mac com chip Apple Silicon (M1, M2, M3 ou variantes Pro/Max/Ultra), o Xcode rodará nativamente e será **significativamente mais rápido** do que em Macs Intel. Ambos funcionam perfeitamente para este curso.

---

## Instalando o Xcode

O Xcode é a IDE oficial da Apple — é onde você escreverá, compilará, testará e depurará seus apps. É **gratuito**.

### Método 1: App Store (Recomendado)

1. Abra o aplicativo **App Store** no seu Mac
2. Na barra de busca, digite `Xcode`
3. Clique em **Obter** (ou no ícone de nuvem se já baixou antes)
4. Aguarde o download — o arquivo tem aproximadamente **7 GB**
5. Após o download, o Xcode estará disponível no **Launchpad** e em `/Applications/Xcode.app`

!!! tip "Dica de velocidade no download"
    Se sua conexão for lenta, inicie o download à noite ou em horários de menor uso. O App Store retoma downloads interrompidos automaticamente. Você pode monitorar o progresso em **Launchpad** — o ícone do Xcode mostrará a barra de progresso.

### Método 2: Developer Portal (Para versões específicas)

Se você precisar de uma versão específica do Xcode (por exemplo, para compatibilidade com um projeto mais antigo):

1. Acesse [developer.apple.com/download/applications](https://developer.apple.com/download/applications)
2. Faça login com seu Apple ID
3. Baixe o arquivo `.xip` desejado
4. Após o download, clique duas vezes no `.xip` para extrair
5. Mova o `Xcode.app` extraído para a pasta `/Applications`

!!! warning "Cuidado com versões do Xcode"
    Versões antigas do Xcode podem não suportar as versões mais recentes do iOS. Para este curso, use **Xcode 15 ou superior**.

---

## Primeiro lançamento do Xcode

Na primeira vez que você abrir o Xcode, algumas coisas acontecerão:

### Aceitando a licença

```
Ao abrir o Xcode pela primeira vez:
1. Uma janela de licença aparecerá
2. Role até o final e clique em "Agree"
3. Digite sua senha de administrador quando solicitado
```

### Instalando componentes adicionais

O Xcode precisará instalar ferramentas de linha de comando (*Command Line Tools*). Isso acontece automaticamente, mas pode demorar alguns minutos.

!!! note "Instalação manual das Command Line Tools"
    Se precisar instalar separadamente, abra o Terminal e execute:
    ```bash
    xcode-select --install
    ```
    Isso instala compiladores, `git`, e outras ferramentas essenciais.

### Tela de boas-vindas

Após a instalação inicial, você verá a tela de boas-vindas do Xcode:

```
┌─────────────────────────────────────────┐
│           Bem-vindo ao Xcode            │
│                                         │
│  > Create a new Xcode project           │
│  > Clone an existing project            │
│  > Open a project or file               │
│                                         │
│  Recentes:                              │
│  (vazio por enquanto)                   │
└─────────────────────────────────────────┘
```

---

## Criando seu primeiro Playground

O **Playground** é o ambiente perfeito para aprender Swift. É uma área de rascunho interativa onde você escreve código e vê os resultados imediatamente, sem precisar criar um projeto completo.

### Passo a passo

**Passo 1:** Abra o Xcode e na tela de boas-vindas, vá em:
```
File → New → Playground...
```
Ou use o atalho: `⌘ + Shift + N` (com Xcode aberto)

**Passo 2:** Escolha o template:

=== "iOS Playground"
    Selecione **iOS** → **Blank**

    Ideal para: testar APIs do iOS, UIKit, SwiftUI

=== "macOS Playground"
    Selecione **macOS** → **Blank**

    Ideal para: testar lógica pura, algoritmos, sem UI

Para este módulo, usaremos **iOS → Blank**.

**Passo 3:** Nomeie seu Playground:
- Nome sugerido: `Modulo01-Fundamentos`
- Escolha onde salvar (ex: `~/Documents/CursoSwift/`)
- Clique em **Create**

### Anatomia de um Playground

```swift
//: A Swift Playground

import UIKit  // (1)

var greeting = "Hello, playground"  // (2)
print(greeting)  // (3)
```

1. Importa o framework UIKit (Interface de usuário do iOS)
2. Declara uma variável com valor inicial — o tipo `String` é inferido
3. Imprime no console lateral — veja o resultado na área de resultados

!!! example "Seu primeiro código"
    Substitua o conteúdo padrão por:
    ```swift
    import Foundation

    let meuNome = "Maria"
    let idade = 25
    print("Olá, meu nome é \(meuNome) e tenho \(idade) anos.")
    ```
    Pressione **⌘ + Return** para executar. Você verá o resultado no painel lateral direito e no console inferior.

### Executando código no Playground

| Ação | Atalho | Descrição |
|---|---|---|
| Executar tudo | `⌘ + Return` | Executa todo o Playground |
| Executar até a linha | `Shift + Return` | Executa até o cursor |
| Pausar/Continuar | Botão ▶ na barra inferior | Controla a execução |

---

## Swift REPL — O modo interativo no Terminal

O **REPL** (*Read-Eval-Print Loop*) é uma forma de executar Swift diretamente no Terminal, linha por linha. É útil para testar expressões rápidas sem abrir o Xcode.

### Iniciando o REPL

Abra o **Terminal** (`⌘ + Espaço` → "Terminal") e execute:

```bash
swift repl
```

Você verá:

```
Welcome to Apple Swift version 5.9.
Type :help for assistance.
  1>
```

### Usando o REPL

```swift
  1> let x = 10
x: Int = 10
  2> let y = 20
y: Int = 20
  3> x + y
$R0: Int = 30
  4> print("Resultado: \(x + y)")
Resultado: 30
  5> :quit
```

!!! tip "Comandos especiais do REPL"
    | Comando | Descrição |
    |---|---|
    | `:help` | Lista todos os comandos |
    | `:quit` ou `:q` | Sai do REPL |
    | `:print <var>` | Imprime o valor de uma variável |
    | `↑` / `↓` | Navega no histórico de comandos |

!!! note "REPL vs Playground"
    O REPL é ótimo para testes rápidos de expressões simples. Para código mais longo ou que envolva múltiplas linhas e funções, o Playground é mais conveniente. Neste curso usaremos principalmente Playgrounds.

---

## Organizando seu espaço de trabalho

Um workspace organizado desde o início poupa horas de confusão no futuro.

### Estrutura de pastas recomendada

```
~/Documents/
└── CursoSwift/
    ├── Modulo01-Fundamentos/
    │   ├── Modulo01-Fundamentos.playground
    │   ├── Exercicios.playground
    │   └── MiniProjeto-Calculadora.playground
    ├── Modulo02-EstruturasDados/
    └── Modulo03-POO/
```

### Configurações do Xcode para maior produtividade

Acesse `Xcode → Settings...` (ou `⌘ + ,`) e configure:

**Aba Text Editing:**
- ✅ Show: Line numbers
- ✅ Automatically trim trailing whitespace
- ✅ Including whitespace-only lines
- Font recomendada: `SF Mono` ou `Menlo`, tamanho 14

**Aba Themes:**
- Tema recomendado para iniciantes: **Default (Light)** ou **Default (Dark)**
- Tema popular: **Dusk** (escuro com bom contraste)

!!! tip "Atalhos essenciais do Xcode"
    Aprenda esses atalhos desde o início — eles aceleram muito o desenvolvimento:

    | Atalho | Ação |
    |---|---|
    | `⌘ + R` | Executar (Run) |
    | `⌘ + B` | Compilar (Build) |
    | `⌘ + .` | Parar execução |
    | `⌘ + /` | Comentar/descomentar linha |
    | `⌘ + [` ou `]` | Indentar bloco |
    | `⌘ + Shift + O` | Abrir arquivo rapidamente |
    | `Control + I` | Re-indentar seleção |
    | `⌘ + 0` | Mostrar/esconder Navigator |
    | `⌘ + Option + Return` | Mostrar/esconder painel de resultados |

---

## Problemas comuns e soluções

### Xcode muito lento ao abrir

!!! warning "Xcode demora para iniciar"
    O Xcode pode demorar 30-60 segundos para abrir na primeira vez ou após atualizações. Isso é normal.

    **Soluções se continuar lento:**
    1. Limpe os arquivos derivados: `Xcode → Product → Clean Build Folder` (`⌘ + Shift + K`)
    2. Reinicie o Mac
    3. Verifique se há espaço em disco disponível (mínimo 5 GB livres)
    4. Feche outros aplicativos pesados (browsers com muitas abas, etc.)

### Simulador não abre ou trava

!!! warning "Problemas com o Simulator"
    Se o simulador do iPhone não abrir ou travar:

    ```bash
    # No Terminal, encerre todos os processos do simulador:
    sudo killall -9 com.apple.CoreSimulator.CoreSimulatorService

    # Ou pelo Xcode:
    # Window → Devices and Simulators → selecione o simulador → Delete
    ```

    Após isso, reinicie o Xcode e tente novamente.

### Playground não executa / resultados não aparecem

!!! warning "Playground congelado"
    Se os resultados do Playground pararem de aparecer:

    1. Clique no botão **Stop** (quadrado) na barra inferior
    2. Aguarde 5 segundos
    3. Clique em **Run** (triângulo) novamente
    4. Se não resolver: `Editor → Run Playground`
    5. Último recurso: feche e reabra o arquivo `.playground`

### Erro "xcrun: error: unable to find utility 'xcodebuild'"

!!! warning "Ferramentas de linha de comando não encontradas"
    Execute no Terminal:
    ```bash
    sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
    ```
    Isso aponta o sistema para a instalação correta do Xcode.

### Xcode pede para baixar componentes toda vez

!!! warning "Componentes do simulador"
    O Xcode baixa automaticamente os runtimes dos simuladores quando necessário. Para pré-baixar:
    `Xcode → Settings → Platforms` → baixe os runtimes que precisar.

    Runtimes que não são usados podem ser deletados para liberar espaço.

---

## Verificando se tudo está funcionando

Após configurar tudo, faça este teste rápido:

1. Crie um novo Playground (iOS → Blank)
2. Digite o código abaixo:

```swift
import Foundation

// Teste básico do ambiente
func verificarAmbiente() {
    let versaoSwift = "Swift 5.9+"    // (1)
    let ambiente = "Xcode Playground" // (2)

    print("✅ Ambiente configurado com sucesso!")
    print("📱 Linguagem: \(versaoSwift)")
    print("🛠️  IDE: \(ambiente)")
    print("📅 Data: \(Date())")
}

verificarAmbiente()
```

1. Constante do tipo String com o nome da versão
2. Outra constante descrevendo o ambiente

3. Execute com `⌘ + Return`
4. No console inferior você deverá ver algo como:
```
✅ Ambiente configurado com sucesso!
📱 Linguagem: Swift 5.9+
🛠️  IDE: Xcode Playground
📅 Data: 2024-01-15 10:30:00 +0000
```

Se você viu essa saída, seu ambiente está **100% configurado** e pronto para o curso!

---

## Checklist de configuração

Confirme cada item antes de avançar para o próximo tópico:

- [ ] macOS 13 (Ventura) ou superior instalado
- [ ] Xcode 15 ou superior instalado via App Store
- [ ] Xcode abriu sem erros na primeira vez
- [ ] Licença do Xcode aceita
- [ ] Command Line Tools instaladas (`xcode-select --install`)
- [ ] Primeiro Playground criado com sucesso
- [ ] Código de teste executou e mostrou resultado no console
- [ ] Swift REPL funcionando no Terminal (`swift repl`)
- [ ] Pasta de organização do curso criada (`~/Documents/CursoSwift/`)
- [ ] Números de linha visíveis no editor do Xcode

---

*Ambiente pronto! Agora vamos aprender a linguagem em si.*

[:octicons-arrow-right-24: Próximo: Sintaxe Básica do Swift](sintaxe.md){ .md-button .md-button--primary }
[:octicons-arrow-left-24: Voltar: Índice do Módulo](index.md){ .md-button }
