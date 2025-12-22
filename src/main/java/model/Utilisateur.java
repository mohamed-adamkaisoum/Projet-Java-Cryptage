package model;

import java.io.Serializable;

/**
 * Classe représentant un utilisateur du système
 * Contient les informations de connexion (nom d'utilisateur et hash du mot de passe)
 */
public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String passwordHash; // Hash du mot de passe (SHA-256)
    
    public Utilisateur(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utilisateur that = (Utilisateur) obj;
        return username != null ? username.equals(that.username) : that.username == null;
    }
    
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}






