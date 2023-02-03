package org.rainyville.exw.core.api;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ObfuscationHelper {
    public static String createMethodDescriptor(boolean obfuscated, String returnType, String... types) {
        StringBuilder result = new StringBuilder("(");

        for (String type : types) {
            if (type.length() == 1)
                result.append(type);
            else {
                result.append("L").append(obfuscated ? FMLDeobfuscatingRemapper.INSTANCE.unmap(type) : type).append(";");
            }
        }

        if (returnType.length() > 1) {
            returnType = "L" + unmapType(obfuscated, returnType) + ";";
        }

        result.append(")").append(returnType);

        return result.toString();
    }

    public static String unmapType(boolean obfuscated, String type) {
        return obfuscated ? FMLDeobfuscatingRemapper.INSTANCE.unmap(type) : type;
    }
}
