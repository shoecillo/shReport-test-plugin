### Welcome to the shReport-test-plugin documentation.

The general purpose of this tool is generate a attractive look report to test results and make easier to detect what test are failed and why.
Is based in surefire maven reports, that generates a html report but totally unstyled and old-fashioned html, that's the origin of this tool.
It's all based in translate the surfire xml result to json objects that html can understand and format in pretty-html reports using `bootstrap`and `jQuery`for interact with report.  

### Usage of plugin
This plugin and dependency jars are in a public nexus repository but not in maven central.
For use this plugin in our project we have to add some configuration in pom.xml:

First of all, include in our pom.xml the plugin repository path: 

```xml
<pluginRepositories>
     <pluginRepository>
         <id>shoe011</id>
         <url>http://nexus-shoe011.rhcloud.com/nexus/content/repositories/shoe011</url>
     </pluginRepository>
</pluginRepositories>
```

After include the plugin repository, we have to configure build section:

```xml
<!-- maven-surefire-plugin configuration,we must ignore faliures -->
<plugin>
	    <artifactId>maven-surefire-plugin</artifactId>
	    <version>2.11</version>
	    <configuration>
	        <testFailureIgnore>true</testFailureIgnore>
	    </configuration>
	</plugin>
<!-- ShReports configuration.Parameter ignoreFailures breaks or not the build 
if there are failures -->
<plugin>
       	<groupId>com.sh</groupId>
	<artifactId>shReports-maven-plugin</artifactId>
	<version>1.0.0</version>
	<executions>
		<execution>
			<phase>test</phase>
			<goals>
			    <goal>sh-report</goal>
			</goals>				
  		</execution>
  	</executions>  	
 	<configuration>
  		<ignoreFailures>false</ignoreFailures>
  	</configuration>	
  	<dependencies>
  		<dependency>
	  		<groupId>com.sh</groupId>
	  		<artifactId>report-junit</artifactId>
	  		<version>1.0.4</version>
		</dependency>
  	</dependencies>
</plugin>
```
> Note: We must ignore failures in surefire,otherwise shReports plugin don't execute, instead we can activate or deactivate this function with `ignoreFailures` parameter,that is a boolean for break or not the build if not all test are ok.

This is all files that generate the plugin.Generates zip with all site folder:

![Tree](http://s20.postimg.org/aqnsqc20d/Generated_Files.png)