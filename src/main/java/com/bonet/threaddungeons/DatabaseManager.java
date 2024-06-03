package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:threaddungeons.db";
    private static final String USERS_JSON = "src/main/resources/usuarios.json";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Crear tablas si no existen
                String createUsuariosTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "login TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "nombre TEXT," +
                        "email TEXT)";
                stmt.execute(createUsuariosTableSQL);

                String createTablerosTableSQL = "CREATE TABLE IF NOT EXISTS tableros (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "estado TEXT NOT NULL)";
                stmt.execute(createTablerosTableSQL);

                String createUsuariosTablerosTableSQL = "CREATE TABLE IF NOT EXISTS usuarios_tableros (" +
                        "usuario_id INTEGER," +
                        "tablero_id INTEGER," +
                        "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                        "FOREIGN KEY (tablero_id) REFERENCES tableros(id))";
                stmt.execute(createUsuariosTablerosTableSQL);

                // Cargar usuarios desde el JSON
                cargarUsuariosDesdeJson(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cargarUsuariosDesdeJson(Connection conn) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(USERS_JSON)) {
            Type userListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(reader, userListType);

            String insertSQL = "INSERT OR IGNORE INTO usuarios (login, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (Usuario usuario : usuarios) {
                    pstmt.setString(1, usuario.getLogin());
                    pstmt.setString(2, usuario.getPassword());
                    pstmt.setString(3, usuario.getEmail());
                    pstmt.executeUpdate();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Usuario getUserByLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean authenticateUser(String login, String password) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Usuario getUserById(int userId) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Tablero getTableroByUserId(int userId) {
        String sql = "SELECT t.estado FROM tableros t " +
                "JOIN usuarios_tableros ut ON t.id = ut.tablero_id " +
                "WHERE ut.usuario_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String json = rs.getString("estado");
                return new Gson().fromJson(json, Tablero.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveTablero(int userId, Tablero tablero) {
        String json = new Gson().toJson(tablero);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String findTableroSql = "SELECT t.id FROM tableros t " +
                    "JOIN usuarios_tableros ut ON t.id = ut.tablero_id " +
                    "WHERE ut.usuario_id = ?";
            int tableroId;
            try (PreparedStatement findStmt = conn.prepareStatement(findTableroSql)) {
                findStmt.setInt(1, userId);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    tableroId = rs.getInt("id");
                } else {
                    tableroId = -1;
                }
            }

            if (tableroId == -1) {
                // Insertar nuevo tablero
                String insertTableroSql = "INSERT INTO tableros (estado) VALUES (?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertTableroSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, json);
                    insertStmt.executeUpdate();
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        tableroId = generatedKeys.getInt(1);
                    }
                }

                // Insertar en la tabla de relación
                String insertRelacionSql = "INSERT INTO usuarios_tableros (usuario_id, tablero_id) VALUES (?, ?)";
                try (PreparedStatement insertRelacionStmt = conn.prepareStatement(insertRelacionSql)) {
                    insertRelacionStmt.setInt(1, userId);
                    insertRelacionStmt.setInt(2, tableroId);
                    insertRelacionStmt.executeUpdate();
                }
            } else {
                // Actualizar tablero existente
                String updateTableroSql = "UPDATE tableros SET estado = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateTableroSql)) {
                    updateStmt.setString(1, json);
                    updateStmt.setInt(2, tableroId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String login, String password, String email) {
        String sql = "INSERT INTO usuarios (login, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, email); // Assuming 'email' is not used and 'email' is optional
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
