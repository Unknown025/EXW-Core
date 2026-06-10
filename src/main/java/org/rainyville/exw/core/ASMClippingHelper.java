package org.rainyville.exw.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.rainyville.exw.core.api.ASMHelper;
import org.rainyville.exw.core.api.IPatcher;
import org.rainyville.exw.core.api.ObfuscationHelper;

import static scala.tools.asm.Opcodes.*;

/**
 * This is a workaround for a Mojang bug. Not currently implemented.
 *
 * @see <a href="https://bugs.mojang.com/browse/MC/issues/MC-63020">Mojira link</a>
 */
public class ASMClippingHelper implements IPatcher {
    private boolean patched = false;
    //TODO: Find obfuscated field names
    private static final String[] IS_BOX_IN_FRUSTUM = {"isBoxInFrustum"};
    private static final String[] FRUSTUM = {"frustum"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (ASMHelper.methodEquals(methodNode, IS_BOX_IN_FRUSTUM, ObfuscationHelper.createMethodDescriptor(EXWClassTransformer.OBFUSCATED, "Z", "D", "D", "D", "D", "D", "D"))) {
                methodNode.instructions.clear();

                InsnList insertMethod = new InsnList();
                insertMethod.add(new VarInsnNode(ALOAD, 0));
                insertMethod.add(new VarInsnNode(ALOAD, 0));
                insertMethod.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/renderer/culling/ClippingHelper", "frustum", "[[F"));
                insertMethod.add(new VarInsnNode(DLOAD, 1));
                insertMethod.add(new VarInsnNode(DLOAD, 3));
                insertMethod.add(new VarInsnNode(DLOAD, 5));
                insertMethod.add(new VarInsnNode(DLOAD, 7));
                insertMethod.add(new VarInsnNode(DLOAD, 9));
                insertMethod.add(new VarInsnNode(DLOAD, 11));
                insertMethod.add(new MethodInsnNode(INVOKESTATIC, "org/rainyville/exw/core/CorrectedFrustum", "isBoxInFrustum", "([[FDDDDDD)Z", false));
                insertMethod.add(new InsnNode(IRETURN));

                methodNode.instructions.insert(insertMethod);
                patched = true;
                break;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean patched() {
        return patched;
    }

    public boolean test() {
        return test2(patched);
    }

    boolean test2(boolean patched) {
        return true;
    }
}
