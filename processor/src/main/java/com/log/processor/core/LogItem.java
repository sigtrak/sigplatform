package com.log.processor.core;
import java.util.Date;

import org.mongojack.ObjectId;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Slf4j
public class LogItem {
	@ObjectId
	@JsonProperty("_id")
	String id;
	@JsonProperty("field_test_unique_id")
	String testUniqueId;
	@JsonProperty("field_test_result")
	String testResult;
	@JsonProperty("field_test_error")
	String testError;
	@JsonProperty("field_test_directory")
	String testDirectory;
	@JsonProperty("field_test_directory_date")
	String testDirectoryDate;
	@JsonProperty("field_test_project")
	String testProject;
	@JsonProperty("field_test_module")
	String testModule;
	@JsonProperty("field_test_release")
	String testRelease;
	@JsonProperty("field_test_sim_time")
	Long testSimulationTime;
	@JsonProperty("field_test_time_completed")
	String timeSimulationCompleted;
	@JsonProperty("field_test_name")
	String testName;
	@JsonProperty("field_test_seed")
	Integer testSeed;
	@JsonProperty("field_build_seed")
	Integer buildSeed;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTestUniqueId() {
		return testUniqueId;
	}
	public void setTestUniqueId(String uniqueId) {
		this.testUniqueId = uniqueId;
	}
	public String getTestResult() {
		return testResult;
	}
	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}
	public String getTestError() {
		return testError;
	}
	public void setTestError(String testError) {
		this.testError = testError;
	}
	public String getTestDirectory() {
		return testDirectory;
	}
	public void setTestDirectory(String testDirectory) {
		this.testDirectory = testDirectory;
	}
	public String getTestDirectoryDate() {
		return testDirectoryDate;
	}
	public void setTestDirectoryDate(String testDirectoryDate) {
		this.testDirectoryDate = testDirectoryDate;
	}
	public String getTestProject() {
		return testProject;
	}
	public void setTestProject(String testProject) {
		this.testProject = testProject;
	}
	public String getTestModule() {
		return testModule;
	}
	public void setTestModule(String testModule) {
		this.testModule = testModule;
	}
	public String getTestRelease() {
		return testRelease;
	}
	public void setTestRelease(String testRelease) {
		this.testRelease = testRelease;
	}
	public Long getTestSimulationTime() {
		return testSimulationTime;
	}
	public void setTestSimulationTime(Long testSimulationTime) {
		this.testSimulationTime = testSimulationTime;
	}
	public String getTimeSimulationCompleted() {
		return timeSimulationCompleted;
	}
	public void setTimeSimulationCompleted(String timeSimulationCompleted) {
		this.timeSimulationCompleted = timeSimulationCompleted;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public Integer getTestSeed() {
		return testSeed;
	}
	public void setTestSeed(Integer testSeed) {
		this.testSeed = testSeed;
	}
	public Integer getBuildSeed() {
		return buildSeed;
	}
	public void setBuildSeed(Integer buildSeed) {
		this.buildSeed = buildSeed;
	}
	
}