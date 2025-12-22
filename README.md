# Système de Cryptographie des Données Utilisateur

Application Java de cryptographie avec authentification et chiffrement RSA.

## Prérequis

- JDK 17 ou supérieur
- Maven (optionnel, pour la compilation avec dépendances JavaFX)

## Compilation et Exécution

### Avec Maven:

```bash
mvn clean compile
mvn javafx:run
```

### Avec Java directement:

Si JavaFX est installé dans votre JDK:
```bash
javac -d target/classes -sourcepath src/main/java src/main/java/app/*.java src/main/java/ui/*.java src/main/java/model/*.java src/main/java/service/*.java src/main/java/util/*.java
java --module-path <chemin-javafx-lib> --add-modules javafx.controls -cp target/classes app.MainApp
```

## Utilisateurs par défaut

- Username: `admin`, Password: `admin123`
- Username: `user`, Password: `user123`

## Fonctionnalités

- Authentification sécurisée avec hachage SHA-256
- Chiffrement/déchiffrement RSA des fichiers
- Stockage sécurisé des fichiers chiffrés
- Interface graphique JavaFX
