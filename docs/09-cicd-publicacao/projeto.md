# Mini-Projeto: Pipeline CI/CD Completo

🔴 **Avançado** · Módulo 09 · Projeto Prático

Vamos montar um pipeline completo para o app de clima: testes automáticos em PRs e deploy para TestFlight a cada push na main.

---

## Estrutura final

```
MeuApp/
├── .github/
│   └── workflows/
│       ├── ci.yml          ← Testes em PRs
│       └── deploy.yml      ← Deploy para TestFlight
├── fastlane/
│   ├── Fastfile
│   ├── Appfile
│   └── Matchfile
└── MeuApp.xcodeproj
```

---

## Appfile

```ruby
# fastlane/Appfile
app_identifier "com.empresa.climaapp"
apple_id       "dev@empresa.com"
team_id        "XXXXXXXXXX"   # Seu Team ID no Developer Portal
```

---

## Matchfile

```ruby
# fastlane/Matchfile
git_url        "https://github.com/empresa/certs-privados"
storage_mode   "git"
type           "appstore"
app_identifier "com.empresa.climaapp"
username       "dev@empresa.com"
```

---

## Fastfile completo

```ruby
# fastlane/Fastfile
default_platform(:ios)

platform :ios do

  before_all do
    ensure_git_status_clean unless is_ci
  end

  lane :test do
    run_tests(
      scheme:      "ClimaApp",
      clean:       true,
      code_coverage: true
    )
  end

  lane :beta do
    sync_code_signing(type: "appstore", readonly: is_ci)

    increment_build_number(
      build_number: latest_testflight_build_number(
        app_identifier: "com.empresa.climaapp"
      ) + 1
    )

    build_app(
      scheme:        "ClimaApp",
      export_method: "app-store"
    )

    upload_to_testflight(
      skip_waiting_for_build_processing: true,
      changelog: last_git_commit[:message]
    )

    clean_build_artifacts
  end

end
```

---

## Workflow CI — ci.yml

```yaml
name: CI

on:
  pull_request:
    branches: [main, develop]

jobs:
  test:
    runs-on: macos-14
    steps:
      - uses: actions/checkout@v4
      - uses: maxim-lobanov/setup-xcode@v1
        with:
          xcode-version: '15.4'
      - run: gem install fastlane
      - run: fastlane test
```

---

## Workflow Deploy — deploy.yml

```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: macos-14
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - uses: maxim-lobanov/setup-xcode@v1
        with:
          xcode-version: '15.4'

      - name: Instalar Fastlane
        run: gem install fastlane

      - name: Match + Build + Upload
        env:
          MATCH_PASSWORD:  ${{ secrets.MATCH_PASSWORD }}
          MATCH_GIT_TOKEN: ${{ secrets.MATCH_GIT_TOKEN }}
          APP_STORE_CONNECT_API_KEY_ID:      ${{ secrets.ASC_KEY_ID }}
          APP_STORE_CONNECT_API_ISSUER_ID:   ${{ secrets.ASC_ISSUER_ID }}
          APP_STORE_CONNECT_API_KEY_CONTENT: ${{ secrets.ASC_KEY_CONTENT }}
        run: fastlane beta
```

---

## Checklist do Módulo 09

- [x] Configuro Fastlane com lanes para test, beta e release
- [x] Uso Match para gerenciar certificados automaticamente
- [x] Crio workflows GitHub Actions para CI e deploy
- [x] Configuro secrets de forma segura
- [x] Entendo o processo de publicação no TestFlight e App Store

**Próximo módulo:** [Avançado →](../10-avancado/index.md)
