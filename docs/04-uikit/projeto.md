# Mini-Projeto: App de Contatos UIKit

🟡 **Intermediário** · Módulo 04 · Projeto Prático

Vamos construir um app de contatos completo usando UIKit puro (sem Storyboard), com lista de contatos, tela de detalhe e formulário de criação.

---

## Estrutura do projeto

```
ContatosApp/
├── Models/
│   └── Contato.swift
├── ViewControllers/
│   ├── ListaContatosVC.swift
│   ├── DetalheContatoVC.swift
│   └── FormularioContatoVC.swift
├── Cells/
│   └── ContatoTableViewCell.swift
└── SceneDelegate.swift
```

---

## Modelo

```swift
// Contato.swift
struct Contato: Identifiable, Equatable {
    let id = UUID()
    var nome:      String
    var telefone:  String
    var email:     String
    var favorito:  Bool = false
    var inicial:   String { String(nome.prefix(1)).uppercased() }
}
```

---

## Lista de contatos

```swift
// ListaContatosVC.swift
final class ListaContatosVC: UIViewController {

    // MARK: - UI
    private let tableView = UITableView(frame: .zero, style: .plain)
    private let searchController = UISearchController(searchResultsController: nil)

    // MARK: - Dados
    private var todosContatos: [Contato] = Contato.exemplos
    private var contatosFiltrados: [Contato] = []

    private var contatosExibidos: [Contato] {
        searchController.isActive && !searchController.searchBar.text!.isEmpty
            ? contatosFiltrados
            : todosContatos
    }

    // MARK: - Ciclo de vida
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Contatos"
        view.backgroundColor = .systemBackground
        configurarSearchController()
        configurarTableView()
        configurarToolbar()
    }

    // MARK: - Setup
    private func configurarTableView() {
        view.addSubview(tableView)
        tableView.frame = view.bounds
        tableView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        tableView.register(ContatoCell.self,
                           forCellReuseIdentifier: ContatoCell.reuseID)
        tableView.dataSource = self
        tableView.delegate   = self
    }

    private func configurarSearchController() {
        searchController.searchResultsUpdater = self
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "Buscar contatos"
        navigationItem.searchController = searchController
    }

    private func configurarToolbar() {
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .add,
            target: self,
            action: #selector(novoContato)
        )
    }

    @objc private func novoContato() {
        let vc = FormularioContatoVC()
        vc.delegate = self
        present(UINavigationController(rootViewController: vc), animated: true)
    }
}

// MARK: - DataSource
extension ListaContatosVC: UITableViewDataSource {

    func tableView(_ tv: UITableView, numberOfRowsInSection section: Int) -> Int {
        contatosExibidos.count
    }

    func tableView(_ tv: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tv.dequeueReusableCell(
            withIdentifier: ContatoCell.reuseID, for: indexPath) as! ContatoCell
        cell.configurar(com: contatosExibidos[indexPath.row])
        return cell
    }

    func tableView(_ tv: UITableView,
                   commit editingStyle: UITableViewCell.EditingStyle,
                   forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let contato = contatosExibidos[indexPath.row]
            todosContatos.removeAll { $0.id == contato.id }
            tv.deleteRows(at: [indexPath], with: .fade)
        }
    }
}

// MARK: - Delegate
extension ListaContatosVC: UITableViewDelegate {
    func tableView(_ tv: UITableView, didSelectRowAt indexPath: IndexPath) {
        tv.deselectRow(at: indexPath, animated: true)
        let contato = contatosExibidos[indexPath.row]
        let detalhe = DetalheContatoVC(contato: contato)
        navigationController?.pushViewController(detalhe, animated: true)
    }

    func tableView(_ tv: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        64
    }
}

// MARK: - Search
extension ListaContatosVC: UISearchResultsUpdating {
    func updateSearchResults(for sc: UISearchController) {
        let busca = sc.searchBar.text ?? ""
        contatosFiltrados = todosContatos.filter {
            $0.nome.localizedCaseInsensitiveContains(busca) ||
            $0.email.localizedCaseInsensitiveContains(busca)
        }
        tableView.reloadData()
    }
}

// MARK: - Delegate do formulário
extension ListaContatosVC: FormularioContatoDelegate {
    func contatoCriado(_ contato: Contato) {
        todosContatos.append(contato)
        todosContatos.sort { $0.nome < $1.nome }
        tableView.reloadData()
    }
}
```

---

## Configurando o SceneDelegate

```swift
func scene(_ scene: UIScene,
           willConnectTo session: UISceneSession,
           options: UIScene.ConnectionOptions) {
    guard let windowScene = (scene as? UIWindowScene) else { return }
    window = UIWindow(windowScene: windowScene)
    let vc  = ListaContatosVC()
    let nav = UINavigationController(rootViewController: vc)
    nav.navigationBar.prefersLargeTitles = true
    window?.rootViewController = nav
    window?.makeKeyAndVisible()
}
```

---

## Checklist do Módulo 04

- [x] Sei o ciclo de vida de um UIViewController
- [x] Crio layouts programáticos com Auto Layout
- [x] Implemento UITableView com células customizadas
- [x] Uso UISearchController para busca
- [x] Navego entre VCs com UINavigationController
- [x] Apresento VCs modalmente
- [x] Construí o app de contatos UIKit completo

**Próximo módulo:** [Arquitetura →](../05-arquitetura/index.md)
