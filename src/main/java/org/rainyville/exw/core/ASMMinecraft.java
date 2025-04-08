package org.rainyville.exw.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.rainyville.exw.core.api.ASMHelper;
import org.rainyville.exw.core.api.IPatcher;
import org.rainyville.exw.core.api.ObfuscationHelper;

import java.util.ListIterator;

import static scala.tools.asm.Opcodes.*;

public class ASMMinecraft implements IPatcher {
    private boolean patchedDisplayCrashReport = false;
    private static final String[] DISPLAY_CRASH_REPORT = {"displayCrashReport", "func_71377_b", "c"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (ASMHelper.methodEquals(methodNode, DISPLAY_CRASH_REPORT, ObfuscationHelper.createMethodDescriptor(EXWClassTransformer.OBFUSCATED, "V", "net/minecraft/crash/CrashReport"))) {
                InsnList injectMethod = new InsnList();
                injectMethod.add(new VarInsnNode(ALOAD, 1));
                injectMethod.add(new MethodInsnNode(INVOKESTATIC, "org/rainyville/exw/core/CrashReporter",
                        "onCrash", ObfuscationHelper.createMethodDescriptor(EXWClassTransformer.OBFUSCATED,
                        "V", "net/minecraft/crash/CrashReport"), false));

                for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode node = it.next();
                    if (node instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                        if (methodInsnNode.name.equals("instance")) {
                            methodNode.instructions.insertBefore(node, injectMethod);
                            patchedDisplayCrashReport = true;
                            break;
                        }
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean patched() {
        return patchedDisplayCrashReport;
    }
}
