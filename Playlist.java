import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int playlistId;
    private String name;
    private List<Song> songs;
    private int currentIndex;

    public Playlist(int playlistId, String name) {
        this.playlistId = playlistId;
        this.name = name;
        songs = new ArrayList<>();
        currentIndex = -1;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    // Add the getId method
    public int getId() {
        return playlistId;
    }


    public String getName() {
        return name;
    }

    public void addSong(Song song) {
        songs.add(song);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }

    public void removeSong(Song song) {
        int index = songs.indexOf(song);
        if (index != -1) {
            songs.remove(index);
            if (currentIndex >= songs.size()) {
                currentIndex = songs.size() - 1;
            }
        }
    }

    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < songs.size()) {
            return songs.get(currentIndex);
        }
        return null;
    }

    public int getCurrentSongIndex() {
        return currentIndex;
    }

    public void setCurrentSongIndex(int index) {
        if (index >= 0 && index < songs.size()) {
            currentIndex = index;
        }
    }

    public void nextSong() {
        currentIndex++;
        if (currentIndex >= songs.size()) {
            currentIndex = 0;
        }
    }

    public void previousSong() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = songs.size() - 1;
        }
    }


    public List<Song> getSongs() {
        return songs;
    }

    public void addAllSongs(List<Song> songs) {
        this.songs.addAll(songs);
        if (currentIndex == -1 && !this.songs.isEmpty()) {
            currentIndex = 0;
        }
    }
}
