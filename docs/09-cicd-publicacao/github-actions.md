# GitHub Actions para iOS

🔴 **Avançado** · Módulo 09

GitHub Actions permite automatizar testes, builds e deploys diretamente no GitHub, sem servidor próprio.

---

## Workflow de testes (CI)

```yaml
# .github/workflows/ci.yml
name: CI — Testes iOS

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    name: Testes Unitários e UI
    runs-on: macos-14        # (1) macOS com Apple Silicon

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Selecionar Xcode
        uses: maxim-lobanov/setup-xcode@v1
        with:
          xcode-version: '15.4'    # (2) Versão específica do Xcode

      - name: Cache SPM
        uses: actions/cache@v4
        with:
          path: |
            ~/Library/Developer/Xcode/DerivedData
            .build
          key: ${{ runner.os }}-spm-${{ hashFiles('**/Package.resolved') }}

      - name: Rodar Testes
        run: |
          xcodebuild test \
            -scheme MeuApp \
            -destination 'platform=iOS Simulator,name=iPhone 15 Pro,OS=17.5' \
            -resultBundlePath TestResults.xcresult \
            | xcpretty             # (3) Formatação legível dos logs

      - name: Upload resultados
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: TestResults
          path: TestResults.xcresult

# (1) Builds iOS precisam de macOS runner
# (2) Fixar versão evita surpresas com updates automáticos
# (3) xcpretty: gem install xcpretty
```

---

## Workflow de deploy para TestFlight

```yaml
# .github/workflows/deploy.yml
name: Deploy → TestFlight

on:
  push:
    branches: [main]
  workflow_dispatch:     # (1) Permite disparar manualmente

jobs:
  deploy:
    name: Build e Deploy
    runs-on: macos-14
    environment: production   # (2) Requer aprovação no GitHub

    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0      # Necessário para fastlane versioning

      - name: Selecionar Xcode
        uses: maxim-lobanov/setup-xcode@v1
        with:
          xcode-version: '15.4'

      - name: Instalar Ruby
        uses: ruby/setup-ruby@v1
        with:
          ruby-version: '3.2'
          bundler-cache: true   # (3) Cache de gems

      - name: Instalar Fastlane
        run: gem install fastlane

      - name: Configurar certificados (Match)
        env:
          MATCH_PASSWORD:    ${{ secrets.MATCH_PASSWORD }}      # (4)
          MATCH_GIT_TOKEN:   ${{ secrets.MATCH_GIT_TOKEN }}
        run: fastlane match appstore --readonly

      - name: Build e Upload
        env:
          APP_STORE_CONNECT_API_KEY_ID:      ${{ secrets.ASC_KEY_ID }}
          APP_STORE_CONNECT_API_ISSUER_ID:   ${{ secrets.ASC_ISSUER_ID }}
          APP_STORE_CONNECT_API_KEY_CONTENT: ${{ secrets.ASC_KEY_CONTENT }}
        run: fastlane beta

# (1) workflow_dispatch permite deploy manual pelo GitHub UI
# (2) environment: production requer aprovação de um reviewer
# (3) bundler-cache economiza ~2 minutos de instalação
# (4) Secrets configurados em Settings → Secrets and variables
```

---

## Secrets necessários

Configure em **Settings → Secrets and variables → Actions**:

| Secret | Descrição |
|---|---|
| `MATCH_PASSWORD` | Senha de encriptação do repositório Match |
| `MATCH_GIT_TOKEN` | Token para acessar o repo privado de certs |
| `ASC_KEY_ID` | ID da App Store Connect API Key |
| `ASC_ISSUER_ID` | Issuer ID do App Store Connect |
| `ASC_KEY_CONTENT` | Conteúdo do arquivo .p8 (base64) |

---

## Verificação de PR com swiftlint

```yaml
# .github/workflows/lint.yml
name: SwiftLint

on: [pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest     # (1) SwiftLint pode rodar no Linux!
    steps:
      - uses: actions/checkout@v4
      - name: SwiftLint
        uses: norio-nomura/action-swiftlint@3.2.1
        with:
          args: --strict
```

---

## Checklist

- [x] Tenho um workflow de CI que roda testes em PRs
- [x] Tenho um workflow de deploy automático para TestFlight
- [x] Configuro secrets de forma segura no GitHub
- [x] Uso caching para acelerar builds
