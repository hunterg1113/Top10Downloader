package com.example.top10downloader;

/**
 * Created by Hunter on 2/5/2018.
 */

public class FeedEntry {
    private String name;
    private String artist;
    private String summary;
    private String genre;
    private String releaseDate;

    public FeedEntry() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Name= " + name + '\n' +
                "Artist= " + artist + '\n' +
                "Summary= " + summary + '\n' +
                "Genre= " + genre + '\n' +
                "Release Date= " + releaseDate + '\n';
    }
}
