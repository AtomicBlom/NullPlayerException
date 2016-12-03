package com.github.atomicblom.nullplayerexception.common;

import com.github.atomicblom.nullplayerexception.Logger;
import com.github.atomicblom.nullplayerexception.NullPlayerExceptionMod;
import com.github.atomicblom.nullplayerexception.common.networking.PlayerCorruptionChangedMessage;
import com.github.atomicblom.nullplayerexception.configuration.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber
public class PlayerEvents {

    @SubscribeEvent
    public static void OnEntityJoinWorld(EntityJoinWorldEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            return;
        }
        if (!(event.getEntity() instanceof EntityPlayerMP)) {
            return;
        }
        Logger.info("Player joining world, spamming with corruption list.");
        List<String> obfuscatedPlayers = Settings.INSTANCE.getObfuscatedPlayers();
        NullPlayerExceptionMod.CHANNEL.sendTo(new PlayerCorruptionChangedMessage(obfuscatedPlayers, true), (EntityPlayerMP) event.getEntity());
    }

    @SubscribeEvent
    public static void OnJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Logger.info("Clearing corrupted Player List");
        NullPlayerExceptionMod.corruptedPlayers.clear();
    }
}
