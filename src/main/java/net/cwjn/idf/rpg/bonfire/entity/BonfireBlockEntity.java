package net.cwjn.idf.rpg.bonfire.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BonfireBlockEntity extends BlockEntity {

    private boolean active = false;
    private String name;
    private UUID id, owner;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(BonfireEntityRegistry.BONFIRE_BASE.get(), pos, state);
        id = UUID.randomUUID();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
        setChanged();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
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
        id = tag.getUUID("id");
        owner = tag.getUUID("owner");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("active", active);
        tag.putString("name", getName());
        tag.putUUID("id", id);
        tag.putUUID("id", owner);
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
