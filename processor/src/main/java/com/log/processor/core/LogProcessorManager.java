package com.log.processor.core;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.inject.Inject;

import io.dropwizard.lifecycle.Managed;

public class LogProcessorManager implements Managed {

	private ExecutorService executor;
	private List<LogFileProcessor> readers;
	
	@Inject
	public LogProcessorManager(
			ExecutorService executor, 
			List<LogFileProcessor> readers) {
		this.executor = executor;
		this.readers = readers;
	}
	
	@Override
	public void start() throws Exception {
		readers.forEach(reader -> executor.execute(reader));
	}

	@Override
	public void stop() throws Exception {
		executor.shutdown();
	}

}
