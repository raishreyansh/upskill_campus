public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private String filePath;

    public Song(int id, String title, String artist, String album, int duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return title;
    }
}
