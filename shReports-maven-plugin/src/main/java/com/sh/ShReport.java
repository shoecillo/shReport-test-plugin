package com.sh;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import extract.ExtractData;


@Mojo(name="sh-report")
public class ShReport extends AbstractMojo 
{
	@Parameter(required= true, defaultValue = "${project.build.directory}/surefire-reports")
	private File folderReport;
	
	@Parameter(required= true, defaultValue = "${project.build.directory}")
	private File targetDir;
	
	@Parameter(required= true, defaultValue = "false")
	private boolean ignoreFailures;
	
	public void execute() throws MojoExecutionException, MojoFailureException 
	{
		getLog().info("maven-surefire reports folder: "+folderReport.getAbsolutePath());
		getLog().info("--------WORKING!!!");
		try 
		{
			
			ExtractData rep = new ExtractData();
			rep.init(targetDir, folderReport);
			
			if(!ignoreFailures)
			{	
				if(rep.isFailed())
				{
					getLog().error("[SH-REPORT]Test Failed!");
					getLog().error("[SH-REPORT]Build cannot continue.");
					throw new MojoExecutionException("Tests Failed.There are errors in test suites.");
				}
			}
			else
			{
				if(rep.isFailed())
				{
					getLog().warn("[SH-REPORT]Test Failed!");
					getLog().warn("[SH-REPORT]Build continue.Ignore Test errors are active.");
				}
			}
		}
		catch (IOException e) 
		{
			getLog().error(e.getMessage());
			throw new MojoExecutionException(e.getMessage(),e.getCause());
		}
		
	}

}
