package com.github.atomicblom.nullplayerexception.common.networking;

import com.github.atomicblom.nullplayerexception.Logger;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.github.atomicblom.nullplayerexception.NullPlayerExceptionMod.corruptedPlayers;

public class PlayerCorruptionChangedMessageHandler implements IMessageHandler<PlayerCorruptionChangedMessage, IMessage>
{
    @SuppressWarnings({"ReturnOfNull", "ConstantConditions"})
    @Override
    public IMessage onMessage(PlayerCorruptionChangedMessage message, MessageContext ctx)
    {

        if (message.areCorrupted()) {
            for (String player : message.getPlayers()) {
                Logger.info("Marking %s as corrupted", player);
                corruptedPlayers.add(player.toLowerCase());
            }
        } else {
            for (String player : message.getPlayers()) {
                Logger.info("Marking %s as no longer corrupted", player);
                corruptedPlayers.remove(player.toLowerCase());
            }
        }

        return null;
    }
}
