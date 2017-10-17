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

        public String kind;
        public String etag;
        public String id;
        public VideoItem[] items;
        public HashMap<String, Integer> pageInfo;
        public SnippetVideoItem snippet;
        public StatisticsVideoItem statistics;
        public VideoItem() {

        }

        public VideoItem(String kind, String etag, String id, VideoItem[] items, HashMap<String, Integer> pageInfo, SnippetVideoItem snippet, StatisticsVideoItem statistics) {
            this.kind = kind;
            this.etag = etag;
            this.id = id;
            this.items = items;
            this.pageInfo = pageInfo;
            this.snippet = snippet;
            this.statistics = statistics;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        public class SnippetVideoItem {
            public String publishedAt;
            public String channelId;
            public String title;
            public String description;
            public String channelTitle;
            public String categoryId;
            public String[] tags;
            public HashMap<String, String> resourceId;
            public HashMap<String, ThumbnailsItens> thumbnails;

            public class ThumbnailsItens {
                public String url;
                public int width;
                public int height;
            }
        }

        public class StatisticsVideoItem {
            public String viewCount;
            public String likeCount;
            public String dislikeCount;
            public String favoriteCount;
            public String commentCount;
        }
    }
}
