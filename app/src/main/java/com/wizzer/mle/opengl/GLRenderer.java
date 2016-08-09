package com.wizzer.mle.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public abstract class GLRenderer implements Renderer
{
    private boolean m_FirstDraw;
    private boolean m_SurfaceCreated;
    private int m_Width;
    private int m_Height;
    private long m_LastTime;
    private int m_FPS;

    public GLRenderer()
    {
        m_FirstDraw = true;
        m_SurfaceCreated = false;
        m_Width = -1;
        m_Height = -1;
        m_LastTime = System.currentTimeMillis();
        m_FPS = 0;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed,
                                 EGLConfig config)
    {
        if (GLES20Debug.DEBUG) {
            Log.i(GLES20Debug.LOG_TAG, "Surface created.");
        }
        m_SurfaceCreated = true;
        m_Width = -1;
        m_Height = -1;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width,
                                 int height)
    {
        if (!m_SurfaceCreated && width == m_Width
                && height == m_Height) {
            if (GLES20Debug.DEBUG) {
                Log.i(GLES20Debug.LOG_TAG,
                        "Surface changed but already handled.");
            }
            return;
        }
        if (GLES20Debug.DEBUG) {
            // Android honeycomb has an option to keep the
            // context.
            String msg = "Surface changed width:" + width
                    + " height:" + height;
            if (m_SurfaceCreated) {
                msg += " context lost.";
            } else {
                msg += ".";
            }
            Log.i(GLES20Debug.LOG_TAG, msg);
        }

        m_Width = width;
        m_Height = height;

        onCreate(m_Width, m_Height, m_SurfaceCreated);
        m_SurfaceCreated = false;
    }

    @Override
    public void onDrawFrame(GL10 notUsed)
    {
        onDrawFrame(m_FirstDraw);

        if (GLES20Debug.DEBUG) {
            m_FPS++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - m_LastTime >= 1000) {
                m_FPS = 0;
                m_LastTime = currentTime;
            }
        }

        if (m_FirstDraw) {
            m_FirstDraw = false;
        }
    }

    public int getFPS()
    {
        return m_FPS;
    }

    public abstract void onCreate(int width, int height,
                                  boolean contextLost);

    public abstract void onDrawFrame(boolean firstDraw);
}