package br.cesupa.fisiovr.dummy;

import com.google.gson.Gson;

import java.util.HashMap;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaylistContent {

    public static class Playlist {

        public String kind;
        public String etag;
        public Playlist[] items;
        public HashMap<String, String> id;

        public Playlist() {

        }

        public Playlist(String kind, String etag, HashMap<String, String> id) {
            this.kind = kind;
            this.etag = etag;
            this.id = id;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
