package com.exp.self.dao;

import com.exp.self.annotation.Component;
import com.exp.self.annotation.Primary;
import com.exp.self.model.Note;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@Primary
public class NoteJdbcDao implements NoteDao {

    private Connection connection;
    private final String url;
    private final String user;
    private final String password;

    public NoteJdbcDao() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find database.properties");
            }
            properties.load(input);
            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

            String driver = properties.getProperty("db.driver");
            Class.forName(driver);

            connection = DriverManager.getConnection(url, user, password);
            createTableIfNotExist();

            if (isTableEmpty()){
                save(new Note("Это моя первая заметка в базе данных"));
            }

        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to load database.properties");
        }
    }

    private void createTableIfNotExist() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS notes (id INT PRIMARY KEY, text VARCHAR(255))");

        }
    }

    private boolean isTableEmpty() throws SQLException{
        try(Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM notes")){
            if(resultSet.next()){
                return resultSet.getInt(1) == 0;
            }
        }
        return true;
    }

    private int findNextAvailableId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM notes ORDER BY id")) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id"));
            }
        }

        int nextId = 1;
        for (int id : existingIds) {
            if (id == nextId) {
                nextId++;
            } else {
                break;
            }
        }

        return nextId;
    }


    @Override
    public void save(Note note) {



        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO notes (id, text) VALUES (?, ?)")) {
            int nextId = findNextAvailableId();
            preparedStatement.setInt(1, nextId);
            preparedStatement.setString(2, note.getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save note", e);
        }
    }

    @Override
    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM notes")) {
            while (resultSet.next()) {
                notes.add(new Note(resultSet.getString("text")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve notes", e);
        }
        return notes;
    }

    @Override
    public void update(int index, String newText) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE notes SET text = ? WHERE id IN (SELECT id FROM notes LIMIT 1 OFFSET ?)")) {
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, index);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update note", e);
        }
    }

    @Override
    public void delete(int index) {

        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM notes WHERE id IN (SELECT id FROM notes LIMIT 1 OFFSET ?)")) {
            preparedStatement.setInt(1, index);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete note", e);
        }

    }
}
