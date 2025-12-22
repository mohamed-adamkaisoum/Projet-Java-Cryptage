package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utilitaire pour le hachage sécurisé des mots de passe
 * Utilise SHA-256 pour créer des hashs de mots de passe
 */
public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Génère un hash SHA-256 d'un mot de passe
     * 
     * @param password Le mot de passe en clair
     * @return Le hash hexadécimal du mot de passe
     * @throws RuntimeException Si l'algorithme SHA-256 n'est pas disponible
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme SHA-256 non disponible", e);
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash donné
     * 
     * @param password Le mot de passe en clair à vérifier
     * @param hash Le hash stocké
     * @return true si le mot de passe correspond au hash, false sinon
     */
    public static boolean verifyPassword(String password, String hash) {
        String passwordHash = hashPassword(password);
        return passwordHash.equals(hash);
    }
    
    /**
     * Convertit un tableau de bytes en représentation hexadécimale
     * 
     * @param bytes Le tableau de bytes à convertir
     * @return La chaîne hexadécimale
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}






