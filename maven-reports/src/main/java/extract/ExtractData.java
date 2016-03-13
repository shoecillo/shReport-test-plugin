package extract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import shoe.utilFiles.ReadDir;
import shoe.utilFiles.WriteReaderF;
import util.Utils;

public class ExtractData 
{
	
	private File dir = new File("site/data/");
	private static final String ROOT_SITE = "site/";
	
	private List<File> lsFilesCreated = new ArrayList<File>();
	
	private ExtractXml xmlRep;
	
	public static void main(String[] args) 
	{
		try 
		{
			//new ExtractData().init(args);
			File target = new File("target");
			File reports = new File("surefire-reports");
			new ExtractData().init(target,reports);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean isFailed()
	{
		return xmlRep.isFailed();
	}
	
	public void init(File target,File dirReports) throws IOException
	{
		copySiteFromJar(target.getAbsolutePath()+"/");
		xmlRep = new ExtractXml(dirReports,target);
		xmlRep.exec();
		zipDir(target.getAbsolutePath()+"/");
		System.out.println("Done!");
	}
	
	public void init(String[] args) throws IOException
	{
		copySiteFromJar("");
		if(args.length == 0)
		{
			String reportPath = "surefire-reports";
			xmlRep = new ExtractXml(new File(reportPath));
			xmlRep.exec();
			zipDir("");
			System.out.println("Done!");
		}
		else if(args.length == 2)
		{
			if(args[0].equals("-xml"))
			{
				new ExtractXml(new File(args[1])).exec();
				zipDir("");
				System.out.println("Done!");
			}
			else if(args[0].equals("-html"))
			{
				exec(args[1]);
				zipDir("");
				System.out.println("Done!");
			}
			else
			{
				System.out.println("Options are [-xml][-html] filePath");
				System.exit(0);
			}
			
		}
		else
		{
			System.out.println("Options are [-xml][-html] filePath");
			System.exit(0);
		}
	}
	
	private void zipDir(String absPath) throws IOException
	{
		File f = new File(absPath+ROOT_SITE);
		if(f.exists())
		{
			ReadDir dirSite = new ReadDir(f.getAbsolutePath());
			List<File> lsSite = dirSite.getRes();
			
			DateFormat format = new SimpleDateFormat("[yyyy-MM-dd_HH.mm.ss.SSSS]");
			String timestamp = format.format(new Date());
			
			FileOutputStream fZip = new FileOutputStream(new File(f.getAbsolutePath()+File.separator+"sh-junit-report"+timestamp+".zip"));
			ZipOutputStream zip = new ZipOutputStream(fZip);
			for(File target : lsSite)
			{
				if(target.exists())
				{
					if(!target.isDirectory())
					{
						String relativePath = target.getAbsolutePath().substring(target.getAbsolutePath().lastIndexOf("site"+File.separator));
						InputStream iStr = new FileInputStream(target);
						
						ZipEntry entry = new ZipEntry(relativePath);
						zip.putNextEntry(entry);
						
						 byte[] buffer = new byte[20 * 1024];
						 int bytesRead;
						 while ((bytesRead = iStr.read(buffer)) != -1) 
						 { 
							 zip.write(buffer, 0, bytesRead);
						 }
						
					}
				}
			}
			zip.closeEntry();
			zip.close();
			
		}
		System.out.println("site.zip Zipped!");
		
	}
	
	private List<String> getListFiles() throws IOException
	{
		InputStream iStr = ExtractData.class.getResourceAsStream("/util/listFilesSite.txt");
		
		 byte[] buffer = new byte[8 * 1024];
		 int bytesRead;
		 while ((bytesRead = iStr.read(buffer)) != -1) {}
		 List<String> lsStr = new ArrayList<String>();
		 String s = "";
		 for(byte b : buffer)
		 {
			 char c = (char)b;
		     if(c == '\n')
		     {
		    	 lsStr.add(s);
		    	 s= "";
		     }
		     else
		     {
		    	 if(c != '\r')
		    		 s += c;
		     }
		 }   
		 iStr.close();
		 return lsStr;
	}
	
	private void copySiteFromJar(String absPath) throws IOException
	{
		
		List<String> lsFiles = getListFiles();
		
		for(String s : lsFiles)
		{
			InputStream iStr = ExtractData.class.getResourceAsStream("/"+s);
			
			String to = s.substring(s.indexOf("/")+1);
			File target = new File(absPath+to);
			if(target.getName().indexOf(".") == -1)
			{
				
				if(!target.exists())
				{
					target.mkdirs();
				}
				System.out.println("Folder Created: "+target.getAbsolutePath());
			}
			else
			{
				
				OutputStream outStream = new FileOutputStream(target);
				 byte[] buffer = new byte[20 * 1024];
				    int bytesRead;
				    while ((bytesRead = iStr.read(buffer)) != -1) {
				        outStream.write(buffer, 0, bytesRead);
				      
				    }
				    iStr.close();
				    outStream.close();
				    lsFilesCreated.add(target);
				    System.out.println("File Created: "+target.getAbsolutePath());
			}
		}

		System.out.println("Done!");
		
	}
	
	public void exec(String reportPath) throws IOException
	{
		File input = new File(reportPath);
		
		if(!dir.exists())
		{
			dir.mkdir();
		}
		if(input.exists())
		{
			Document doc = Jsoup.parse(input,"UTF-8");
			Elements sections =  doc.select("#contentBox > .section");
			Element summary = sections.get(1);
			Element packageList = sections.get(2);
			Element testCases = sections.get(3);
			
			extractSummary(summary);
			extractPkg(packageList);
			extractTestCases(testCases);
		}
	}
	
	private void extractTestCases(Element testCases) throws IOException
	{
		Elements tests = testCases.select("> .section");
		StringBuffer res = new StringBuffer();
		res.append("var testCases = \n[\n");
		
		for(Element e : tests )
		{
			String name = e.select(" > h3").text();
			Elements table = e.select(" > table");
			
			res.append("{");
			res.append("\n\ttestSuite : '"+name+"',\n");
			res.append("\ttable : \n\t"+Utils.addTabs(Utils.processTableNoHeader(table).toString(), 1));
			res.append("\n},");
		}
		res.deleteCharAt(res.lastIndexOf(","));
		res.append("\n]");
		new WriteReaderF().Wfile(res.toString(), dir.getAbsolutePath()+"/testCases.js");
		
	}
	
	private StringBuffer extractPkg(Element pkgList) throws IOException
	{
		Elements disclaimer = pkgList.select("p:not(p:has(a))");
		Elements tbSummary = pkgList.select(" > table");
		
		
		Elements sectionsPkg = pkgList.select(" > .section");
		Elements tbPkgDetails = sectionsPkg.select(" > table");
		
		StringBuffer b = new StringBuffer();
		b.append(Utils.processTable(tbSummary,"pkgSummary").toString()+",\n");
		b.append("pkgsList : \n[");
		
		for(Element pkg : sectionsPkg)
		{
			Elements a = pkg.select(" > h3 > a");
			String pkgName = a.attr("name");
			
			b.append("\n\t{\n");
			b.append("\t\tPackage : '"+pkgName+"',\n");
			Elements tab = pkg.select(" > table");
			StringBuffer buff = Utils.processTable(tab,"table");
			b.append(Utils.addTabs(buff.toString(), 2));
			b.append("\n\t},");
			
		}
		b.deleteCharAt(b.lastIndexOf(","));
		b.append("\n]\n");
		StringBuffer res = new StringBuffer();
		res.append("var packages = \n{\n");
		res.append(Utils.addTabs(b.toString(), 1));
		res.append("\n}\n");
		
		new WriteReaderF().Wfile(res.toString(), dir.getAbsolutePath()+"/pkgList.js");
		return res;
	}
	
	private StringBuffer extractSummary(Element summary) throws IOException
	{
		Elements disclaimer = summary.select("p:not(p:has(a))");
		Elements table = summary.select("table");
	
		StringBuffer res = new StringBuffer();
		StringBuffer JSONTable = Utils.processTable(table,"table");
		res.append("var summary = \n{");
		res.append(Utils.addTabs("\ndisclaimer : '"+disclaimer.text()+"',\n", 1));
		res.append(Utils.addTabs(JSONTable.toString(), 1));
		res.append("\n}");
		new WriteReaderF().Wfile(res.toString(), dir.getAbsolutePath()+"/summary.js");
		return res;
	}
	
	
}
