import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        MusicPlayerGUI musicPlayerGUI = new MusicPlayerGUI();
        PlaylistManager playlistManager = new PlaylistManager();

        // Update the track list table in the database
        databaseManager.updateTrackListTable();

        // Retrieve all songs from the database
        List<Song> songs = databaseManager.getAllSongs();

        // Create a new playlist or open an existing playlist
        Playlist playlist = playlistManager.createOrOpenPlaylist("My Playlist");

        // Add songs to the playlist
        for (Song song : songs) {
            playlistManager.addSong(playlist, song);
        }

        // Display the music player GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                musicPlayerGUI.setVisible(true);
            }
        });
    }
}
