package com.log.processor.db;

import com.log.processor.core.LogItem;

public interface LogItemWriter {
	public void writeLogItem( LogItem item, String directory, String name );
}
