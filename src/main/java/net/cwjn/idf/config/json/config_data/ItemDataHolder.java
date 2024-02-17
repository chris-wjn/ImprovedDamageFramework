package net.cwjn.idf.config.json.config_data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    Class that holds data for specific items to be used by IDF.
    Each class holds one item (by namespace:name), it's damage class, modifiers, and tags.
 */
public class ItemDataHolder {

    //The item this data holder is for
    private final Item item;
    //The damage class of the item (if it has one)
    private final String damageClass;
    //The modifiers of the item
    private final Multimap<Attribute, AttributeModifier> modifiers;
    //The tags of the item
    private final List<String> tags;

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

    public List<String> getTags() {
        return tags;
    }

    public ItemDataHolder(Item item, String damageClass, Multimap<Attribute, AttributeModifier> modifiers, List<String> tags) {
        this.item = item;
        this.damageClass = damageClass;
        this.modifiers = modifiers;
        this.tags = tags;
    }

    public ItemDataHolder(Item item, String damageClass, Map<Attribute, List<AttributeModifier>> modifiers, List<String> tags) {
        this.item = item;
        this.damageClass = damageClass;
        this.modifiers = HashMultimap.create();
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : modifiers.entrySet()) {
            this.modifiers.putAll(entry.getKey(), entry.getValue());
        }
        this.tags = tags;
    }

    private static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION_CODEC = Codec.STRING.xmap(AttributeModifier.Operation::valueOf, AttributeModifier.Operation::name);

    private static final Codec<AttributeModifier> ATTRIBUTE_MODIFIER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(AttributeModifier::getName),
                    Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::getAmount),
                    ATTRIBUTE_MODIFIER_OPERATION_CODEC.fieldOf("operation").forGetter(AttributeModifier::getOperation)
            ).apply(instance, AttributeModifier::new)
    );

    public static final Codec<ItemDataHolder> ITEM_DATA_HOLDER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(ItemDataHolder::getItem),
                    Codec.STRING.fieldOf("damage_class").forGetter(ItemDataHolder::getDamageClass),
                    Codec.unboundedMap(ForgeRegistries.ATTRIBUTES.getCodec(), Codec.list(ATTRIBUTE_MODIFIER_CODEC)).fieldOf("modifiers").forGetter(ItemDataHolder::getModifiersAsNormalMap),
                    Codec.STRING.listOf().fieldOf("tags").forGetter(ItemDataHolder::getTags)
            ).apply(instance, ItemDataHolder::new)
    );



}
