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

Cette méthode est la plus simple car elle configure automatiquement tous les services nécessaires.

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

Cette commande va :
- Démarrer PostgreSQL sur le port **5433**
- Créer automatiquement la base de données `crypto`
- Initialiser les tables nécessaires
- Compiler et lancer l'application Java

#### Étape 3 : Accéder à l'application

L'application JavaFX devrait s'ouvrir automatiquement. Si ce n'est pas le cas, vérifiez les logs Docker.

#### Étape 4 : Arrêter l'application

```bash
# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes (⚠️ supprime les données)
docker-compose down -v
```

#### Accès à la base de données (optionnel)

Si vous voulez accéder à la base de données via une interface web :
- Ouvrez votre navigateur à l'adresse : `http://localhost:5050`
- Email : `admin@crypto.local`
- Mot de passe : `admin`

---

### Méthode 2 : Installation manuelle

#### Étape 1 : Installer PostgreSQL

1. Téléchargez et installez PostgreSQL depuis [postgresql.org](https://www.postgresql.org/download/)
2. Notez le mot de passe que vous définissez pour l'utilisateur `postgres`
3. Assurez-vous que PostgreSQL est en cours d'exécution

#### Étape 2 : Créer la base de données

1. Ouvrez **pgAdmin** ou utilisez la ligne de commande `psql`
2. Connectez-vous avec l'utilisateur `postgres`
3. Créez la base de données :

```sql
CREATE DATABASE crypto;
```

#### Étape 3 : Initialiser le schéma de la base de données

1. Exécutez le script SQL situé dans `docker/init/01-init-schema.sql`
2. Vous pouvez le faire via pgAdmin ou en ligne de commande :

```bash
psql -U postgres -d crypto -f docker/init/01-init-schema.sql
```

#### Étape 4 : Configurer la connexion (si nécessaire)

Par défaut, l'application se connecte à :
- **URL** : `jdbc:postgresql://localhost:5433/crypto`
- **Utilisateur** : `postgres`
- **Mot de passe** : `postgres`
- **Port** : `5433`

Si votre configuration PostgreSQL est différente, vous pouvez :

**Option A : Modifier les variables d'environnement**

Sur Windows (PowerShell) :
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/crypto"
$env:DB_USER="postgres"
$env:DB_PASSWORD="votre_mot_de_passe"
```

Sur Linux/Mac :
```bash
export DB_URL="jdbc:postgresql://localhost:5432/crypto"
export DB_USER="postgres"
export DB_PASSWORD="votre_mot_de_passe"
```

**Option B : Modifier directement le fichier `src/main/java/util/Db.java`**

Changez les valeurs par défaut dans le fichier (lignes 9-11).

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

Par défaut, l'application se connecte à PostgreSQL sur le port **5433** (pour éviter les conflits avec une installation PostgreSQL existante sur le port 5432).

Si vous utilisez le port standard 5432, modifiez la variable `DB_URL` :
```
DB_URL=jdbc:postgresql://localhost:5432/crypto
```

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

**Solutions :**
1. Vérifiez que PostgreSQL est en cours d'exécution
2. Vérifiez les paramètres de connexion (URL, utilisateur, mot de passe)
3. Vérifiez que la base de données `crypto` existe
4. Vérifiez que les tables ont été créées (exécutez le script SQL)

```bash
# Tester la connexion PostgreSQL
psql -U postgres -h localhost -p 5433 -d crypto
```

### Erreur "Pilote PostgreSQL introuvable"

**Solution :** Les dépendances Maven ne sont pas téléchargées. Exécutez :

```bash
mvn clean install
```

### L'application se lance mais ne peut pas se connecter à la base de données

**Vérifications :**
1. PostgreSQL est-il démarré ?
2. Le port est-il correct (5433 par défaut) ?
3. La base de données `crypto` existe-t-elle ?
4. Les tables ont-elles été créées ?

**Test de connexion manuel :**
```bash
# Windows
psql -U postgres -h localhost -p 5433 -d crypto

# Linux/Mac
psql -U postgres -h localhost -p 5433 -d crypto
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

