package beans;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteBean 
{
	// <testsuite failures="2" time="0.007" errors="0" skipped="0" tests="3" name="beans.TestBean">
	
	private String failures;
	private String time;
	private String  errors;
	private String  skipped;
	private String  tests;
	private String  name;
	private List<TestCaseBean> lsCases = new ArrayList<TestCaseBean>();
	
	public String getFailures() {
		return failures;
	}
	public void setFailures(String failures) {
		this.failures = failures;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getErrors() {
		return errors;
	}
	public void setErrors(String errors) {
		this.errors = errors;
	}
	public String getSkipped() {
		return skipped;
	}
	public void setSkipped(String skipped) {
		this.skipped = skipped;
	}
	public String getTests() {
		return tests;
	}
	public void setTests(String tests) {
		this.tests = tests;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TestCaseBean> getLsCases() {
		return lsCases;
	}
	public void setLsCases(List<TestCaseBean> lsCases) {
		this.lsCases = lsCases;
	}
	
	
	
}
