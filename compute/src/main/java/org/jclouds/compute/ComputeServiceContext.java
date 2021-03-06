/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute;

import java.util.Map;

import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.rest.RestContext;

import com.google.common.annotations.Beta;
import com.google.inject.ImplementedBy;

/**
 * Represents a cloud that has compute functionality.
 * 
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(ComputeServiceContextImpl.class)
public interface ComputeServiceContext {

   ComputeService getComputeService();

   /**
    * 
    * @return null, if the cloud does not support load balancer services
    */
   LoadBalancerService getLoadBalancerService();

   <S, A> RestContext<S, A> getProviderSpecificContext();

   /**
    * retrieves a list of credentials for resources created within this context, keyed on {@code id}
    * of the resource. We are testing this approach for resources such as compute nodes, where you
    * could access this externally.
    * 
    */
   @Beta
   Map<String, Credentials> getCredentialStore();

   @Beta
   Map<String, Credentials> credentialStore();

   Utils getUtils();

   /**
    * @see #getUtils
    */
   Utils utils();

   void close();
}