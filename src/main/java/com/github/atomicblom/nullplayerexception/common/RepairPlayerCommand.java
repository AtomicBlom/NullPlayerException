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

public class RepairPlayerCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "npeRepair";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.npe.repair.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException("commands.npe.repair.usage");
        }

        String playerName;
        try {
            playerName = getPlayerName(server, sender, args[0]);
        } catch (PlayerNotFoundException e) {
            playerName = args[0];
        }

        if (!Settings.INSTANCE.removeObfuscatedPlayer(playerName)) {
            Logger.warning("Attempt to mark %s as not corrupted, but they weren't in the list of corrupted players.", playerName);
            notifyCommandListener(sender, this, "commands.npe.repair.playernotcorrupted", playerName);
        } else {
            Logger.info("Marked %s as no longer corrupted.", playerName);
            NullPlayerExceptionMod.CHANNEL.sendToAll(new PlayerCorruptionChangedMessage(playerName, false));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : Collections.<String>emptyList();
    }
}
