package service;

import model.Utilisateur;
import util.PasswordUtils;
import util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthentificationService {

    public AuthentificationService() {
    }

    public boolean registerUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        return insertUser(username, passwordHash);
    }

    public Utilisateur authenticate(String username, String password) {
        Utilisateur user = findUser(username);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'utilisateur: " + e.getMessage());
            return false;
        }
    }

    private Utilisateur findUser(String username) {
        String sql = "SELECT username, password_hash FROM users WHERE username = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getString("username"),
                            rs.getString("password_hash")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        }
        return null;
    }

    private boolean insertUser(String username, String passwordHash) {
        String sql = "INSERT INTO users(username, password_hash) VALUES (?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'utilisateur: " + e.getMessage());
            return false;
        }
    }
}
