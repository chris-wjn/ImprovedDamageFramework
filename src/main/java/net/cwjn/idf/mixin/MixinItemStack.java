package net.cwjn.idf.mixin;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.Color;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.cwjn.idf.event.ClientEvents;
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
public abstract class MixinItemStack {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final Style symbolStyle = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_IDF);

    /**
     * @author cwJn
     */
    @Overwrite
    public List<Component> getTooltipLines(@Nullable Player player, TooltipFlag tooltipMode) {
        ItemStack thisItemStack = (ItemStack)(Object) this;
        List<Component> list = Lists.newArrayList();
        MutableComponent mutablecomponent = (new TextComponent("")).append(thisItemStack.getHoverName());//.withStyle(item.getRarity().color);

        //for maps... like treasure maps
        list.add(mutablecomponent);
        if (!tooltipMode.isAdvanced() && !thisItemStack.hasCustomHoverName() && thisItemStack.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId((ItemStack)(Object)this);
            if (integer != null) {
                list.add((new TextComponent("#" + integer)).withStyle(ChatFormatting.GRAY));
            }
        }

        //dont know what this does
        int j = this.getHideFlags();
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
            thisItemStack.getItem().appendHoverText((ItemStack)(Object)this, player == null ? null : player.level, list, tooltipMode);
        }


        if (thisItemStack.hasTag() && thisItemStack.getTag().contains("idf.equipment")) {
            list.add(Util.withColor(new TranslatableComponent("idf.description.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
            MutableComponent durability = new TextComponent("  ");
            durability.append(new TranslatableComponent("idf.durability_icon").withStyle(symbolStyle));
            durability.append(Util.withColor(new TranslatableComponent("idf.item.durability"), Color.LIGHTSLATEGREY));
            durability.append(Util.withColor(new TextComponent(": " + (thisItemStack.getMaxDamage() - thisItemStack.getDamageValue()) + "/" + thisItemStack.getMaxDamage()), Color.FLORALWHITE));
            list.add(durability);
            if (thisItemStack.getTag().contains("idf.damage_class")) {
                MutableComponent damageClass = new TextComponent("  ");
                damageClass.append(new TranslatableComponent("idf.attack_icon").withStyle(symbolStyle));
                damageClass.append(new TranslatableComponent("idf.damage_class.tooltip." + thisItemStack.getTag().getString("idf.damage_class")));
                list.add(damageClass);
            }
        }

        if (thisItemStack.hasTag()) {
            if (this.tag.contains("display", 10)) {
                CompoundTag compoundtag = this.tag.getCompound("display");
                //list.add(new TranslatableComponent("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
                if (compoundtag.getTagType("Lore") == 9) {
                    ListTag listtag = compoundtag.getList("Lore", 8);
                    for(int i = 0; i < listtag.size(); ++i) {
                        String s = listtag.getString(i);
                        try {
                            MutableComponent mutablecomponent1 = Component.Serializer.fromJson(s);
                            if (mutablecomponent1 != null) {
                                //if (this.tag.contains("idf.equipment")) {
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
        }

        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            Map<Attribute, Double> mappedOperation0 = new HashMap<>(5);
            Map<Attribute, Double> mappedOperation1 = new HashMap<>();
            Map<Attribute, Double> mappedOperation2 = new HashMap<>();
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) { //for each item, we want to check the modifiers it gives for every equipment slot.
                Multimap<Attribute, AttributeModifier> multimap = thisItemStack.getAttributeModifiers(equipmentslot); //attribute and modifier map for the slot in this iteration
                if (!multimap.isEmpty()) { //make sure there is at least one entry in the map
                    for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) { //iterate through each attribute and attribute modifier pair.
                        AttributeModifier attributemodifier = entry.getValue(); //get the modifier in this iteration
                        Attribute attribute = entry.getKey();
                        double modifierAmount = attributemodifier.getAmount(); //get the amount that it modifies by
                        AttributeModifier.Operation operation = attributemodifier.getOperation();
                        if (modifierAmount != 0) {
                            if (operation == AttributeModifier.Operation.ADDITION) {
                                if (mappedOperation0.containsKey(attribute)) {
                                    mappedOperation0.put(attribute, (modifierAmount + mappedOperation0.get(attribute)));
                                } else {
                                    mappedOperation0.put(attribute, modifierAmount);
                                }
                            } else if (operation == AttributeModifier.Operation.MULTIPLY_BASE) {
                                if (mappedOperation1.containsKey(attribute)) {
                                    mappedOperation1.put(attribute, (modifierAmount + mappedOperation1.get(attribute)));
                                } else {
                                    mappedOperation1.put(attribute, modifierAmount);
                                }
                            } else {
                                if (mappedOperation2.containsKey(attribute)) {
                                    mappedOperation2.put(attribute, (modifierAmount + mappedOperation2.get(attribute)));
                                } else {
                                    mappedOperation2.put(attribute, modifierAmount);
                                }
                            }
                        }
                    }
                }
            }
            appendAttributes(player, list, mappedOperation0, mappedOperation1, mappedOperation2);
        }

        if (thisItemStack.hasTag()) {
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
                if (!EnchantmentHelper.getEnchantments((ItemStack)(Object)this).isEmpty() && !(thisItemStack.getItem() instanceof EnchantedBookItem)) {
                    list.add(Util.withColor(new TranslatableComponent("idf.enchantments.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    ItemStack.appendEnchantmentNames(list, thisItemStack.getEnchantmentTags());
                    list.add(new TextComponent(""));
                }
            }
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
            if (thisItemStack.isDamaged()) {
                list.add(new TranslatableComponent("item.durability", thisItemStack.getMaxDamage() - thisItemStack.getDamageValue(), thisItemStack.getMaxDamage()));
            }

            list.add((new TextComponent(Registry.ITEM.getKey(thisItemStack.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
            if (thisItemStack.hasTag()) {
                list.add((new TranslatableComponent("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onItemTooltip((ItemStack)(Object)this, player, list, tooltipMode);
        return list;
    }

    private void appendAttributes(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedOperation0, Map<Attribute, Double> mappedOperation1, Map<Attribute, Double> mappedOperation2) {
        ItemStack thisItemStack = (ItemStack)(Object) this;
        if (player == null) return;
        if (mappedOperation0.containsKey(Attributes.ATTACK_SPEED)) {
            double value = mappedOperation0.get(Attributes.ATTACK_SPEED) + player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.attack_speed_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.attack_speed_tooltip"));
            component.append(new TextComponent(df.format(value)));
            list.add(component);
        }
        if (thisItemStack.hasTag() && this.tag.contains("idf.equipment")) {
            list.add(Util.withColor(new TranslatableComponent("idf.attributes.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
        }
        if (mappedOperation0.containsKey(Attributes.ATTACK_DAMAGE) || mappedOperation0.containsKey(AttributeRegistry.FIRE_DAMAGE.get()) || mappedOperation0.containsKey(AttributeRegistry.MAGIC_DAMAGE.get()) || mappedOperation0.containsKey(AttributeRegistry.WATER_DAMAGE.get())
        || mappedOperation0.containsKey(AttributeRegistry.LIGHTNING_DAMAGE.get()) || mappedOperation0.containsKey(AttributeRegistry.DARK_DAMAGE.get())) {
            appendDamageComponent(player, list, mappedOperation0);
        }
        if (mappedOperation0.containsKey(Attributes.ARMOR) || mappedOperation0.containsKey(Attributes.ARMOR_TOUGHNESS) || mappedOperation0.containsKey(AttributeRegistry.FIRE_RESISTANCE.get()) ||
        mappedOperation0.containsKey(AttributeRegistry.WATER_RESISTANCE.get()) || mappedOperation0.containsKey(AttributeRegistry.LIGHTNING_RESISTANCE.get()) || mappedOperation0.containsKey(AttributeRegistry.MAGIC_RESISTANCE.get()) || mappedOperation0.containsKey(AttributeRegistry.DARK_RESISTANCE.get())) {
            appendDefensiveComponent(mappedOperation0, list);
            if (mappedOperation0.containsKey(AttributeRegistry.STRIKE_MULT.get()) || mappedOperation0.containsKey(AttributeRegistry.PIERCE_MULT.get()) || mappedOperation0.containsKey(AttributeRegistry.SLASH_MULT.get())
            || mappedOperation0.containsKey(AttributeRegistry.CRUSH_MULT.get()) || mappedOperation0.containsKey(AttributeRegistry.GENERIC_MULT.get())) {
                appendDamageClassComponent(mappedOperation0, list);
            }
        }
        if (mappedOperation0.containsKey(AttributeRegistry.CRIT_CHANCE.get()) || mappedOperation0.containsKey(AttributeRegistry.LIFESTEAL.get()) ||
                mappedOperation0.containsKey(AttributeRegistry.PENETRATING.get())
                || mappedOperation0.containsKey(Attributes.KNOCKBACK_RESISTANCE) || mappedOperation0.containsKey(AttributeRegistry.EVASION.get())
                || mappedOperation0.containsKey(Attributes.MOVEMENT_SPEED) || mappedOperation0.containsKey(Attributes.MAX_HEALTH) || mappedOperation0.containsKey(Attributes.LUCK)
                || (!mappedOperation1.isEmpty() || !mappedOperation2.isEmpty())
        ) {
            if (ClientEvents.checkShiftDown()) {
                if (mappedOperation0.containsKey(AttributeRegistry.CRIT_CHANCE.get()) || mappedOperation0.containsKey(AttributeRegistry.LIFESTEAL.get()) ||
                        mappedOperation0.containsKey(AttributeRegistry.PENETRATING.get())
                        || mappedOperation0.containsKey(Attributes.KNOCKBACK_RESISTANCE) || mappedOperation0.containsKey(AttributeRegistry.EVASION.get())
                        || mappedOperation0.containsKey(Attributes.MOVEMENT_SPEED) || mappedOperation0.containsKey(Attributes.MAX_HEALTH) || mappedOperation0.containsKey(Attributes.LUCK)
                ) {
                    list.add(Util.withColor(new TranslatableComponent("idf.auxiliary.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    appendAuxiliaryComponent(player, list, mappedOperation0);
                }
                if ((!mappedOperation1.isEmpty() || !mappedOperation2.isEmpty())) {
                    list.add(Util.withColor(new TranslatableComponent("idf.multipliers.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    appendMultipliers(mappedOperation1, mappedOperation2, list);
                }
            } else {
                MutableComponent component = new TextComponent("");
                component.append(new TranslatableComponent("idf.auxiliary_icon").withStyle(symbolStyle));
                component.append(new TranslatableComponent("idf.hold_shift_for_aux"));
                list.add(component);
            }
        }
    }

    private void appendDamageClassComponent(Map<Attribute, Double> mappedAttributes, List<Component> list) {
        if (mappedAttributes.containsKey(AttributeRegistry.STRIKE_MULT.get())) {
            double value = mappedAttributes.get(AttributeRegistry.STRIKE_MULT.get()) * 100;
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.strike_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.strike_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(new TranslatableComponent("idf.strike_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.strike_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.PIERCE_MULT.get())) {
            double value = mappedAttributes.get(AttributeRegistry.PIERCE_MULT.get()) * 100;
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.pierce_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.pierce_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(new TranslatableComponent("idf.pierce_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.pierce_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.SLASH_MULT.get())) {
            double value = mappedAttributes.get(AttributeRegistry.SLASH_MULT.get()) * 100;
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.slash_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.slash_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(new TranslatableComponent("idf.slash_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.slash_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
    }

    private void appendMultipliers(Map<Attribute, Double> mappedOperation1, Map<Attribute, Double> mappedOperation2, List<Component> list) {
        if (!mappedOperation1.isEmpty()) {
            list.add(Util.withColor(new TranslatableComponent("idf.operation1.tooltip").withStyle(ChatFormatting.ITALIC), Color.LIGHTGRAY));
            for (Map.Entry<Attribute, Double> entry : mappedOperation1.entrySet()) {
                TranslatableComponent name = new TranslatableComponent(entry.getKey().getDescriptionId());
                MutableComponent op1 = name.append(new TextComponent(" " + df.format(entry.getValue() * 100) + "%"));
                list.add(Util.withColor(op1, Color.GRAY));
            }
        }
        if (!mappedOperation2.isEmpty()) {
            list.add(Util.withColor(new TranslatableComponent("idf.operation2.tooltip").withStyle(ChatFormatting.ITALIC), Color.LIGHTGRAY));
            new TextComponent("");
            for (Map.Entry<Attribute, Double> entry : mappedOperation2.entrySet()) {
                TranslatableComponent name = new TranslatableComponent(entry.getKey().getDescriptionId());
                MutableComponent op2 = name.append(new TextComponent(" " + df.format(entry.getValue() * 100) + "%"));
                list.add(Util.withColor(op2, Color.GRAY));
            }
        }
    }

    private void appendDefensiveComponent(Map<Attribute, Double> mappedAttributes, List<Component> list) {
        if (mappedAttributes.containsKey(Attributes.ARMOR_TOUGHNESS)) {
            double value = mappedAttributes.get(Attributes.ARMOR_TOUGHNESS)/3;
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.defense_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.defense_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.defense_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.defense_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.ARMOR)) {
            double value = mappedAttributes.get(Attributes.ARMOR) * 3;
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.physical_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.physical_resistance_tooltip"), Color.BEIGE));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.physical_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.physical_resistance_tooltip"), Color.BEIGE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.FIRE_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.FIRE_RESISTANCE.get());
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.fire_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.fire_resistance_tooltip"), Color.ORANGE));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.fire_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.fire_resistance_tooltip"), Color.ORANGE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.WATER_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.WATER_RESISTANCE.get());
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.water_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.water_resistance_tooltip"), Color.BLUE));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.water_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.water_resistance_tooltip"), Color.BLUE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIGHTNING_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIGHTNING_RESISTANCE.get());
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.lightning_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.lightning_resistance_tooltip"), Color.YELLOW));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.lightning_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.lightning_resistance_tooltip"), Color.YELLOW));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.MAGIC_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.MAGIC_RESISTANCE.get());
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.magic_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.magic_resistance_tooltip"), Color.AQUA));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.magic_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.magic_resistance_tooltip"), Color.AQUA));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.DARK_RESISTANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.DARK_RESISTANCE.get());
            MutableComponent component = new TextComponent("  ");
            if (value < 0) {
                component.append(new TranslatableComponent("idf.dark_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.dark_resistance_tooltip"), Color.PURPLE));
                component.append(Util.withColor(new TextComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(new TranslatableComponent("idf.dark_icon").withStyle(symbolStyle));
                component.append(Util.withColor(new TranslatableComponent("idf.dark_resistance_tooltip"), Color.PURPLE));
                component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
    }

    private void appendDamageComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes) {
        if (mappedAttributes.containsKey(Attributes.ATTACK_DAMAGE)) {
            double value = mappedAttributes.get(Attributes.ATTACK_DAMAGE) + player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getDamageBonus((ItemStack) (Object) this, MobType.UNDEFINED);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.physical_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.physical_damage_tooltip"), Color.BEIGE));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.FIRE_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.FIRE_DAMAGE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.fire_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.fire_damage_tooltip"), Color.ORANGE));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.WATER_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.WATER_DAMAGE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.water_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.water_damage_tooltip"), Color.BLUE));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIGHTNING_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIGHTNING_DAMAGE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.lightning_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.lightning_damage_tooltip"), Color.YELLOW));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.MAGIC_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.MAGIC_DAMAGE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.magic_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.magic_damage_tooltip"), Color.AQUA));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.DARK_DAMAGE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.DARK_DAMAGE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.dark_icon").withStyle(symbolStyle));
            component.append(Util.withColor(new TranslatableComponent("idf.dark_damage_tooltip"), Color.PURPLE));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
    }

    private void appendAuxiliaryComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes) {
        if (mappedAttributes.containsKey(Attributes.ATTACK_KNOCKBACK)) {
            double value = mappedAttributes.get(Attributes.ATTACK_KNOCKBACK);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.knockback_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.knockback_tooltip"));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.LIFESTEAL.get())) {
            double value = mappedAttributes.get(AttributeRegistry.LIFESTEAL.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.lifesteal_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.lifesteal_tooltip"));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.PENETRATING.get())) {
            double value = mappedAttributes.get(AttributeRegistry.PENETRATING.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.pen_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.pen_tooltip"));
            component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.CRIT_CHANCE.get())) {
            double value = mappedAttributes.get(AttributeRegistry.CRIT_CHANCE.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.crit_chance_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.crit_chance_tooltip"));
            component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.KNOCKBACK_RESISTANCE)) {
            double value = mappedAttributes.get(Attributes.KNOCKBACK_RESISTANCE);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.knockback_resistance_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.knockback_resistance_tooltip"));
            component.append(Util.withColor(new TextComponent("" + df.format(value*100) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(AttributeRegistry.EVASION.get())) {
            double value = mappedAttributes.get(AttributeRegistry.EVASION.get());
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.evasion_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.evasion_tooltip"));
            component.append(Util.withColor(new TextComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.MAX_HEALTH)) {
            double value = mappedAttributes.get(Attributes.MAX_HEALTH);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.maxhp_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.maxhp_tooltip"));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.MOVEMENT_SPEED)) {
            double value = mappedAttributes.get(Attributes.MOVEMENT_SPEED);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.movespeed_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.movespeed_tooltip"));
            component.append(Util.withColor(new TextComponent("" + df.format(value*1000) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.LUCK)) {
            double value = mappedAttributes.get(Attributes.LUCK);
            MutableComponent component = new TextComponent("  ");
            component.append(new TranslatableComponent("idf.luck_icon").withStyle(symbolStyle));
            component.append(new TranslatableComponent("idf.luck_tooltip"));
            component.append(Util.withColor(new TextComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
    }

    @Shadow
    private int getHideFlags() {
        throw new IllegalStateException("Mixin failed to shadow getHideFlags()");
    }
    @Shadow
    private CompoundTag tag;
    @Shadow
    private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        throw new IllegalStateException("Mixin failed to shadow shouldShowInTooltip(int i, ItemStack.TooltipPart xxx)");
    }
    @Shadow
    private static Collection<Component> expandBlockState(String p_41762_) {
        throw new IllegalStateException("Mixin failed to shadow expandBlockState(String s)");
    }
}

