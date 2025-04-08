package org.rainyville.exw.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.rainyville.exw.core.api.ASMHelper;
import org.rainyville.exw.core.api.IPatcher;
import org.rainyville.exw.core.api.ObfuscationHelper;

import java.util.ListIterator;
import java.util.Objects;

public class ASMEntityRenderer implements IPatcher {
    private boolean patchedOrientCamera = false;
    private boolean patchedUpdateRenderer = false;
    private static final String[] ORIENT_CAMERA = {"orientCamera", "func_78467_g", "f"};
    private static final String[] UPDATE_RENDERER = {"updateRenderer", "func_78464_a", "e"};
    private static final String[] THIRD_PERSON_PREV_NAMES = {"thirdPersonDistancePrev", "field_78491_C", "r"};
    private static final String[] THIRD_PERSON_NAMES = {"thirdPersonDistance", "field_78490_B", "q"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (ASMHelper.methodEquals(methodNode, UPDATE_RENDERER, ObfuscationHelper.createMethodDescriptor(EXWClassTransformer.OBFUSCATED, "V"))) {
                for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode node = it.next();

                    // If the current node is thirdPersonDistancePrev and the following is a float of 4.0F
                    if (node.getOpcode() == Opcodes.PUTFIELD && ASMHelper.fieldEquals(((FieldInsnNode) node), THIRD_PERSON_PREV_NAMES, "F")
                            && node.getPrevious().getOpcode() == Opcodes.LDC && Objects.equals(((LdcInsnNode) node.getPrevious()).cst, 4.0F)) {
                        AbstractInsnNode replace = node.getPrevious();
                        methodNode.instructions.insertBefore(replace, new VarInsnNode(Opcodes.ALOAD, 0));
                        methodNode.instructions.insertBefore(replace, new FieldInsnNode(Opcodes.GETFIELD, name.replace(".", "/"),
                                EXWClassTransformer.OBFUSCATED ? THIRD_PERSON_NAMES[1] : THIRD_PERSON_NAMES[0], "F"));
                        methodNode.instructions.remove(replace);

                        patchedUpdateRenderer = true;
                        break;
                    }
                }
            }
            if (ASMHelper.methodEquals(methodNode, ORIENT_CAMERA, ObfuscationHelper.createMethodDescriptor(EXWClassTransformer.OBFUSCATED, "V", "F"))) {
                for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode node = it.next();

                    // If the current node is thirdPersonDistancePrev and the following is a float of 4.0F
                    if (node.getOpcode() == Opcodes.GETFIELD && ASMHelper.fieldEquals(((FieldInsnNode) node), THIRD_PERSON_PREV_NAMES, "F")
                            && node.getNext().getOpcode() == Opcodes.LDC && Objects.equals(((LdcInsnNode) node.getNext()).cst, 4.0F)) {
                        AbstractInsnNode replace = node.getNext();
                        methodNode.instructions.insertBefore(replace, new VarInsnNode(Opcodes.ALOAD, 0));
                        methodNode.instructions.insertBefore(replace, new FieldInsnNode(Opcodes.GETFIELD, name.replace(".", "/"),
                                EXWClassTransformer.OBFUSCATED ? THIRD_PERSON_NAMES[1] : THIRD_PERSON_NAMES[0], "F"));
                        methodNode.instructions.remove(replace);

                        patchedOrientCamera = true;
                        break;
                    }
                }

                break;
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
        return patchedOrientCamera && patchedUpdateRenderer;
    }
}
