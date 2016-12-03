package com.github.atomicblom.nullplayerexception.client.gui;

import com.github.atomicblom.nullplayerexception.Logger;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ReplaceGuiEvent
{
    @SubscribeEvent
    public static void loadWorldEvent(WorldEvent.Load onWorldLoaded) {
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

    private static void setMinecraftField(Object obj, Class<?> expectedClazz, Object newValue, String develName, String obfuscated) {
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
