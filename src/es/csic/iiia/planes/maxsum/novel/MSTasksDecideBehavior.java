/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2012 Marc Pujol <mpujol@iiia.csic.es>.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.planes.maxsum.novel;

import es.csic.iiia.planes.maxsum.*;
import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.behaviors.AbstractBehavior;
import es.csic.iiia.planes.MessagingAgent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Behavior that implements the decisions of tasks.
 * <p/>
 * Using this behavior, the decision made by {@link MSTaskNode}s are translated
 * into actual movements of tasks between the planes.
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
public class MSTasksDecideBehavior extends AbstractBehavior {
    private static final Logger LOG = Logger.getLogger(MSTasksDecideBehavior.class.getName());

    public MSTasksDecideBehavior(MessagingAgent agent) {
        super(agent);
    }

    @Override
    public Class[] getDependencies() {
        return new Class[]{MSExecutionBehavior.class};
    }

    @Override
    public boolean isPromiscuous() {
        return false;
    }

    @Override
    public MSPlane getAgent() {
        return (MSPlane)super.getAgent();
    }

    @Override
    public void beforeMessages() {

    }

    /**
     * Receive a task from a neighboring plane.
     *
     * @param msg message hading a task to us.
     */
    public void on(HandTaskMessage msg) {
        getAgent().addTask(msg.getTask());
        LOG.log(Level.FINER, "[{2}] {0} incorporates {1}",
                new Object[]{getAgent(), msg.getTask(), getAgent().getWorld().getTime()});
    }

    /**
     * Implements the decisions made by the {@link MSTaskNode}s currently
     * running whithin this plane.
     * <p/>
     * That is, it checks the plane preferred by each task, and hands them out
     * to their preferred planes if they do not match the current one.
     */
    @Override
    public void afterMessages() {
        final long remainder = getAgent().getWorld().getTime() % getConfiguration().getMsStartEvery();
        if (getAgent().isInactive() || remainder != getConfiguration().getMsIterations()) {
            return;
        }

        final MSPlane p = getAgent();

        // And now the tasks choose
        List<Task> tasks = p.getTasks();
        for (int i=tasks.size()-1; i>=0; i--) {
            final Task t = tasks.get(i);
            final MSTaskNode f = p.getTaskFunction(t);
            final MSPlane choice = f.makeDecision();
            LOG.log(Level.FINE, "[{2}] {0} chooses {1} (inside {3})", new Object[]{f, choice, getAgent().getWorld().getTime(), getAgent()});
            if (choice != p && choice != null) {
                relocateTask(t, choice);
            }
        }

    }

    /**
     * Send a task to a neighbor.
     *
     * @param t task being sent.
     * @param choice plane that will be the new owner of the given task.
     */
    private void relocateTask(Task t, MSPlane choice) {
        getAgent().removeTask(t);
        HandTaskMessage msg = new HandTaskMessage(t);
        msg.setRecipient(choice);
        getAgent().send(msg);
    }

}