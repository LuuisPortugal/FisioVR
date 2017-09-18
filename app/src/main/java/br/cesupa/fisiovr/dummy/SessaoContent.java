package br.cesupa.fisiovr.dummy;

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
public class SessaoContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SessaoItem> ITEMS = new ArrayList<SessaoItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, SessaoItem> ITEM_MAP = new HashMap<String, SessaoItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(SessaoItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static SessaoItem createDummyItem(int position) {
        return new SessaoItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        return "Details about Item: " + position;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SessaoItem {
        public final String id;
        public final String content;
        public final String details;

        public SessaoItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
