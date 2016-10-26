package com.log.processor.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.log.processor.common.CommonUtils;
import com.log.processor.db.LogItemWriter;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

import com.log.processor.core.AbstractLogProcessor;
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

public class S3LogFileProcessor extends AbstractLogProcessor implements Runnable {
	private static final Logger log = getLogger(LogFileProcessor.class);
	private LogItemWriter writer;
	private LogItemBuilder builder;
	private String bucketName;
	private AmazonS3 s3;
	private S3Api s3Api;

	@Inject
	public S3LogFileProcessor( @Named("bucket")  String bucketName, S3LogItemBuilderImpl builder, LogItemWriter writer  ) {
		this.bucketName = bucketName;
		this.builder = builder;
		this.writer = writer;
		try {
			this.s3Api = new S3Api();
			this.s3 = s3Api.getConnection();
		} catch (AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means your request made it "
			+ "to Amazon S3, but was rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means the client encountered "
			+ "a serious internal problem while trying to communicate with S3, "
			+ "such as not being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
		}
	}

	@Override
	public void run() {
		log.info("Entered S3LogFileProcessor...");
		while(true) {
			try {
				process();
				Thread.sleep(200);
			} catch(Exception e) {
				log.error("Exception in LogReader thread. [Exception={}] ", e);
			}
		}
	}

	public void process() {
		List<S3ObjectSummary> objectListing = new S3Api().getObjectsFromBucket(s3, bucketName);
		if(objectListing != null) {
			for (S3ObjectSummary objectSummary : objectListing) {
				if(objectSummary.getKey().endsWith(".log")) {
					try {
						String key = objectSummary.getKey();
						LogItem logItem = builder.buildLogItem(s3Api, s3, bucketName, key);
						if(logItem != null) {
							log.info("Key is : ", key);
							writer.writeLogItem(logItem, s3Api, s3, bucketName, key);
							String newKey = key + ".DONE";
							s3Api.renameFile(s3, bucketName, key, newKey);
						}
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
