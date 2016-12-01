package com.github.atomicblom.thingforvaygrim;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

@Mod(modid = ExampleMod.MOD_ID, version = ExampleMod.VERSION, clientSideOnly = true)
public class ExampleMod
{
    public static final String MOD_ID = "thingforvaygrim";
    public static final String VERSION = "1.0";

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        //event.getSuggestedConfigurationFile();
        ShaderHelper.initShaders();
    }

    public static final float DEFAULT_GRAIN_INTENSITY = 0.05F;
    public static final float DEFAULT_DISFIGURATION = 0.025F;

    public static float grainIntensity = DEFAULT_GRAIN_INTENSITY;
    public static float disfiguration = DEFAULT_DISFIGURATION;

    private static final FloatBuffer intBuffer = GLAllocation.createDirectFloatBuffer(3);

    public static final ShaderCallback callback = new ShaderCallback() {
        @Override
        public void call(int shader)
        {
            intBuffer.clear();
            //GlStateManager.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            intBuffer.put(Minecraft.getMinecraft().displayWidth);
            intBuffer.put(Minecraft.getMinecraft().displayWidth);
            intBuffer.put(0);
            intBuffer.flip();
            //intBuffer.flip().limit(16);

            int disfigurationUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "disfiguration");
            ARBShaderObjects.glUniform1fARB(disfigurationUniform, disfiguration);

            // Vert Uniforms
            /*int grainIntensityUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "grainIntensity");
            ARBShaderObjects.glUniform1fARB(grainIntensityUniform, grainIntensity);*/
            int uniform = ARBShaderObjects.glGetUniformLocationARB(shader, "iResolution");
            ARBShaderObjects.glUniform3ARB(uniform, intBuffer);
        }
    };


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void preRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        ShaderHelper.useShader(ShaderHelper.doppleganger, callback);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void postRenderPlayerEvent(RenderPlayerEvent.Post event) {
        ShaderHelper.releaseShader();
    }

    /*
    Stencil buffer experiment
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void preEntityRender(RenderPlayerEvent.Pre event) {
        //GL11.glEnable(GL11.GL_STENCIL_TEST);
        //GL11.glStencilMask(0xFF);
        //GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        //GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void postEntityRender(RenderPlayerEvent.Post event) {

        //GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    private final FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer topLeftResults = GLAllocation.createDirectFloatBuffer(3);
    private final FloatBuffer bottomRightResults = GLAllocation.createDirectFloatBuffer(3);
    private final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);

    private final FloatBuffer temp = GLAllocation.createDirectFloatBuffer(16);

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
        projectionMatrixBuffer.clear();
        modelviewMatrixBuffer.clear();
        intBuffer.clear();
        topLeftResults.clear();
        bottomRightResults.clear();
        temp.clear();

        GlStateManager.getFloat(GL_PROJECTION_MATRIX, projectionMatrixBuffer);
        GlStateManager.getFloat(GL_MODELVIEW_MATRIX, modelviewMatrixBuffer);
        projectionMatrixBuffer.flip().limit(16);
        modelviewMatrixBuffer.flip().limit(16);

        GlStateManager.glGetInteger(GL_VIEWPORT, intBuffer);

        intBuffer.flip().limit(16);

        boolean tlResult = Project.gluUnProject(0, 0, 0.1f, modelviewMatrixBuffer, projectionMatrixBuffer, intBuffer, topLeftResults);
        boolean trResult = Project.gluUnProject(1, 1, 0.1f, modelviewMatrixBuffer, projectionMatrixBuffer, intBuffer, bottomRightResults);
        float[] tl = new float[3];
        float[] br = new float[3];

        topLeftResults.get(tl);
        bottomRightResults.get(br);

        //GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        //GL11.glStencilMask(0xFF);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // set the color of the quad (R,G,B,A)
        GL11.glColor3f(0.5f,0.5f,1.0f);

        // draw quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(tl[0], tl[1], tl[2]);
        GL11.glVertex3f(tl[0], br[1], br[2]);
        GL11.glVertex3f(br[0], tl[1], tl[2]);
        GL11.glVertex3f(br[0], br[1], br[2]);
        GL11.glEnd();

        //GL11.glStencilMask(0xFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    */
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
