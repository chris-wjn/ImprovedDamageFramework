package net.cwjn.idf.json.config_data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Class that holds data for specific items to be used by IDF.
    Each class holds one item (by namespace:name), it's damage class, modifiers, and tags.
 */
public class ItemData {

    //The item this data holder is for
    private final Item item;
    //The damage class of the item (if it has one)
    private final String damageClass;
    //The modifiers of the item
    private final Multimap<Attribute, AttributeModifier> modifiers;
    //The tags of the item
    private final List<ItemTag> tags;

    public Item getItem() {
        return item;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public Multimap<Attribute, AttributeModifier> getModifiers() {
        return modifiers;
    }

    public Map<Attribute, List<AttributeModifier>> getModifiersAsNormalMap() {
        Map<Attribute, List<AttributeModifier>> returnMap = new HashMap<>();
        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet()) {
            List<AttributeModifier> retList = entry.getValue().stream().toList();
            returnMap.put(entry.getKey(), retList);
        }
        return returnMap;
    }

    public List<ItemTag> getTags() {
        return tags;
    }

    public ItemData(Item item, String damageClass, Multimap<Attribute, AttributeModifier> modifiers, List<ItemTag> tags) {
        this.item = item;
        this.damageClass = damageClass;
        this.modifiers = modifiers;
        this.tags = tags;
    }

    public ItemData(Item item, String damageClass) {
        this.item = item;
        this.damageClass = damageClass;
        this.modifiers = HashMultimap.create();
        this.tags = List.of();
    }

    public ItemData(Item item, String damageClass, Map<Attribute, List<AttributeModifier>> modifiers, List<ItemTag> tags) {
        this.item = item;
        this.damageClass = damageClass;
        this.modifiers = HashMultimap.create();
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : modifiers.entrySet()) {
            this.modifiers.putAll(entry.getKey(), entry.getValue());
        }
        this.tags = tags;
    }

}
