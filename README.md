# Système de Cryptographie des Données Utilisateur

Application Java permettant de chiffrer, stocker et déchiffrer des fichiers de manière sécurisée en utilisant l'algorithme RSA.

## 📋 Description

Ce projet est une application de bureau développée en Java qui offre :
- **Authentification sécurisée** : Inscription et connexion avec hachage de mots de passe (SHA-256)
- **Chiffrement RSA** : Chiffrement des fichiers avec RSA 2048 bits
- **Stockage sécurisé** : Stockage des fichiers chiffrés dans une base de données PostgreSQL
- **Interface graphique** : Interface utilisateur intuitive développée avec JavaFX

## 🛠️ Prérequis

### Option 1 : Avec Docker (Recommandé - Plus simple)

- [Docker](https://www.docker.com/get-started) (version 20.10 ou supérieure)
- [Docker Compose](https://docs.docker.com/compose/install/) (généralement inclus avec Docker Desktop)

### Option 2 : Installation manuelle

- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou supérieur
- [Maven](https://maven.apache.org/download.cgi) (version 3.6 ou supérieure)
- [PostgreSQL](https://www.postgresql.org/download/) (version 12 ou supérieure)

## 🚀 Installation et Lancement

### Méthode 1 : Avec Docker (Recommandé)

Cette méthode est la plus simple car elle configure automatiquement tous les services nécessaires **dans des conteneurs Docker**. 

**⚠️ Important :** Avec cette méthode, PostgreSQL est exécuté dans un conteneur Docker, **pas besoin d'installer PostgreSQL localement** sur votre machine.

#### Étape 1 : Cloner ou télécharger le projet

```bash
# Si vous utilisez Git
git clone <url-du-projet>
cd Projet-Java-Cryptage

# Ou simplement téléchargez et extrayez le projet dans un dossier
```

#### Étape 2 : Lancer avec Docker Compose

```bash
# Démarrer tous les services (base de données + application)
docker-compose up

# Ou en arrière-plan
docker-compose up -d
```

Cette commande va automatiquement :
- **Créer et démarrer un conteneur PostgreSQL** (image `postgres:16-alpine`)
- **Créer et démarrer un conteneur pour l'application Java**
- **Créer et démarrer un conteneur pgAdmin** (interface web pour la base de données)
- Exposer PostgreSQL sur le port **5433** de votre machine (mappé depuis le port 5432 du conteneur)
- Créer automatiquement la base de données `crypto` dans le conteneur PostgreSQL
- Initialiser automatiquement les tables nécessaires (via le script SQL dans `docker/init/`)
- Compiler et lancer l'application Java dans son conteneur

**Note :** La base de données PostgreSQL est **entièrement gérée par Docker**. Vous n'avez pas besoin d'installer PostgreSQL sur votre système.

#### Étape 3 : Accéder à l'application

L'application JavaFX devrait s'ouvrir automatiquement depuis le conteneur Docker. Si ce n'est pas le cas, vérifiez les logs :

```bash
# Voir les logs de l'application
docker-compose logs app

# Voir tous les logs
docker-compose logs
```

#### Étape 4 : Arrêter l'application

```bash
# Arrêter tous les services (conserve les données)
docker-compose down

# Arrêter et supprimer les volumes (⚠️ supprime toutes les données de la base de données)
docker-compose down -v
```

**Note :** Les données de la base de données sont stockées dans un volume Docker. Si vous utilisez `docker-compose down` (sans `-v`), les données sont conservées. Si vous utilisez `docker-compose down -v`, toutes les données sont supprimées.

#### Accès à la base de données Docker (optionnel)

Si vous voulez accéder à la base de données PostgreSQL qui tourne dans Docker via une interface web :

1. **Via pgAdmin (recommandé)** :
   - Ouvrez votre navigateur à l'adresse : `http://localhost:5050`
   - Email : `admin@crypto.local`
   - Mot de passe : `admin`
   - Ajoutez un nouveau serveur avec :
     - Host : `postgres` (nom du service dans docker-compose)
     - Port : `5432` (port interne du conteneur)
     - Database : `crypto`
     - Username : `postgres`
     - Password : `postgres`

2. **Via ligne de commande** (si vous avez `psql` installé localement) :
   ```bash
   # Se connecter à PostgreSQL dans Docker
   psql -U postgres -h localhost -p 5433 -d crypto
   # Mot de passe : postgres
   ```

**Note :** La base de données est accessible depuis votre machine via `localhost:5433`, mais elle tourne dans le conteneur Docker, pas sur votre système local.

---

### Méthode 2 : Installation manuelle (sans Docker)

**⚠️ Important :** Cette méthode nécessite d'installer PostgreSQL **localement sur votre machine**. Si vous utilisez Docker (Méthode 1), vous n'avez **pas besoin** de cette méthode.

#### Étape 1 : Installer PostgreSQL localement

1. Téléchargez et installez PostgreSQL depuis [postgresql.org](https://www.postgresql.org/download/)
2. Notez le mot de passe que vous définissez pour l'utilisateur `postgres`
3. Assurez-vous que le service PostgreSQL est en cours d'exécution sur votre machine
4. Par défaut, PostgreSQL s'installe sur le port **5432** (pas 5433)

**Note :** Si vous avez déjà PostgreSQL installé localement, vous pouvez l'utiliser. Sinon, installez-le maintenant.

#### Étape 2 : Créer la base de données

1. Ouvrez **pgAdmin** (installé avec PostgreSQL) ou utilisez la ligne de commande `psql`
2. Connectez-vous avec l'utilisateur `postgres` et votre mot de passe
3. Créez la base de données :

```sql
CREATE DATABASE crypto;
```

#### Étape 3 : Initialiser le schéma de la base de données

1. Exécutez le script SQL situé dans `docker/init/01-init-schema.sql`
2. Vous pouvez le faire via pgAdmin ou en ligne de commande :

```bash
# Si PostgreSQL est sur le port 5432 (port par défaut)
psql -U postgres -d crypto -f docker/init/01-init-schema.sql

# Si PostgreSQL est sur un autre port
psql -U postgres -p <votre_port> -d crypto -f docker/init/01-init-schema.sql
```

#### Étape 4 : Configurer la connexion

**Par défaut, l'application se connecte à :**
- **URL** : `jdbc:postgresql://localhost:5433/crypto`
- **Utilisateur** : `postgres`
- **Mot de passe** : `postgres`
- **Port** : `5433`

**⚠️ Attention :** Si vous utilisez PostgreSQL installé localement, il tourne probablement sur le port **5432** (port par défaut), pas 5433. Vous devez donc configurer l'application.

Vous pouvez configurer la connexion de deux façons :

**Option A : Modifier les variables d'environnement (recommandé)**

Sur Windows (PowerShell) :
```powershell
# Si PostgreSQL est sur le port 5432 (port par défaut)
$env:DB_URL="jdbc:postgresql://localhost:5432/crypto"
$env:DB_USER="postgres"
$env:DB_PASSWORD="votre_mot_de_passe"

# Puis lancer l'application
mvn javafx:run
```

Sur Linux/Mac :
```bash
# Si PostgreSQL est sur le port 5432 (port par défaut)
export DB_URL="jdbc:postgresql://localhost:5432/crypto"
export DB_USER="postgres"
export DB_PASSWORD="votre_mot_de_passe"

# Puis lancer l'application
mvn javafx:run
```

**Option B : Modifier directement le fichier `src/main/java/util/Db.java`**

Changez les valeurs par défaut dans le fichier (lignes 9-11) pour pointer vers votre installation PostgreSQL locale :

```java
private static final String URL = "jdbc:postgresql://localhost:5432/crypto";  // Port 5432 au lieu de 5433
private static final String USER = "postgres";
private static final String PASSWORD = "votre_mot_de_passe";  // Votre mot de passe PostgreSQL
```

#### Étape 5 : Compiler le projet

```bash
# Dans le répertoire du projet
mvn clean compile
```

#### Étape 6 : Lancer l'application

```bash
# Lancer avec Maven
mvn javafx:run

# Ou compiler et exécuter manuellement
mvn clean package
java --module-path <chemin-vers-javafx> --add-modules javafx.controls -cp target/classes:target/dependency/* app.MainApp
```

---

## 📖 Utilisation

### Première utilisation

1. **Lancer l'application** (voir instructions ci-dessus)
2. **Créer un compte** :
   - Cliquez sur le bouton "S'inscrire"
   - Entrez un nom d'utilisateur (minimum 3 caractères)
   - Entrez un mot de passe (minimum 4 caractères)
   - Confirmez le mot de passe
   - Cliquez sur "OK"

3. **Se connecter** :
   - Entrez votre nom d'utilisateur et mot de passe
   - Cliquez sur "Se connecter"

### Chiffrer un fichier

1. Cliquez sur "Sélectionner un fichier"
2. Choisissez le fichier que vous voulez chiffrer
3. Cliquez sur "Chiffrer & Stocker"
4. Le fichier est maintenant chiffré et stocké dans la base de données

### Déchiffrer un fichier

1. Sélectionnez un fichier dans la liste "Mes fichiers chiffrés"
2. Cliquez sur "Déchiffrer & Ouvrir"
3. Choisissez où sauvegarder le fichier déchiffré
4. Le fichier original est restauré

### Actualiser la liste

Cliquez sur "Actualiser la liste" pour voir tous vos fichiers chiffrés.

---

## 🔧 Configuration avancée

### Variables d'environnement

Vous pouvez configurer l'application avec des variables d'environnement :

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `DB_URL` | URL de connexion à PostgreSQL | `jdbc:postgresql://localhost:5433/crypto` |
| `DB_USER` | Nom d'utilisateur PostgreSQL | `postgres` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres` |

### Port PostgreSQL

**Avec Docker (Méthode 1) :**
- PostgreSQL dans Docker est accessible sur le port **5433** de votre machine
- C'est le port mappé depuis le conteneur (port interne 5432 → port externe 5433)
- L'application se connecte automatiquement à `localhost:5433`

**Sans Docker (Méthode 2 - Installation manuelle) :**
- PostgreSQL installé localement utilise généralement le port **5432** (port par défaut)
- Vous devez configurer l'application pour utiliser le port 5432 au lieu de 5433
- Modifiez la variable `DB_URL` :
```
DB_URL=jdbc:postgresql://localhost:5432/crypto
```

**Note :** Le port 5433 est utilisé par défaut dans le code pour éviter les conflits avec une installation PostgreSQL locale existante sur le port 5432. Si vous utilisez Docker, gardez 5433. Si vous utilisez PostgreSQL local, changez pour 5432.

---

## 🐛 Dépannage

### L'application ne se lance pas

**Problème :** Erreur "JavaFX runtime components are missing"

**Solution :** Assurez-vous d'utiliser Java 17 ou supérieur et que JavaFX est bien inclus dans les dépendances Maven.

```bash
# Vérifier la version de Java
java -version

# Doit afficher version 17 ou supérieure
```

**Problème :** Erreur de connexion à la base de données

**Solutions selon votre méthode d'installation :**

**Si vous utilisez Docker (Méthode 1) :**
1. Vérifiez que les conteneurs Docker sont en cours d'exécution : `docker-compose ps`
2. Vérifiez les logs : `docker-compose logs postgres`
3. Vérifiez que le conteneur PostgreSQL écoute sur le port 5433 : `docker-compose ps`
4. Testez la connexion :
   ```bash
   # Se connecter à PostgreSQL dans Docker
   psql -U postgres -h localhost -p 5433 -d crypto
   # Mot de passe : postgres
   ```

**Si vous utilisez PostgreSQL local (Méthode 2) :**
1. Vérifiez que le service PostgreSQL est démarré sur votre machine
2. Vérifiez que vous utilisez le bon port (5432 pour installation locale, pas 5433)
3. Vérifiez les paramètres de connexion (URL, utilisateur, mot de passe)
4. Vérifiez que la base de données `crypto` existe
5. Vérifiez que les tables ont été créées (exécutez le script SQL)
6. Testez la connexion :
   ```bash
   # Se connecter à PostgreSQL local
   psql -U postgres -h localhost -p 5432 -d crypto
   ```

### Erreur "Pilote PostgreSQL introuvable"

**Solution :** Les dépendances Maven ne sont pas téléchargées. Exécutez :

```bash
mvn clean install
```

### L'application se lance mais ne peut pas se connecter à la base de données

**Vérifications selon votre méthode :**

**Avec Docker :**
1. Les conteneurs Docker sont-ils démarrés ? (`docker-compose ps`)
2. Le port est-il correct (5433 pour Docker) ?
3. La base de données `crypto` existe-t-elle dans le conteneur ?
4. Les tables ont-elles été créées automatiquement ?

**Sans Docker (PostgreSQL local) :**
1. Le service PostgreSQL est-il démarré sur votre machine ?
2. Le port est-il correct (5432 pour installation locale) ?
3. La base de données `crypto` existe-t-elle ?
4. Les tables ont-elles été créées (avez-vous exécuté le script SQL) ?
5. Les identifiants de connexion sont-ils corrects ?

**Test de connexion manuel :**

Avec Docker :
```bash
# Se connecter à PostgreSQL dans Docker
psql -U postgres -h localhost -p 5433 -d crypto
# Mot de passe : postgres
```

Sans Docker (PostgreSQL local) :
```bash
# Se connecter à PostgreSQL local
psql -U postgres -h localhost -p 5432 -d crypto
# Utilisez votre mot de passe PostgreSQL
```

### Docker ne démarre pas

**Problème :** Port déjà utilisé

**Solution :** Modifiez les ports dans `docker-compose.yml` si nécessaire :
- Port PostgreSQL : changez `5433:5432` en `5434:5432` (ou autre port libre)
- Port pgAdmin : changez `5050:80` en `5051:80` (ou autre port libre)

**Problème :** Erreur de permissions Docker

**Solution :** Assurez-vous que Docker Desktop est en cours d'exécution et que vous avez les permissions nécessaires.

---

## 📁 Structure du projet

```
Projet-Java-Cryptage/
├── src/
│   └── main/
│       ├── java/
│       │   ├── app/           # Point d'entrée de l'application
│       │   ├── model/         # Modèles de données (Utilisateur, SecureFile)
│       │   ├── service/       # Logique métier (Authentification, Crypto, Storage)
│       │   ├── ui/            # Interfaces graphiques (LoginView, MainView)
│       │   └── util/          # Utilitaires (Db, PasswordUtils)
│       └── resources/
│           └── images/        # Images de l'interface
├── docker/
│   └── init/
│       └── 01-init-schema.sql  # Script d'initialisation de la base de données
├── docker-compose.yml         # Configuration Docker
├── Dockerfile                 # Image Docker de l'application
├── pom.xml                    # Configuration Maven
└── README.md                  # Ce fichier
```

---

## 🔐 Sécurité

### Mécanismes de sécurité implémentés

- **Hachage des mots de passe** : SHA-256 (les mots de passe ne sont jamais stockés en clair)
- **Chiffrement RSA** : 2048 bits pour les fichiers
- **Authentification** : Vérification de l'identité avant accès aux fichiers
- **Isolation des données** : Chaque utilisateur ne peut accéder qu'à ses propres fichiers

### ⚠️ Limitations de sécurité

- Les clés RSA sont générées à chaque session (perdues à la fermeture de l'application)
- Pas de récupération de mot de passe
- Pas de chiffrement des clés privées stockées

Pour plus de détails, consultez le document `PRESENTATION_PROJET_CRYPTOGRAPHIE.md`.

---

## 🛠️ Technologies utilisées

- **Java 17** : Langage de programmation
- **JavaFX 17.0.2** : Interface graphique
- **PostgreSQL** : Base de données
- **Maven** : Gestion des dépendances
- **Docker** : Conteneurisation (optionnel)
- **Java Cryptography Architecture (JCA)** : Cryptographie

---

## 📝 Dépendances Maven

Les dépendances sont gérées automatiquement par Maven. Principales dépendances :

- `org.openjfx:javafx-controls:17.0.2` - Interface graphique
- `org.postgresql:postgresql:42.7.3` - Pilote PostgreSQL

---

## 👤 Auteur

**Adam Kaisoum**

Encadré par : **Professeur TOUIMI Yassine**

---

## 📄 Licence

Ce projet est un projet académique.

---

## 💡 Aide supplémentaire

Pour une explication détaillée du projet et des concepts utilisés, consultez :
- `PRESENTATION_PROJET_CRYPTOGRAPHIE.md` - Guide de présentation et explications pédagogiques

---

## ❓ Questions fréquentes

**Q : Puis-je utiliser une autre base de données que PostgreSQL ?**  
R : Non, le projet est spécifiquement conçu pour PostgreSQL. Modifier le code serait nécessaire pour utiliser une autre base de données.

**Q : Dois-je installer PostgreSQL si j'utilise Docker ?**  
R : Non ! Avec Docker, PostgreSQL tourne dans un conteneur. Vous n'avez pas besoin d'installer PostgreSQL sur votre machine. Docker gère tout automatiquement.

**Q : Puis-je utiliser Docker pour la base de données et lancer l'application manuellement ?**  
R : Oui, c'est possible. Lancez seulement le service PostgreSQL avec Docker : `docker-compose up postgres -d`, puis configurez l'application pour se connecter à `localhost:5433` et lancez l'application avec `mvn javafx:run`.

**Q : Les fichiers sont-ils stockés sur le disque ou dans la base de données ?**  
R : Les fichiers chiffrés sont stockés dans la base de données PostgreSQL (dans la colonne `encrypted_blob` de la table `secure_files`).

**Q : Puis-je récupérer mes fichiers si je perds mon mot de passe ?**  
R : Non, actuellement il n'y a pas de système de récupération de mot de passe. Vous devez vous souvenir de votre mot de passe.

**Q : L'application fonctionne-t-elle sur Mac/Linux ?**  
R : Oui, l'application Java est multiplateforme. Elle fonctionne sur Windows, Mac et Linux.

**Q : Puis-je chiffrer des fichiers de plusieurs Go ?**  
R : Techniquement oui, mais cela peut être lent car RSA doit diviser les fichiers en petits blocs. Pour de très gros fichiers, un algorithme hybride (RSA + AES) serait plus approprié.

---

**Bon chiffrement ! 🔐**

