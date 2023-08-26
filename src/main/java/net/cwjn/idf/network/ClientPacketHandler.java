package net.cwjn.idf.network;

import com.mojang.math.Vector3f;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.gui.InfoScreen;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.cwjn.idf.particle.IDFParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

import static net.cwjn.idf.data.CommonData.*;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void syncClientConfig(SyncClientConfigPacket packet) {
        LOGICAL_ARMOUR_MAP_FLAT = packet.armourFlat;
        LOGICAL_ARMOUR_MAP_MULT = packet.armourMult;
        LOGICAL_WEAPON_MAP_FLAT = packet.weaponFlat;
        LOGICAL_WEAPON_MAP_MULT = packet.weaponMult;
        JSONHandler.updateItems();
    }

    public static void addDamageIndicatorParticle(double x, double y, double z, float f, int col, double horizontalOffset, double yOffset, UUID id) {
        if (Minecraft.getInstance().player == null) return;
        if (id.equals(Minecraft.getInstance().player.getUUID())) return;
        Level world = Minecraft.getInstance().level;
        Vector3f lookVector = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        float newZ = -1*lookVector.x();
        float newX = lookVector.z();
        if (world != null) {
            world.addParticle(IDFParticles.NUMBER_PARTICLE.get().setNumber(f).setColour(col), x, y, z, (newX*horizontalOffset)/10, yOffset/3, (newZ*horizontalOffset)/10);
        }
    }

    public static void addMissParticle(double x, double y, double z, float horizontalOffset, double yOffset, UUID id) {
        if (Minecraft.getInstance().player == null) return;
        if (id.equals(Minecraft.getInstance().player.getUUID())) return;
        Level world = Minecraft.getInstance().level;
        Vector3f lookVector = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        float newZ = -1*lookVector.x();
        float newX = lookVector.z();
        if (world != null) {
            world.addParticle(IDFParticles.MISS_PARTICLE.get(), x, y, z, (newX*horizontalOffset)/10, yOffset/3, (newZ*horizontalOffset)/10);
        }
    }

    public static void updateSkyDarken(int i) {
        ClientData.skyDarken = i;
    }

    public static void openInfoScreen() {
        Minecraft.getInstance().setScreen(new InfoScreen());
    }

}
