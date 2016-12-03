package com.github.atomicblom.nullplayerexception.common;

import com.github.atomicblom.nullplayerexception.Logger;
import com.github.atomicblom.nullplayerexception.NullPlayerExceptionMod;
import com.github.atomicblom.nullplayerexception.common.networking.PlayerCorruptionChangedMessage;
import com.github.atomicblom.nullplayerexception.configuration.Settings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
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

        if (!Settings.INSTANCE.removeObfuscatedPlayer(args[0])) {
            Logger.warning("Attempt to mark %s as not corrupted, but they weren't in the list of corrupted players.", args[0]);
            notifyCommandListener(sender, this, "commands.npe.repair.playernotcorrupted", args[0]);
        } else {
            Logger.info("Marked %s as no longer corrupted.", args[0]);
            NullPlayerExceptionMod.CHANNEL.sendToAll(new PlayerCorruptionChangedMessage(args[0], false));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : Collections.<String>emptyList();
    }
}
