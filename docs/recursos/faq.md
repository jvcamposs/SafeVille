# Perguntas Frequentes (FAQ)

Respostas para as dúvidas mais comuns de quem começa no desenvolvimento iOS.

---

## Sobre a linguagem

### Swift ou Objective-C?

**Swift**, sem hesitar. Objective-C é a linguagem legada do iOS, criada nos anos 80. Swift é moderna, segura e expressiva. Ainda existem apps legados em Objective-C no mercado, mas novos projetos são 100% Swift.

!!! info "Quando aprender Objective-C?"
    Apenas se você for trabalhar especificamente em manutenção de apps legados. Mesmo nesses casos, a maioria das empresas está migrando gradualmente para Swift.

---

### SwiftUI ou UIKit?

**Para aprender hoje: SwiftUI primeiro.**

| Situação | Recomendação |
|---|---|
| Novo app, iOS 16+ | SwiftUI |
| App legado existente | UIKit (com adição gradual de SwiftUI) |
| Entrevista de emprego | Conheça ambos |
| Aprendendo em 2025 | SwiftUI |

SwiftUI é o futuro, mas UIKit ainda é amplamente usado no mercado. Este curso cobre os dois.

---

### Preciso saber programação antes?

Não é obrigatório, mas ajuda. O Módulo 01 começa do zero com variáveis e tipos básicos. Se você nunca programou, reserve mais tempo nos primeiros módulos e pratique bastante nos exercícios.

---

## Sobre o ambiente

### Preciso de um Mac?

**Sim, para publicar na App Store.** O Xcode (IDE obrigatório) só roda no macOS. Alternativas limitadas:

- **iPad com Swift Playgrounds** — para aprender Swift básico
- **Máquina virtual macOS** (cloud) — serviços como MacStadium ou GitHub Actions

Para o curso completo, um Mac é essencial. Um Mac mini M2 (o mais acessível) funciona muito bem.

---

### Qual Mac comprar?

Para desenvolvimento iOS em 2025, qualquer Mac com chip Apple Silicon (M1 ou superior) é excelente. A versão mais acessível:

- **Mac mini M2** — R$ 5.000–6.000 no Brasil
- **MacBook Air M2** — R$ 7.000–8.000

Evite Macs Intel para novo desenvolvimento — o Simulator é significativamente mais lento.

---

### Xcode gratuito?

**Sim, 100% gratuito** pela Mac App Store. A Apple Developer Account (USD 99/ano) só é necessária para publicar na App Store — não para desenvolver e testar no Simulator.

---

## Sobre a carreira

### Quanto tempo para conseguir emprego?

Varia muito, mas um guia realista:

| Dedicação | Tempo estimado |
|---|---|
| 4h/dia focado | 8–12 meses |
| 2h/dia | 14–18 meses |
| Fim de semana | 2–3 anos |

O portfólio importa mais que certificados. Tenha 2–3 apps publicados na App Store ou no GitHub.

---

### iOS ou Android (Kotlin)?

Ambos têm mercado. No Brasil, Android tem maior market share (~75%), mas iOS tem salários médios mais altos e menor oferta de desenvolvedores. Se você gosta do ecossistema Apple e tem Mac, iOS é uma excelente escolha.

---

### React Native vs Swift nativo?

Depende do objetivo:

| Fator | Swift Nativo | React Native |
|---|---|---|
| Performance | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Acesso a APIs Apple | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| Compartilhar com Android | ❌ | ✅ |
| Curva de aprendizado | Moderada | Baixa (se sabe JS) |
| Mercado de trabalho | Alto (específico) | Alto (mais amplo) |

Se você quer ser especialista em iOS, Swift nativo é o caminho. Se quer desenvolver para ambas as plataformas com um código, React Native/Flutter são alternativas.

---

### Qual salário esperar?

Valores aproximados no Brasil (2025):

| Nível | Faixa CLT | Faixa PJ |
|---|---|---|
| Júnior | R$ 3.000–5.000 | R$ 5.000–8.000 |
| Pleno | R$ 6.000–10.000 | R$ 9.000–15.000 |
| Sênior | R$ 12.000–20.000+ | R$ 18.000–30.000+ |

Freelance internacional (em USD) pode ser significativamente maior.

---

## Sobre o curso

### Os conteúdos se atualizam?

Sim. O repositório está no GitHub e aceitamos contribuições via Pull Request. Issues com conteúdo desatualizado são bem-vindas.

### Swift 5.9 vs versões anteriores — o que muda?

As novidades mais importantes do Swift 5.9 cobertas no curso:

- **Macros** — `@Observable`, `@Model`
- **Parameter packs** — para código genérico avançado
- **`if`/`switch` como expressão** — `let x = if condição { a } else { b }`

---

### Preciso da Apple Developer Account para este curso?

Apenas para o Módulo 09 (CI/CD e publicação). Todo o resto funciona com o Simulator gratuito.

---

### Posso usar este material no meu curso/aula?

Sim! O conteúdo está licenciado sob MIT. Você pode usar, adaptar e redistribuir, incluindo em contextos educacionais, com atribuição ao repositório original.

---

## Dúvidas técnicas

### Xcode está muito lento — o que fazer?

1. Abra apenas um projeto por vez
2. Limpe o build: **Product → Clean Build Folder** (⇧⌘K)
3. Delete a pasta DerivedData: `~/Library/Developer/Xcode/DerivedData/`
4. Reinicie o Mac
5. Considere adicionar mais RAM (16GB mínimo recomendado)

### Simulator não abre — o que fazer?

```bash
# No Terminal:
xcrun simctl shutdown all
xcrun simctl erase all

# Ou via Xcode: Window → Devices and Simulators → Erase All Content and Settings
```

### Como debugar networking no Simulator?

Use o **Network Link Conditioner** (vem com Additional Tools for Xcode) para simular condições de rede lentas ou offline.

Para inspecionar requests HTTP, use o **Proxyman** ou **Charles Proxy** — ferramentas que funcionam como proxy e mostram todo o tráfego de rede.
