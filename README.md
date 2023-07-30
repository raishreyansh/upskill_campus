# upskill_campus


## Overview
This repository contains the source code for a Java-based Music Player App with music playback and playlist management features. The application allows users to manage their music collection, create custom playlists, and store playlist data in a MySQL database for easy retrieval and organization of audio titles and user playlists.

## Requirements
To run the Music Player App, ensure you have the following requirements installed on your system:
- Java Development Kit (JDK) 8 or higher
- MySQL Server (with necessary privileges and connection information)

Note: Before running the app, ensure you have:

Updated the MySQL database credentials in the DatabaseManager class to match your own MySQL server setup.
Created the necessary tables (Playlist Table and Song Table) with the properties mentioned in the music_player_db.sql script.
Placed your .mp3 files in a folder of your choice. Update the directory location in the Song class (src/com/example/musicplayer/Song.java) to point to the folder containing your .mp3 files.

##**Creating the Required Tables**
Before running the application, you need to create three tables in the MySQL database to store playlist and track information. Use the following SQL queries to create the tables:

**playlist_track Table:**
CREATE TABLE playlist_track (
  PlaylistTrackId INT NOT NULL AUTO_INCREMENT,
  PlaylistId INT NOT NULL,
  TrackId INT NOT NULL,
  PRIMARY KEY (PlaylistTrackId),
  FOREIGN KEY (PlaylistId) REFERENCES playlists(PlaylistId),
  FOREIGN KEY (TrackId) REFERENCES track_list(id)
);

**playlists Table:**
CREATE TABLE playlists (
  PlaylistId INT NOT NULL AUTO_INCREMENT,
  Name VARCHAR(100) NOT NULL,
  PRIMARY KEY (PlaylistId)
);

**track_list Table:**
CREATE TABLE track_list (
  id INT NOT NULL AUTO_INCREMENT,
  Title VARCHAR(255) NOT NULL,
  Artist VARCHAR(255),
  Album VARCHAR(255),
  Duration INT,
  file_path VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);

The Music Player App's graphical user interface (GUI) will appear, allowing you to interact with the application and enjoy music playback and playlist management.

## Usage
- **Main Menu:** The main menu displays options to view existing playlists, create a new playlist, or exit the application.
- **Playlist Management:** When creating a new playlist, you can name it and add songs from your music collection. Existing playlists can be selected to view and manage the songs they contain.
- **Music Playback:** Upon selecting a playlist or song, the app will start playing the music. You can control playback using the provided buttons (play, pause, stop, skip).

## Future Work
This project is open to future enhancements and contributions. Feel free to explore the [Future Work Scope](link-to-future-work-readme) section in the repository for potential areas of improvement and additional features.

## Contributing
Contributions to the Music Player App are welcome. If you encounter any issues or have ideas for improvements, please submit a pull request or open an issue in the repository.


---
Feel free to reach out to me for any questions or clarifications related to this project.

**Note:** You must have a running MySQL server and update the database connection details in the `DatabaseManager` class to execute the application successfully.

Enjoy your music with the Music Player App! 🎵🎧
