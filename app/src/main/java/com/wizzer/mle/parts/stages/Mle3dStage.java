package com.wizzer.mle.parts.stages;

import com.wizzer.mle.parts.j3d.MleJ3dPlatformData;
import com.wizzer.mle.parts.j3d.MleRenderEngine;
import com.wizzer.mle.parts.j3d.stages.I3dStage;
import com.wizzer.mle.runtime.MleTitle;
import com.wizzer.mle.runtime.core.MleRuntimeException;
import com.wizzer.mle.runtime.core.MleSet;
import com.wizzer.mle.runtime.core.MleStage;
import com.wizzer.mle.runtime.scheduler.MleTask;

import android.content.Context;

/**
 * This class implements a simple Stage for 3D applications.
 *
 * @author Mark S. Millard

 */
public class Mle3dStage extends MleStage implements I3dStage
{
    private String DEFAULT_STAGE_NAME = "MLE 3D Stage";  /* The default stage name. */

    /** The application <code>GLSurfaceView</code> for the stage. */
    public MleGLES20ApplicationView m_windowView;


    // true when stage is ready for rendering.
    private boolean m_ready;

    /**
     * The default constructor.
     */
    public Mle3dStage()
    {
        super();

        // Not ready for rendering.
        m_ready = false;

        // Create the window component.
        Context context = ((MleJ3dPlatformData)(MleTitle.getInstance().m_platformData)).m_context;
        //m_windowView = new MleGLES20ApplicationView(context,DEFAULT_STAGE_NAME,width,height);
        m_windowView = new MleGLES20ApplicationView(context);

        // Set the stage.
        g_theStage = this;
    }

    /**
     * Registers a new Set with this Stage.
     *
     * @param renderer The rendering thread for the Set being registered.
     * @param set The Set to register.
     *
     * @return A reference to the scheduled renderer task is returned.
     */
    @Override
    public MleTask addSet(MleRenderEngine renderer, MleSet set)
    {
        MleTask item = new MleTask(renderer);
        renderer.setCallData(this);
        renderer.setClientData(set);

        // Just pass this function on to the scheduler.
        MleTitle.getInstance().m_theScheduler.addTask(MleTitle.g_theSetPhase,item);

        return item;
    }

    /**
     * Initialize the stage.
     *
     * @throws MleRuntimeException This exception is thrown if the
     * stage can not be successfully initialized.
     */
    @Override
    public synchronized void init() throws MleRuntimeException
    {
        // ToDo: Insert resize callback into event dispatch manager.
        // ToDo: Bump priority of dispatched callback.
        // ToDo: Insert stage blitter (or off screen renderer if required) into the scheduler.
        // ToDo: Show the window view.
        // ToDO: Call local resize() to explicitly create the off screen buffer and pixelmaps.
        //       This may be already handled by GLSurfaceView
    }

    /**
     * Clean up the Stage and dispose of resources.
     */
    @Override
    public void dispose()
            throws MleRuntimeException
    {
        // Disable rendering.
        m_ready = false;

        // Dispose of UI resources.
        m_windowView.dispose();
    }

    /**
     * Resume the stage from a paused state.
     */
    public synchronized void resume()
    {
        m_windowView.onResume();

        // Enable rendering.
        m_ready = true;
    }

    /**
     * Pause the stage.
     */
    public synchronized void pause()
    {
        m_windowView.onPause();

        // Disable rendering.
        m_ready = false;
    }
}
