package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Color;
import com.cwjn.idf.ImprovedDamageFramework;
import com.cwjn.idf.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

@Mixin(ItemStack.class)
public class MixinItemStack {

    private static DecimalFormat df = new DecimalFormat("#.##");
    private static final Style symbolStyle = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_IDF);

    /**
     * @author cwJn
     */
    @Overwrite
    public List<Component> getTooltipLines(@Nullable Player player, TooltipFlag tooltipMode) {
        List<Component> list = Lists.newArrayList();
        MutableComponent mutablecomponent = (new TextComponent("")).append(this.getHoverName());//.withStyle(item.getRarity().color);

        /*if (item.hasCustomHoverName()) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }*/

        //for maps... like treasure maps
        list.add(mutablecomponent);
        list.add(new TextComponent(""));
        if (!tooltipMode.isAdvanced() && !this.hasCustomHoverName() && this.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId((ItemStack)(Object)this);
            if (integer != null) {
                list.add((new TextComponent("#" + integer)).withStyle(ChatFormatting.GRAY));
            }
        }

        //dont know what this does
        int j = this.getHideFlags();
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
            this.getItem().appendHoverText((ItemStack)(Object)this, player == null ? null : player.level, list, tooltipMode);
        }

        if (this.hasTag()) {
            if (this.tag.contains("display", 10)) {
                CompoundTag compoundtag = this.tag.getCompound("display");
                /*if (shouldShowInTooltip(j, ItemStack.TooltipPart.DYE) && compoundtag.contains("color", 99)) {
                    if (tooltipMode.isAdvanced()) {
                        list.add((new TranslatableComponent("item.color", String.format("#%06X", compoundtag.getInt("color")))).withStyle(ChatFormatting.GRAY));
                    } else {
                        list.add((new TranslatableComponent("item.dyed")).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}));
                    }
                }*/
                if (compoundtag.getTagType("Lore") == 9) {
                    ListTag listtag = compoundtag.getList("Lore", 8);
                    for(int i = 0; i < listtag.size(); ++i) {
                        String s = listtag.getString(i);
                        try {
                            MutableComponent mutablecomponent1 = Component.Serializer.fromJson(s);
                            if (mutablecomponent1 != null) {
                                //if (this.tag.contains("idf.equipment")) {
                                    list.add(Util.withColor(new TranslatableComponent("idf.description.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                                    //TODO: add weapon scaling and other info here.
                                    list.add(Util.withColor(mutablecomponent1, Color.LIGHTGRAY));
                                //} else {
                                //    list.add(Util.withColor(mutablecomponent1, Color.LIGHTGRAY));
                                //}
                            }
                        } catch (Exception exception) {
                            compoundtag.remove("Lore");
                        }
                    }
                }
            }
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
                if (!EnchantmentHelper.getEnchantments((ItemStack)(Object)this).isEmpty() && !(this.getItem() instanceof EnchantedBookItem)) {
                    list.add(Util.withColor(new TranslatableComponent("idf.enchantments.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    ItemStack.appendEnchantmentNames(list, this.getEnchantmentTags());
                    list.add(new TextComponent(""));
                }
            }
        }

        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            if (this.hasTag() && this.tag.contains("idf.equipment"))
                list.add(Util.withColor(new TranslatableComponent("idf.attributes.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
            //NOTE! we change the way tooltips are calculated so that we can attach more than 1 attribute modifier per attribute.
            Map<Attribute, Double> mappedAttributes = new HashMap<>(32);
            HashMap<UUID, Boolean> done = new HashMap<>(32);
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) { //for each item, we want to check the modifiers it gives for every equipment slot.
                Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslot); //attribute and modifier map for the slot in this iteration
                if (!multimap.isEmpty()) { //make sure there is at least one entry in the map
                    for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) { //iterate through each attribute and attribute modifier pair.
                        AttributeModifier attributemodifier = entry.getValue(); //get the modifier in this iteration
                        Attribute attribute = entry.getKey();
                        double modifierAmount = attributemodifier.getAmount(); //get the amount that it modifies by
                        double postOperationFactoring = modifierAmount;

                        if (attributemodifier.getOperation() != AttributeModifier.Operation.ADDITION || entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                            postOperationFactoring = modifierAmount * 100.0D; //convert to percentage
                        }

                        if (modifierAmount != 0) {
                            if (mappedAttributes.containsKey(attribute)) {
                                mappedAttributes.put(attribute, (postOperationFactoring + mappedAttributes.get(attribute)));
                            } else {
                                mappedAttributes.put(attribute, postOperationFactoring);
                            }
                        }
                    }
                }
            }
            appendAttributes(player, list, mappedAttributes);
        }

        if (this.hasTag()) {
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add((new TranslatableComponent("item.unbreakable")).withStyle(ChatFormatting.BLUE));
            }

            if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                ListTag listtag1 = this.tag.getList("CanDestroy", 8);
                if (!listtag1.isEmpty()) {
                    list.add(TextComponent.EMPTY);
                    list.add((new TranslatableComponent("item.canBreak")).withStyle(ChatFormatting.GRAY));

                    for(int k = 0; k < listtag1.size(); ++k) {
                        list.addAll(expandBlockState(listtag1.getString(k)));
                    }
                }
            }

            if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                ListTag listtag2 = this.tag.getList("CanPlaceOn", 8);
                if (!listtag2.isEmpty()) {
                    list.add(TextComponent.EMPTY);
                    list.add((new TranslatableComponent("item.canPlace")).withStyle(ChatFormatting.GRAY));

                    for(int l = 0; l < listtag2.size(); ++l) {
                        list.addAll(expandBlockState(listtag2.getString(l)));
                    }
                }
            }
        }

        if (tooltipMode.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableComponent("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
            }

            list.add((new TextComponent(Registry.ITEM.getKey(this.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add((new TranslatableComponent("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onItemTooltip((ItemStack)(Object)this, player, list, tooltipMode);
        return list;
    }

    private void appendAttributes(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes) {
        if (player == null) return;
        MutableComponent damageComponent = new TextComponent("");
        appendDamageComponent(player, list, mappedAttributes, damageComponent);
        MutableComponent auxiliaryComponent = new TextComponent("");
        appendAuxiliaryComponent(player, list, mappedAttributes, auxiliaryComponent);
        MutableComponent defensiveComponent = new TextComponent("");
        appendDefensiveComponent(mappedAttributes, list, defensiveComponent);
    }

    private void appendDefensiveComponent(Map<Attribute, Double> mappedAttributes, List<Component> list, MutableComponent defensiveComponent) {
        defensiveComponent.append(new TranslatableComponent("idf.resistance_icon").withStyle(symbolStyle));
        defensiveComponent.append(new TextComponent(": "));
        if (mappedAttributes.containsKey(Attributes.ARMOR_TOUGHNESS)) {
            double value = mappedAttributes.get(Attributes.ARMOR_TOUGHNESS);
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.defense_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(Attributes.ARMOR)) {
            double value = mappedAttributes.get(Attributes.ARMOR) * 4;
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.physical_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.FIRE_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.FIRE_RESISTANCE.get());
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.fire_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.WATER_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.WATER_RESISTANCE.get());
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.water_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIGHTNING_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIGHTNING_RESISTANCE.get());
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.lightning_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.MAGIC_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.MAGIC_RESISTANCE.get());
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.magic_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.DARK_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.DARK_RESISTANCE.get());
            if (value < 0) {
                defensiveComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), ChatFormatting.RED));
            } else {
                defensiveComponent.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            }
            defensiveComponent.append(new TranslatableComponent("idf.dark_icon").withStyle(symbolStyle));
        }
        list.add(defensiveComponent);
    }

    private void appendAuxiliaryComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes, MutableComponent auxiliaryComponent) {
        auxiliaryComponent.append(new TranslatableComponent("idf.black_diamond"));
        auxiliaryComponent.append(new TextComponent(": "));
        if (mappedAttributes.containsKey(Attributes.ATTACK_SPEED)) {
            double value = mappedAttributes.get(Attributes.ATTACK_SPEED) + player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.WHITESMOKE));
            auxiliaryComponent.append(new TranslatableComponent("idf.attack_speed_icon"));
        }
        if (mappedAttributes.containsKey(Attributes.ATTACK_KNOCKBACK)) {
            double value = mappedAttributes.get(Attributes.ATTACK_KNOCKBACK);
            auxiliaryComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.knockback_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIFESTEAL.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIFESTEAL.get());
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.lifesteal_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.PENETRATING.get())) {
            double value = mappedAttributes.get(AttributeRegistry.PENETRATING.get());
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.pen_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.CRIT_CHANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.CRIT_CHANCE.get());
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.crit_chance_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(Attributes.KNOCKBACK_RESISTANCE)) {
            double value = mappedAttributes.get(Attributes.KNOCKBACK_RESISTANCE);
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.knockback_resistance_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.EVASION.get())) {
            double value = mappedAttributes.get(AttributeRegistry.EVASION.get());
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.evasion_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(Attributes.MAX_HEALTH)) {
            double value = mappedAttributes.get(Attributes.MAX_HEALTH);
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.maxhp_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(Attributes.MOVEMENT_SPEED)) {
            double value = mappedAttributes.get(Attributes.MOVEMENT_SPEED);
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.movespeed_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(Attributes.LUCK)) {
            double value = mappedAttributes.get(Attributes.LUCK);
            auxiliaryComponent.append(Util.withColor(new TextComponent("" + df.format(value)), Color.LIGHTGREEN));
            auxiliaryComponent.append(new TranslatableComponent("idf.luck_icon").withStyle(symbolStyle));
        }
        list.add(auxiliaryComponent);
    }

    private void appendDamageComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes, MutableComponent damageComponent) {
        damageComponent.append(new TranslatableComponent("idf.attack_icon").withStyle(symbolStyle));
        damageComponent.append(new TextComponent(": "));
        if (mappedAttributes.containsKey(Attributes.ATTACK_DAMAGE)) {
            double value = mappedAttributes.get(Attributes.ATTACK_DAMAGE) + player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getDamageBonus((ItemStack) (Object) this, MobType.UNDEFINED);
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.physical_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.FIRE_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.FIRE_DAMAGE.get());
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.fire_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.WATER_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.WATER_DAMAGE.get());
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.water_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIGHTNING_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIGHTNING_DAMAGE.get());
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.lightning_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.MAGIC_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.MAGIC_DAMAGE.get());
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.magic_icon").withStyle(symbolStyle));
        }
        if (mappedAttributes.containsKey(AttributeRegistry.DARK_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.DARK_DAMAGE.get());
            damageComponent.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            damageComponent.append(new TranslatableComponent("idf.dark_icon").withStyle(symbolStyle));
        }
        list.add(damageComponent);
    }

    @Shadow
    private int getHideFlags() {
        throw new IllegalStateException("Mixin failed to shadow getHideFlags()");
    }
    @Shadow
    private CompoundTag tag;
    @Shadow
    public boolean hasTag() {
        throw new IllegalStateException("Mixin failed to shadow hasTag()");
    }
    @Shadow
    private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        throw new IllegalStateException("Mixin failed to shadow shouldShowInTooltip(int i, ItemStack.TooltipPart xxx)");
    }
    @Shadow
    private static Collection<Component> expandBlockState(String p_41762_) {
        throw new IllegalStateException("Mixin failed to shadow expandBlockState(String s)");
    }
    @Shadow
    public boolean hasCustomHoverName() {
        throw new IllegalStateException("Mixin failed to shadow hasCustomHoverName()");
    }
    @Shadow
    public boolean is(Item p_150931_) {
        throw new IllegalStateException("Mixin failed to shadow is(Item i)");
    }
    @Shadow
    public Item getItem() {
        throw new IllegalStateException("Mixin failed to shadow getItem()");
    }
    @Shadow
    public ListTag getEnchantmentTags() {
        throw new IllegalStateException("Mixin failed to shadow getEnchantmentTags()");
    }
    @Shadow
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot p_41639_) {
        throw new IllegalStateException("Mixin failed to shadow getAttributeModifiers(EquipmentSlot e)");
    }
    @Shadow
    public boolean isDamaged() {
        throw new IllegalStateException("Mixin failed to shadow isDamaged()");
    }
    @Shadow
    public int getMaxDamage() {
        throw new IllegalStateException("Mixin failed to shadow getMaxDamage()");
    }
    @Shadow
    public int getDamageValue() {
        throw new IllegalStateException("Mixin failed to shadow getDamageValue()");
    }
    @Shadow
    public Component getHoverName() {
        throw new IllegalStateException("Mixin failed to shadow getHoverName()");
    }
}

