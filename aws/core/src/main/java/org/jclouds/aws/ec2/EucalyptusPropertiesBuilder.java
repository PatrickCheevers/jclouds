/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_ENDPOINT;

import java.util.Properties;

/**
 * Builds properties used in Eucalyptus Clients
 * 
 * @author Adrian Cole
 */
public class EucalyptusPropertiesBuilder extends EC2PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_EC2_ENDPOINT,
            "http://173.205.188.130:8773/services/Eucalyptus");
      // TODO
      // properties.setProperty(PROPERTY_ELB_ENDPOINT,
      // "https://elasticloadbalancing.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "admin");
      return properties;
   }

   public EucalyptusPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public EucalyptusPropertiesBuilder(String id, String secret) {
      super(id, secret);
   }
}