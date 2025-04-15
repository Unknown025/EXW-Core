package org.rainyville.exw.core;

@SuppressWarnings("unused")
public class CorrectedFrustum {
    private static float dot(float[] array, float x, float y, float z) {
        return array[0] * x + array[1] * y + array[2] * z + array[3];
    }

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public static boolean isBoxInFrustum(float[][] frustum, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float mX = (float) minX;
        float mY = (float) minY;
        float mZ = (float) minZ;
        float mxX = (float) maxX;
        float mxY = (float) maxY;
        float mxZ = (float) maxZ;

        for (int i = 0; i < 6; ++i) {
            float[] row = frustum[i];

            if (dot(row, mX, mY, mZ) <= 0.0F   && dot(row, mxX, mY, mZ) <= 0.0F
             && dot(row, mX, mxY, mZ) <= 0.0F  && dot(row, mxX, mxY, mZ) <= 0.0F
             && dot(row, mX, mY, mxZ) <= 0.0F  && dot(row, mxX, mY, mxZ) <= 0.0F
             && dot(row, mX, mxY, mxZ) <= 0.0F && dot(row, mxX, mxY, mxZ) <= 0.0F) {
                return false;
            }
        }

        return true;
    }
}
