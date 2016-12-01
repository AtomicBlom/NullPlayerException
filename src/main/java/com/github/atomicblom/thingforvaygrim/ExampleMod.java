package com.github.atomicblom.thingforvaygrim;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.List;

@Mod(modid = ExampleMod.MOD_ID, version = ExampleMod.VERSION, clientSideOnly = true)
public class ExampleMod
{
    public static final String MOD_ID = "thingforvaygrim";
    public static final String VERSION = "1.0";

    /*@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getSuggestedConfigurationFile();
    }*/

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void preEntityRender(RenderPlayerEvent.Pre event) {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void postEntityRender(RenderPlayerEvent.Post event) {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void loadWorldEvent(WorldEvent.Load onWorldLoaded) {
        Minecraft minecraft = Minecraft.getMinecraft();
        GuiIngame ingameGUI = minecraft.ingameGUI;
        if (minecraft.ingameGUI == null) {
            return;
        }
        if (minecraft.ingameGUI.getTabList() instanceof AlternativePlayerList) {
            return;
        }
        Logger.info("World Loaded");

        setMinecraftField(minecraft.ingameGUI, Gui.class, new AlternativePlayerList(minecraft, ingameGUI), "overlayPlayerList", "field_175196_v");
    }

    private void setMinecraftField(Object obj, Class<?> expectedClazz, Object newValue, String develName, String obfuscated) {
        Class<?> clazz = obj.getClass();
        Logger.info("Finding field %s in class %s", develName, clazz.getName());

        Optional<Field> field = Optional.absent();
        Iterable<Field> fieldsUpTo = getFieldsUpTo(clazz, expectedClazz);
        for (Field fieldToTest : fieldsUpTo) {
            String fieldName = fieldToTest.getName();
            if (fieldName.equals(develName) || fieldName.equals(obfuscated)) {
                field = Optional.of(fieldToTest);
                break;
            }
        }

        if (!field.isPresent()) {
            throw new RuntimeException(String.format("Unable to locate field %s in class %s", develName, clazz.getName()));
        }

        Logger.info("Changing field value");
        try
        {
            EnumHelper.setFailsafeFieldValue(field.get(), obj, newValue);
        } catch (Exception e) {
            Logger.severe("Unable to set field value.");
            throw new RuntimeException(e);
        }
    }

    public static Iterable<Field> getFieldsUpTo(Class<?> startClass,
                                                Class<?> exclusiveParent) {

        List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                    (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

}
