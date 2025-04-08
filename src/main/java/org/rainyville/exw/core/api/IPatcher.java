package org.rainyville.exw.core.api;

public interface IPatcher {
    /**
     * Called from the ClassTransformer to apply a transformation.
     *
     * @param name            Name of the class.
     * @param transformedName Transformed name (often unobfuscated).
     * @param basicClass      Byte representation of this class.
     * @return The transformed class.
     */
    byte[] transform(String name, String transformedName, byte[] basicClass);

    /**
     * Whether this patch is required for the game to work.
     *
     * @return Whether the game should crash if the patcher did not exit successfully.
     */
    boolean isRequired();

    /**
     * Whether the patcher ran successfully.
     *
     * @return Patch success.
     */
    boolean patched();
}
