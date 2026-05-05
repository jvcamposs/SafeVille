# Widgets com WidgetKit

🔴 **Avançado** · Módulo 10

Widgets aparecem na tela inicial e na tela de bloqueio do iPhone. Eles mostram informações do app sem precisar abri-lo.

---

## Criando a extensão de Widget

1. No Xcode: **File → New → Target → Widget Extension**
2. Dê um nome: `ClimaWidget`
3. Marque "Include Configuration Intent" se quiser configuração do usuário

Isso cria:

```
ClimaWidget/
├── ClimaWidget.swift          ← Código principal
├── ClimaWidgetBundle.swift    ← Registra os widgets
└── Assets.xcassets/
```

---

## Estrutura de um Widget

```swift
import WidgetKit
import SwiftUI

// MARK: - Entry (snapshot do estado)

struct ClimaEntry: TimelineEntry {
    let date:         Date
    let cidade:       String
    let temperatura:  Int
    let codigoClima:  Int
}

// MARK: - Provider (fornece dados ao WidgetKit)

struct ClimaProvider: TimelineProvider {

    // Placeholder — exibido durante carregamento
    func placeholder(in context: Context) -> ClimaEntry {
        ClimaEntry(date: .now, cidade: "São Paulo",
                   temperatura: 25, codigoClima: 0)
    }

    // Snapshot — preview na galeria de widgets
    func getSnapshot(in context: Context,
                     completion: @escaping (ClimaEntry) -> Void) {
        completion(placeholder(in: context))
    }

    // Timeline — quando e quais dados exibir
    func getTimeline(in context: Context,
                     completion: @escaping (Timeline<ClimaEntry>) -> Void) {

        Task {
            let entrada = await buscarClimaAtual()
            let proximaAtualizacao = Calendar.current.date(
                byAdding: .hour, value: 1, to: .now)!

            let timeline = Timeline(
                entries: [entrada],
                policy: .after(proximaAtualizacao)  // (1)
            )
            completion(timeline)
        }
    }

    private func buscarClimaAtual() async -> ClimaEntry {
        // Busca dados reais da API ou do cache local
        return ClimaEntry(date: .now, cidade: "São Paulo",
                          temperatura: 23, codigoClima: 1)
    }
}

// (1) .after(data) — atualiza após essa data
//     .atEnd — atualiza ao fim das entradas
//     .never — não atualiza automaticamente
```

---

## View do Widget

```swift
// MARK: - View

struct ClimaWidgetView: View {
    var entry: ClimaProvider.Entry
    @Environment(\.widgetFamily) var family   // (1)

    var body: some View {
        switch family {
        case .systemSmall:  smallView
        case .systemMedium: mediumView
        default:            smallView
        }
    }

    var smallView: some View {
        VStack(spacing: 4) {
            Image(systemName: WeatherCode.icone(for: entry.codigoClima))
                .font(.system(size: 32))
                .symbolRenderingMode(.multicolor)
            Text("\(entry.temperatura)°C")
                .font(.title2.weight(.bold))
            Text(entry.cidade)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .padding()
        .containerBackground(.ultraThinMaterial, for: .widget)  // (2)
    }

    var mediumView: some View {
        HStack {
            smallView
            Divider()
            // Previsão dos próximos dias...
        }
    }
}

// (1) widgetFamily: .systemSmall, .systemMedium, .systemLarge, .accessoryCircular
// (2) containerBackground é obrigatório no iOS 17+
```

---

## Registrando o Widget

```swift
// ClimaWidgetBundle.swift
@main
struct ClimaWidgetBundle: WidgetBundle {
    var body: some Widget {
        ClimaWidget()
        // OutroWidget()  ← adicione mais aqui
    }
}

// Widget principal
struct ClimaWidget: Widget {
    let kind = "ClimaWidget"   // (1) Identificador único

    var body: some WidgetConfiguration {
        StaticConfiguration(
            kind: kind,
            provider: ClimaProvider()
        ) { entry in
            ClimaWidgetView(entry: entry)
        }
        .configurationDisplayName("Clima")
        .description("Mostra o clima atual da sua cidade.")
        .supportedFamilies([.systemSmall, .systemMedium])   // (2)
    }
}

// (1) kind é usado para atualizar o widget programaticamente
// (2) Tamanhos suportados
```

---

## Compartilhando dados com App Group

Para o widget acessar dados do app principal:

```swift
// No app principal — salvar dados
let defaults = UserDefaults(suiteName: "group.com.empresa.climaapp")!
defaults.set("São Paulo", forKey: "ultimaCidade")
defaults.set(23, forKey: "temperaturaAtual")

// No widget — ler dados
let defaults = UserDefaults(suiteName: "group.com.empresa.climaapp")!
let cidade = defaults.string(forKey: "ultimaCidade") ?? "Desconhecida"
let temp   = defaults.integer(forKey: "temperaturaAtual")
```

---

## Atualizando widgets do app

```swift
import WidgetKit

// Em qualquer lugar no app:
WidgetCenter.shared.reloadAllTimelines()

// Ou um widget específico:
WidgetCenter.shared.reloadTimelines(ofKind: "ClimaWidget")
```

---

## Checklist

- [x] Crio uma Widget Extension no Xcode
- [x] Implemento `TimelineProvider` com placeholder, snapshot e timeline
- [x] Suporto múltiplos tamanhos com `@Environment(\.widgetFamily)`
- [x] Compartilho dados com App Group
- [x] Atualizo o widget do app com `WidgetCenter`
