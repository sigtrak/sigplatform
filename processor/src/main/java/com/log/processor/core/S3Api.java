/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.log.processor.core;

import java.util.*;
import java.io.File;
import java.io.Writer;
import java.util.UUID;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;



/**
 * This sample demonstrates how to make basic requests to Amazon S3 using
 * the AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
 * account, and be signed up to use Amazon S3. For more information on
 * Amazon S3, see http://aws.amazon.com/s3.
 * <p>
 * <b>Important:</b> Be sure to fill in your AWS access credentials in
 * ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows
 * users) before you try to run this sample.
 */

public class S3Api {

	/*
	 * Establishing the connection to the default s3, that was provided in the aws/credentials
     * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users) 
     * and save the following lines after replacing the underlined values with your own.
     *
     * [default]
     * aws_access_key_id = YOUR_ACCESS_KEY_ID
     * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
     */
	public S3Api() {
		AmazonS3 s3 = new AmazonS3Client();
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest2);

        String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
        String key = "MyObjectKey";

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");
	}

    /**
     *
     * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users) 
     * and save the following lines after replacing the underlined values with your own.
     *
     * [default]
     * aws_access_key_id = YOUR_ACCESS_KEY_ID
     * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
     * @return returns the AmazonS3 object.
     */

    public AmazonS3 getConnection() {
    	AmazonS3 s3 = null;
    	try {
			s3 = new AmazonS3Client();
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			s3.setRegion(usWest2);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return s3;
    }

	/*
     * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
     * so once a bucket name has been taken by any user, you can't create
     * another bucket with that same name.
     *
     * You can optionally specify a location for your bucket if you want to
     * keep your data closer to your applications or users.
     */

	public boolean createBucket(String bucketName, AmazonS3 s3) {
		try {
			System.out.println("Creating bucket " + bucketName + "\n");
			s3.createBucket(bucketName);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/*
	 * List the buckets in your account
	 */
	public List<Bucket> getBucketList(AmazonS3 s3) {
		try {
			System.out.println("gettings buckets...");
			return s3.listBuckets();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
		// for (Bucket bucket : s3.listBuckets()) {
		// 	System.out.println(" - " + bucket.getName());
		// }
	}

	/*
	 * Upload an object to your bucket - You can easily upload a file to
	 * S3, or upload directly an InputStream if you know the length of
	 * the data in the stream. You can also specify your own metadata
	 *  when uploading to S3, which allows you set a variety of options
	 * like content-type and content-encoding, plus additional metadata
	 * specific to your applications.
	 */

	public boolean uploadObject(AmazonS3 s3, String bucketName, String key, String fileName, String jsonString) {
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile(fileName, jsonString)));
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/*
	 * getLastModified() gives you the last modified date of the object 
	 * from the bucket based on the key.
	*/

	public Date getLastModified(AmazonS3 s3, String bucketName, String key) {
		return (s3.getObjectMetadata(bucketName, key)).getLastModified();
	}

	/*
	 * Download an object - When you download an object, you get all of
	 * the object's metadata and a stream from which to read the contents.
	 * It's important to read the contents of the stream as quickly as
	 * possibly since the data is streamed directly from Amazon S3 and your
	 * network connection will remain open until you read all the data or
	 * close the input stream.
	 *
	 * GetObjectRequest also supports several other options, including
	 * conditional downloading of objects based on modification times,
  	 * ETags, and selectively downloading a range of an object.
 	 */

	public List<String> downloadObject(AmazonS3 s3, String bucketName, String key) throws IOException {
		System.out.println("Downloading an object");
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
		return displayTextInputStream(object.getObjectContent());
	}

	/*
	 * List objects in your bucket - There are many options for
	 * listing the objects in your bucket.  Keep in mind that buckets with
	 * many objects might truncate their results when listing their objects,
	 * so be sure to check if the returned object listing is truncated, and
	 * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
	 * additional results.
	 */
	public List<S3ObjectSummary> getObjectsFromBucket(AmazonS3 s3, String bucketName) {
		try {
			System.out.println("Listing objects");
			ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
				.withBucketName(bucketName));
			return objectListing.getObjectSummaries();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;

		// for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
		// 	System.out.println(" - " + objectSummary.getKey() + "  " +
		// 			"(size = " + objectSummary.getSize() + ")");
		// 	if(objectSummary.getKey().endsWith(".log.DONE")) {
		// 		String temp = objectSummary.getKey();
		// 		String key = temp.substring(temp.lastIndexOf("/")+1);
		// 		String bucketname = "newregressions/" + temp.substring(0,temp.last$
		// 		System.out.println("Bucket Name " + bucketname + " Key = "  + key);
		// 		//deleteObject(s3, bucketname, key);
		// 		String newKey = key.substring(0, key.lastIndexOf("."));
		// 		System.out.println("New Key is : " + newKey);
		// 		//String newKey = key + ".DONE";
		// 	}
		// }
		// System.out.println();
	}

	/**
	  * Renaming a file / key - renaming a file / key in the same bucket 
	  * by providing a bucketName, key (old file name), newKey (new file name)
	  *
	  * @param bucketName : folder name.
	  * @param key : old file name.
	  * @param newKey : new file name.
	  * @return true on successful renaming the file, otherwise.
	  *
	  */

	public boolean renameFile(AmazonS3 s3, String bucketName, String key, String newKey) {
		try {
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, key, bucketName, newKey);
			s3.copyObject(copyObjRequest);
			s3.deleteObject(new DeleteObjectRequest(bucketName, key));	
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/*
	 * Delete an object - Unless versioning has been turned on for your bucket,
	 * there is no way to undelete an object, so use caution when deleting objects.
	 */
	public boolean deleteObject(AmazonS3 s3, String bucketName, String key) {
		try {
			System.out.println("Deleting an object\n");
			s3.deleteObject(bucketName, key);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Delete a bucket - A bucket must be completely empty before it can be
	 * deleted, so remember to delete any objects from your buckets before
	 * you try to delete them.
	 *
	 * @param s3 object.
	 * @return true on successful deleteion of bucket, otherwise false.
	 */
	public boolean deleteBucket(AmazonS3 s3, String bucketName) {
		try {
			System.out.println("Deleting bucket " + bucketName + "\n");
			s3.deleteBucket(bucketName);
			return true;	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
		
	}

	/**
	 * Creates a temporary file with text data to demonstrate uploading a file
	 * to Amazon S3
	 *
	 * @return A newly created temporary file with text data.
	 *
	 * @throws IOException
	 */
	private static File createSampleFile(String fileName, String jsonString) throws IOException {
		File file = File.createTempFile(fileName, ".json");
		// file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		//	Your code goes here to write the json file in s3 storage.
		writer.write(jsonString);
		return file;
	}

	/**
	* Displays the contents of the specified input stream as text.
	*
	* @param input
	*            The input stream to display as text.
	*
	* @throws IOException
	*/
	private static List<String> displayTextInputStream(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		List<String> list = new ArrayList<String>();
		list = reader.lines().collect(Collectors.toList());
		return list;
	}

	public String getBucketName(String objectSummary) {
      return objectSummary.substring(0,objectSummary.lastIndexOf("/"));
   }

   public String getKey(String objectSummary) {
      return objectSummary.substring(objectSummary.lastIndexOf("/")+1);
   }
}


// public class S3Sample {

//     public static void main(String[] args) throws IOException {
// 	 // 	/*
//      //     * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users) 
//      //     * and save the following lines after replacing the underlined values with your own.
//      //     *
//      //     * [default]
//      //     * aws_access_key_id = YOUR_ACCESS_KEY_ID
//      //     * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
//      //     */

//      //    AmazonS3 s3 = new AmazonS3Client();
//      //    Region usWest2 = Region.getRegion(Regions.US_WEST_2);
//      //    s3.setRegion(usWest2);

//      //    String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();
//      //    String key = "MyObjectKey";

//      //    System.out.println("===========================================");
//      //    System.out.println("Getting Started with Amazon S3");
//      //    System.out.println("===========================================\n");

//      //    S3Api s3Api = new S3Api();


		// try {
		// 	AmazonS3 s3 = new S3Api().getConnection();
		// } catch (AmazonServiceException ase) {
		// 	System.out.println("Caught an AmazonServiceException, which means your request made it "
		// 	+ "to Amazon S3, but was rejected with an error response for some reason.");
		// 	System.out.println("Error Message:    " + ase.getMessage());
		// 	System.out.println("HTTP Status Code: " + ase.getStatusCode());
		// 	System.out.println("AWS Error Code:   " + ase.getErrorCode());
		// 	System.out.println("Error Type:       " + ase.getErrorType());
		// 	System.out.println("Request ID:       " + ase.getRequestId());
		// } catch (AmazonClientException ace) {
		// 	System.out.println("Caught an AmazonClientException, which means the client encountered "
		// 	+ "a serious internal problem while trying to communicate with S3, "
		// 	+ "such as not being able to access the network.");
		// 	System.out.println("Error Message: " + ace.getMessage());
		// }

// 	}
// }