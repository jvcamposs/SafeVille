# Fastlane

🔴 **Avançado** · Módulo 09

Fastlane é a ferramenta open-source mais popular para automação de builds e publicação iOS/Android. Com ela, um único comando faz tudo: testes, build, assinatura e upload.

---

## Instalação

```bash
# Instalar via Homebrew
brew install fastlane

# Ou via RubyGems
sudo gem install fastlane

# Na raiz do projeto iOS:
fastlane init
```

---

## Estrutura do Fastfile

```ruby
# fastlane/Fastfile

default_platform(:ios)

platform :ios do

  # Variáveis de configuração
  APP_ID          = "com.empresa.meuapp"
  SCHEME          = "MeuApp"
  WORKSPACE       = "MeuApp.xcworkspace"
  BUNDLE_ID       = "com.empresa.meuapp"

  # ─── Lanes ──────────────────────────────────────

  desc "Rodar testes"
  lane :teste do
    run_tests(
      workspace: WORKSPACE,
      scheme:    SCHEME,
      device:    "iPhone 15 Pro",
      clean:     true
    )
  end

  desc "Build para TestFlight"
  lane :beta do
    # 1. Incrementar build number
    increment_build_number(
      build_number: latest_testflight_build_number + 1
    )

    # 2. Sincronizar certificados e perfis
    sync_code_signing(
      type:   "appstore",
      app_identifier: BUNDLE_ID
    )

    # 3. Build
    build_app(
      workspace: WORKSPACE,
      scheme:    SCHEME,
      export_method: "app-store"
    )

    # 4. Upload para TestFlight
    upload_to_testflight(
      skip_waiting_for_build_processing: true
    )

    # 5. Notificação Slack (opcional)
    slack(
      message:    "✅ Nova build enviada para TestFlight!",
      slack_url:  ENV["SLACK_WEBHOOK_URL"]
    ) if ENV["SLACK_WEBHOOK_URL"]
  end

  desc "Publicar na App Store"
  lane :release do
    teste        # Roda os testes primeiro
    beta         # Sobe para TestFlight
    upload_to_app_store(
      skip_metadata:    false,
      skip_screenshots: false,
      submit_for_review: true,
      automatic_release: false
    )
  end

  # ─── Callbacks ──────────────────────────────────

  error do |lane, exception|
    slack(
      message:    "❌ Erro na lane #{lane}: #{exception.message}",
      success:    false,
      slack_url:  ENV["SLACK_WEBHOOK_URL"]
    ) if ENV["SLACK_WEBHOOK_URL"]
  end

end
```

---

## Match — gerenciamento de certificados

```bash
# Inicializar match (repositório Git privado para certificados)
fastlane match init

# Criar/sincronizar certificados de desenvolvimento
fastlane match development

# Criar/sincronizar para App Store
fastlane match appstore
```

```ruby
# No Fastfile — usando match
lane :beta do
  sync_code_signing(
    type:           "appstore",
    app_identifier: BUNDLE_ID,
    git_url:        "https://github.com/empresa/certificados-privados",
    readonly:       is_ci  # (1) Em CI, apenas lê, não cria
  )
  build_app(...)
end

# (1) is_ci retorna true quando rodando em GitHub Actions
```

---

## Ações úteis do Fastlane

```ruby
# Capturar screenshots automaticamente
capture_screenshots(
  workspace:  WORKSPACE,
  scheme:     SCHEME,
  devices:    ["iPhone 15 Pro", "iPhone SE (3rd generation)", "iPad Pro (12.9-inch)"],
  languages:  ["pt-BR", "en-US"]
)

# Frame screenshots com frames de dispositivo
frame_screenshots(
  path:  "./fastlane/screenshots",
  white: false
)

# Incrementar versão semântica
increment_version_number(bump_type: "patch") # 1.0.0 → 1.0.1
increment_version_number(bump_type: "minor") # 1.0.0 → 1.1.0
increment_version_number(bump_type: "major") # 1.0.0 → 2.0.0

# Verificar se há problemas no código
swiftlint(
  mode:         :lint,
  config_file:  ".swiftlint.yml",
  strict:       true
)
```

---

## Checklist

- [x] Instalei Fastlane e inicializei no projeto
- [x] Criei lanes para test, beta e release
- [x] Configuro match para gerenciar certificados
- [x] Uso `is_ci` para comportamentos específicos de CI
