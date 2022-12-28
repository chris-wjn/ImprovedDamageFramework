package net.cwjn.idf.mixin;

import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.event.ClientEventsForgeBus;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Util;
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
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final Style symbolStyle = Style.EMPTY.withFont(FONT_ICONS);

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasCustomHoverName()Z"))
    private boolean removeItalicName(ItemStack instance) {
        return false;
    }

    @Inject(method = "getTooltipLines", at=@At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z", ordinal = 0),
    locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectInformationCode(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent mutablecomponent, int j) {
        if (this.hasTag() && this.getTag().contains("idf.equipment")) {
            list.add(Util.withColor(Util.translationComponent("idf.description.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
            MutableComponent durability = Util.textComponent("  ");
            durability.append(Util.translationComponent("idf.icon.durability").withStyle(symbolStyle));
            durability.append(Util.withColor(Util.translationComponent("idf.item.durability"), Color.LIGHTSLATEGREY));
            if (this.isDamageableItem()) {
                durability.append(Util.withColor(Util.textComponent(": " + (this.getMaxDamage() - this.getDamageValue()) + "/" + this.getMaxDamage()), Color.FLORALWHITE));
            } else {
                durability.append(Util.withColor(Util.translationComponent("idf.infinity.symbol"), Color.FLORALWHITE));
            }
            list.add(durability);
            if (this.getTag().contains("idf.damage_class")) {
                MutableComponent damageClass = Util.textComponent("  ");
                damageClass.append(Util.translationComponent("idf.icon.damage_class").withStyle(symbolStyle));
                damageClass.append(Util.translationComponent("idf.damage_class.tooltip." + this.getTag().getString("idf.damage_class")));
                list.add(damageClass);
            }
        }
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 1))
    private boolean removeEnchantmentDisplay(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 2))
    private boolean removeColourDisplay(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Redirect(method = "getTooltipLines", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 3))
    private boolean removeVanillaModifiersTooltip(int hideFlags, ItemStack.TooltipPart tooltipPart) {
        return false;
    }

    @Inject(method = "getTooltipLines", at=@At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 3),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCustomModifiersTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> callback, List<Component> list, MutableComponent mutablecomponent, int j) {
        Map<Attribute, Double> mappedOperation0 = new HashMap<>(5);
        Map<Attribute, Double> mappedOperation1 = new HashMap<>();
        Map<Attribute, Double> mappedOperation2 = new HashMap<>();
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) { //for each equipment slot, check the modifiers the item gives
            Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslot); //attribute and modifier map for the slot in this iteration
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
        if (this.hasTag() && this.getTag().contains("idf.equipment")) appendAttributes(player, list, mappedOperation0, mappedOperation1, mappedOperation2);
    }

    @Inject(method = "getTooltipLines", at=@At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectEnchantmentTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent mutablecomponent, int j) {
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
            if (!EnchantmentHelper.getEnchantments((ItemStack)(Object)this).isEmpty() && !(this.getItem() instanceof EnchantedBookItem)) {
                list.add(Util.withColor(Util.translationComponent("idf.enchantments.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                ItemStack.appendEnchantmentNames(list, this.getEnchantmentTags());
            }
        }
    }

    private void appendAttributes(Player player, List<Component> list, Map<Attribute, Double> mappedOperation0, Map<Attribute, Double> mappedOperation1, Map<Attribute, Double> mappedOperation2) {
        if (player == null) return;
        boolean damaging = hasDamage(mappedOperation0);
        boolean defensive = hasDefense(mappedOperation0);
        boolean aux = hasAuxiliary(mappedOperation0);
        boolean mult = hasMultipliers(mappedOperation1, mappedOperation2);
        if (damaging) {
            if (mappedOperation0.containsKey(Attributes.ATTACK_SPEED)) {
                double value1 = mappedOperation0.get(Attributes.ATTACK_SPEED);
                MutableComponent component1 = Util.textComponent("  ");
                component1.append(Util.translationComponent("idf.icon.attack_speed").withStyle(symbolStyle));
                component1.append(Util.translationComponent("idf.attack_speed_tooltip"));
                if (value1 > 0) component1.append("+");
                component1.append(Util.textComponent(df.format(value1)));
                list.add(component1);
            }
            if (mappedOperation0.containsKey(IDFAttributes.FORCE.get())) {
                double value2 = mappedOperation0.get(IDFAttributes.FORCE.get());
                MutableComponent component2 = Util.textComponent("  ");
                component2.append(Util.translationComponent("idf.icon.force").withStyle(symbolStyle));
                component2.append(Util.translationComponent("idf.force_tooltip"));
                if (value2 > 0) component2.append("+");
                component2.append(Util.textComponent(df.format(value2)));
                list.add(component2);
            }
        }
        list.add(Util.withColor(Util.translationComponent("idf.attributes.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
        if (damaging) {
            appendDamageComponent(player, list, mappedOperation0);
        }
        if (defensive) {
            appendDefensiveComponent(mappedOperation0, list);
            if (hasDamageClass(mappedOperation0)) {
                appendDamageClassComponent(mappedOperation0, list);
            }
        }
        if (aux || mult) {
            if (ClientEventsForgeBus.checkShiftDown()) {
                if (aux) {
                    list.add(Util.withColor(Util.translationComponent("idf.auxiliary.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    appendAuxiliaryComponent(player, list, mappedOperation0);
                }
                if (mult) {
                    list.add(Util.withColor(Util.translationComponent("idf.multipliers.tooltip").withStyle(ChatFormatting.BOLD), Color.FLORALWHITE));
                    appendMultipliers(mappedOperation1, mappedOperation2, list);
                }
            } else {
                MutableComponent component = Util.textComponent("");
                list.add(component.append(Util.translationComponent("idf.hold_shift_for_aux")));
            }
        }
    }

    private void appendDamageClassComponent(Map<Attribute, Double> mappedAttributes, List<Component> list) {
        if (mappedAttributes.containsKey(IDFAttributes.STRIKE_MULT.get())) {
            double value = mappedAttributes.get(IDFAttributes.STRIKE_MULT.get()) * 100;
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.strike").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.strike_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(Util.translationComponent("idf.icon.strike").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.strike_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.PIERCE_MULT.get())) {
            double value = mappedAttributes.get(IDFAttributes.PIERCE_MULT.get()) * 100;
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.pierce").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.pierce_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(Util.translationComponent("idf.icon.pierce").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.pierce_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.SLASH_MULT.get())) {
            double value = mappedAttributes.get(IDFAttributes.SLASH_MULT.get()) * 100;
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.slash").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.slash_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(Util.translationComponent("idf.icon.slash").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.slash_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.CRUSH_MULT.get())) {
            double value = mappedAttributes.get(IDFAttributes.CRUSH_MULT.get()) * 100;
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.crush").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.crush_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(Util.translationComponent("idf.icon.crush").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.crush_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.GENERIC_MULT.get())) {
            double value = mappedAttributes.get(IDFAttributes.GENERIC_MULT.get()) * 100;
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.generic").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.generic_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            } else {
                component.append(Util.translationComponent("idf.icon.generic").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.generic_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), ChatFormatting.RED));
            }
            list.add(component);
        }
    }

    private void appendMultipliers(Map<Attribute, Double> mappedOperation1, Map<Attribute, Double> mappedOperation2, List<Component> list) {
        if (!mappedOperation1.isEmpty()) {
            list.add(Util.withColor(Util.translationComponent("idf.operation1.tooltip").withStyle(ChatFormatting.ITALIC), Color.LIGHTGOLDENRODYELLOW));
            for (Map.Entry<Attribute, Double> entry : mappedOperation1.entrySet()) {
                MutableComponent name = Util.textComponent("   ").append(Util.withColor(Util.translationComponent(entry.getKey().getDescriptionId()), Color.LIGHTGRAY));
                MutableComponent op1;
                if (entry.getValue() > 0) {
                    op1 = name.append(Util.withColor(Util.textComponent("  +" + df.format((entry.getValue() + 1) * 100) + "%"), Color.LIGHTGREEN));
                } else {
                    op1 = name.append(Util.withColor(Util.textComponent("  " + df.format((entry.getValue() + 1) * 100) + "%"), Color.TOMATO));
                }
                list.add(Util.withColor(op1, Color.GRAY));
            }
        }
        if (!mappedOperation2.isEmpty()) {
            list.add(Util.withColor(Util.translationComponent("idf.operation2.tooltip").withStyle(ChatFormatting.ITALIC), Color.LIGHTGOLDENRODYELLOW));
            Util.textComponent("");
            for (Map.Entry<Attribute, Double> entry : mappedOperation2.entrySet()) {
                MutableComponent name = Util.textComponent("   ").append(Util.withColor(Util.translationComponent(entry.getKey().getDescriptionId()), Color.LIGHTGRAY));
                MutableComponent op2;
                if (entry.getValue() > 0) {
                    op2 = name.append(Util.withColor(Util.textComponent("  +" + df.format((entry.getValue() + 1) * 100) + "%"), Color.GREEN));
                } else {
                    op2 = name.append(Util.withColor(Util.textComponent("  " + df.format((entry.getValue() + 1) * 100) + "%"), Color.RED));
                }
                list.add(Util.withColor(op2, Color.GRAY));
            }
        }
    }

    private void appendDefensiveComponent(Map<Attribute, Double> mappedAttributes, List<Component> list) {
        if (mappedAttributes.containsKey(Attributes.ARMOR_TOUGHNESS)) {
            double value = mappedAttributes.get(Attributes.ARMOR_TOUGHNESS);
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.defense").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.defense_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.defense").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.defense_tooltip"), Color.FLORALWHITE));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.ARMOR)) {
            double value = mappedAttributes.get(Attributes.ARMOR);
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.physical_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.physical_resistance_tooltip"), Color.PHYSICAL_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.physical_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.physical_resistance_tooltip"), Color.PHYSICAL_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.FIRE_RESISTANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.FIRE_RESISTANCE.get());
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.fire_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.fire_resistance_tooltip"), Color.FIRE_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.fire_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.fire_resistance_tooltip"), Color.FIRE_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.WATER_RESISTANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.WATER_RESISTANCE.get());
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.water_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.water_resistance_tooltip"), Color.WATER_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.water_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.water_resistance_tooltip"), Color.WATER_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.LIGHTNING_RESISTANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.LIGHTNING_RESISTANCE.get());
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.lightning_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.lightning_resistance_tooltip"), Color.LIGHTNING_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.lightning_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.lightning_resistance_tooltip"), Color.LIGHTNING_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.MAGIC_RESISTANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.MAGIC_RESISTANCE.get());
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.magic_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.magic_resistance_tooltip"), Color.MAGIC_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.magic_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.magic_resistance_tooltip"), Color.MAGIC_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.DARK_RESISTANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.DARK_RESISTANCE.get());
            MutableComponent component = Util.textComponent("  ");
            if (value < 0) {
                component.append(Util.translationComponent("idf.icon.dark_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.dark_resistance_tooltip"), Color.DARK_COLOUR));
                component.append(Util.withColor(Util.textComponent("" + df.format(value)), ChatFormatting.RED));
            } else {
                component.append(Util.translationComponent("idf.icon.dark_resistance").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.dark_resistance_tooltip"), Color.DARK_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            }
            list.add(component);
        }
    }

    private void appendDamageComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes) {
        if (mappedAttributes.containsKey(Attributes.ATTACK_DAMAGE)) {
            double value = mappedAttributes.get(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getDamageBonus((ItemStack) (Object) this, MobType.UNDEFINED);
            if (value != 0) {
                MutableComponent component = Util.textComponent("  ");
                component.append(Util.translationComponent("idf.icon.physical_damage").withStyle(symbolStyle));
                component.append(Util.withColor(Util.translationComponent("idf.physical_damage_tooltip"), Color.PHYSICAL_COLOUR));
                component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
                list.add(component);
            }
        }
        if (mappedAttributes.containsKey(IDFAttributes.FIRE_DAMAGE.get())) {
            double value = mappedAttributes.get(IDFAttributes.FIRE_DAMAGE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.fire_damage").withStyle(symbolStyle));
            component.append(Util.withColor(Util.translationComponent("idf.fire_damage_tooltip"), Color.FIRE_COLOUR));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.WATER_DAMAGE.get())) {
            double value = mappedAttributes.get(IDFAttributes.WATER_DAMAGE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.water_damage").withStyle(symbolStyle));
            component.append(Util.withColor(Util.translationComponent("idf.water_damage_tooltip"), Color.WATER_COLOUR));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.LIGHTNING_DAMAGE.get())) {
            double value = mappedAttributes.get(IDFAttributes.LIGHTNING_DAMAGE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.lightning_damage").withStyle(symbolStyle));
            component.append(Util.withColor(Util.translationComponent("idf.lightning_damage_tooltip"), Color.LIGHTNING_COLOUR));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.MAGIC_DAMAGE.get())) {
            double value = mappedAttributes.get(IDFAttributes.MAGIC_DAMAGE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.magic_damage").withStyle(symbolStyle));
            component.append(Util.withColor(Util.translationComponent("idf.magic_damage_tooltip"), Color.MAGIC_COLOUR));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.DARK_DAMAGE.get())) {
            double value = mappedAttributes.get(IDFAttributes.DARK_DAMAGE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.dark_damage").withStyle(symbolStyle));
            component.append(Util.withColor(Util.translationComponent("idf.dark_damage_tooltip"), Color.DARK_COLOUR));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
    }

    private void appendAuxiliaryComponent(@NotNull Player player, List<Component> list, Map<Attribute, Double> mappedAttributes) {
        if (mappedAttributes.containsKey(Attributes.ATTACK_KNOCKBACK)) {
            double value = mappedAttributes.get(Attributes.ATTACK_KNOCKBACK);
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.knockback").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.knockback_tooltip"));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.LIFESTEAL.get())) {
            double value = mappedAttributes.get(IDFAttributes.LIFESTEAL.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.lifesteal").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.lifesteal_tooltip"));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.PENETRATING.get())) {
            double value = mappedAttributes.get(IDFAttributes.PENETRATING.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.armour_penetration").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.pen_tooltip"));
            component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.CRIT_CHANCE.get())) {
            double value = mappedAttributes.get(IDFAttributes.CRIT_CHANCE.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.critical_chance").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.crit_chance_tooltip"));
            component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.KNOCKBACK_RESISTANCE)) {
            double value = mappedAttributes.get(Attributes.KNOCKBACK_RESISTANCE);
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.knockback_resistance").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.knockback_resistance_tooltip"));
            component.append(Util.withColor(Util.textComponent("" + df.format(value*100) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(IDFAttributes.EVASION.get())) {
            double value = mappedAttributes.get(IDFAttributes.EVASION.get());
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.evasion").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.evasion_tooltip"));
            component.append(Util.withColor(Util.textComponent("" + df.format(value) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.MAX_HEALTH)) {
            double value = mappedAttributes.get(Attributes.MAX_HEALTH);
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.health").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.maxhp_tooltip"));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.MOVEMENT_SPEED)) {
            double value = mappedAttributes.get(Attributes.MOVEMENT_SPEED);
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.movespeed").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.movespeed_tooltip"));
            component.append(Util.withColor(Util.textComponent("" + df.format(value*1000) + "%"), Color.LIGHTGREEN));
            list.add(component);
        }
        if (mappedAttributes.containsKey(Attributes.LUCK)) {
            double value = mappedAttributes.get(Attributes.LUCK);
            MutableComponent component = Util.textComponent("  ");
            component.append(Util.translationComponent("idf.icon.luck").withStyle(symbolStyle));
            component.append(Util.translationComponent("idf.luck_tooltip"));
            component.append(Util.withColor(Util.textComponent("+" + df.format(value)), Color.LIGHTGREEN));
            list.add(component);
        }
    }

    private static boolean hasDamage(Map<Attribute, Double> map) {
        return map.containsKey(Attributes.ATTACK_DAMAGE) || map.containsKey(IDFAttributes.FIRE_DAMAGE.get()) || map.containsKey(IDFAttributes.MAGIC_DAMAGE.get()) || map.containsKey(IDFAttributes.WATER_DAMAGE.get())
                || map.containsKey(IDFAttributes.LIGHTNING_DAMAGE.get()) || map.containsKey(IDFAttributes.DARK_DAMAGE.get());
    }

    private static boolean hasDefense(Map<Attribute, Double> map) {
        return map.containsKey(Attributes.ARMOR) || map.containsKey(Attributes.ARMOR_TOUGHNESS) || map.containsKey(IDFAttributes.FIRE_RESISTANCE.get()) ||
                map.containsKey(IDFAttributes.WATER_RESISTANCE.get()) || map.containsKey(IDFAttributes.LIGHTNING_RESISTANCE.get()) || map.containsKey(IDFAttributes.MAGIC_RESISTANCE.get())
                || map.containsKey(IDFAttributes.DARK_RESISTANCE.get());
    }

    private static boolean hasDamageClass(Map<Attribute, Double> map) {
        return map.containsKey(IDFAttributes.STRIKE_MULT.get()) || map.containsKey(IDFAttributes.PIERCE_MULT.get()) || map.containsKey(IDFAttributes.SLASH_MULT.get())
                || map.containsKey(IDFAttributes.CRUSH_MULT.get()) || map.containsKey(IDFAttributes.GENERIC_MULT.get());
    }

    private static boolean hasAuxiliary(Map<Attribute, Double> map) {
        return map.containsKey(IDFAttributes.CRIT_CHANCE.get()) || map.containsKey(IDFAttributes.LIFESTEAL.get()) ||
                map.containsKey(IDFAttributes.PENETRATING.get()) || map.containsKey(Attributes.ATTACK_KNOCKBACK)
                || map.containsKey(Attributes.KNOCKBACK_RESISTANCE) || map.containsKey(IDFAttributes.EVASION.get())
                || map.containsKey(Attributes.MOVEMENT_SPEED) || map.containsKey(Attributes.MAX_HEALTH) || map.containsKey(Attributes.LUCK);
    }

    private static boolean hasMultipliers(Map<Attribute, Double> map, Map<Attribute, Double> map1) {
        return (!map.isEmpty() || !map1.isEmpty());
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void constructorInjection(ItemLike item, int amount, CompoundTag capNBT, CallbackInfo callback) {
        OnItemStackCreatedEvent event = new OnItemStackCreatedEvent((ItemStack)(Object)this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Shadow private int getHideFlags() {
        throw new IllegalStateException("Mixin failed to shadow getHideFlags()");
    }
    @Shadow private CompoundTag tag;
    @Shadow private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        throw new IllegalStateException("Mixin failed to shadow shouldShowInTooltip(int i, ItemStack.TooltipPart xxx)");
    }
    @Shadow private static Collection<Component> expandBlockState(String p_41762_) {
        throw new IllegalStateException("Mixin failed to shadow expandBlockState(String s)");
    }
    @Shadow public abstract ListTag getEnchantmentTags();
    @Shadow public abstract Item getItem();
    @Shadow public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot p_41639_);
    @Shadow public abstract int getDamageValue();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract boolean isDamageableItem();
    @Shadow @Nullable public abstract CompoundTag getTag();
    @Shadow public abstract boolean hasTag();
}

