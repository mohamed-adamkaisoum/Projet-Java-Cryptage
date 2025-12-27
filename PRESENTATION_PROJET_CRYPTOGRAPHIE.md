# PRÉSENTATION DU PROJET
## Système de Cryptographie des Données Utilisateur

---

## 1. PAGE DE TITRE

**Titre du projet :** Système de Cryptographie des Données Utilisateur

**Réalisé par :** Adam Kaisoum

**Encadré par :** Professeur TOUIMI Yassine

**Contexte académique :** Projet de développement Java avec focus sur la sécurité informatique et le chiffrement de données

---

## 2. PROBLÉMATIQUE

### Pourquoi la sécurité des données est importante ?

- **Protection de la vie privée** : Les fichiers personnels contiennent souvent des informations sensibles (documents, photos, données professionnelles)
- **Risques actuels** : Vol de données, piratage, accès non autorisé aux fichiers
- **Besoin de confidentialité** : Les utilisateurs doivent pouvoir stocker leurs fichiers en toute sécurité

### Pourquoi le chiffrement est nécessaire ?

- **Sécurité renforcée** : Même si quelqu'un accède aux fichiers, ils ne peuvent pas les lire sans la clé de déchiffrement
- **Conformité** : Respect des normes de protection des données
- **Confiance** : L'utilisateur peut stocker ses fichiers en toute sérénité

**Question centrale :** Comment permettre aux utilisateurs de stocker leurs fichiers de manière sécurisée avec un système de chiffrement robuste ?

---

## 3. OBJECTIFS DU PROJET

### Objectif principal
Créer une application Java permettant aux utilisateurs de chiffrer, stocker et déchiffrer leurs fichiers de manière sécurisée.

### Objectifs spécifiques

#### 1. Authentification sécurisée
- Permettre aux utilisateurs de créer un compte
- Vérifier l'identité lors de la connexion
- Protéger les mots de passe avec un hachage

#### 2. Chiffrement des fichiers
- Chiffrer les fichiers avec l'algorithme RSA
- Générer des clés cryptographiques sécurisées
- Permettre le déchiffrement uniquement au propriétaire

#### 3. Stockage sécurisé
- Stocker les fichiers chiffrés dans une base de données
- Conserver les métadonnées (nom, taille, date)
- Assurer que seul le propriétaire peut accéder à ses fichiers

---

## 4. FONCTIONNEMENT GLOBAL DE L'APPLICATION

### Parcours utilisateur complet

#### Étape 1 : Démarrage de l'application
- L'application se lance et affiche une fenêtre de connexion
- L'utilisateur voit un formulaire avec nom d'utilisateur et mot de passe

#### Étape 2 : Inscription (première utilisation)
- L'utilisateur clique sur "S'inscrire"
- Il entre un nom d'utilisateur (minimum 3 caractères)
- Il entre un mot de passe (minimum 4 caractères) et le confirme
- Le système vérifie que le nom d'utilisateur n'existe pas déjà
- Le mot de passe est haché (transformé en une chaîne sécurisée) avant stockage
- Le compte est créé dans la base de données

#### Étape 3 : Connexion
- L'utilisateur entre son nom d'utilisateur et son mot de passe
- Le système récupère le haché du mot de passe stocké
- Il compare le haché du mot de passe saisi avec celui stocké
- Si les deux correspondent, l'utilisateur est authentifié
- L'application affiche l'interface principale

#### Étape 4 : Interface principale
- L'utilisateur voit la liste de ses fichiers chiffrés (vide au début)
- Il peut sélectionner un fichier depuis son ordinateur
- Il peut chiffrer et stocker ce fichier
- Il peut voir la liste de tous ses fichiers chiffrés
- Il peut déchiffrer et récupérer un fichier

#### Étape 5 : Chiffrement d'un fichier
- L'utilisateur sélectionne un fichier (bouton "Sélectionner un fichier")
- Il clique sur "Chiffrer & Stocker"
- Le système lit le contenu du fichier
- Une paire de clés RSA est générée automatiquement
- Le fichier est divisé en blocs (car RSA a des limites de taille)
- Chaque bloc est chiffré avec la clé publique
- Le fichier chiffré est stocké dans la base de données avec ses métadonnées
- Un message de confirmation s'affiche

#### Étape 6 : Déchiffrement d'un fichier
- L'utilisateur sélectionne un fichier dans la liste
- Il clique sur "Déchiffrer & Ouvrir"
- Il choisit où sauvegarder le fichier déchiffré
- Le système récupère le fichier chiffré depuis la base de données
- Le fichier est déchiffré avec la clé privée
- Le fichier original est restauré et sauvegardé

#### Étape 7 : Déconnexion
- L'utilisateur clique sur "Déconnexion"
- L'application se ferme

---

## 5. ARCHITECTURE DU SYSTÈME

### Organisation générale

L'application suit une architecture en couches, séparant clairement les responsabilités :

```
┌─────────────────────────────────────┐
│     INTERFACE UTILISATEUR (UI)     │
│  - LoginView (fenêtre de connexion)│
│  - MainView (fenêtre principale)   │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      LOGIQUE MÉTIER (SERVICE)       │
│  - AuthentificationService          │
│  - CryptoService                    │
│  - SecureStorageService             │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      MODÈLES DE DONNÉES (MODEL)     │
│  - Utilisateur                      │
│  - SecureFile                       │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      UTILITAIRES (UTIL)             │
│  - Db (connexion base de données)   │
│  - PasswordUtils (hachage)          │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      BASE DE DONNÉES (PostgreSQL)   │
│  - Table users                      │
│  - Table secure_files               │
└─────────────────────────────────────┘
```

### Séparation des responsabilités

#### Couche Interface (UI)
- **Rôle** : Afficher les fenêtres et gérer les interactions utilisateur
- **Fichiers** : `LoginView.java`, `MainView.java`
- **Responsabilités** :
  - Afficher les formulaires
  - Récupérer les saisies utilisateur
  - Afficher les résultats
  - Gérer les boutons et actions

#### Couche Service (Logique métier)
- **Rôle** : Contenir toute la logique de l'application
- **Fichiers** : `AuthentificationService.java`, `CryptoService.java`, `SecureStorageService.java`
- **Responsabilités** :
  - Vérifier les identifiants
  - Chiffrer/déchiffrer les fichiers
  - Gérer le stockage et la récupération

#### Couche Modèle (Données)
- **Rôle** : Représenter les données de l'application
- **Fichiers** : `Utilisateur.java`, `SecureFile.java`
- **Responsabilités** :
  - Stocker les informations d'un utilisateur
  - Stocker les informations d'un fichier sécurisé

#### Couche Utilitaire
- **Rôle** : Fournir des fonctions réutilisables
- **Fichiers** : `Db.java`, `PasswordUtils.java`
- **Responsabilités** :
  - Gérer la connexion à la base de données
  - Hacher les mots de passe

### Point d'entrée

- **Fichier** : `MainApp.java`
- **Rôle** : Démarrer l'application et coordonner les différents composants
- **Fonctionnement** :
  1. Crée les services nécessaires
  2. Affiche la fenêtre de connexion
  3. Après connexion réussie, affiche la fenêtre principale

---

## 6. MÉCANISMES DE SÉCURITÉ

### 6.1 Authentification

#### Principe
L'authentification vérifie que la personne qui se connecte est bien celle qu'elle prétend être.

#### Processus d'inscription
1. L'utilisateur choisit un nom d'utilisateur unique
2. Il choisit un mot de passe
3. **Le mot de passe n'est JAMAIS stocké en clair**
4. Le mot de passe est transformé en "haché" (une chaîne de caractères unique)
5. Seul le haché est stocké dans la base de données

#### Processus de connexion
1. L'utilisateur entre son nom d'utilisateur et son mot de passe
2. Le système récupère le haché stocké pour ce nom d'utilisateur
3. Le mot de passe saisi est haché avec le même algorithme
4. Les deux hachés sont comparés
5. Si identiques → connexion autorisée
6. Si différents → accès refusé

**Avantage** : Même si quelqu'un accède à la base de données, il ne peut pas connaître les mots de passe réels.

### 6.2 Hachage des mots de passe

#### Qu'est-ce que le hachage ?
Le hachage est une transformation mathématique à sens unique :
- On peut transformer un mot de passe en haché facilement
- On ne peut **PAS** retrouver le mot de passe à partir du haché
- Le même mot de passe produit toujours le même haché

#### Algorithme utilisé : SHA-256
- **SHA-256** signifie "Secure Hash Algorithm 256 bits"
- C'est un algorithme standard et sécurisé
- Il produit toujours un haché de 64 caractères hexadécimaux
- Exemple : "motdepasse123" → "a1b2c3d4e5f6..." (64 caractères)

#### Pourquoi c'est sécurisé ?
- **Irréversible** : Impossible de retrouver le mot de passe original
- **Déterministe** : Le même mot de passe donne toujours le même haché
- **Rapide** : La vérification est instantanée

### 6.3 Chiffrement RSA

#### Qu'est-ce que RSA ?
**RSA** (Rivest-Shamir-Adleman) est un algorithme de chiffrement asymétrique :
- Il utilise **deux clés différentes** : une clé publique et une clé privée
- La clé publique sert à **chiffrer** (rendre illisible)
- La clé privée sert à **déchiffrer** (rendre lisible)

#### Analogie simple
Imaginez une boîte aux lettres :
- **Clé publique** = Serrure accessible à tous (tout le monde peut mettre une lettre)
- **Clé privée** = Clé unique que seul vous possédez (seul vous pouvez ouvrir)

#### Comment ça fonctionne dans le projet ?

**Génération des clés :**
- Au démarrage, le système génère automatiquement une paire de clés RSA
- Taille des clés : 2048 bits (très sécurisé)
- La clé publique et la clé privée sont liées mathématiquement

**Chiffrement d'un fichier :**
1. Le fichier est lu en mémoire
2. Il est divisé en petits blocs (car RSA a une limite de taille)
3. Chaque bloc est chiffré avec la clé publique
4. Les blocs chiffrés sont assemblés
5. Le fichier chiffré est stocké dans la base de données

**Déchiffrement d'un fichier :**
1. Le fichier chiffré est récupéré depuis la base de données
2. Il est divisé en blocs de la taille appropriée
3. Chaque bloc est déchiffré avec la clé privée
4. Les blocs déchiffrés sont assemblés
5. Le fichier original est restauré

#### Pourquoi RSA est sécurisé ?
- **Asymétrie** : La clé publique ne peut pas déchiffrer ce qu'elle a chiffré
- **Complexité mathématique** : Casser RSA nécessite de factoriser de très grands nombres (impossible avec les ordinateurs actuels)
- **Standard** : RSA est utilisé partout dans le monde (banques, sites web sécurisés, etc.)

### 6.4 Stockage sécurisé

#### Base de données PostgreSQL
- Les fichiers chiffrés sont stockés dans une base de données PostgreSQL
- Chaque fichier est associé à son propriétaire (via le nom d'utilisateur)
- Seul le propriétaire peut voir et accéder à ses fichiers

#### Métadonnées stockées
- Nom original du fichier
- Nom du fichier chiffré (unique, généré automatiquement)
- Taille du fichier original
- Date de stockage
- Propriétaire (nom d'utilisateur)
- Contenu chiffré (sous forme de données binaires)

#### Sécurité au niveau base de données
- Les utilisateurs ne peuvent accéder qu'à leurs propres fichiers
- Les requêtes SQL vérifient toujours l'identité du propriétaire
- Les fichiers sont stockés sous forme chiffrée (même la base de données ne peut pas les lire)

---

## 7. TECHNOLOGIES UTILISÉES

### 7.1 Langage de programmation : Java

#### Pourquoi Java ?
- **Langage orienté objet** : Permet une organisation claire du code
- **Portabilité** : Fonctionne sur Windows, Linux, Mac
- **Sécurité intégrée** : Java a des mécanismes de sécurité intégrés
- **Bibliothèques riches** : Beaucoup d'outils disponibles

#### Version utilisée
- **Java 17** : Version moderne et stable

### 7.2 Interface graphique : JavaFX

#### Qu'est-ce que JavaFX ?
JavaFX est une bibliothèque Java pour créer des interfaces graphiques modernes.

#### Pourquoi JavaFX ?
- **Moderne** : Interface visuelle attrayante
- **Facile à utiliser** : Création de fenêtres, boutons, formulaires simple
- **Intégré à Java** : Pas besoin d'outils externes

#### Éléments utilisés dans le projet
- **Fenêtres (Stage)** : Les différentes écrans de l'application
- **Scènes (Scene)** : Le contenu de chaque fenêtre
- **Boutons (Button)** : Actions de l'utilisateur
- **Champs de texte (TextField, PasswordField)** : Saisie de données
- **Listes (ListView)** : Affichage des fichiers
- **Dialogs** : Fenêtres de dialogue (inscription, messages)

### 7.3 Cryptographie : Java Cryptography Architecture (JCA)

#### Qu'est-ce que JCA ?
JCA est l'API Java standard pour la cryptographie. Elle fournit :
- **KeyPairGenerator** : Génération de paires de clés RSA
- **Cipher** : Chiffrement et déchiffrement
- **MessageDigest** : Hachage (SHA-256)
- **SecureRandom** : Génération de nombres aléatoires sécurisés

#### Avantages
- **Standard** : Utilisé partout dans l'industrie
- **Sécurisé** : Implémentations testées et validées
- **Facile à utiliser** : API simple et claire

### 7.4 Base de données : PostgreSQL

#### Qu'est-ce que PostgreSQL ?
PostgreSQL est un système de gestion de base de données relationnelle (SGBD) open-source et puissant.

#### Pourquoi PostgreSQL ?
- **Fiable** : Très stable et robuste
- **Performant** : Gère efficacement de grandes quantités de données
- **Sécurisé** : Gestion avancée des permissions
- **Support des données binaires** : Peut stocker les fichiers chiffrés (type BYTEA)

#### Utilisation dans le projet
- Stockage des utilisateurs et de leurs mots de passe hachés
- Stockage des fichiers chiffrés et de leurs métadonnées
- Gestion des relations entre utilisateurs et fichiers

### 7.5 Gestion de projet : Maven

#### Qu'est-ce que Maven ?
Maven est un outil de gestion de projet Java qui facilite :
- La gestion des dépendances (bibliothèques externes)
- La compilation du code
- Le packaging de l'application

#### Dépendances utilisées
- **JavaFX Controls** : Interface graphique
- **PostgreSQL Driver** : Connexion à la base de données

---

## 8. SCÉNARIO DE DÉMONSTRATION

### Préparation avant la démonstration

1. **Vérifier que la base de données est démarrée**
   - S'assurer que PostgreSQL est en cours d'exécution
   - Vérifier la connexion

2. **Préparer des fichiers de test**
   - Avoir quelques fichiers de test (texte, image, document)
   - Noter leurs noms et tailles

3. **Tester l'application avant**
   - S'assurer que tout fonctionne
   - Préparer un compte de démonstration

### Démonstration étape par étape

#### Phase 1 : Présentation de l'interface de connexion (2 minutes)

**Actions à faire :**
1. Lancer l'application
2. Montrer la fenêtre de connexion
3. Expliquer : "Voici l'interface de connexion. Les utilisateurs doivent s'authentifier pour accéder au système."

**Points à mentionner :**
- "L'interface est simple et intuitive"
- "Il y a deux options : se connecter ou s'inscrire"

#### Phase 2 : Inscription d'un nouvel utilisateur (3 minutes)

**Actions à faire :**
1. Cliquer sur "S'inscrire"
2. Remplir le formulaire :
   - Nom d'utilisateur : "demo_user"
   - Mot de passe : "demo1234"
   - Confirmer le mot de passe
3. Cliquer sur "OK"
4. Montrer le message de succès

**Points à mentionner :**
- "Le système vérifie que le nom d'utilisateur est unique"
- "Le mot de passe doit faire au moins 4 caractères"
- "Le mot de passe est haché avant d'être stocké - il n'est jamais stocké en clair"

#### Phase 3 : Connexion (2 minutes)

**Actions à faire :**
1. Entrer le nom d'utilisateur : "demo_user"
2. Entrer le mot de passe : "demo1234"
3. Cliquer sur "Se connecter"
4. Montrer l'interface principale qui s'affiche

**Points à mentionner :**
- "Le système vérifie le mot de passe en comparant les hachés"
- "Si les identifiants sont corrects, l'utilisateur accède à l'application"

#### Phase 4 : Chiffrement d'un fichier (5 minutes)

**Actions à faire :**
1. Montrer l'interface principale
2. Expliquer : "La liste des fichiers est vide car c'est un nouveau compte"
3. Cliquer sur "Sélectionner un fichier"
4. Choisir un fichier de test (par exemple un fichier texte)
5. Montrer que le fichier est sélectionné
6. Cliquer sur "Chiffrer & Stocker"
7. Attendre le message de confirmation
8. Cliquer sur "Actualiser la liste"
9. Montrer que le fichier apparaît dans la liste

**Points à mentionner :**
- "Le fichier est lu depuis le disque"
- "Une paire de clés RSA est générée automatiquement"
- "Le fichier est chiffré par blocs avec la clé publique"
- "Le fichier chiffré est stocké dans la base de données"
- "Seul le propriétaire peut voir ses fichiers"

#### Phase 5 : Déchiffrement d'un fichier (4 minutes)

**Actions à faire :**
1. Sélectionner le fichier dans la liste
2. Cliquer sur "Déchiffrer & Ouvrir"
3. Choisir un emplacement pour sauvegarder le fichier déchiffré
4. Attendre le message de confirmation
5. Ouvrir le fichier déchiffré pour montrer qu'il est identique à l'original

**Points à mentionner :**
- "Le fichier chiffré est récupéré depuis la base de données"
- "Il est déchiffré avec la clé privée"
- "Le fichier original est restauré exactement comme il était"
- "Seul le propriisateur peut déchiffrer ses fichiers"

#### Phase 6 : Sécurité et architecture (3 minutes)

**Actions à faire :**
1. Expliquer brièvement l'architecture
2. Mentionner les mécanismes de sécurité
3. Répondre aux questions éventuelles

**Points à mentionner :**
- "L'application suit une architecture en couches"
- "Les mots de passe sont hachés avec SHA-256"
- "Les fichiers sont chiffrés avec RSA 2048 bits"
- "Tout est stocké de manière sécurisée dans PostgreSQL"

### Durée totale de la démonstration : environ 19 minutes

### Conseils pour la démonstration

- **Parler lentement** : Expliquer chaque action
- **Anticiper les erreurs** : Si quelque chose ne fonctionne pas, expliquer pourquoi
- **Montrer la sécurité** : Insister sur les aspects sécurité
- **Répondre aux questions** : Être prêt à expliquer les concepts

---

## 9. LIMITATIONS ET AMÉLIORATIONS POSSIBLES

### 9.1 Limitations actuelles

#### Limitation 1 : Clés RSA générées à chaque session
**Problème :** Les clés RSA sont générées à chaque démarrage de l'application. Si l'application est fermée, les clés sont perdues et les fichiers ne peuvent plus être déchiffrés.

**Impact :** Les fichiers chiffrés lors d'une session ne peuvent être déchiffrés que pendant cette même session.

**Pourquoi c'est une limitation :** En production, les utilisateurs doivent pouvoir déchiffrer leurs fichiers même après avoir fermé et rouvert l'application.

#### Limitation 2 : Pas de gestion de plusieurs utilisateurs simultanés
**Problème :** L'application n'est pas conçue pour gérer plusieurs utilisateurs connectés en même temps.

**Impact :** Un seul utilisateur peut utiliser l'application à la fois.

**Pourquoi c'est une limitation :** Dans un environnement réel, plusieurs utilisateurs doivent pouvoir utiliser le système simultanément.

#### Limitation 3 : Pas de récupération de mot de passe
**Problème :** Si un utilisateur oublie son mot de passe, il ne peut pas le récupérer.

**Impact :** L'utilisateur doit créer un nouveau compte et perd l'accès à ses anciens fichiers.

**Pourquoi c'est une limitation :** Les applications modernes offrent généralement un système de récupération de mot de passe.

#### Limitation 4 : Taille des fichiers limitée par RSA
**Problème :** RSA a des limites de taille (environ 245 octets par bloc pour une clé de 2048 bits). Les gros fichiers doivent être divisés en nombreux blocs.

**Impact :** Le chiffrement de très gros fichiers peut être lent.

**Pourquoi c'est une limitation :** Pour de très gros fichiers, d'autres algorithmes (comme AES) seraient plus efficaces.

#### Limitation 5 : Pas de partage de fichiers
**Problème :** Les utilisateurs ne peuvent pas partager leurs fichiers chiffrés avec d'autres utilisateurs.

**Impact :** Chaque fichier est strictement privé à son propriétaire.

**Pourquoi c'est une limitation :** Dans certains cas, les utilisateurs veulent partager des fichiers sécurisés.

### 9.2 Améliorations possibles

#### Amélioration 1 : Persistance des clés RSA
**Solution :** Stocker les clés privées de manière sécurisée (chiffrées avec le mot de passe de l'utilisateur) dans la base de données.

**Avantages :**
- Les utilisateurs peuvent déchiffrer leurs fichiers même après avoir fermé l'application
- Les fichiers restent accessibles à long terme

**Complexité :** Moyenne - Nécessite un chiffrement supplémentaire des clés privées

#### Amélioration 2 : Système multi-utilisateurs
**Solution :** Implémenter une gestion de sessions utilisateur avec authentification par token.

**Avantages :**
- Plusieurs utilisateurs peuvent utiliser l'application simultanément
- Meilleure scalabilité

**Complexité :** Élevée - Nécessite une refonte partielle de l'architecture

#### Amélioration 3 : Récupération de mot de passe
**Solution :** Ajouter un système de réinitialisation de mot de passe par email.

**Avantages :**
- Meilleure expérience utilisateur
- Réduction des comptes abandonnés

**Complexité :** Moyenne - Nécessite l'intégration d'un service d'email

#### Amélioration 4 : Chiffrement hybride (RSA + AES)
**Solution :** Utiliser AES pour chiffrer le fichier (rapide) et RSA pour chiffrer la clé AES (sécurisé).

**Avantages :**
- Meilleure performance pour les gros fichiers
- Conserve la sécurité de RSA

**Complexité :** Moyenne - Nécessite l'implémentation d'AES

#### Amélioration 5 : Partage de fichiers
**Solution :** Permettre aux utilisateurs de partager des fichiers avec d'autres utilisateurs en utilisant leurs clés publiques.

**Avantages :**
- Collaboration entre utilisateurs
- Partage sécurisé

**Complexité :** Élevée - Nécessite une gestion complexe des permissions

#### Amélioration 6 : Interface web
**Solution :** Créer une version web de l'application (au lieu de JavaFX).

**Avantages :**
- Accessible depuis n'importe quel navigateur
- Pas besoin d'installer l'application
- Meilleure portabilité

**Complexité :** Très élevée - Nécessite une refonte complète

#### Amélioration 7 : Audit et logs
**Solution :** Enregistrer toutes les actions (connexions, chiffrements, déchiffrements) dans un journal.

**Avantages :**
- Traçabilité des actions
- Détection d'activités suspectes
- Conformité réglementaire

**Complexité :** Faible à moyenne - Ajout d'un système de logging

---

## 10. CONCLUSION

### 10.1 Ce que le projet démontre

#### Compétences techniques acquises

**Programmation Java :**
- Maîtrise de la programmation orientée objet
- Utilisation des classes, méthodes, constructeurs
- Gestion des exceptions
- Utilisation des packages

**Interface graphique :**
- Création d'interfaces utilisateur avec JavaFX
- Gestion des événements utilisateur
- Design d'expérience utilisateur

**Sécurité informatique :**
- Compréhension des principes de cryptographie
- Implémentation du hachage de mots de passe
- Utilisation du chiffrement RSA
- Bonnes pratiques de sécurité

**Base de données :**
- Conception de schéma de base de données
- Utilisation de SQL avec Java (JDBC)
- Stockage de données binaires

**Architecture logicielle :**
- Séparation des responsabilités (couches)
- Organisation modulaire du code
- Réutilisabilité des composants

### 10.2 Objectifs atteints

✅ **Authentification sécurisée** : Les utilisateurs peuvent créer un compte et se connecter de manière sécurisée

✅ **Chiffrement des fichiers** : Les fichiers sont chiffrés avec RSA avant stockage

✅ **Stockage sécurisé** : Les fichiers chiffrés sont stockés dans une base de données PostgreSQL

✅ **Interface utilisateur** : Une interface graphique intuitive permet d'utiliser facilement l'application

✅ **Sécurité** : Les mots de passe sont hachés et les fichiers sont chiffrés

### 10.3 Valeur pédagogique

Ce projet démontre :
- La capacité à comprendre et implémenter des concepts de sécurité
- La maîtrise de technologies Java modernes
- La capacité à structurer un projet de manière professionnelle
- La compréhension des enjeux de sécurité des données

### 10.4 Applications réelles

Les concepts appris dans ce projet sont utilisés dans :
- **Applications bancaires** : Chiffrement des transactions
- **Systèmes de messagerie sécurisée** : Protection des communications
- **Stockage cloud sécurisé** : Protection des données dans le cloud
- **Systèmes de vote électronique** : Sécurisation des votes
- **Applications médicales** : Protection des données de santé

---

## 11. CONSEILS POUR LA DÉFENSE ORALE

### 11.1 Phrases clés à utiliser

#### Introduction
- "Ce projet a pour objectif de créer un système sécurisé de chiffrement de fichiers utilisant l'algorithme RSA."
- "L'application permet aux utilisateurs de stocker leurs fichiers de manière confidentielle grâce au chiffrement."

#### Authentification
- "Les mots de passe sont hachés avec SHA-256 avant d'être stockés, ce qui garantit qu'ils ne sont jamais stockés en clair."
- "L'authentification se fait par comparaison de hachés, ce qui est une méthode sécurisée et standard."

#### Chiffrement
- "RSA est un algorithme de chiffrement asymétrique qui utilise une paire de clés : une clé publique pour chiffrer et une clé privée pour déchiffrer."
- "Les fichiers sont chiffrés par blocs car RSA a des limitations de taille, mais cela garantit une sécurité maximale."

#### Architecture
- "L'application suit une architecture en couches qui sépare l'interface utilisateur, la logique métier et l'accès aux données."
- "Cette séparation facilite la maintenance et permet une meilleure organisation du code."

#### Sécurité
- "La sécurité est assurée à plusieurs niveaux : hachage des mots de passe, chiffrement RSA des fichiers, et contrôle d'accès au niveau de la base de données."

### 11.2 Points à mettre en avant

#### 1. Sécurité
- **Insister sur** : "Les mots de passe ne sont jamais stockés en clair"
- **Expliquer** : "Même si quelqu'un accède à la base de données, il ne peut pas connaître les mots de passe"
- **Mentionner** : "RSA 2048 bits est un standard de sécurité utilisé dans l'industrie"

#### 2. Architecture
- **Insister sur** : "Séparation claire des responsabilités"
- **Expliquer** : "Chaque couche a un rôle précis et communique avec les autres de manière structurée"
- **Mentionner** : "Cette architecture facilite l'évolution et la maintenance"

#### 3. Technologies
- **Insister sur** : "Utilisation de technologies standards et éprouvées"
- **Expliquer** : "Java, JavaFX, PostgreSQL et JCA sont des technologies professionnelles"
- **Mentionner** : "Ces technologies sont utilisées dans l'industrie"

#### 4. Fonctionnalités
- **Insister sur** : "Application complète et fonctionnelle"
- **Expliquer** : "Tous les aspects sont implémentés : authentification, chiffrement, stockage, déchiffrement"
- **Mentionner** : "L'interface est intuitive et facile à utiliser"

### 11.3 Réponses aux questions fréquentes

#### Question : "Pourquoi avez-vous choisi RSA plutôt qu'un autre algorithme ?"
**Réponse :** "RSA est un algorithme de chiffrement asymétrique standard et très sécurisé. Il est utilisé partout dans l'industrie pour la sécurité des données. Bien qu'il soit plus lent que les algorithmes symétriques comme AES, il offre une sécurité maximale pour ce type d'application."

#### Question : "Pourquoi les clés sont-elles générées à chaque session ?"
**Réponse :** "C'est effectivement une limitation de la version actuelle. Dans une version améliorée, les clés privées seraient stockées de manière sécurisée (chiffrées avec le mot de passe de l'utilisateur) pour permettre le déchiffrement même après fermeture de l'application."

#### Question : "Comment garantissez-vous que seul le propriétaire peut accéder à ses fichiers ?"
**Réponse :** "Plusieurs mécanismes sont en place : d'abord, l'authentification vérifie l'identité de l'utilisateur. Ensuite, les requêtes SQL vérifient toujours que l'utilisateur est bien le propriétaire du fichier. Enfin, seul le propriétaire possède la clé privée nécessaire pour déchiffrer ses fichiers."

#### Question : "Que se passe-t-il si quelqu'un accède à la base de données ?"
**Réponse :** "Même si quelqu'un accède à la base de données, il ne peut pas faire grand-chose : les mots de passe sont hachés (irréversibles), et les fichiers sont chiffrés avec RSA. Sans la clé privée, il est mathématiquement impossible de déchiffrer les fichiers."

#### Question : "Pourquoi utilisez-vous SHA-256 pour les mots de passe ?"
**Réponse :** "SHA-256 est un algorithme de hachage standard et sécurisé. Il transforme le mot de passe en une chaîne unique de 64 caractères. C'est unidirectionnel : on peut créer le haché à partir du mot de passe, mais on ne peut pas retrouver le mot de passe à partir du haché."

#### Question : "Comment fonctionne le chiffrement par blocs ?"
**Réponse :** "RSA a une limitation de taille : avec une clé de 2048 bits, on ne peut chiffrer que des blocs d'environ 245 octets. Donc, pour un fichier plus grand, on le divise en blocs, on chiffre chaque bloc séparément, puis on assemble les blocs chiffrés. Le déchiffrement fait l'inverse : on divise le fichier chiffré en blocs, on déchiffre chaque bloc, puis on assemble."

### 11.4 Structure de présentation recommandée

#### 1. Introduction (2 minutes)
- Présentation du projet
- Contexte et problématique

#### 2. Objectifs (2 minutes)
- Objectifs principaux
- Objectifs spécifiques

#### 3. Architecture (3 minutes)
- Organisation du code
- Séparation des couches
- Flux de données

#### 4. Sécurité (5 minutes)
- Authentification
- Hachage des mots de passe
- Chiffrement RSA
- Stockage sécurisé

#### 5. Technologies (2 minutes)
- Java et JavaFX
- PostgreSQL
- JCA

#### 6. Démonstration (5 minutes)
- Inscription et connexion
- Chiffrement d'un fichier
- Déchiffrement d'un fichier

#### 7. Limitations et améliorations (3 minutes)
- Limitations actuelles
- Améliorations possibles

#### 8. Conclusion (2 minutes)
- Ce que le projet démontre
- Compétences acquises

#### 9. Questions (5-10 minutes)
- Réponses aux questions du jury

**Durée totale : environ 25-30 minutes**

### 11.5 Conseils généraux

#### Avant la présentation
- **Préparer la démonstration** : Tester plusieurs fois avant
- **Anticiper les questions** : Préparer des réponses aux questions fréquentes
- **Connaître le code** : Être capable d'expliquer chaque partie
- **Préparer des fichiers de test** : Avoir des fichiers prêts pour la démo

#### Pendant la présentation
- **Parler clairement** : Articuler et parler à un rythme modéré
- **Expliquer les concepts** : Ne pas juste montrer, expliquer pourquoi
- **Rester calme** : Prendre son temps, respirer
- **Être honnête** : Si on ne sait pas quelque chose, l'admettre et proposer de chercher

#### Gestion du stress
- **Respirer profondément** avant de commencer
- **Boire de l'eau** si nécessaire
- **Faire des pauses** si besoin
- **Sourire** : Montrer que vous êtes confiant

#### Réponses aux questions difficiles
- **Écouter attentivement** : Bien comprendre la question avant de répondre
- **Réfléchir avant de répondre** : Prendre quelques secondes si nécessaire
- **Être précis** : Répondre directement à la question
- **Admettre les limites** : Si quelque chose n'est pas implémenté, l'expliquer

---

## 12. RÉSUMÉ EXÉCUTIF

### En une phrase
Ce projet est une application Java qui permet aux utilisateurs de chiffrer leurs fichiers avec RSA et de les stocker de manière sécurisée dans une base de données PostgreSQL.

### Points clés à retenir

1. **Sécurité** : Mots de passe hachés (SHA-256) + fichiers chiffrés (RSA 2048 bits)
2. **Architecture** : Organisation en couches (UI, Service, Model, Util)
3. **Technologies** : Java 17, JavaFX, PostgreSQL, JCA
4. **Fonctionnalités** : Authentification, chiffrement, stockage, déchiffrement
5. **Séparation des responsabilités** : Code organisé et maintenable

### Message principal pour la défense

"J'ai créé une application complète de chiffrement de fichiers qui démontre ma compréhension des concepts de sécurité informatique, ma maîtrise de Java et de ses bibliothèques, et ma capacité à structurer un projet de manière professionnelle. L'application utilise des technologies standards de l'industrie et implémente des mécanismes de sécurité robustes pour protéger les données des utilisateurs."

---

**Fin du document de présentation**

*Ce document vous aidera à préparer votre présentation orale et vos slides. Adaptez le contenu selon vos besoins et le temps alloué pour votre défense.*

