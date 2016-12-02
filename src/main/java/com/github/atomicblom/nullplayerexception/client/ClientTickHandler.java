/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Feb 3, 2014, 9:59:17 PM (GMT)]
 */
package com.github.atomicblom.nullplayerexception.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public final class ClientTickHandler {

    private ClientTickHandler() {}

    public static int ticksInGame = 0;

    @SubscribeEvent
    public static void clientTickEnd(ClientTickEvent event) {
        if(event.phase == Phase.END) {
            final GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if(gui == null || !gui.doesGuiPauseGame()) {
                ticksInGame++;
            }
        }
    }
}