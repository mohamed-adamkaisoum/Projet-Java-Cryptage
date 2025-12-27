# Documentation Technique - Système de Cryptage de Fichiers en Java

## Guide Complet pour Débutant

*Généré le : 25 décembre 2025*

---

## Table des matières
1. [Vue d’ensemble du projet](#1-vue-densemble-du-projet)
2. [Analyse détaillée fichier par fichier](#2-analyse-détaillée-fichier-par-fichier)
3. [Concepts Java expliqués pour débutants](#3-concepts-java-expliqués-pour-débutants)
4. [Flux d’exécution complet](#4-flux-dexécution-complet)
5. [Bonnes pratiques et explications](#5-bonnes-pratiques-et-explications)
6. [Comment utiliser le projet](#6-comment-utiliser-le-projet)
7. [Exercices pratiques pour apprendre](#7-exercices-pratiques-pour-apprendre)
8. [Annexes](#8-annexes)

---

# 1. Vue d’ensemble du projet

## 1.1 Introduction

Ce projet est un **système de cryptage de fichiers** en Java permettant à un utilisateur :
- De créer un compte sécurisé
- De chiffrer et stocker tout fichier directement dans une base PostgreSQL
- De lister et déchiffrer ses fichiers via une application graphique JavaFX

**Usages :**
- Protéger ses documents privés
- Stockage sécurisé pour images, PDF, textes confidentiels

**Technologies :**  
Java, JavaFX, PostgreSQL, Cryptographie RSA, SHA-256, JDBC

---

## 1.2 Architecture Globale

```
Utilisateur
    ↕
[JavaFX] = Interface graphique
    ↕
AuthentificationService <--------> Table users (PostgreSQL)
    ↕
SecureStorageService <----------> Table secure_files (Binaires cryptés)
        |
   CryptoService (RSA)
```

**Liste des fichiers principaux :**
- `MainApp.java` : Lance l’application, gère la navigation
- `ui/LoginView.java` & `ui/MainView.java` : Interfaces graphiques
- `service/AuthentificationService.java` : Gestion utilisateurs
- `service/SecureStorageService.java` : Stockage fichiers chiffrés
- `service/CryptoService.java` : Tout le chiffrement RSA
- `model/Utilisateur.java`, `model/SecureFile.java` : Structures métier
- `util/Db.java`, `util/PasswordUtils.java` : Connexion base, hash mot de passe

---

## 1.3 Concepts Clés pour Débutants

- **Cryptage/chiffrement :** Rendre illisible sauf pour celui qui a la bonne clé
- **RSA :** Un cadenas avec 2 clés – une pour fermer (publique), une (secrète) pour ouvrir (privée)
- **SHA-256 :** Signature numérique unique, impossible à inverser
- **Glossaire** : (voir Annexe B)

---

# 2. Analyse détaillée fichier par fichier

---

## MainApp.java

### A. Rôle  
Lance et coordonne l’ensemble du programme.

### B. Imports
```java
import javafx.application.Application;
import javafx.stage.Stage;
import service.AuthentificationService;
import service.CryptoService;
import service.SecureStorageService;
import ui.LoginView;
import ui.MainView;
import model.Utilisateur;
```
Chaque import correspond à un composant ou à un modèle métier du projet.

### C. Variables
- `primaryStage`: Fenêtre JavaFX principale
- `authService`: Service des utilisateurs
- `cryptoService`: Fournisseur de chiffrement
- `storageService`: Gestionnaire des fichiers protégés

### D. Constructeur  
Gestion automatique via JavaFX.

### E. Méthodes Principales
- `start(Stage)` : Démarre l’application et affiche l’écran de connexion
- `showLoginView()` : Affiche LoginView
- `onLoginSuccess(Utilisateur)` : Passe à l’interface principale MainView

### F. Schéma de flux
```
Utilisateur → (LoginView → AuthentificationService) → OK ?
    ↓
(MainView, CryptoService, SecureStorageService)
```

---

## ui/LoginView.java

### A. Rôle  
Fenêtre UI pour la connexion/inscription

### B. Imports
```java
import javafx.* etc.
import service.AuthentificationService;
import model.Utilisateur;
import java.util.function.Consumer;
```
JavaFX pour l’UI, service pour la logique métiers, consumer pour callback

### C. Variables  
Stage principal, références au service

### D. Constructeur  
Reçoit le service et le callback

### E. Méthodes
- `show()` : Affiche l’UI de connexion
- `showRegisterDialog()` : Affiche la fenêtre d’inscription
- `validateAndRegister(...)` : Vérifie et enregistre les nouveaux comptes

### F. Schéma
```
Utilisateur saisit |→ bouton → services → validation (OK/erreur)
```

---

## ui/MainView.java

### A. Rôle  
Page principale de l’utilisateur, permet chargement/chiffrement/décryptage/listage.

### B. Imports
JavaFX pour UI, services/metier pour logique

### C. Variables  
Stage, utilisateur courant, liste des fichiers, CryptoService, StorageService

### D. Constructeur
Initialise tous les composants

### E. Méthodes
- `show()` : Compose les éléments graphiques + handlers de bouton
- `refreshFileList()` : Recharge les fichiers depuis la base
- `encryptButton`/`decryptButton` : Gèrent le stockage et la récupération via SecureStorageService

### F. Schéma
```
Utilisateur sélectionne un fichier → bouton "Chiffrer" → CryptoService → SecureStorageService → stockage base
Utilisateur sélectionne un fichier chiffré → bouton "Déchiffrer" → CryptoService → restitution disque
```

---

## service/AuthentificationService.java

### A. Rôle  
Gestion des utilisateurs, inscription, connexion (avec hash de mot de passe)

### B. Imports
JDBC pour accès base, PasswordUtils pour hash mot de passe

### C. Pas de variables de classe

### D. Constructeur  
Vide.

### E. Méthodes
- `registerUser` : Inscription (avec hashage du mot de passe)
- `authenticate` : Connexion (validation hash)
- `userExists`, `findUser` : Utilitaires de recherche

---

## service/SecureStorageService.java

### A. Rôle  
Stocke tous les fichiers chiffrés directement en base, gère l’accès et déchiffrement

### B. Imports
JDBC pour base, CryptoService pour cryptographie

### C. Variables
CryptoService pour tout le chiffrement/déchiffrement

### D. Constructeur
Reçoit une instance de CryptoService

### E. Méthodes
- `encryptAndStore(File, owner)`: Chiffre puis stocke en base ;
- `getUserFiles(username)`: Liste des fichiers pour l’utilisateur
- `decryptAndRetrieve(SecureFile, outputPath)`: Sort et déchiffre le fichier
- `deleteFile(SecureFile)`: Supprime fichier et méta

---

## service/CryptoService.java

### A. Rôle  
Toute la logique de chiffrement/déchiffrement RSA, gestion clé privée/publique

### B. Imports
`javax.crypto.Cipher`, `java.security.Key*` etc.

### C. Variables
- ALGORITHM : RSA
- KEY_SIZE : 2048 bits (sécurité élevée)
- keyPair : paire de clés générée au démarrage

### D. Constructeur
Génère les clés immédiatement

### E. Méthodes
- `encrypt` / `decrypt`: Pour un bloc de données
- `encryptFile` / `decryptFile`: Sur de larges fichiers (en plusieurs blocs)
- `exportPublicKey` / `exportPrivateKey` / `importPublicKey`: Gèrent le stockage/chargement clé pour utilisateur

---

## model/Utilisateur.java  
Structure java pour retenir username et password hashé (accès base)

## model/SecureFile.java  
Structure pour stocker les fichiers cryptés (nom, owner, date...)

## util/Db.java  
Méthode de connexion universelle à la base (voir open/close automatique avec try-with-resources)

## util/PasswordUtils.java  
Hashage des mots de passe via SHA-256

---

# 3. Concepts Java expliqués pour débutants

## 3.1 Concepts Orientés Objet
- Classe = plan (recette) pour fabriquer des objets (des vraies instances)
- Objet = un utilisateur réel, ou un fichier réel, etc.
- `private` / `public` contrôlent l'accès (sécurité)
- Héritage = "hériter" d’un plan de base (non utilisé dans ce projet)
- Polymorphisme : redéfinir ou adapter un comportement (non utilisé ici)

## 3.2 Gestion des Exceptions
- try/catch : encadrent le code susceptible d’avoir une erreur (ex : SQLException)
- throws : indique qu’une erreur peut être propagée
- throw : déclenche l’erreur volontairement

## 3.3 Structures de données
- ArrayList : liste dynamique utilisée pour stocker les fichiers en mémoire

## 3.4 Streams et I/O
- FileInputStream/FileOutputStream utilisés via Files.readAllBytes et Files.write pour lire/écrire des fichiers

## 3.5 Cryptographie en Java
- Cipher permet de chiffrer/déchiffrer
- KeyPair, PublicKey/PrivateKey sont les « cadenas » pour protéger/déverrouiller les données

---

# 4. Flux d’exécution complet

## 4.1. Scénario : Crypter un Fichier
1. L’utilisateur lance l’application
2. Crée un compte / se connecte
3. Clique sur "Sélectionner un fichier"
4. Clique "Chiffrer & Stocker"
5. Le fichier est :
   - lu
   - chiffré par CryptoService
   - stocké (binaire) en base via SecureStorageService

## 4.2. Scénario : Décrypter un Fichier
1. L’utilisateur sélectionne son fichier chiffré
2. Clique "Déchiffrer & Ouvrir"
3. SecureStorageService récupère le fichier, le déchiffre, et le remet en sortie sur disque

---

# 5. Bonnes pratiques et explications

- Hashage/salage des mots de passe (jamais stocker en clair)
- Toujours chiffrer avant de stocker
- Préférer try-with-resources pour les connexions à la base (évite fuites mémoire)
- Messages utilisateurs explicites
- Supprimer les fichiers inutilisés

---

# 6. Comment utiliser le projet

## 6.1 Prérequis
- JDK 11 ou supérieur
- PostgreSQL actif
- Dépendances JavaFX et JDBC

## 6.2 Compilation
```bash
javac -d bin src/main/java/**/*.java
```

## 6.3 Exécution
```bash
java -cp bin app.MainApp
```

## 6.4 Exemples d’utilisation
- Lancer le programme, se connecter, sélectionner un fichier "photo.jpg" puis "Chiffrer"
- Retrouver le fichier dans la liste, et (ré-)enregistrer grâce à "Déchiffrer"

---

# 7. Exercices pratiques

1. Changer l’algorithme de chiffrement dans CryptoService (essayez AES)
2. Ajouter un log utilisateur des actions réalisées
3. Rendre la vérification du mot de passe non sensible à la casse
4. Ajouter une page « À propos » dans MainView
5. Permettre le traitement de plusieurs fichiers en une seule fois (boucle sur encryptAndStore)

---

# 8. Annexes

## A. Code complet (sans commentaires)

*(Colle ici le code de l’ensemble des fichiers Java fournis, sans commentaires.)*

---

## B. Glossaire technique

Classe, Objet, Exception, Hash, Cryptage, Chiffrement, RSA, SHA-256, JDBC, ArrayList, Binaire, Blob, Encapsulation, Polymorphisme, Public/Private, static, final, try/catch, buffer, Logger, Stream, Key, Password, Salt, PKCS…

---

## C. Ressources

- [Documentation Java](https://docs.oracle.com/javase/8/docs/api/)
- [JavaFX Documentation](https://openjfx.io/)
- [PostgreSQL Tutoriels](https://www.postgresql.org/docs/current/tutorial-start.html)
- [Guide Cryptographie](https://cryptobook.nakov.com)

---

