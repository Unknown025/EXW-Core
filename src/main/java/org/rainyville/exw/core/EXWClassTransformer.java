package org.rainyville.exw.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.rainyville.exw.core.api.IPatcher;

import java.util.HashMap;

public class EXWClassTransformer implements IClassTransformer {
    public static final boolean OBFUSCATED = !(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    private static final HashMap<String, IPatcher> patchers = new HashMap<>();

    public EXWClassTransformer() {
        EXWLoadingPlugin.LOGGER.info("Running in {} mode", OBFUSCATED ? "obfuscated" : "deobfuscated");

        patchers.put("net.minecraft.client.renderer.EntityRenderer", new ASMEntityRenderer());
        patchers.put("net.minecraft.client.Minecraft", new ASMMinecraft());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (patchers.containsKey(transformedName)) {
            IPatcher patcher = patchers.get(transformedName);

            EXWLoadingPlugin.LOGGER.info("Patching: {} ({})", name, transformedName);
            basicClass = patcher.transform(name, transformedName, basicClass);

            if (!patcher.patched()) {
                EXWLoadingPlugin.LOGGER.error("Failed to patch: {}", name);
                if (patcher.isRequired())
                    throw new RuntimeException("Required patcher " + transformedName + " did not exit successfully!");
            } else {
                EXWLoadingPlugin.LOGGER.info("Patched: {}", name);
            }
            return basicClass;
        }

        return basicClass;
    }
}
