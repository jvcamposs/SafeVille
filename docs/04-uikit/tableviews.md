# Table Views e Collection Views

🟡 **Intermediário** · Módulo 04

`UITableView` e `UICollectionView` são os componentes de lista mais poderosos do UIKit. Embora SwiftUI os abstraia, entendê-los é essencial para trabalhar em apps legados e para casos de uso avançados.

---

## UITableView básica

```swift
class ContatosViewController: UIViewController {

    // MARK: - Propriedades

    private let tableView = UITableView(frame: .zero, style: .insetGrouped)

    private var contatos: [String] = [
        "Ana Silva", "Bruno Costa", "Carla Mendes", "Diego Rocha"
    ]

    // MARK: - Ciclo de vida

    override func viewDidLoad() {
        super.viewDidLoad()
        configurarTableView()
    }

    // MARK: - Setup

    private func configurarTableView() {
        view.addSubview(tableView)
        tableView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])

        tableView.register(UITableViewCell.self,              // (1)
                           forCellReuseIdentifier: "Cell")
        tableView.dataSource = self
        tableView.delegate   = self
    }
}

// MARK: - DataSource

extension ContatosViewController: UITableViewDataSource {

    func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        contatos.count
    }

    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(              // (2)
            withIdentifier: "Cell", for: indexPath)

        var config = cell.defaultContentConfiguration()
        config.text       = contatos[indexPath.row]
        config.image      = UIImage(systemName: "person.circle")
        cell.contentConfiguration = config
        return cell
    }
}

// MARK: - Delegate

extension ContatosViewController: UITableViewDelegate {

    func tableView(_ tableView: UITableView,
                   didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        print("Selecionado: \(contatos[indexPath.row])")
    }
}

// (1) Registrar a célula antes de usar
// (2) dequeueReusableCell reutiliza células fora da tela — ESSENCIAL para performance
```

---

## Célula customizada

```swift
final class ContatoCell: UITableViewCell {

    static let reuseID = "ContatoCell"

    private let avatarView: UIImageView = {
        let iv = UIImageView()
        iv.contentMode = .scaleAspectFill
        iv.clipsToBounds = true
        iv.layer.cornerRadius = 22
        iv.translatesAutoresizingMaskIntoConstraints = false
        return iv
    }()

    private let nomeLabel: UILabel = {
        let l = UILabel()
        l.font = .systemFont(ofSize: 16, weight: .semibold)
        l.translatesAutoresizingMaskIntoConstraints = false
        return l
    }()

    private let subtituloLabel: UILabel = {
        let l = UILabel()
        l.font = .systemFont(ofSize: 13)
        l.textColor = .secondaryLabel
        l.translatesAutoresizingMaskIntoConstraints = false
        return l
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupLayout()
    }

    required init?(coder: NSCoder) { fatalError() }

    private func setupLayout() {
        let stack = UIStackView(arrangedSubviews: [nomeLabel, subtituloLabel])
        stack.axis    = .vertical
        stack.spacing = 2
        stack.translatesAutoresizingMaskIntoConstraints = false

        contentView.addSubview(avatarView)
        contentView.addSubview(stack)

        NSLayoutConstraint.activate([
            avatarView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            avatarView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            avatarView.widthAnchor.constraint(equalToConstant: 44),
            avatarView.heightAnchor.constraint(equalToConstant: 44),

            stack.leadingAnchor.constraint(equalTo: avatarView.trailingAnchor, constant: 12),
            stack.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            stack.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
        ])
    }

    func configurar(nome: String, subtitulo: String) {
        nomeLabel.text      = nome
        subtituloLabel.text = subtitulo
        avatarView.image    = UIImage(systemName: "person.circle.fill")
    }
}
```

---

## Diffable Data Source (moderno)

```swift
// Enum para seções (requer Hashable)
enum Secao: Int { case principal }

class ListaModernaVC: UIViewController {

    private let tableView = UITableView()

    // (1) Diffable Data Source — o jeito moderno
    private var dataSource: UITableViewDiffableDataSource<Secao, String>!

    private var itens: [String] = ["Item 1", "Item 2", "Item 3"]

    override func viewDidLoad() {
        super.viewDidLoad()
        configurar()
        aplicarSnapshot()
    }

    private func configurar() {
        view.addSubview(tableView)
        tableView.frame = view.bounds
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")

        dataSource = UITableViewDiffableDataSource(
            tableView: tableView
        ) { tableView, indexPath, item in
            let cell = tableView.dequeueReusableCell(withIdentifier: "Cell",
                                                     for: indexPath)
            var config = cell.defaultContentConfiguration()
            config.text = item
            cell.contentConfiguration = config
            return cell
        }
    }

    private func aplicarSnapshot(animated: Bool = true) {
        var snapshot = NSDiffableDataSourceSnapshot<Secao, String>()
        snapshot.appendSections([.principal])
        snapshot.appendItems(itens, toSection: .principal)
        dataSource.apply(snapshot, animatingDifferences: animated)  // (2)
    }

    func adicionarItem(_ item: String) {
        itens.append(item)
        aplicarSnapshot()  // (3) Animação automática das diferenças
    }
}

// (1) Substitui delegate/dataSource antigo — sem mais erros de consistência
// (2) Calcula diff automaticamente e anima
// (3) Chame sempre que os dados mudarem
```

---

## UICollectionView com Compositional Layout

```swift
class GaleriaViewController: UIViewController {

    private var collectionView: UICollectionView!

    override func viewDidLoad() {
        super.viewDidLoad()
        let layout = criarLayout()
        collectionView = UICollectionView(frame: view.bounds,
                                          collectionViewLayout: layout)
        collectionView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        collectionView.register(UICollectionViewCell.self,
                                forCellWithReuseIdentifier: "Cell")
        collectionView.dataSource = self
        view.addSubview(collectionView)
    }

    private func criarLayout() -> UICollectionViewLayout {
        // (1) Grade de 3 colunas
        let itemSize = NSCollectionLayoutSize(
            widthDimension:  .fractionalWidth(1/3),
            heightDimension: .fractionalWidth(1/3)  // quadrado
        )
        let item  = NSCollectionLayoutItem(layoutSize: itemSize)
        item.contentInsets = NSDirectionalEdgeInsets(top: 2, leading: 2, bottom: 2, trailing: 2)

        let groupSize = NSCollectionLayoutSize(
            widthDimension:  .fractionalWidth(1.0),
            heightDimension: .fractionalWidth(1/3)
        )
        let group   = NSCollectionLayoutGroup.horizontal(layoutSize: groupSize, subitems: [item])
        let section = NSCollectionLayoutSection(group: group)
        return UICollectionViewCompositionalLayout(section: section)
    }
}

// (1) Compositional Layout é extremamente flexível
```

---

## Checklist

- [x] Implemento `UITableView` com `dataSource` e `delegate`
- [x] Crio células customizadas com `UITableViewCell`
- [x] Uso `dequeueReusableCell` para performance
- [x] Aplico `Diffable Data Source` para atualizações animadas
- [x] Crio layouts com `UICollectionViewCompositionalLayout`
