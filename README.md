
# 🐍 SQL Parser en Scala

Bienvenue dans notre projet de **SQL Parser** en Scala ! Ce projet utilise la bibliothèque [FastParse](https://github.com/com-lihaoyi/fastparse) pour analyser des requêtes SQL simples et générer un plan d'exécution. Il a été développé par six étudiants de 3e année à l'ESIEE Paris dans le cadre de leur formation en ingénierie. 👨‍🎓👩‍🎓

## 📋 Description du Projet

L'objectif est de créer un parser capable de reconnaître et d'interpréter des requêtes SQL contenant des clauses comme `SELECT`, `WHERE`, `ORDER BY`, et `LIMIT`. En analysant la requête, le parser génère un **plan d'exécution** sous forme d'AST (arbre syntaxique abstrait) pour que les requêtes puissent être traitées de manière structurée.

## 🏗️ Architecture du Projet

Le projet est organisé de manière modulaire afin de séparer les différentes responsabilités :

### 📂 Structure des Dossiers

```
parsersql
├── src
│   ├── main
│   │   └── scala
│   │       └── sqlparser
│   │           ├── Main.scala             // Point d'entrée pour exécuter le parser
│   │           ├── Models.scala           // Définition des modèles de données (AST)
│   │           ├── SqlParser.scala        // Interface du parser SQL
│   │           └── SqlParserImpl.scala    // Implémentation concrète du parser avec FastParse
│   └── test
│       └── scala
│           └── sqlparser
│               └── SqlParserSpec.scala    // Tests unitaires pour le parser SQL
```

### 📝 Explication des Fichiers Principaux

- **`SqlParser.scala`** : Ce fichier définit un **trait** Scala, `SqlParser`, qui est une interface pour le parser SQL. La méthode `parse` y est définie pour prendre une chaîne SQL et renvoyer soit un `ExecutionPlan`, soit une erreur `SqlParsingError`.

- **`Models.scala`** : Ce fichier contient les classes et traits représentant les éléments de l'AST, y compris :
  - **`ExecutionPlan`** : Représente la structure d'exécution d'une requête.
  - **Expressions** : Les différentes expressions de comparaison (`Equals`, `GreaterThan`, etc.) et logiques (`And`, `Or`, `Not`).
  - **`OrderBy`** et **`Range`** : Pour les expressions de tri et de limitation.

- **`SqlParserImpl.scala`** : C'est l'implémentation du parser SQL. Utilisant FastParse, ce fichier contient :
  - **Méthodes de parsing** pour chaque composant SQL : `selectStmt`, `fields`, `where`, `orderBy`, `range`, etc.
  - **Gestion des erreurs** : Si le parsing échoue, une erreur `SyntaxError` est renvoyée avec un message détaillé.
  
- **`Main.scala`** : Ce fichier contient le point d'entrée du programme. Il prend en argument une requête SQL, la passe au parser, et affiche soit le plan d'exécution, soit une erreur de syntaxe.

## ✨ Fonctionnalités

Le parser prend en charge les fonctionnalités suivantes :

- **`SELECT`** : Extraction de champs spécifiques ou de tous les champs avec `*`.
- **`WHERE`** : Filtrage des données avec des conditions de comparaison (`=`, `!=`, `>`, `>=`, `<`, `<=`) et des opérateurs logiques (`AND`, `OR`, `NOT`).
- **`ORDER BY`** : Tri des résultats par colonne, en ordre ascendant ou descendant.
- **`RANGE` / `LIMIT`** : Limitation du nombre de lignes retournées.

## 🛠️ Exemple de Code - Parsing d'une Requête SQL

Voici comment le parser fonctionne avec une requête SQL basique :

```scala
val sql = "SELECT * FROM users WHERE age > 18 ORDER BY name DESC LIMIT 10"
val result = SqlParserImpl.parse(sql)
result match {
  case Right(plan) => println(s"Plan d'exécution : $plan")
  case Left(error) => println(s"Erreur de syntaxe : ${error.message}")
}
```

### 📤 Exemple de Sortie

```plaintext
Plan d'exécution : SelectPlan(fields=Seq("*"), table="users", where=Some(GreaterThan("age", "18")), orderBy=Some(OrderBy("name", Descending())), range=Some(Range(0, 10)))
```

## ✅ Tests

Des tests unitaires sont fournis dans **`SqlParserSpec.scala`** pour vérifier le bon fonctionnement du parser. Voici un aperçu des tests :

- **Tests de base** : Vérifie la validité de requêtes simples comme `SELECT * FROM table`.
- **Conditions `WHERE`** : Valide les expressions de comparaison et les combinaisons logiques.
- **Tri avec `ORDER BY`** : Teste le tri dans les deux sens (ascendant et descendant).
- **`RANGE` et `LIMIT`** : Vérifie que les limitations de lignes fonctionnent correctement.
- **Erreurs de syntaxe** : S'assure que les erreurs sont détectées et traitées correctement.

### 📌 Exemple de Test

```scala
test("Parse SELECT with WHERE clause using Equals") {
  val sql = "SELECT * FROM users WHERE age = 30"
  val result = SqlParserImpl.parse(sql)
  assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(Equals("age", "30")))))
}
```

Pour exécuter les tests :

```bash
sbt test
```

## 🚀 Installation et Utilisation

### Prérequis

- [Scala](https://www.scala-lang.org/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/)

### Installation

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/LucienLaumont/E5_Scala_parser-sql.git
   cd parserSQL
   ```

2. Compilez le projet avec sbt :
   ```bash
   sbt compile
   ```

### Lancement

Exécutez le parser avec `sbt run` et fournissez une requête SQL en argument :

```bash
sbt "run SELECT * FROM users WHERE age > 21 LIMIT 5"
```

La réponse attendue devrait être :

```bash
Execution Plan: SelectPlan(List(*),users,Some(GreaterThan(age,21)),None,Some(Range(0,5)))
```

## 📚 Guide pour les Contributeurs

Ce projet a été réalisé par six étudiants de l'ESIEE Paris en 3e année d'ingénierie. Nous encourageons les contributions et suggestions d'amélioration ! Voici quelques idées :

- Ajouter des fonctionnalités SQL supplémentaires (`JOIN`, `GROUP BY`, `HAVING`).
- Optimiser les performances du parser.
- Améliorer la gestion des erreurs et fournir des messages d'erreur plus détaillés.

## 📜 Licence

Ce projet est sous licence MIT. Consultez le fichier [LICENSE](./LICENSE) pour plus de détails.

## 🎓 Auteurs

- **Équipe d'étudiants de l'ESIEE Paris** : Ce projet a été réalisé par six étudiants dans le cadre de leur formation.

