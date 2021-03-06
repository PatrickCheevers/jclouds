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

package org.jclouds.rackspace.cloudservers.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Set;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersAddNodeWithTagStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersDestroyNodeStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersGetNodeMetadataStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersListNodesStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersRebootNodeStrategy;
import org.jclouds.rackspace.cloudservers.compute.suppliers.CloudServersHardwareSupplier;
import org.jclouds.rackspace.cloudservers.compute.suppliers.CloudServersImageSupplier;
import org.jclouds.rackspace.config.RackspaceLocationsSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Injector;

/**
 * Configures the {@link CloudServersComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new CloudServersComputeServiceDependenciesModule());
      super.configure();
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).imageNameMatches(".*10\\.?04.*");
   }

   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return CloudServersAddNodeWithTagStrategy.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return CloudServersDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return CloudServersGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return CloudServersHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return CloudServersImageSupplier.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return CloudServersListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return CloudServersRebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return RackspaceLocationsSupplier.class;
   }
}
