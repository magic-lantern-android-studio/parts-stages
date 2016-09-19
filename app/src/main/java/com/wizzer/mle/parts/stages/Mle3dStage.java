package com.wizzer.mle.parts.stages;

import com.wizzer.mle.parts.j3d.MleJ3dPlatformData;
import com.wizzer.mle.parts.j3d.MleRenderEngine;
import com.wizzer.mle.parts.j3d.stages.I3dStage;
import com.wizzer.mle.runtime.MleTitle;
import com.wizzer.mle.runtime.core.IMleCallbackId;
import com.wizzer.mle.runtime.core.MleRuntimeException;
import com.wizzer.mle.runtime.core.MleSet;
import com.wizzer.mle.runtime.core.MleSize;
import com.wizzer.mle.runtime.core.MleStage;
import com.wizzer.mle.runtime.event.MleEvent;
import com.wizzer.mle.runtime.event.MleEventCallback;
import com.wizzer.mle.runtime.event.MleEventManager;
import com.wizzer.mle.runtime.scheduler.MleTask;

import android.content.Context;

import java.util.Vector;

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

    /** Registry of sets. */
    public Vector<MleSet> m_sets;

    /** Size of stage. */
    protected MleSize m_size = null;

    // true when stage is ready for rendering.
    private boolean m_ready;

    /**
     * This inner class is used to process resize events.
     */
    protected class Mle3dStageResizeCallback extends MleEventCallback
    {
        /**
         * The default constructor.
         */
        public Mle3dStageResizeCallback() {
            super();
            // Do nothing extra.
        }

        /**
         * The callback dispatch method. This method is responsible for
         * handling the <i>resize</i> event.
         *
         * @param event      The event that is being dispatched by the handler.
         * @param clientData Client data registered with this handler.
         * @return If the event is successfully dispatched, then <b>true</b> should be
         * returned. Otherwise, <b>false</b> should be returned.
         * @see com.wizzer.mle.runtime.event.IMleEventCallback#dispatch(com.wizzer.mle.runtime.event.MleEvent, java.lang.Object)
         */
        public boolean dispatch(MleEvent event, Object clientData) {
            boolean result = true;

            // Cast the client data to the stage being resized.
            Mle3dStage theStage = (Mle3dStage) clientData;
            // Get the width and height from the event call data.
            MleSize newSize = (MleSize) event.getCallData();

            try
            {
                theStage.resize((int)newSize.getWidth(), (int)newSize.getHeight());
            } catch (MleRuntimeException ex)
            {
                result = false;
            }

            return result;
        }
    }

    /**
     * This inner class is used to render the stage.
     */
    protected class Mle3dStageRenderTask extends Thread
    {
        // The stage.
        private Mle3dStage m_stage = null;

        /**
         * A constructor that initializes the stage to update
         * by calling the associated GLSurfaceView.
         */
        public Mle3dStageRenderTask(Mle3dStage stage)
        {
            super();
            m_stage = stage;
        }

        /**
         * Execute the scene graph rendering.
         */
        public void run()
        {
            // Skip this phase if the stage has not yet been established.
            if (m_stage == null)
                return;

            // Skip this phase if the GLSurfaceView is currently not ready for rendering.
            if (! m_stage.m_ready)
                return;

            // Call the GLSurfaceView to perform the render.
            m_stage.m_windowView.render();
        }
    }

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
        // And set the stage on the renderer.
        m_windowView.m_renderer.setTheStage(this);

        // Create a registry of sets that can be controlled and managed by the stage.
        m_sets = new Vector<MleSet>();

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
        // Create a task for performing any rendering activity during the set phase.
        MleTask item = new MleTask(renderer);
        renderer.setCallData(this);
        renderer.setClientData(set);

        // Just pass this function on to the scheduler.
        MleTitle.getInstance().m_theScheduler.addTask(MleTitle.g_theSetPhase,item);

        // Add the set to the registry.
        m_sets.add(set);

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
        // Declare local variables.
        IMleCallbackId cbId;

        // Insert resize callback into event dispatch manager.
        Mle3dStageResizeCallback resizeEventCB = new Mle3dStageResizeCallback();
        cbId = MleTitle.getInstance().m_theDispatcher.installEventCB(
                MleEventManager.MLE_SIZE, resizeEventCB, this);

        // Bump priority of dispatched callback.
        MleTitle.getInstance().m_theDispatcher.changeCBPriority(
                MleEventManager.MLE_SIZE,cbId,
                MleEventManager.MLE_RESIZE_STAGE_PRIORITY);

        // Insert stage render task into the scheduler.
        Mle3dStageRenderTask render = new Mle3dStageRenderTask(this);
        MleTask item = new MleTask(render, "3D Stage Renderer");
        MleTitle.getInstance().m_theScheduler.addTask(MleTitle.g_theStagePhase,item);

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

    /**
     * Get the size of the stage.
     *
     * @return The size of the stage's component is returned.
     * Magic Lantern 1.0 supports one component per stage: this is the
     * default component size.
     */
    public synchronized MleSize getSize()
    {
        return m_size;
    }

    /**
     * Resize the stage.
     *
     * @param width The new width of the stage.
     * @param height The new height of the stage.
     * @throws MleRuntimeException This exception is thrown if the stage can
     * not be resized.
     */
    public synchronized void resize(int width, int height)
        throws MleRuntimeException
    {
        // Not ready for rendering.
        m_ready = false;

        // Store new values.
        m_size = new MleSize(width, height);

        // Note: sets should receive their own notification on a resize event.
        // Therefore the stage does not need to call a set's resize handler.

        // Ready for rendering.
        m_ready = true;
    }
}
