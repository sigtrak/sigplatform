package com.log.processor.core;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import java.io.IOException;
import org.slf4j.Logger;
import com.log.processor.common.CommonUtils;

public class S3LogItemBuilderImpl implements LogItemBuilder {
	private static final Logger log = getLogger(LogItemBuilderImpl.class);
	
	@Override
	public LogItem buildLogItem(File logFile) {
		return null;
	}

	@Override
	public LogItem buildLogItem(S3Api s3Api, AmazonS3 s3, String bucketName, String key) {
		LogItem item = null;
		try {
			List<String> list = s3Api.downloadObject(s3, bucketName, key);
			item = new LogItem();
			String lineStr = readResultLine(list,item);
			buildLogItemAttributesForS3(s3Api, s3, lineStr, bucketName, key, item);
		} catch(Exception e) {
			log.error("Exception while building the items : " + key + " Exception" + e);
		}
		return item;
	}

	private String readResultLine(List<String> list, LogItem item) {
		String lineStr = "";
		String errorStr = "";
		for (String line : list) {
			if ( line.contains(CommonUtils.ERROR) ) {
				if (errorStr.equals("")) {
					line = line.startsWith("#") ? line.substring(1) : line;
					errorStr = line.trim();
				}
			}
			if ( line.contains(CommonUtils.PASSED) ) {
				item.setTestResult(CommonUtils.PASSED);
				item.setTestError( null );
				lineStr = line;
				break;
			} else if  ( line.contains(CommonUtils.FAILED) ) {
				item.setTestResult(CommonUtils.FAILED);
				if ( errorStr.equals("")) {
					line = line.startsWith("#") ? line.substring(1) : line;
					item.setTestError( line.trim() );
				} else {
					item.setTestError(errorStr);
				}
				lineStr = line;
				break;
			} else if  ( line.contains(CommonUtils.TERMINATED) ) {
				item.setTestResult(CommonUtils.TERMINATED);
				line = line.startsWith("#") ? line.substring(1) : line;
				item.setTestError( line.trim() );
				lineStr = line;
				break;
			}
		}
		return lineStr;
	}

	private void buildLogItemAttributesForS3(S3Api s3Api, AmazonS3 s3, String lineStr, String bucketName, String key, LogItem item) {
		int index = key.indexOf("/");
		String s = key.substring(0, index);
		String testElements[] = s.split("_");
		String project = testElements[0] + "_" + testElements[1];
		item.setTestProject( project );
		item.setTestModule( testElements[2] );
		item.setTestRelease( testElements[3] );
		item.setTestRegression(key.substring(0,key.lastIndexOf("/")));

		item.setTestDirectory(bucketName+"/"+key.substring(0,key.lastIndexOf("/")));
		
		Date parentDate = s3Api.getLastModified(s3, bucketName, key);
		DateFormat parentFormat = new SimpleDateFormat(CommonUtils.SIMPLEDATEFORMAT);
        parentFormat.setTimeZone(TimeZone.getTimeZone(CommonUtils.TIMEZONEFORMAT));
        String parentFormatted = parentFormat.format(parentDate);
        item.setTestDirectoryDate(parentFormatted);
        //	Added by Sankar
        item.setTestRegressionDate(parentFormatted);
        item.setTimeSimulationCompleted(parentFormatted);
        
		int lIdx = lineStr.indexOf("[");
		int rIdx = lineStr.indexOf("]");
		if ( lIdx >=0 && rIdx >= 0 ) {
			String simTimeStr = lineStr.substring(lineStr.indexOf("[") + 1, lineStr.indexOf("]"));
			item.setTestSimulationTime( Long.parseLong(simTimeStr) );
		}

		item.setTestName(key.substring(key.lastIndexOf("/")+1, key.lastIndexOf(".")));
		item.setTestUniqueId(bucketName +"/" + key.substring(0, key.lastIndexOf(".")));

		if ( project != null && project.startsWith(CommonUtils.PCIXP)) {
			item.setTestSeed( 0 );
			item.setBuildSeed( 0 );
		}
		return;
	}
}