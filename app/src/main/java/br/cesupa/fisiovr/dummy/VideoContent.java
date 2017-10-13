package br.cesupa.fisiovr.dummy;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class VideoContent {

    public static final List<VideoItem> ITEMS = new ArrayList<VideoItem>();

    public static final Map<String, VideoItem> ITEM_MAP = new HashMap<String, VideoItem>();

    public static class VideoItem {
        public String id;
        public String title;
        public String thumbnail;
        public String upload_date;
        public Integer view_count;
        public List<String> tags;
        public List<String> categories;
        public String description;
        public String uploader_id;
        public String uploader;

        public VideoItem() {
        }

        public VideoItem(String id, String title, String thumbnail, String upload_date, Integer view_count, ArrayList<String> tags, ArrayList<String> categories, String description, String uploader_id, String uploader) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
            this.upload_date = upload_date;
            this.view_count = view_count;
            this.tags = tags;
            this.categories = categories;
            this.description = description;
            this.uploader_id = uploader_id;
            this.uploader = uploader;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
