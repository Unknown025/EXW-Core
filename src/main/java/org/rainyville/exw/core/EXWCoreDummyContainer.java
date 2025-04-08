package org.rainyville.exw.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class EXWCoreDummyContainer extends DummyModContainer {
    public static final String VERSION = "@VERSION@";

    public EXWCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "exw_core";
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
}
