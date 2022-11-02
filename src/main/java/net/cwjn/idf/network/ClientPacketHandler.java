package net.cwjn.idf.network;

import net.cwjn.idf.block.BonfireBlock;
import net.cwjn.idf.block.entity.BonfireBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void syncBonfire(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        Level level = Minecraft.getInstance().level;
        if (level.getBlockEntity(pos) instanceof BonfireBlockEntity) {
            BonfireBlockEntity be = (BonfireBlockEntity) level.getBlockEntity(pos);
            if (be != null) {
                be.setActive(true);
                be.getBlockState().setValue(BonfireBlock.ACTIVE, true);
            }
        }
    }

}
