import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

public class DatabaseManager {
    //your own database details
    private static final String DB_URL = "****";
    private static final String DB_USERNAME = "****";
    private static final String DB_PASSWORD = "****";

    private static final String MUSIC_DIRECTORY = "/* any music directory with mp3 files */";

    private Connection connection;
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTrackListTable() {
        File musicDirectory = new File(MUSIC_DIRECTORY);
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String filePath = file.getAbsolutePath();
                        try {
                            AudioFile audioFile = AudioFileIO.read(file);
                            String title = audioFile.getTag().getFirst(FieldKey.TITLE);
                            String artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
                            String album = audioFile.getTag().getFirst(FieldKey.ALBUM);
                            int duration = audioFile.getAudioHeader().getTrackLength();

                            // Check if the song already exists in the table
                            if (!songExists(title)) {
                                addSongToTable(title, artist, album, duration, filePath);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private boolean songExists(String title) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM track_list WHERE Title = ?");
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            statement.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addSongToTable(String title, String artist, String album, int duration, String filePath) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO track_list (Title, Artist, Album, Duration, file_path) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, title);
            statement.setString(2, artist);
            statement.setString(3, album);
            statement.setInt(4, duration);
            statement.setString(5, filePath);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM track_list");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("Title");
                String artist = resultSet.getString("Artist");
                String album = resultSet.getString("Album");
                int duration = resultSet.getInt("Duration");
                String filePath = resultSet.getString("file_path");

                Song song = new Song(id, title, artist, album, duration, filePath);
                songs.add(song);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    // Method to add a song to the playlist_track table
    public void addSongToPlaylistTrack(int playlistId, int trackId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO playlist_track (PlaylistId, TrackId) VALUES (?, ?)")) {
            statement.setInt(1, playlistId);
            statement.setInt(2, trackId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager();
        manager.updateTrackListTable();
        List<Song> songs = manager.getAllSongs();

        MusicPlayerGUI musicPlayerGUI = new MusicPlayerGUI();
        PlaylistManager playlistManager = new PlaylistManager();

        System.out.println("All Songs:");
        for (Song song : songs) {
            System.out.println(song.getTitle());
        }
    }
    // Other methods for database management...
}
