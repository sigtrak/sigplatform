package com.log.processor.core;

import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import com.amazonaws.services.s3.AmazonS3;

import com.log.processor.common.CommonUtils;

public class LogItemBuilderImpl implements LogItemBuilder {
	private static final Logger log = getLogger(LogItemBuilderImpl.class);
	
	@Override
	public LogItem buildLogItem(File file) {
		System.out.println("Entered buildLogItem");
		if (!file.exists() ) {
			log.error("File object does not exist. Can not build log item.");
		}
		LogItem item = new LogItem();
		List<String> list = buildLines(file);
		String lineStr = readResultLine(list,item);
		buildLogItemAttributes(file, lineStr, item);
		return item;
	}

	@Override
	public LogItem buildLogItem(S3Api s3Api, AmazonS3 s3, String bucketName, String key) {
		return null;		
	}

	private List<String> buildLines(File file) {
		List<String> list = new ArrayList<>();
		try(BufferedReader br = Files.newBufferedReader( Paths.get(file.getCanonicalPath())) ) {
			list = br.lines().collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Exception while reading the file. fileName: " + file.getName() + " exception: " + e);
		}
		return list;
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
					//	Added by Sankar.
					String str[] = errorStr.substring((errorStr.lastIndexOf("]")+1)).trim().split(" ");
					StringBuffer sb = new StringBuffer();
					for(int i=0; i<str.length; i++)
						if(!str[i].matches("-?[0-9A-Fa-f]+"))
							sb.append(str[i] + " ");
						else
							sb.append("N ");

					item.setTestErrorCategory(sb.toString().trim());
					log.info(sb.toString().trim());
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

	private void buildLogItemAttributes(File file, String lineStr, LogItem item) {
		String parent = file.getParent();
		File parentFile = file.getParentFile();
		String[] dirs = parent.split("/");
		String uniqueId = null;
		String testDir = null;
		String project = null;
		int idx = -1;
		for (int i=0; i < dirs.length; i++) {
			if ( dirs[i].contains(CommonUtils.REGRESS) ) {
				idx = i +1;
				break;
			}
		}
		if ( idx != -1 ) {
			testDir = dirs[idx];
			String[] testElements = testDir.split("_");	
			project = testElements[0] + "_" + testElements[1];
			item.setTestProject( project );
			item.setTestModule( testElements[2] );
			item.setTestRelease( testElements[3] );
			item.setTestRegression(testDir+"/"+dirs[dirs.length-1]);
		}

		item.setTestDirectory( parent );
		Date parentDate = new Date(parentFile.lastModified());
		// Changed the date format from dd/MM/yyyy to yyyy-MM-dd
		DateFormat parentFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parentFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String parentFormatted = parentFormat.format(parentDate);
        item.setTestDirectoryDate(parentFormatted);
        //	Added by Sankar
        item.setTestRegressionDate(parentFormatted);
        
		int lIdx = lineStr.indexOf("[");
		int rIdx = lineStr.indexOf("]");
		if ( lIdx >=0 && rIdx >= 0 ) {
			String simTimeStr = lineStr.substring(lineStr.indexOf("[") + 1, lineStr.indexOf("]"));
			item.setTestSimulationTime( Long.parseLong(simTimeStr) );
		}
		Date date = new Date(file.lastModified());
		// Changed the date format from dd/MM/yyyy to yyyy-MM-dd
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
		item.setTimeSimulationCompleted( formatted );
		item.setTestName( file.getName().substring(0, file.getName().lastIndexOf('.')) );
		uniqueId = testDir + file.getName().substring(0, file.getName().lastIndexOf('.'));
		item.setTestUniqueId(parent + "/" + file.getName().substring(0, file.getName().lastIndexOf('.')));
		if ( project != null && project.startsWith(CommonUtils.PCIXP)) {
			item.setTestSeed( 0 );
			item.setBuildSeed( 0 );
		}
		return;
	}
}
