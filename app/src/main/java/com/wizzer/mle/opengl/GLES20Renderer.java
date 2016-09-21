package com.wizzer.mle.opengl;

import android.opengl.GLES20;
import android.util.Log;

import com.wizzer.mle.parts.stages.Mle3dStage;
import com.wizzer.mle.runtime.MleTitle;
import com.wizzer.mle.runtime.core.MleRuntimeException;
import com.wizzer.mle.runtime.core.MleSet;
import com.wizzer.mle.runtime.core.MleSize;
import com.wizzer.mle.runtime.event.IMleEventCallback;
import com.wizzer.mle.runtime.event.MleEventManager;

public class GLES20Renderer extends GLRenderer
{
    /* The associated 3D stage. */
    private Mle3dStage m_theStage;

    public synchronized void setTheStage(Mle3dStage stage)
    { m_theStage = stage; }

    @Override
    public void onSurfaceCreated()
    {
         m_theStage.setReady(true);
    }

    @Override
    public void onSurfaceChanged(int width, int height, boolean contextLost)
    {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        // A resize event occurred. Dispatch the resize event callbacks in immediate mode.
        MleSize callData = new MleSize(width, height);
        MleTitle.getInstance().m_theDispatcher.processEvent(MleEventManager.MLE_SIZE,
            callData, IMleEventCallback.MLE_EVENT_IMMEDIATE);
    }

    @Override
    public void onDrawFrame(boolean firstDraw)
    {
        if ((m_theStage == null) || (! m_theStage.isReady())) return;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Invoke the sets on the stage to render themselves.
        for (int i  = 0; i < m_theStage.m_sets.size(); i++)
        {
            MleSet nextSet = m_theStage.m_sets.elementAt(i);

            try {
                // If the first time rendering, then initialize the set.
                if (firstDraw)
                    nextSet.initRender();

                // Tell the set to render itself.
                nextSet.render();
            } catch (MleRuntimeException ex)
            {
                Log.e(MleTitle.DEBUG_TAG, ex.getMessage());
            }
        }
    }
}
