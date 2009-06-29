/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
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
 */
package org.jclouds.aws.s3.commands;

import static org.jclouds.aws.s3.internal.StubS3Connection.TEST_ACL_EMAIL;
import static org.jclouds.aws.s3.internal.StubS3Connection.TEST_ACL_ID;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all commands that retrieve Access Control Lists (ACLs).
 * 
 * @author James Murty
 */
@Test(groups = { "integration", "live" }, testName = "s3.PutAccessControlListIntegrationTest")
public class PutAccessControlListIntegrationTest extends S3IntegrationTest {

   @Test
   void testUpdateBucketACL() throws InterruptedException, ExecutionException, TimeoutException,
            IOException, Exception {
      String bucketName = getScratchBucketName();
      try {
         // Confirm the bucket is private
         AccessControlList acl = client.getBucketACL(bucketName).get(10, TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();
         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(client.putBucketACL(bucketName, acl).get(10, TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = client.getBucketACL(bucketName).get(10, TimeUnit.SECONDS);
         checkGrants(acl);
      } finally {
         returnScratchBucket(bucketName);
      }

   }

   @Test
   void testUpdateObjectACL() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String bucketName = getBucketName();
      try {
         String objectKey = "pr�vate-acl";

         // Private object
         addObjectToBucket(bucketName, objectKey);
         AccessControlList acl = client.getObjectACL(bucketName, objectKey).get(10,
                  TimeUnit.SECONDS);
         String ownerId = acl.getOwner().getId();

         assertEquals(acl.getGrants().size(), 1);
         assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL));

         addGrantsToACL(acl);
         assertEquals(acl.getGrants().size(), 4);
         assertTrue(client.putObjectACL(bucketName, objectKey, acl).get(10, TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck.
         acl = client.getObjectACL(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         checkGrants(acl);

         /*
          * Revoke all of owner's permissions!
          */
         acl.revokeAllPermissions(new CanonicalUserGrantee(ownerId));
         if (!ownerId.equals(TEST_ACL_ID))
            acl.revokeAllPermissions(new CanonicalUserGrantee(TEST_ACL_ID));
         assertEquals(acl.getGrants().size(), 1);
         // Only public read permission should remain...
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ));

         // Update the object's ACL settings
         assertTrue(client.putObjectACL(bucketName, objectKey, acl).get(10, TimeUnit.SECONDS));

         // Confirm that the updated ACL has stuck
         acl = client.getObjectACL(bucketName, objectKey).get(10, TimeUnit.SECONDS);
         assertEquals(acl.getGrants().size(), 1);
         assertEquals(acl.getPermissions(ownerId).size(), 0);
         assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      } finally {
         returnBucket(bucketName);
      }

   }

   private void checkGrants(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();

      assertEquals(acl.getGrants().size(), 4, acl.toString());

      assertTrue(acl.hasPermission(ownerId, Permission.FULL_CONTROL), acl.toString());
      assertTrue(acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ), acl.toString());
      assertTrue(acl.hasPermission(ownerId, Permission.WRITE_ACP), acl.toString());
      // EmailAddressGrantee is replaced by a CanonicalUserGrantee, so we cannot test by email addr
      assertTrue(acl.hasPermission(TEST_ACL_ID, Permission.READ_ACP), acl.toString());
   }

   private void addGrantsToACL(AccessControlList acl) {
      String ownerId = acl.getOwner().getId();
      acl.addPermission(GroupGranteeURI.ALL_USERS, Permission.READ);
      acl.addPermission(new EmailAddressGrantee(TEST_ACL_EMAIL), Permission.READ_ACP);
      acl.addPermission(new CanonicalUserGrantee(ownerId), Permission.WRITE_ACP);
   }

}