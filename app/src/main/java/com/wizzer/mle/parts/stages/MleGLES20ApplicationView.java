package com.wizzer.mle.parts.stages;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.wizzer.mle.opengl.GLES20Renderer;

/**
 * This class is a utility for creating a <code>GLSurfaceView</code> in
 * which a Magic Lantern application can render into.
 *
 * @author Mark S. Millard
 */
public class MleGLES20ApplicationView extends GLSurfaceView
{
    /** The OpenGL ES renderer handle. */
    public GLES20Renderer m_renderer;

    /** The name of the <code>GLSurfaceView</code>. */
    protected String m_name;

    /**
     * The default constructor.
     *
     * @param context An interface to global information about an application environment.
     */
    public MleGLES20ApplicationView(Context context)
    {
        super(context);

        m_name = "MleGLES20ApplicationView v1.0";

        // Initialize the View behavior.
        init();
    }

    /**
     * Initializes the user interface behavior.
     */
    protected void init()
    {
        // Set EGL behavior.
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        // Set OpenGL renderer.
        m_renderer = new GLES20Renderer();
        setRenderer(m_renderer);
    }

    /**
     * Dispose of system resources.
     */
    public void dispose()
    {
        // Does nothing for now.
    }
}
