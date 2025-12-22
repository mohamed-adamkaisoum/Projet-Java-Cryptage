package service;

import model.Utilisateur;
import util.PasswordUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Service d'authentification des utilisateurs
 * Gère l'enregistrement et la vérification des utilisateurs
 * Stocke les utilisateurs dans un fichier sérialisé
 */
public class AuthentificationService {
    
    private static final String USERS_FILE = "src/main/resources/users.dat";
    private Map<String, Utilisateur> users;
    
    public AuthentificationService() {
        this.users = new HashMap<>();
        loadUsers();
        initializeDefaultUsers();
    }
    
    /**
     * Charge les utilisateurs depuis le fichier de stockage
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Map<String, Utilisateur>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erreur lors du chargement des utilisateurs: " + e.getMessage());
                users = new HashMap<>();
            }
        }
    }
    
    /**
     * Sauvegarde les utilisateurs dans le fichier de stockage
     */
    private void saveUsers() {
        try {
            File file = new File(USERS_FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs: " + e.getMessage());
        }
    }
    
    /**
     * Initialise deux utilisateurs par défaut pour le système
     */
    private void initializeDefaultUsers() {
        if (users.isEmpty()) {
            // Créer deux utilisateurs par défaut
            registerUser("admin", "admin123");
            registerUser("user", "user123");
        }
    }
    
    /**
     * Enregistre un nouvel utilisateur
     * 
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe en clair (sera hashé)
     * @return true si l'utilisateur a été créé, false s'il existe déjà
     */
    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Utilisateur déjà existant
        }
        
        String passwordHash = PasswordUtils.hashPassword(password);
        Utilisateur user = new Utilisateur(username, passwordHash);
        users.put(username, user);
        saveUsers();
        return true;
    }
    
    /**
     * Authentifie un utilisateur avec son nom d'utilisateur et son mot de passe
     * 
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur si l'authentification réussit, null sinon
     */
    public Utilisateur authenticate(String username, String password) {
        Utilisateur user = users.get(username);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
    
    /**
     * Vérifie si un utilisateur existe
     * 
     * @param username Le nom d'utilisateur
     * @return true si l'utilisateur existe
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}






