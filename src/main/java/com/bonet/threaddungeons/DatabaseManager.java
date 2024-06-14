package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:threaddungeons.db"; // URL de la base de datos SQLite
    private static final String USERS_JSON = "/usuarios.json"; // Ruta del archivo JSON de usuarios

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Crear tabla de usuarios si no existe
                String createUsuariosTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "login TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "email TEXT)";
                stmt.execute(createUsuariosTableSQL);

                // Crear tabla de tableros si no existe
                String createTablerosTableSQL = "CREATE TABLE IF NOT EXISTS tableros (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "estado TEXT NOT NULL)";
                stmt.execute(createTablerosTableSQL);

                // Crear tabla de relación usuarios-tableros si no existe
                String createUsuariosTablerosTableSQL = "CREATE TABLE IF NOT EXISTS usuarios_tableros (" +
                        "usuario_id INTEGER," +
                        "tablero_id INTEGER," +
                        "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                        "FOREIGN KEY (tablero_id) REFERENCES tableros(id))";
                stmt.execute(createUsuariosTablerosTableSQL);

                // Crear tabla de puntuaciones si no existe
                String createScoresTableSQL = "CREATE TABLE IF NOT EXISTS scores (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "username TEXT," +
                        "etapa_actual INTEGER," +
                        "casilla_actual INTEGER," +
                        "dmg REAL," +
                        "speed REAL," +
                        "oro INTEGER," +
                        "FOREIGN KEY (user_id) REFERENCES usuarios(id))";
                stmt.execute(createScoresTableSQL);

                // Cargar usuarios desde el JSON
                cargarUsuariosDesdeJson(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cargarUsuariosDesdeJson(Connection conn) {
        Gson gson = new Gson();
        try (InputStreamReader reader = new InputStreamReader(DatabaseManager.class.getResourceAsStream(USERS_JSON))) {
            Type userListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(reader, userListType);

            // Insertar usuarios en la base de datos si no existen
            String insertSQL = "INSERT OR IGNORE INTO usuarios (login, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (Usuario usuario : usuarios) {
                    pstmt.setString(1, usuario.getLogin());
                    pstmt.setString(2, usuario.getPassword());
                    pstmt.setString(3, usuario.getEmail());
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
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
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveScore(Usuario user, Tablero tablero) {
        String sqlSelect = "SELECT * FROM scores WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, user.getId());
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                int existingOro = rs.getInt("oro");
                if (tablero.getJugador().getOro() > existingOro) {
                    String sqlUpdate = "UPDATE scores SET etapa_actual = ?, casilla_actual = ?, dmg = ?, speed = ?, oro = ? WHERE user_id = ?";
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setInt(1, tablero.getJugador().getEtapaActual());
                        pstmtUpdate.setInt(2, tablero.getJugador().getCasillaActual());
                        pstmtUpdate.setDouble(3, tablero.getJugador().getDmg());
                        pstmtUpdate.setDouble(4, tablero.getJugador().getVelocidad());
                        pstmtUpdate.setInt(5, tablero.getJugador().getOro());
                        pstmtUpdate.setInt(6, user.getId());
                        pstmtUpdate.executeUpdate();
                    }
                }
            } else {
                String sqlInsert = "INSERT INTO scores (user_id, username, etapa_actual, casilla_actual, dmg, speed, oro) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                    pstmtInsert.setInt(1, user.getId());
                    pstmtInsert.setString(2, user.getLogin());
                    pstmtInsert.setInt(3, tablero.getJugador().getEtapaActual());
                    pstmtInsert.setInt(4, tablero.getJugador().getCasillaActual());
                    pstmtInsert.setDouble(5, tablero.getJugador().getDmg());
                    pstmtInsert.setDouble(6, tablero.getJugador().getVelocidad());
                    pstmtInsert.setInt(7, tablero.getJugador().getOro());
                    pstmtInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM scores ORDER BY oro DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                scores.add(new Score(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("etapa_actual"),
                        rs.getInt("casilla_actual"),
                        rs.getDouble("dmg"),
                        rs.getDouble("speed"),
                        rs.getInt("oro")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}
