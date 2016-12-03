package com.github.atomicblom.nullplayerexception;

import com.github.atomicblom.nullplayerexception.common.CommonRenderProxy;
import com.github.atomicblom.nullplayerexception.common.CorruptPlayerCommand;
import com.github.atomicblom.nullplayerexception.common.RepairPlayerCommand;
import com.github.atomicblom.nullplayerexception.common.networking.PlayerCorruptionChangedMessage;
import com.github.atomicblom.nullplayerexception.common.networking.PlayerCorruptionChangedMessageHandler;
import com.github.atomicblom.nullplayerexception.configuration.ConfigurationHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod(modid = NullPlayerExceptionMod.MOD_ID, version = NullPlayerExceptionMod.VERSION)
public class NullPlayerExceptionMod
{
    public static final String MOD_ID = "nullplayerexception";
    public static final String MOD_NAME = "Null Player Exception";
    public static final String VERSION = "1.0";

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final Set<String> corruptedPlayerNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(32));

    @SidedProxy(
            modId = MOD_ID,
            clientSide = "com.github.atomicblom.nullplayerexception.client.ClientRenderProxy",
            serverSide = "com.github.atomicblom.nullplayerexception.common.CommonRenderProxy")
    public static CommonRenderProxy renderProxy = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        CHANNEL.registerMessage(PlayerCorruptionChangedMessageHandler.class, PlayerCorruptionChangedMessage.class, 0, Side.CLIENT);

        renderProxy.registerRenderers();
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CorruptPlayerCommand());
        event.registerServerCommand(new RepairPlayerCommand());
    }
}
