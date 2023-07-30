import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class MusicPlayerGUI extends JFrame {
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton nextButton;
    private JButton prevButton;
    private JButton newPlaylistButton;
    private JButton openPlaylistButton;
    private JButton addSongButton;
    private JButton removeSongButton;
    private JList<Song> songList;
    private DefaultListModel<Song> songListModel;
    private Player player;
    private PlaylistManager playlistManager;
    private Playlist currentPlaylist;
    private DatabaseManager databaseManager;
    private JLabel currentSongLabel;
    private JLabel currentPlaylistLabel;
    private Song currentlyPlayingSong; // Add a new member variable to store the currently playing song
    private int currentlyPlayingIndex; // Index of the currently playing song in the playlist

    public MusicPlayerGUI() {
        setTitle("Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Create a main panel to hold the control panel and playlist panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        nextButton = new JButton("Next");
        prevButton = new JButton("Previous");
        newPlaylistButton = new JButton("New Playlist");
        openPlaylistButton = new JButton("Open Playlist");
        addSongButton = new JButton("Add Song");
        removeSongButton = new JButton("Remove Song");

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        controlPanel.add(newPlaylistButton);
        controlPanel.add(openPlaylistButton);
        controlPanel.add(addSongButton);
        controlPanel.add(removeSongButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Playlist Panel
        JPanel playlistPanel = new JPanel();
        playlistPanel.setLayout(new BorderLayout());

        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        JScrollPane scrollPane = new JScrollPane(songList);

        playlistPanel.add(scrollPane, BorderLayout.CENTER);

        // Playlist Control Panel
        JPanel playlistControlPanel = new JPanel();
        playlistControlPanel.setLayout(new FlowLayout());

        playlistPanel.add(playlistControlPanel, BorderLayout.SOUTH);

        mainPanel.add(playlistPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        databaseManager = new DatabaseManager();
        playlistManager = new PlaylistManager();

        // Retrieve songs from the database and add them to the default playlist
        List<Song> songs = databaseManager.getAllSongs();
        currentPlaylist = playlistManager.createOrOpenPlaylist("My Playlist");
        currentPlaylist.addAllSongs(songs);

        updateSongList();

        currentPlaylistLabel = new JLabel("Current Playlist: None");
        playlistPanel.add(currentPlaylistLabel, BorderLayout.NORTH);
        currentSongLabel = new JLabel("Currently Playing: NA");
        playlistPanel.add(currentSongLabel, BorderLayout.SOUTH);
        playlistPanel.setPreferredSize(new Dimension(500, 300));
        add(mainPanel, BorderLayout.CENTER);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = songList.getSelectedValue();
                if (selectedSong != null) {
                    stop();
                    play(selectedSong.getFilePath());
                    currentSongLabel.setText("Currently Playing: " + selectedSong.getTitle());
                }
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int playlistSize = currentPlaylist.getSongs().size();
                if (playlistSize == 0) {
                    // If the playlist is empty, do nothing
                    return;
                }

                if (currentlyPlayingIndex == -1) {
                    // If no song is currently playing, play the first song in the playlist
                    currentlyPlayingIndex = 0;
                } else {
                    // Otherwise, play the next song in the playlist
                    currentlyPlayingIndex = (currentlyPlayingIndex + 1) % playlistSize;
                }

                playSongByIndex(currentlyPlayingIndex);
            }
        });

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int playlistSize = currentPlaylist.getSongs().size();
                if (playlistSize == 0) {
                    // If the playlist is empty, do nothing
                    return;
                }

                if (currentlyPlayingIndex == -1) {
                    // If no song is currently playing, play the last song in the playlist
                    currentlyPlayingIndex = playlistSize - 1;
                } else {
                    // Otherwise, play the previous song in the playlist
                    currentlyPlayingIndex = (currentlyPlayingIndex - 1 + playlistSize) % playlistSize;
                }

                playSongByIndex(currentlyPlayingIndex);
            }
        });



        newPlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playlistName = JOptionPane.showInputDialog("Enter the name of the new playlist:");
                if (playlistName != null && !playlistName.isEmpty()) {
                    currentPlaylist = playlistManager.createOrOpenPlaylist(playlistName);
                    updateSongList();
                    currentSongLabel.setText("Currently Playing: NA");
                }
            }
        });

        openPlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playlistName = JOptionPane.showInputDialog("Enter the name of the playlist to open:");
                if (playlistName != null && !playlistName.isEmpty()) {
                    currentPlaylist = playlistManager.createOrOpenPlaylist(playlistName);
                    if (currentPlaylist != null) {
                        updateSongList();
                        updateCurrentPlaylistLabel(); // Update the current playlist label
                        currentSongLabel.setText("Currently Playing: NA");
                    } else {
                        JOptionPane.showMessageDialog(null, "Playlist not found!");
                    }
                }
            }
        });


        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = songList.getSelectedValue();
                if (selectedSong != null) {
                    String playlistName = promptForPlaylistName();
                    if (playlistName != null && !playlistName.isEmpty()) {
                        Playlist playlist = playlistManager.getPlaylistByName(playlistName);
                        if (playlist == null) {
                            // If the playlist does not exist, create a new one
                            playlist = playlistManager.createPlaylist(databaseManager, playlistName);
                        }
                        playlistManager.addSongToPlaylist(playlist, selectedSong);
                        updateSongList();
                    }
                }
                showAddSongDialog();
            }
        });


        removeSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = songList.getSelectedValue();
                if (selectedSong != null) {
                    String playlistName = promptForPlaylistName();
                    if (playlistName != null && !playlistName.isEmpty()) {
                        Playlist playlist = playlistManager.getPlaylistByName(playlistName);
                        if (playlist != null) {
                            playlistManager.removeSongFromPlaylist(playlist, selectedSong);
                            updateSongList();
                        } else {
                            JOptionPane.showMessageDialog(null, "Playlist not found!");
                        }
                    }
                }
            }
        });

        // Add a ListSelectionListener to the songList to handle song selection
        songList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Song selectedSong = songList.getSelectedValue();
                    if (selectedSong != null) {
                        stop();
                        play(selectedSong.getFilePath());
                        currentSongLabel.setText("Currently Playing: " + selectedSong.getTitle());
                    }
                }
            }
        });


    }

    private void play(String filePath) {
        try {
            // Stop the previous player thread if it's running
            stop();

            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            player = new Player(is);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (Exception e) {
                        System.out.println("Error playing song: " + e.getMessage());
                    } finally {
                        // No need to reset currentlyPlayingIndex here.
                        // It will be updated in playSongByIndex method.
                        updateCurrentPlaylistLabel(); // Update the label after playing
                    }
                }
            });
            t.start();
        } catch (Exception e) {
            System.out.println("Error opening song: " + e.getMessage());
        }
    }

    private void playSongByIndex(int index) {
        // Update currentlyPlayingIndex before playing the song
        currentlyPlayingIndex = index;

        currentPlaylist.setCurrentSongIndex(index);
        Song songToPlay = currentPlaylist.getCurrentSong();
        if (songToPlay != null) {
            play(songToPlay.getFilePath());
            currentSongLabel.setText("Currently Playing: " + songToPlay.getTitle());
        }
    }

    private void pause() {
        if (player != null) {
            player.close();
        }
    }

    private void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
    }

    private void updateSongList() {
        songListModel.clear();
        List<Song> songs = currentPlaylist.getSongs();
        for (Song song : songs) {
            songListModel.addElement(song);
        }
    }

    private void openPlaylist(String playlistName) {
        currentPlaylist = playlistManager.getPlaylistByName(playlistName);
        updateSongList();
        updateCurrentPlaylistLabel(); // Update the current playlist label

        // Show the current playlist's songs in the JList
        if (currentPlaylist != null) {
            List<Song> playlistSongs = currentPlaylist.getSongs();
            DefaultListModel<Song> playlistSongListModel = new DefaultListModel<>();
            for (Song song : playlistSongs) {
                playlistSongListModel.addElement(song);
            }
            songList.setModel(playlistSongListModel);
        }

        // Update the "Current Playlist" label after opening a new playlist
        updateCurrentPlaylistLabel();
    }

    private String promptForPlaylistName() {
        String playlistName = JOptionPane.showInputDialog("Enter the name of the playlist:");
        return playlistName;
    }

    private void updateCurrentPlaylistLabel() {
        if (currentPlaylist != null) {
            currentPlaylistLabel.setText("Current Playlist: " + currentPlaylist.getName());
        } else {
            currentPlaylistLabel.setText("Current Playlist: None");
        }
    }

    private void showAddSongDialog() {
        // Retrieve all songs from the track_list table using the DatabaseManager
        List<Song> allSongs = databaseManager.getAllSongs();

        // Create an array of song titles to display in the JList
        String[] songTitles = allSongs.stream().map(Song::getTitle).toArray(String[]::new);

        // Create a JList with the array of song titles
        JList<String> songJList = new JList<>(songTitles);

        // Create a scroll pane to hold the JList
        JScrollPane scrollPane = new JScrollPane(songJList);

        // Show the custom dialog box
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Add Song", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Check if the user clicked the OK button
        if (result == JOptionPane.OK_OPTION) {
            // Get the selected song title from the JList
            String selectedSongTitle = songJList.getSelectedValue();

            // Add the selected song to the current playlist
            if (selectedSongTitle != null) {
                Song selectedSong = allSongs.stream().filter(song -> song.getTitle().equals(selectedSongTitle)).findFirst().orElse(null);
                if (selectedSong != null) {
                    // Use the PlaylistManager to add the song to the playlist
                    playlistManager.addSongToPlaylist(currentPlaylist, selectedSong);
                    updateSongList(); // Update the song list in the GUI to display the newly added song
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MusicPlayerGUI playerGUI = new MusicPlayerGUI();
                playerGUI.setVisible(true);

            }


        });

    }
}
