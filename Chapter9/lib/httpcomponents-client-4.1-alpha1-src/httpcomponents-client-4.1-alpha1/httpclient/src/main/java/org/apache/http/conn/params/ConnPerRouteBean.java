/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.conn.params;

import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.routing.HttpRoute;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a map of HTTP routes to maximum number of connections allowed 
 * for those routes. This class can be used by pooling 
 * {@link org.apache.http.conn.ClientConnectionManager connection managers} for 
 * a fine-grained control of connections on a per route basis. 
 * 
 * @since 4.0
 */
@ThreadSafe
public final class ConnPerRouteBean implements ConnPerRoute {

    /** The default maximum number of connections allowed per host */
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;   // Per RFC 2616 sec 8.1.4
    
    @GuardedBy("this")
    private final Map<HttpRoute, Integer> maxPerHostMap;
    
    @GuardedBy("this")
    private int defaultMax;
    
    public ConnPerRouteBean(int defaultMax) {
        super();
        this.maxPerHostMap = new HashMap<HttpRoute, Integer>();
        setDefaultMaxPerRoute(defaultMax);
    }
    
    public ConnPerRouteBean() {
        this(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
    }
    
    @Deprecated
    public synchronized int getDefaultMax() {
        return this.defaultMax;
    }

    /**
     * @since 4.1
     */
    public synchronized int getDefaultMaxPerRoute() {
        return this.defaultMax;
    }

    public synchronized void setDefaultMaxPerRoute(int max) {
        if (max < 1) {
            throw new IllegalArgumentException
                ("The maximum must be greater than 0.");
        }
        this.defaultMax = max;
    }

    public synchronized void setMaxForRoute(final HttpRoute route, int max) {
        if (route == null) {
            throw new IllegalArgumentException
                ("HTTP route may not be null.");
        }
        if (max < 1) {
            throw new IllegalArgumentException
                ("The maximum must be greater than 0.");
        }
        this.maxPerHostMap.put(route, Integer.valueOf(max));
    }

    public synchronized int getMaxForRoute(final HttpRoute route) {
        if (route == null) {
            throw new IllegalArgumentException
                ("HTTP route may not be null.");
        }
        Integer max = this.maxPerHostMap.get(route);
        if (max != null) {
            return max.intValue();
        } else {
            return this.defaultMax;
        }
    }
    
    public synchronized void setMaxForRoutes(final Map<HttpRoute, Integer> map) {
        if (map == null) {
            return;
        }
        this.maxPerHostMap.clear();
        this.maxPerHostMap.putAll(map);
    }

    @Override
    public synchronized String toString() {
        return this.maxPerHostMap.toString();
    }
    
}
