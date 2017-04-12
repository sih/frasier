package eu.waldonia.labs.frasier.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 *
 * @author sih
 */
public class SimpleResult {

    private String status;
    private List<String> artists;
    private String album;
    private String track;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void addArtist(String artist) {
        if (null == artists) {
            artists = new ArrayList<>();
        }
        artists.add(artist);
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
