package com.github.atomicblom.thingforvaygrim;

import com.github.atomicblom.thingforvaygrim.client.render.ShaderHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CorruptedMod.MOD_ID, version = CorruptedMod.VERSION, clientSideOnly = true)
public class CorruptedMod
{
    public static final String MOD_ID = "corrupted";
    public static final String VERSION = "1.0";

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        //event.getSuggestedConfigurationFile();
        ShaderHelper.initShaders();
    }
}
