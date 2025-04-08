package org.rainyville.exw.core.api;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rainyville.exw.core.EXWClassTransformer;
import org.rainyville.exw.core.EXWLoadingPlugin;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class ASMHelper {
    public static boolean methodEquals(MethodNode methodNode, String[] names, String desc) {
        boolean nameMatches = false;

        for (String name : names) {
            if (methodNode.name.equals(name)) {
                nameMatches = true;
                break;
            }
        }

        return nameMatches && methodNode.desc.equals(desc);
    }

    public static boolean fieldEquals(FieldInsnNode fieldNode, String[] names, String desc) {
        boolean nameMatches = false;

        for (String name : names) {
            if (fieldNode.name.equals(name)) {
                nameMatches = true;
                break;
            }
        }

        return nameMatches && fieldNode.desc.equals(desc);
    }

    public static boolean methodInsnEquals(MethodInsnNode methodInsnNode, String[] names, String desc) {
        boolean nameMatches = false;

        for (String name : names) {
            if (methodInsnNode.name.equals(name)) {
                nameMatches = true;
                break;
            }
        }

        return nameMatches && methodInsnNode.desc.equals(desc);
    }

    /**
     * Write class to file for debugging.
     *
     * @param writer ClassWriter containing transformed class.
     * @param name   Class name.
     */
    public static void writeToFile(ClassWriter writer, String name) {
        if (EXWClassTransformer.OBFUSCATED) return;

        try {
            FileUtils.writeByteArrayToFile(new File(name.split("\\.")[name.split("\\.").length - 1] + ".class"), writer.toByteArray());
        } catch (IOException e) {
            EXWLoadingPlugin.LOGGER.error(e);
        }
    }
}
