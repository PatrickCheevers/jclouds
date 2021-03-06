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

package org.jclouds.aws.s3.binders;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindS3ObjectToPayload implements Binder {
   private final BindUserMetadataToHeadersWithPrefix blobBinder;
   private final ObjectToBlob object2Blob;

   @Inject
   public BindS3ObjectToPayload(ObjectToBlob object2Blob, BindUserMetadataToHeadersWithPrefix blobBinder) {
      this.blobBinder = blobBinder;
      this.object2Blob = object2Blob;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      S3Object s3Object = (S3Object) payload;
      checkArgument(s3Object.getPayload().getContentMetadata().getContentLength() != null,
               "contentLength must be set, streaming not supported");
      checkArgument(s3Object.getPayload().getContentMetadata().getContentLength() <= 5l * 1024 * 1024 * 1024,
               "maximum size for put object is 5GB");
      blobBinder.bindToRequest(request, object2Blob.apply(s3Object));

      if (s3Object.getMetadata().getCacheControl() != null) {
         request.getHeaders().put(HttpHeaders.CACHE_CONTROL, s3Object.getMetadata().getCacheControl());
      }

   }
}
