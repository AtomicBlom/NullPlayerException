package com.github.atomicblom.nullplayerexception.common;

import com.github.atomicblom.nullplayerexception.Logger;
import com.github.atomicblom.nullplayerexception.NullPlayerExceptionMod;
import com.github.atomicblom.nullplayerexception.common.networking.PlayerCorruptionChangedMessage;
import com.github.atomicblom.nullplayerexception.configuration.Settings;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CorruptPlayerCommand extends CommandBase{
    @Override
    public String getCommandName() {
        return "npeCorrupt";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.npe.corrupt.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException("commands.npe.corrupt.usage");
        }
        String playerName;
        try {
            playerName = getPlayerName(server, sender, args[0]);
        } catch (PlayerNotFoundException e) {
            playerName = args[0];
        }

        if (!Settings.INSTANCE.addObfuscatedPlayer(playerName)) {
            Logger.warning("Attempt to mark %s as corrupted, but they were already corrupt.", playerName);
        } else {
            Logger.info("%s was marked as corrupted.", playerName);
            NullPlayerExceptionMod.CHANNEL.sendToAll(new PlayerCorruptionChangedMessage(playerName, true));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : Collections.<String>emptyList();
    }
}
