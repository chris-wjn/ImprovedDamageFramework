package net.cwjn.idf.mixin.irons_spells;

import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.CastSource;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.cwjn.idf.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

@Mixin(TooltipsUtils.class)
public class MixinTooltipsUtils {

    @Redirect(method = "formatActiveSpellTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"), remap = false)
    private static void addDamageIcon(List<MutableComponent> instance, Consumer<MutableComponent> consumer, ItemStack stack, CastSource castSource, @Nonnull LocalPlayer player) {
        instance.forEach((line) -> {
            if (line.toString().toLowerCase().contains("damage")) {
                AbstractSpell spell = stack.getItem() instanceof SpellBook ? SpellBookData.getSpellBookData(stack).getActiveSpell() : SpellData.getSpellData(stack).getSpell();
                SpellType spellType = spell.getSpellType();
                String s = line.toString().substring(line.toString().indexOf('[')+1, line.toString().indexOf(']'));
                MutableComponent c = Component.empty();
                switch (spellType.getSchoolType()) {
                    case FIRE -> c.append(Util.writeIcon("fire", true)).append(Component.translatable("idf.irons_spellbook.fire_damage_tooltip", s));
                    case ICE -> c.append(Util.writeIcon("water", true)).append(Component.translatable("idf.irons_spellbook.water_damage_tooltip", s));
                    case LIGHTNING -> c.append(Util.writeIcon("lightning", true)).append(Component.translatable("idf.irons_spellbook.lightning_damage_tooltip", s));
                    case ENDER,POISON -> c.append(Util.writeIcon("magic", true)).append(Component.translatable("idf.irons_spellbook.magic_damage_tooltip", s));
                    case BLOOD,VOID -> c.append(Util.writeIcon("dark", true)).append(Component.translatable("idf.irons_spellbook.dark_damage_tooltip", s));
                    case HOLY -> c.append(Util.writeIcon("holy", true)).append(Component.translatable("idf.irons_spellbook.holy_damage_tooltip", s));
                    default -> c.append(Util.writeIcon("physical", true)).append(Component.translatable("idf.irons_spellbook.physical_damage_tooltip", s));
                }
                if (line.toString().contains("AOE")) instance.add(Component.literal(" ").append(c).append(Component.translatable("idf.AOE.text")));
                else instance.add(Component.literal(" ").append(c));
            }
            else {
                instance.add(Component.literal(" ").append(line.withStyle(ChatFormatting.DARK_GREEN)));
            }
        });
    }

}
