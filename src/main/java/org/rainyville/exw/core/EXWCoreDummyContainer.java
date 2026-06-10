package org.rainyville.exw.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MouseFilter;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("UnstableApiUsage")
public class EXWCoreDummyContainer extends DummyModContainer {
    public static final String VERSION = "@VERSION@";

    private URL updateJSONUrl;

    private boolean zoomMode = false;
    private MouseFilter mouseX;
    private MouseFilter mouseY;

    public EXWCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "exw_core";
        meta.name = "Expansive Weaponry Core";
        meta.version = VERSION;
        meta.credits = "";
        meta.authorList = Arrays.asList("Unknown025", "MKVIIGTI");
        meta.description = "Provides core functionality for Expansive Weaponry.";
        meta.url = "https://exw.rainyville.org/";
        meta.screenshots = new String[0];
        meta.logoFile = "assets/exw/textures/logo.png";
        meta.dependants = Collections.emptyList();
        meta.childMods = Collections.emptyList();

        try {
            // Expose current version
            updateJSONUrl = new URL("https://rainyville.org/exwc/update.json?version=" + VERSION);
        } catch (MalformedURLException e) {
            EXWLoadingPlugin.LOGGER.error("Failed to parse update URL", e);
            updateJSONUrl = null;
        }
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public File getSource() {
        return EXWLoadingPlugin.COREMOD_LOCATION;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        if (getSource() == null) return null;

        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public Certificate getSigningCertificate() {
        Certificate[] certificates = getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

    @Override
    public URL getUpdateUrl() {
        return updateJSONUrl;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        EXWLoadingPlugin.LOGGER.info("Initializing EXW Core");
        if (!event.getSide().isClient()) return;

        Field mouseX = ReflectionHelper.findField(EntityRenderer.class, "mouseFilterXAxis", "field_78527_v");
        Field mouseY = ReflectionHelper.findField(EntityRenderer.class, "mouseFilterYAxis", "field_78526_w");

        mouseX.setAccessible(true);
        mouseY.setAccessible(true);

        Minecraft mc = Minecraft.getMinecraft();
        try {
            this.mouseX = (MouseFilter) mouseX.get(mc.entityRenderer);
            this.mouseY = (MouseFilter) mouseY.get(mc.entityRenderer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGetFOV(EntityViewRenderEvent.FOVModifier event) {
        if (mouseX == null || mouseY == null || EXWClassTransformer.OBFUSCATED) return;

        Minecraft mc = Minecraft.getMinecraft();
        boolean zoomActive = false;
        float f = event.getFOV();

        if (mc.currentScreen == null) {
            if (Keyboard.KEY_LCONTROL < 0) {
                zoomActive = Mouse.isButtonDown(Keyboard.KEY_LCONTROL + 100);
            } else {
                zoomActive = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            }
        }

        if (zoomActive) {
            if (!zoomMode) {
                zoomMode = true;
                mc.gameSettings.smoothCamera = true;
            }

            f /= 4.0F;
        } else if (zoomMode) {
            zoomMode = false;
            mc.gameSettings.smoothCamera = false;
            mouseX.reset();
            mouseY.reset();
            mc.renderGlobal.setDisplayListEntitiesDirty();
        }

        event.setFOV(f);
    }
}
