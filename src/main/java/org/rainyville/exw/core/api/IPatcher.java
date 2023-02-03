package org.rainyville.exw.core.api;

public interface IPatcher {
    byte[] transform(String name, String transformedName, byte[] basicClass);

    boolean isRequired();

    boolean patched();
}
