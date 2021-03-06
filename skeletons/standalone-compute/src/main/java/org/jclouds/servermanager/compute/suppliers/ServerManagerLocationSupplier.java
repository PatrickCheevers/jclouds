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

package org.jclouds.servermanager.compute.suppliers;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rest.annotations.Provider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerManagerLocationSupplier implements Supplier<Set<? extends Location>> {

   private final String providerName;

   @Inject
   ServerManagerLocationSupplier(@Provider String providerName) {
      this.providerName = providerName;
   }

   @Override
   public Set<? extends Location> get() {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      return ImmutableSet.of(new LocationImpl(LocationScope.ZONE, "1", "SFO", provider));
   }
}