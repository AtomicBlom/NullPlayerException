package com.github.atomicblom.nullplayerexception.common.networking;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

public class PlayerCorruptionChangedMessage implements IMessage
{
    private boolean firstConnect;
    private boolean areCorrupted;
    private List<String> playerNames;

    @SuppressWarnings("unused")
    public PlayerCorruptionChangedMessage() {}

    public PlayerCorruptionChangedMessage(String playerName, boolean isCorrupted)
    {
        this.playerNames = Lists.newArrayList(playerName);
        this.areCorrupted = isCorrupted;
        this.firstConnect = false;
    }

    public PlayerCorruptionChangedMessage(List<String> playerNames, boolean areCorrupted)
    {
        this.playerNames = playerNames;
        this.firstConnect = true;
        this.areCorrupted = areCorrupted;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.areCorrupted = buf.readBoolean();
        int length = buf.readInt();
        playerNames = Lists.newArrayListWithExpectedSize(length);
        for (int i = 0; i < length; i++) {
            playerNames.add(ByteBufUtils.readUTF8String(buf));
        }
        this.firstConnect = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(areCorrupted);
        buf.writeInt(playerNames.size());
        for (String playerName : playerNames) {
            ByteBufUtils.writeUTF8String(buf, playerName);
        }
        buf.writeBoolean(firstConnect);
    }

    boolean areCorrupted() { return areCorrupted; }

    boolean isFirstConnect() { return firstConnect; }

    List<String> getPlayers() { return playerNames; }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("areCorrupted", areCorrupted)
                .add("playerNames", playerNames)
                .toString();
    }
}