/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2013 Expression application is undefined on line 6, column 57 in Templates/Licenses/license-bsd.txt..
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
 *   Neither the name of Expression application is undefined on line 21, column 41 in Templates/Licenses/license-bsd.txt.
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   Expression application is undefined on line 25, column 21 in Templates/Licenses/license-bsd.txt.
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

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.maxsum.algo.CostFactor;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marc Pujol <mpujol at iiia.csic.es>
 */
public class MSPlaneNode {

    private CostFactor factor;
    private MSPlane plane;
    private Map<Task, ProxyFactor<MSPlane, Task>> proxies = new HashMap<Task, ProxyFactor<MSPlane, Task>>();

    public MSPlaneNode(MSPlane plane) {
        factor = new CostFactor();
        this.plane = plane;
    }

    public void addNeighbor(Task t, MSPlane location) {
        ProxyFactor<MSPlane, Task> proxy = new ProxyFactor<MSPlane, Task>(plane, t, plane, location);
        factor.addNeighbor(proxy);
        factor.setPotential(proxy, plane.getCost(t));
        proxies.put(t, proxy);
    }

    public void clearNeighbors() {
        proxies.clear();
        factor.getNeighbors().clear();
        factor.clearPotentials();
    }

    public void removeNeighbor(Task t) {
        ProxyFactor<MSPlane, Task> proxy = proxies.remove(t);
        factor.removePotential(proxy);
    }

    public void receive(MSMessage<Task, MSPlane> message) {
        proxies.get(message.getLogicalSender()).receive(message);
    }

    public void run() {
        factor.tick();
        factor.run();
    }



}