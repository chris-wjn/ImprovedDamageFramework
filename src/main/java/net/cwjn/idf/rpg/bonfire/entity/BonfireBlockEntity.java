package net.cwjn.idf.rpg.bonfire.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BonfireBlockEntity extends BlockEntity {

    private boolean active = false;
    private String name;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(BonfireEntityRegistry.BONFIRE_BASE.get(), pos, state);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
        setChanged();
    }

    public String getName() {
        return name == null ? "N/A" : name;
    }

    public void setName(String s) {
        name = s;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        active = tag.getBoolean("active");
        name = tag.getString("name");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("active", active);
        tag.putString("name", getName());
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
