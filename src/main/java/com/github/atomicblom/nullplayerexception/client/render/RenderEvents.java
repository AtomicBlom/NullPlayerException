package com.github.atomicblom.nullplayerexception.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
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


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        ShaderHelper.useShader(ShaderHelper.corrupted, callback);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void postRenderPlayerEvent(RenderPlayerEvent.Post event) {
        ShaderHelper.releaseShader();
    }
}
