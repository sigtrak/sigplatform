package com.log.processor.core;

import java.io.File;

public interface LogItemBuilder {
	
	public LogItem buildLogItem(File logFile);
}
