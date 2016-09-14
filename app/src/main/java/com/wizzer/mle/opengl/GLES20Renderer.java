package com.wizzer.mle.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLES20Renderer extends GLRenderer
{
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms
     * world space to eye space; it positions things relative to our eye.
     */
    private float[] m_ViewMatrix = new float[16];
    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] m_ProjectionMatrix = new float[16];

    @Override
    public void onSurfaceCreated()
    {
         // Do nothing for now.
    }

    @Override
    public void onSurfaceChanged(int width, int height, boolean contextLost)
    {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        // ToDo: Move this camera implementation to the Mle3dSet.

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance.
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(m_ViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        // Set the projection matrix.
        Matrix.frustumM(m_ProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(boolean firstDraw)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
