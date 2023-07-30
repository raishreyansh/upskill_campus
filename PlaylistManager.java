import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PlaylistManager {
    private Map<String, Playlist> playlists;
    private int maxPlaylistId; // Current maximum playlist ID
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "0247";
    private Connection connection;

    private DatabaseManager databaseManager;


    public PlaylistManager() {
        playlists = new HashMap<>();
        maxPlaylistId = 0;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Playlist createOrOpenPlaylist(String playlistName) {
        int playlistId = generatePlaylistId();
        Playlist playlist = new Playlist(playlistId, playlistName);
        playlists.put(String.valueOf(playlistId), playlist);
        createPlaylistsTable();

        try {
            // Check if the playlist already exists in the table
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM playlists WHERE Name = ?");
            checkStatement.setString(1, playlistName);
            ResultSet resultSet = checkStatement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            checkStatement.close();

            if (!exists) {
                // Insert the new playlist into the table
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO playlists (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                insertStatement.setString(1, playlistName);
                insertStatement.executeUpdate();

                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    playlistId = generatedKeys.getInt(1);
                    playlist.setPlaylistId(playlistId);
                }
                insertStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        playlists.put(String.valueOf(playlistId), playlist);
        return playlist;
    }


    public void createPlaylistsTable() {
        try {
            Statement statement = connection.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS playlists ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "Name VARCHAR(255) NOT NULL)";
            statement.executeUpdate(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int generatePlaylistId() {
        maxPlaylistId++;
        return maxPlaylistId;
    }

    public void addSong(Playlist playlist, Song song) {
        playlist.addSong(song);
    }

    public Playlist createPlaylist(DatabaseManager databaseManager, String playlistName) {
        try {
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO playlists (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playlistName);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int playlistId = generatedKeys.getInt(1);
                Playlist playlist = new Playlist(playlistId, playlistName);
                playlists.put(String.valueOf(playlistId), playlist);
                return playlist;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to add a song to a playlist and update the playlist_track table
    // Method to add a song to a playlist and update the playlist_track table
    public void addSongToPlaylist(Playlist playlist, Song song) {
        // Add the song to the playlist
        playlist.addSong(song);

        // Get the playlist ID
        int playlistId = playlist.getPlaylistId();

        // Get the track ID
        int trackId = song.getId();

        // Update the playlist_track table with the new association
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO playlist_track (PlaylistId, TrackId) VALUES (?, ?)")) {
            statement.setInt(1, playlistId);
            statement.setInt(2, trackId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeSongFromPlaylist(Playlist playlist, Song song) {
        playlist.removeSong(song);
    }

    public List<String> getAllPlaylistNames() {
        return new ArrayList<>(playlists.keySet());
    }

    public Playlist getPlaylistByName(String playlistName) {
        return playlists.get(playlistName);
    }
}
