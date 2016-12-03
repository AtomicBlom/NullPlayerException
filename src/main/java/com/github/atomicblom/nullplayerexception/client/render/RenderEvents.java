package com.github.atomicblom.nullplayerexception.client.render;

import com.github.atomicblom.nullplayerexception.NullPlayerExceptionMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import java.nio.FloatBuffer;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderEvents
{
    private static final float DEFAULT_DISFIGURATION = 0.025F;

    private static float disfiguration = DEFAULT_DISFIGURATION;

    private static final FloatBuffer viewportFloatBuffer = GLAllocation.createDirectFloatBuffer(3);
    private static int viewportValueWidth = 0;
    private static int viewportValueHeight = 0;

    private static final ShaderCallback callback = new ShaderCallback() {
        @Override
        public void call(int shader)
        {
            final Minecraft minecraft = Minecraft.getMinecraft();
            if (viewportValueHeight != minecraft.displayHeight || viewportValueWidth != minecraft.displayWidth) {
                viewportFloatBuffer.clear();
                viewportFloatBuffer.put(viewportValueWidth = minecraft.displayWidth);
                viewportFloatBuffer.put(viewportValueHeight = minecraft.displayHeight);
                viewportFloatBuffer.put(0);
                viewportFloatBuffer.flip();
            }

            final int disfigurationUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "disfiguration");
            ARBShaderObjects.glUniform1fARB(disfigurationUniform, disfiguration);

            final int uniform = ARBShaderObjects.glGetUniformLocationARB(shader, "iResolution");
            ARBShaderObjects.glUniform3ARB(uniform, viewportFloatBuffer);
        }
    };

    private static boolean isRunningShader = false;
    private static String playerName;
    private static boolean alwaysRenderNameTag = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        final EntityPlayer entityPlayer = event.getEntityPlayer();
        GameProfile currentGameProfile = entityPlayer.getGameProfile();
        playerName = currentGameProfile.getName().toLowerCase();
        if (NullPlayerExceptionMod.corruptedPlayerNames.contains(playerName))
        {
            isRunningShader = true;
            alwaysRenderNameTag = entityPlayer.getAlwaysRenderNameTagForRender();
            entityPlayer.setAlwaysRenderNameTag(false);
            entityPlayer.setCustomNameTag("");
            ShaderHelper.useShader(ShaderHelper.corrupted, callback);
        } else {
            isRunningShader = false;
        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void postRenderPlayerEvent(RenderPlayerEvent.Post event) {
        if (isRunningShader)
        {
            final EntityPlayer entityPlayer = event.getEntityPlayer();
            ShaderHelper.releaseShader();
            entityPlayer.setAlwaysRenderNameTag(alwaysRenderNameTag);
            isRunningShader = false;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTryRenderPlayerName(RenderPlayerEvent.Specials.Pre event) {
        final EntityPlayer entityPlayer = event.getEntityPlayer();
        final GameProfile currentGameProfile = entityPlayer.getGameProfile();
        playerName = currentGameProfile.getName();
        if (NullPlayerExceptionMod.corruptedPlayerNames.contains(playerName)) {
            event.setCanceled(true);
        }
    }

    //There aren't pre/post events for this to enable/disable the shader.
    /*
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        final String name = Minecraft.getMinecraft().getConnection().getGameProfile().getName();
        if (NullPlayerExceptionMod.corruptedPlayerNames.contains(name)) {
            ShaderHelper.useShader(ShaderHelper.corrupted, callback);
        }
    }
    */
}
