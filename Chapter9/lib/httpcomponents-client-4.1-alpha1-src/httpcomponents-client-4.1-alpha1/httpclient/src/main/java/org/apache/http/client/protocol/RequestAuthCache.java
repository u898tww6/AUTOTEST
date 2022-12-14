/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.client.protocol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * Request interceptor that can preemptively authenticate against known hosts, 
 * if there is a cached {@link AuthScheme} instance in the local 
 * {@link AuthCache} associated with the given target or proxy host.
 * 
 * @since 4.1
 */
@Immutable
public class RequestAuthCache implements HttpRequestInterceptor {

    private final Log log = LogFactory.getLog(getClass());
    
    public RequestAuthCache() {
        super();
    }
    
    public void process(final HttpRequest request, final HttpContext context) 
            throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }

        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (authCache == null) {
            context.setAttribute(ClientContext.AUTH_CACHE, new BasicAuthCache());
        } else {
            
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                    ClientContext.CREDS_PROVIDER);
            
            HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if (target != null) {
                AuthScheme authScheme = authCache.get(target);
                if (authScheme != null) {
                    doPreemptiveAuth(
                            target, 
                            authScheme,                     
                            (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE), 
                            credsProvider);
                }
            }

            HttpHost proxy = (HttpHost) context.getAttribute(ExecutionContext.HTTP_PROXY_HOST);
            if (proxy != null) {
                AuthScheme authScheme = authCache.get(proxy);
                if (authScheme != null) {
                    doPreemptiveAuth(
                            proxy, 
                            authScheme,                     
                            (AuthState) context.getAttribute(ClientContext.PROXY_AUTH_STATE), 
                            credsProvider);
                }
            }
        }
    }

    private void doPreemptiveAuth(
            final HttpHost host, 
            final AuthScheme authScheme, 
            final AuthState authState,
            final CredentialsProvider credsProvider) {
        String schemeName = authScheme.getSchemeName();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Re-using cached '" + schemeName + "' auth scheme for " + host);
        }
        
        Credentials creds = credsProvider.getCredentials(
                new AuthScope(host.getHostName(), host.getPort(), schemeName));
        
        if (creds != null) {
            authState.setAuthScheme(authScheme);
            authState.setCredentials(creds);
        } else {
            this.log.debug("No credentials for preemptive authentication");
        }
    }
    
}
