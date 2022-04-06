package com.cwjn.idf.mixin;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Color;
import com.cwjn.idf.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    private static DecimalFormat df = new DecimalFormat("#.##");
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    @Shadow abstract int getHideFlags();
    @Shadow private CompoundTag tag;

    @Shadow @Final public static DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

    @Shadow public abstract boolean hasTag();

    private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        return (p_41627_ & p_41628_.getMask()) == 0;
    }

    /**
     * @author cwJn
     */
    @Overwrite
    public List<Component> getTooltipLines(@Nullable Player player, TooltipFlag tooltipMode) {
        ItemStack item = (ItemStack)((Object)this);
        List<Component> list = Lists.newArrayList();
        MutableComponent mutablecomponent = (new TextComponent("")).append(item.getHoverName());//.withStyle(item.getRarity().color);

        /*if (item.hasCustomHoverName()) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }*/

        //for maps... like treasure maps
        list.add(mutablecomponent);
        list.add(new TextComponent(""));
        if (!tooltipMode.isAdvanced() && !item.hasCustomHoverName() && item.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId(item);
            if (integer != null) {
                list.add((new TextComponent("#" + integer)).withStyle(ChatFormatting.GRAY));
            }
        }

        //dont know what this does
        int j = this.getHideFlags();
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
            item.getItem().appendHoverText(item, player == null ? null : player.level, list, tooltipMode);
        }

        if (item.hasTag()) {
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
                                if (this.tag.contains("idf.equipment")) {
                                    list.add(Util.withColor(new TranslatableComponent("idf.description.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                                    //TODO: add weapon scaling and other info here.
                                    list.add(Util.withColor(mutablecomponent1, Color.LIGHTGRAY));
                                } else {
                                    list.add(Util.withColor(mutablecomponent1, Color.LIGHTGRAY));
                                }
                            }
                        } catch (Exception exception) {
                            compoundtag.remove("Lore");
                        }
                    }
                }
            }
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
                if (!EnchantmentHelper.getEnchantments(item).isEmpty()) {
                    list.add(Util.withColor(new TranslatableComponent("idf.enchantments.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    ItemStack.appendEnchantmentNames(list, item.getEnchantmentTags());
                    list.add(new TextComponent(""));
                }
            }
        }

        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            if (this.hasTag() && this.tag.contains("idf.equipment")) list.add(Util.withColor(new TranslatableComponent("idf.attributes.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
            for(EquipmentSlot equipmentslot : EquipmentSlot.values()) { //check all equipment slots
                Multimap<Attribute, AttributeModifier> multimap = item.getAttributeModifiers(equipmentslot); //attribute and modifier map for the slot in this iteration
                if (!multimap.isEmpty()) { //make sure there is at least one entry in the map
                    //list.add((new TranslatableComponent("item.modifiers." + equipmentslot.getName())).withStyle(ChatFormatting.GRAY));
                    for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) { //iterate through each attribute and attribute modifier pair
                        AttributeModifier attributemodifier = entry.getValue(); //get the modifier in this iteration
                        Attribute attribute = entry.getKey();
                        double modifierAmount = attributemodifier.getAmount(); //get the amount that it modifies by

                        double percentageModifierAmount;
                        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                percentageModifierAmount = modifierAmount * 10.0D;
                            } else {
                                percentageModifierAmount = modifierAmount;
                            }
                        } else {
                            percentageModifierAmount = modifierAmount * 100.0D;
                        }
                        MutableComponent name;
                        MutableComponent value;
                        switch (attribute.getDescriptionId()) {
                            case "attribute.name.generic.attack_damage":
                                name = Util.withColor(new TranslatableComponent("idf.physical_damage_tooltip"), Color.DARKORANGE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "attribute.name.generic.attack_speed":
                                name = Util.withColor(new TranslatableComponent("idf.attack_speed_tooltip"), Color.WHITESMOKE);
                                double baseAtkSpeed = 0;
                                if (player != null) baseAtkSpeed = player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                value = Util.withColor(new TranslatableComponent(df.format(attributemodifier.getAmount()+baseAtkSpeed), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.fire_damage":
                                name = Util.withColor(new TranslatableComponent("idf.fire_damage_tooltip"), Color.ORANGERED);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.water_damage":
                                name = Util.withColor(new TranslatableComponent("idf.water_damage_tooltip"), Color.STEELBLUE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.lightning_damage":
                                name = Util.withColor(new TranslatableComponent("idf.lightning_damage_tooltip"), Color.YELLOW);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.magic_damage":
                                name = Util.withColor(new TranslatableComponent("idf.magic_damage_tooltip"), Color.MAGICBLUE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.dark_damage":
                                name = Util.withColor(new TranslatableComponent("idf.dark_damage_tooltip"), Color.DARKVIOLET);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "attribute.name.generic.armor_toughness":
                                name = Util.withColor(new TranslatableComponent("idf.defense_tooltip"), Color.GHOSTWHITE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "attribute.name.generic.armor":
                                name = Util.withColor(new TranslatableComponent("idf.physical_resistance_tooltip"), Color.DARKORANGE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.fire_resistance":
                                name = Util.withColor(new TranslatableComponent("idf.fire_resistance_tooltip"), Color.ORANGERED);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.water_resistance":
                                name = Util.withColor(new TranslatableComponent("idf.water_resistance_tooltip"), Color.STEELBLUE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.lightning_resistance":
                                name = Util.withColor(new TranslatableComponent("idf.lightning_resistance_tooltip"), Color.YELLOW);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.magic_resistance":
                                name = Util.withColor(new TranslatableComponent("idf.magic_resistance_tooltip"), Color.MAGICBLUE);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            case "idf.attribute.dark_resistance":
                                name = Util.withColor(new TranslatableComponent("idf.dark_resistance_tooltip"), Color.DARKVIOLET);
                                value = Util.withColor(new TranslatableComponent("" + attributemodifier.getAmount(), df.format(percentageModifierAmount)), Color.LIGHTGREEN);
                                list.add(name.append(value));
                                break;
                            default:
                                if (modifierAmount > 0.0D) {
                                    list.add((new TranslatableComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(percentageModifierAmount), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                                } else if (modifierAmount < 0.0D) {
                                    percentageModifierAmount *= -1.0D;
                                    list.add((new TranslatableComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(percentageModifierAmount), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.RED));
                                }
                        }
                    }
                }
            }
        }

        if (item.hasTag()) {
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
            if (item.isDamaged()) {
                list.add(new TranslatableComponent("item.durability", item.getMaxDamage() - item.getDamageValue(), item.getMaxDamage()));
            }

            list.add((new TextComponent(Registry.ITEM.getKey(item.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
            if (item.hasTag()) {
                list.add((new TranslatableComponent("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onItemTooltip(item, player, list, tooltipMode);
        return list;
    }

    private static Collection<Component> expandBlockState(String p_41762_) {
        try {
            BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(p_41762_), true)).parse(true);
            BlockState blockstate = blockstateparser.getState();
            ResourceLocation resourcelocation = blockstateparser.getTag();
            boolean flag = blockstate != null;
            boolean flag1 = resourcelocation != null;
            if (flag || flag1) {
                if (flag) {
                    return Lists.newArrayList(blockstate.getBlock().getName().withStyle(ChatFormatting.DARK_GRAY));
                }

                Tag<Block> tag = BlockTags.getAllTags().getTag(resourcelocation);
                if (tag != null) {
                    Collection<Block> collection = tag.getValues();
                    if (!collection.isEmpty()) {
                        return collection.stream().map(Block::getName).map((p_41717_) -> {
                            return p_41717_.withStyle(ChatFormatting.DARK_GRAY);
                        }).collect(Collectors.toList());
                    }
                }
            }
        } catch (CommandSyntaxException commandsyntaxexception) {
        }

        return Lists.newArrayList((new TextComponent("missingno")).withStyle(ChatFormatting.DARK_GRAY));
    }

}

