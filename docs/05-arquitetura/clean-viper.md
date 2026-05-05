# Clean Architecture e VIPER

🔴 **Avançado** · Módulo 05

Clean Architecture e VIPER são padrões para apps de grande escala, usados em equipes grandes onde cada módulo precisa ser independente e testável.

---

## Clean Architecture

Proposta por Robert C. Martin ("Uncle Bob"), divide o app em camadas concêntricas:

```
┌─────────────────────────────────────────┐
│  Frameworks & Drivers (UIKit, SwiftUI)  │
│  ┌───────────────────────────────────┐  │
│  │   Interface Adapters              │  │
│  │   (Presenters, ViewModels, VCs)   │  │
│  │  ┌────────────────────────────┐  │  │
│  │  │  Application Business      │  │  │
│  │  │  Rules (Use Cases)         │  │  │
│  │  │  ┌─────────────────────┐  │  │  │
│  │  │  │ Enterprise Business  │  │  │  │
│  │  │  │ Rules (Entities)     │  │  │  │
│  │  │  └─────────────────────┘  │  │  │
│  │  └────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Regra de dependência:** as setas apontam apenas para dentro. Camadas internas não conhecem camadas externas.

---

## VIPER

VIPER divide cada feature em 5 componentes:

| Letra | Componente | Responsabilidade |
|---|---|---|
| **V** | View | Exibe dados, captura input |
| **I** | Interactor | Lógica de negócio, casos de uso |
| **P** | Presenter | Formata dados para a View |
| **E** | Entity | Modelos de dados simples |
| **R** | Router | Navegação entre módulos |

---

## Exemplo: Feature de Login VIPER

```swift
// MARK: - Entities
struct UsuarioEntidade {
    let id: String
    let nome: String
    let email: String
}

// MARK: - Protocolos (contratos entre componentes)

protocol LoginViewProtocol: AnyObject {
    func mostrarLoading()
    func esconderLoading()
    func mostrarErro(_ mensagem: String)
    func loginSucesso()
}

protocol LoginPresenterProtocol: AnyObject {
    func loginTapped(email: String, senha: String)
}

protocol LoginInteractorProtocol: AnyObject {
    func fazerLogin(email: String, senha: String) async
}

protocol LoginRouterProtocol: AnyObject {
    func navigarParaHome()
}

// MARK: - Interactor (lógica de negócio)

final class LoginInteractor: LoginInteractorProtocol {
    weak var presenter: LoginPresenterProtocol?
    private let authService: AuthServiceProtocol

    init(authService: AuthServiceProtocol = AuthService()) {
        self.authService = authService
    }

    func fazerLogin(email: String, senha: String) async {
        do {
            _ = try await authService.login(email: email, senha: senha)
            await MainActor.run { presenter?.loginSucesso() }
        } catch {
            await MainActor.run {
                presenter?.loginFalhou(mensagem: error.localizedDescription)
            }
        }
    }
}

// MARK: - Presenter

final class LoginPresenter: LoginPresenterProtocol {
    weak var view: LoginViewProtocol?
    var interactor: LoginInteractorProtocol?
    var router: LoginRouterProtocol?

    func loginTapped(email: String, senha: String) {
        guard !email.isEmpty, !senha.isEmpty else {
            view?.mostrarErro("Preencha todos os campos")
            return
        }
        view?.mostrarLoading()
        Task { await interactor?.fazerLogin(email: email, senha: senha) }
    }

    func loginSucesso() {
        view?.esconderLoading()
        router?.navigarParaHome()
    }

    func loginFalhou(mensagem: String) {
        view?.esconderLoading()
        view?.mostrarErro(mensagem)
    }
}

// MARK: - Router

final class LoginRouter: LoginRouterProtocol {
    weak var viewController: UIViewController?

    static func criarModulo() -> UIViewController {
        let vc         = LoginViewController()
        let presenter  = LoginPresenter()
        let interactor = LoginInteractor()
        let router     = LoginRouter()

        vc.presenter        = presenter
        presenter.view      = vc
        presenter.interactor = interactor
        presenter.router    = router
        interactor.presenter = presenter as? any LoginPresenterOutputProtocol
        router.viewController = vc

        return vc
    }

    func navigarParaHome() {
        let homeVC = HomeRouter.criarModulo()
        viewController?.navigationController?.pushViewController(homeVC, animated: true)
    }
}
```

---

## Prós e contras

=== "Prós do VIPER"
    - ✅ Cada componente tem uma única responsabilidade
    - ✅ Altamente testável (todos os protocolos são mockáveis)
    - ✅ Features são isoladas — equipes diferentes podem trabalhar sem conflito
    - ✅ Escalável para apps muito grandes

=== "Contras do VIPER"
    - ❌ Muito boilerplate para features simples
    - ❌ Curva de aprendizado íngreme
    - ❌ Excesso de arquivos por feature
    - ❌ Comunicação entre componentes pode ser confusa

---

## Alternativa moderna: TCA

The Composable Architecture (TCA) do Point-Free é uma alternativa popular para SwiftUI que aplica Clean Architecture de forma funcional. Vale explorar após dominar MVVM.

---

## Checklist

- [x] Entendo os princípios do Clean Architecture
- [x] Conheço os cinco componentes do VIPER
- [x] Sei quando VIPER é (e não é) a escolha certa
- [x] Entendo a regra de dependência (setas para dentro)
