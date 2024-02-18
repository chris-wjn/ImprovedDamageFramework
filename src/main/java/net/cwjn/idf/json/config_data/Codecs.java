package net.cwjn.idf.json.config_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class Codecs {

    private static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION = Codec.STRING.xmap(AttributeModifier.Operation::valueOf, AttributeModifier.Operation::name);

    private static final Codec<AttributeModifier> ATTRIBUTE_MODIFIER = RecordCodecBuilder.create(
            instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(AttributeModifier::getId),
                    Codec.STRING.fieldOf("name").forGetter(AttributeModifier::getName),
                    Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::getAmount),
                    ATTRIBUTE_MODIFIER_OPERATION.fieldOf("operation").forGetter(AttributeModifier::getOperation)
            ).apply(instance, AttributeModifier::new)
    );

    private static final Codec<ItemTag> ITEM_TAG = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ItemTag::getName)
            ).apply(instance, ItemTag::new)
    );

    public static final Codec<ItemData> ITEM_DATA_HOLDER = RecordCodecBuilder.create(
            instance -> instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(ItemData::getItem),
                    Codec.STRING.fieldOf("damage_class").forGetter(ItemData::getDamageClass),
                    Codec.unboundedMap(ForgeRegistries.ATTRIBUTES.getCodec(), Codec.list(ATTRIBUTE_MODIFIER)).fieldOf("modifiers").forGetter(ItemData::getModifiersAsNormalMap),
                    ITEM_TAG.listOf().fieldOf("tags").forGetter(ItemData::getTags)
            ).apply(instance, ItemData::new)
    );

}
