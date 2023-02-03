package org.rainyville.exw.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

public class EXWLoadingPlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("EXW Core");

    public static File COREMOD_LOCATION;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{EXWClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return EXWCoreDummyContainer.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        COREMOD_LOCATION = (File) data.get("coremodLocation");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
