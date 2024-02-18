package net.cwjn.idf.json.config_data;

/**
 * An ItemTag can be attached to an item through an ItemData. Represents data about an item or how an
 * item should be handled by IDF in-game.
 */
public class ItemTag {

    //String representation of the tag, so we can (de)serialize it to json.
    private final String name;

    public ItemTag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
