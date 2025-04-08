package org.rainyville.exw.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;

public class EXWCoreDummyContainer extends DummyModContainer {
    public static final String VERSION = "@VERSION@";

    private URL updateJSONUrl;

    public EXWCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "exwc";
        meta.name = "Expansive Weaponry Core";
        meta.version = VERSION;
        meta.credits = "";
        meta.authorList = Arrays.asList("Unknown025", "MKVIIGTI");
        meta.description = "Provides core functionality for Expansive Weaponry.";
        meta.url = "https://rainyville.org/expansive-weaponry/";
        meta.screenshots = new String[0];
        meta.logoFile = "assets/exw/textures/logo.png";
        meta.dependants = new ArrayList<>();
        meta.childMods = new ArrayList<>();

        try {
            // Expose current version
            updateJSONUrl = new URL("https://rainyville.org/exwc/update.json?version=" + VERSION);
        } catch (MalformedURLException e) {
            updateJSONUrl = null;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
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
}
