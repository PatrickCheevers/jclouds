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

package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.aws.ec2.util.EC2Utils.getAllRunningInstancesInRegion;
import static org.jclouds.aws.ec2.util.EC2Utils.parseHandle;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2GetNodeMetadataStrategy implements GetNodeMetadataStrategy {

   private final EC2Client client;
   private final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;

   @Inject
   protected EC2GetNodeMetadataStrategy(EC2Client client,
         Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata) {
      this.client = client;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
   }

   @Override
   public NodeMetadata execute(String id) {
      String[] parts = parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      try {
         RunningInstance runningInstance = getOnlyElement(getAllRunningInstancesInRegion(client.getInstanceServices(),
               region, instanceId));
         return runningInstanceToNodeMetadata.apply(runningInstance);
      } catch (NoSuchElementException e) {
         return null;
      }
   }

}