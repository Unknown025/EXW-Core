package org.rainyville.exw.core.api;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

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
}
