package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5433/crypto";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Pilote PostgreSQL introuvable", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
