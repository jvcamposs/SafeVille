# Mini-Projeto: App de Clima com API

🟡 **Intermediário** · Módulo 07 · Projeto Prático

Vamos construir um app de clima completo que consome a API gratuita do Open-Meteo (sem necessidade de cadastro ou API key).

---

## Funcionalidades

- ✅ Buscar clima atual por nome de cidade (via geocoding)
- ✅ Exibir temperatura, umidade e condição
- ✅ Previsão para 7 dias
- ✅ Suporte a modo escuro

---

## APIs utilizadas

| API | URL | Custo |
|---|---|---|
| Open-Meteo | `https://api.open-meteo.com/v1/forecast` | Gratuita |
| Geocoding | `https://geocoding-api.open-meteo.com/v1/search` | Gratuita |

---

## Modelos

```swift
// Resposta do geocoding
struct GeocodingResposta: Codable {
    let results: [Cidade]?
}

struct Cidade: Codable, Identifiable {
    let id:        Int
    let name:      String
    let country:   String
    let latitude:  Double
    let longitude: Double
}

// Resposta do clima
struct ClimaResposta: Codable {
    let current: ClimaAtual
    let daily:   PrevisaoDiaria
}

struct ClimaAtual: Codable {
    let temperature2m:        Double
    let relativeHumidity2m:   Int
    let weatherCode:          Int
    let windSpeed10m:         Double

    enum CodingKeys: String, CodingKey {
        case temperature2m       = "temperature_2m"
        case relativeHumidity2m  = "relative_humidity_2m"
        case weatherCode         = "weather_code"
        case windSpeed10m        = "wind_speed_10m"
    }
}

struct PrevisaoDiaria: Codable {
    let time:            [String]
    let temperature2mMax: [Double]
    let temperature2mMin: [Double]
    let weatherCode:     [Int]

    enum CodingKeys: String, CodingKey {
        case time
        case temperature2mMax = "temperature_2m_max"
        case temperature2mMin = "temperature_2m_min"
        case weatherCode      = "weather_code"
    }

    var diasPrevisao: [DiaPrevisao] {
        zip(zip(time, temperature2mMax), zip(temperature2mMin, weatherCode))
            .map { outer, inner in
                DiaPrevisao(data: outer.0, maxTemp: outer.1,
                            minTemp: inner.0, codigo: inner.1)
            }
    }
}

struct DiaPrevisao: Identifiable {
    let id = UUID()
    let data: String
    let maxTemp: Double
    let minTemp: Double
    let codigo: Int

    var icone: String { WeatherCode.icone(for: codigo) }
    var descricao: String { WeatherCode.descricao(for: codigo) }
}

// Mapeamento de códigos WMO para ícones
enum WeatherCode {
    static func icone(for code: Int) -> String {
        switch code {
        case 0:       return "sun.max.fill"
        case 1...3:   return "cloud.sun.fill"
        case 45, 48:  return "cloud.fog.fill"
        case 51...67: return "cloud.drizzle.fill"
        case 71...77: return "snowflake"
        case 80...82: return "cloud.heavyrain.fill"
        case 85, 86:  return "cloud.snow.fill"
        case 95...99: return "cloud.bolt.fill"
        default:      return "questionmark.circle"
        }
    }

    static func descricao(for code: Int) -> String {
        switch code {
        case 0:       return "Céu limpo"
        case 1:       return "Principalmente limpo"
        case 2:       return "Parcialmente nublado"
        case 3:       return "Nublado"
        case 45, 48:  return "Neblina"
        case 51...55: return "Garoa"
        case 61...65: return "Chuva"
        case 71...75: return "Neve"
        case 80...82: return "Pancadas de chuva"
        case 95:      return "Tempestade"
        default:      return "Desconhecido"
        }
    }
}
```

---

## Service

```swift
final class ClimaService {

    func buscarCidade(nome: String) async throws -> [Cidade] {
        let encoded = nome.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? nome
        let url = URL(string: "https://geocoding-api.open-meteo.com/v1/search?name=\(encoded)&count=5&language=pt&format=json")!
        let (data, _) = try await URLSession.shared.data(from: url)
        let resposta  = try JSONDecoder().decode(GeocodingResposta.self, from: data)
        return resposta.results ?? []
    }

    func buscarClima(lat: Double, lon: Double) async throws -> ClimaResposta {
        let url = URL(string: """
            https://api.open-meteo.com/v1/forecast\
            ?latitude=\(lat)&longitude=\(lon)\
            &current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m\
            &daily=temperature_2m_max,temperature_2m_min,weather_code\
            &timezone=auto&forecast_days=7
            """.replacingOccurrences(of: "\n", with: ""))!
        let (data, _) = try await URLSession.shared.data(from: url)
        return try JSONDecoder().decode(ClimaResposta.self, from: data)
    }
}
```

---

## ViewModel

```swift
@Observable
final class ClimaViewModel {
    var cidadeBuscada = ""
    var cidadesEncontradas: [Cidade] = []
    var cidadeSelecionada: Cidade?
    var clima: ClimaResposta?
    var isLoading = false
    var errorMessage: String?

    private let service = ClimaService()

    var temperaturaAtual: String {
        guard let t = clima?.current.temperature2m else { return "--" }
        return "\(Int(t))°C"
    }

    @MainActor
    func buscarCidades() async {
        guard !cidadeBuscada.isEmpty else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            cidadesEncontradas = try await service.buscarCidade(nome: cidadeBuscada)
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    @MainActor
    func selecionarCidade(_ cidade: Cidade) async {
        cidadeSelecionada = cidade
        cidadesEncontradas = []
        isLoading = true
        defer { isLoading = false }
        do {
            clima = try await service.buscarClima(lat: cidade.latitude, lon: cidade.longitude)
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
```

---

## View principal

```swift
struct ClimaView: View {
    @State private var vm = ClimaViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 24) {
                    if vm.isLoading {
                        ProgressView().padding(.top, 100)
                    } else if let clima = vm.clima,
                              let cidade = vm.cidadeSelecionada {
                        ClimaAtualView(cidade: cidade, clima: clima)
                        PrevisaoSemanaView(dias: clima.daily.diasPrevisao)
                    } else {
                        placeholderView
                    }
                }
                .padding()
            }
            .navigationTitle("Clima")
            .searchable(text: $vm.cidadeBuscada,
                        prompt: "Buscar cidade...")
            .onSubmit(of: .search) {
                Task { await vm.buscarCidades() }
            }
            .searchSuggestions {
                ForEach(vm.cidadesEncontradas) { cidade in
                    Button("\(cidade.name), \(cidade.country)") {
                        Task { await vm.selecionarCidade(cidade) }
                    }
                }
            }
        }
    }

    var placeholderView: some View {
        ContentUnavailableView("Busque uma cidade",
            systemImage: "magnifyingglass",
            description: Text("Digite o nome de uma cidade para ver o clima"))
            .padding(.top, 60)
    }
}

struct ClimaAtualView: View {
    let cidade: Cidade
    let clima:  ClimaResposta

    var body: some View {
        VStack(spacing: 8) {
            Text("\(cidade.name), \(cidade.country)")
                .font(.title2).fontWeight(.semibold)
            Image(systemName: WeatherCode.icone(for: clima.current.weatherCode))
                .font(.system(size: 72))
                .symbolRenderingMode(.multicolor)
            Text("\(Int(clima.current.temperature2m))°C")
                .font(.system(size: 64, weight: .thin))
            Text(WeatherCode.descricao(for: clima.current.weatherCode))
                .foregroundStyle(.secondary)
            HStack(spacing: 24) {
                Label("\(clima.current.relativeHumidity2m)%",
                      systemImage: "humidity")
                Label("\(Int(clima.current.windSpeed10m)) km/h",
                      systemImage: "wind")
            }
            .font(.callout)
            .foregroundStyle(.secondary)
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(.ultraThinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 20))
    }
}
```

---

## Checklist do Módulo 07

- [x] Faço requests com URLSession e async/await
- [x] Decodifico JSON complexo com Codable
- [x] Uso `async let` para requests paralelos
- [x] Estruturo um NetworkService reutilizável
- [x] Uso o Repository Pattern para abstração
- [x] Construí o app de clima consumindo API real

**Próximo módulo:** [Testes →](../08-testes/index.md)
