# TestFlight e App Store Connect

🔴 **Avançado** · Módulo 09

Antes de publicar na App Store, todo app passa pelo TestFlight — a plataforma de beta testing da Apple. Este capítulo cobre o processo completo de publicação.

---

## App Store Connect

App Store Connect (appstoreconnect.apple.com) é o painel da Apple para:

- Gerenciar apps e versões
- Configurar TestFlight
- Revisar métricas e crashes
- Responder reviews
- Configurar precificação

---

## Configuração inicial

### 1. Bundle Identifier
```
com.empresa.nomedo app  ← deve ser único no mundo
```

### 2. Certificados e Perfis

| Tipo | Uso |
|---|---|
| Development Certificate | Rodar no dispositivo físico |
| Distribution Certificate | Publicar na App Store |
| Ad Hoc Profile | Distribuição para testers específicos |
| App Store Profile | Publicação na App Store |

!!! tip "Use Match (Fastlane)"
    Gerenciar certificados manualmente é trabalhoso. Use `fastlane match` para sincronizar tudo automaticamente entre máquinas e CI.

---

## Subindo para TestFlight

### Via Xcode

1. **Product → Archive** (ou ⌘⇧B em modo Archive)
2. Na janela **Organizer**, selecione o arquivo
3. Clique em **Distribute App**
4. Escolha **App Store Connect**
5. Aguarde o processamento (~15 minutos)

### Via Fastlane (recomendado)

```bash
fastlane beta
```

---

## Testers internos vs externos

| | Internos | Externos |
|---|---|---|
| Limite | 100 pessoas | 10.000 pessoas |
| Revisão Apple | Não necessária | Necessária (1x) |
| Acesso | Imediato após upload | Após aprovação |
| Quem são | Membros da Developer Account | Qualquer e-mail |

---

## Checklist de submissão para App Store

Antes de submeter para revisão, verifique:

- [ ] Ícone do app em todos os tamanhos (1024x1024 para App Store)
- [ ] Screenshots para iPhone e iPad (se suportar)
- [ ] Descrição em todos os idiomas suportados
- [ ] Palavras-chave relevantes
- [ ] URL de suporte e privacidade
- [ ] Rating de conteúdo preenchido
- [ ] Export Compliance preenchido
- [ ] Não há referências a plataformas concorrentes
- [ ] Não usa APIs privadas da Apple
- [ ] Login de demonstração fornecido (se o app requer login)

---

## Motivos comuns de rejeição

!!! warning "Rejeições comuns"
    1. **Funcionalidade incompleta** — app com telas placeholder ou em construção
    2. **Guidelines de UI** — interface muito diferente das HIG da Apple
    3. **Informações de privacidade** — falta de explicação para permissões solicitadas
    4. **Conteúdo de terceiros** — uso de marcas/logos sem permissão
    5. **Login with Apple** — apps com login social devem oferecer "Sign in with Apple"

---

## Checklist

- [x] Entendo a diferença entre testers internos e externos
- [x] Sei configurar certificados e perfis
- [x] Conheço o checklist de submissão para App Store
- [x] Sei como usar fastlane para automatizar o processo
