package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils 
{
	
	public static String addTabs(String buff,int numTabs)
	{
		String tabs = "";
		for(int i=0;i<numTabs;i++)
		{
			tabs +="\t";
		}
		String res = buff.replaceAll("\n", "\n"+tabs);
		return res;
	}
	
	public static StringBuffer processTable(Elements table,String nVar)
	{
		Elements rows = table.select("tr");
		Elements header = rows.select("tr > th");
		List<String> lsHeader = new ArrayList<String>();
		for(Element e : header)
		{
			lsHeader.add(e.text().replaceAll(" ", "_"));
		}
		
		StringBuffer JSONTable = new StringBuffer();
		JSONTable.append("\n"+nVar+" :\n[");
		for(int i = 1;i<rows.size();i++)
		{
			Elements row = rows.get(i).select(" > td");
			JSONTable.append("{\n");
			int conta = 0;
			for(Element td : row)
			{
				if(!td.select("img").attr("src").equals(""))
				{
					if(td.select("img").attr("src").contains("success"))
					{
						JSONTable.append("\tResult : 'OK',\n");
					}
					else
					{
						JSONTable.append("\tResult : 'KO',\n");
					}
				}
				else
				{
					JSONTable.append("\t"+lsHeader.get(conta)+" : '"+td.text()+"',\n");
				}
				conta++;
			}
			JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
			JSONTable.append("},");
		}
		JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
		JSONTable.append("]");
		return JSONTable;
	}
	public static StringBuffer processTable(Elements table)
	{
		Elements rows = table.select("tr");
		Elements header = rows.select("tr > th");
		List<String> lsHeader = new ArrayList<String>();
		for(Element e : header)
		{
			lsHeader.add(e.text().replaceAll(" ", "_"));
		}
		
		StringBuffer JSONTable = new StringBuffer();
		JSONTable.append("[");
		for(int i = 1;i<rows.size();i++)
		{
			Elements row = rows.get(i).select(" > td");
			JSONTable.append("{\n");
			int conta = 0;
			for(Element td : row)
			{
				if(!td.select("img").attr("src").equals(""))
				{
					if(td.select("img").attr("src").contains("success"))
					{
						JSONTable.append("\tResult : 'OK',\n");
					}
					else
					{
						JSONTable.append("\tResult : 'KO',\n");
					}
				}
				else
				{
					JSONTable.append("\t"+lsHeader.get(conta)+" : '"+td.text()+"',\n");
				}
				conta++;
			}
			JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
			JSONTable.append("},");
		}
		JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
		JSONTable.append("]");
		return JSONTable;
	}
	
	public static StringBuffer processTableNoHeader(Elements table)
	{
		List<String> lsHeader = new ArrayList<String>();
		lsHeader.add("Result");
		lsHeader.add("nMethod");
		lsHeader.add("Time");
		
		Elements rows = table.select("tr");
		StringBuffer JSONTable = new StringBuffer();
		JSONTable.append("[");
		
		int contaError = 0;
		boolean flagError = false;
		String ErrorLog = "";
		for(int i = 0;i<rows.size();i++)
		{
			Elements row = rows.get(i).select(" > td");
			
			int conta = 0;
			if(flagError)
			{
				if(contaError<2)
				{
					ErrorLog += rows.get(i).select(" > td:eq(1)").text()+"##";
					contaError++;
				}
				else
				{
					JSONTable.append("\tResult : 'KO',");
					JSONTable.append("\n\tnMethod : '"+ErrorLog.substring(0, ErrorLog.lastIndexOf("##"))+"',");
					JSONTable.append("\n\tTime : '0'");
					JSONTable.append("\n},");
					flagError = false;
					contaError = 0;
					ErrorLog = "";
					
					JSONTable.append("{\n");
					for(Element td : row)
					{
						if(!td.select("img").attr("src").equals(""))
						{
							if(td.select("img").attr("src").contains("success"))
							{
								JSONTable.append("\tResult : 'OK',\n");
							}
							else
							{
								//JSONTable.append("\tResult : 'KO',\n");
								flagError = true;
								ErrorLog += rows.get(i).select(" > td:eq(1)").text()+"##";
								break;
							}
						}
						else
						{
							
							JSONTable.append("\t"+lsHeader.get(conta)+" : '"+td.text()+"',\n");
						}
						conta++;
					}
					JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
					JSONTable.append("},");
				}
			}
			else
			{
				JSONTable.append("{\n");
				for(Element td : row)
				{
					if(!td.select("img").attr("src").equals(""))
					{
						if(td.select("img").attr("src").contains("success"))
						{
							JSONTable.append("\tResult : 'OK',\n");
						}
						else
						{
							//JSONTable.append("\tResult : 'KO',\n");
							flagError = true;
							String descr = rows.get(i).select(" > td:eq(1)").text();
							ErrorLog += descr.substring(0, descr.indexOf("+"))+"##";
							break;
						}
					}
					else
					{
						
						JSONTable.append("\t"+lsHeader.get(conta)+" : '"+td.text()+"',\n");
					}
					conta++;
				}
				if(!flagError)
				{
					JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
					JSONTable.append("},");
				}
			}
		}
		if(flagError)
		{
			JSONTable.append("\tResult : 'KO',");
			JSONTable.append("\n\tnMethod : '"+ErrorLog.substring(0, ErrorLog.lastIndexOf("##"))+"',");
			JSONTable.append("\n\tTime : '0'");
			JSONTable.append("\n},");
			flagError = false;
			contaError = 0;
			ErrorLog = "";
		}
		JSONTable.deleteCharAt(JSONTable.lastIndexOf(","));
		JSONTable.append("]");
		return JSONTable;
	}
	
	public static void copyFolder(File src, File dest) throws IOException
	{
			
	    	if(src.isDirectory()){
	 
	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		   System.out.println("Directory copied from " 
	                              + src + "  to " + dest);
	    		}
	 
	    		//list all the directory contents
	    		String files[] = src.list();
	 
	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}
	 
	    	}else{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
	    	        OutputStream out = new FileOutputStream(dest); 
	 
	    	        byte[] buffer = new byte[1024];
	 
	    	        int length;
	    	        //copy the file content in bytes 
	    	        while ((length = in.read(buffer)) > 0){
	    	    	   out.write(buffer, 0, length);
	    	        }
	 
	    	        in.close();
	    	        out.close();
	    	        System.out.println("File copied from " + src + " to " + dest);
	    	}
	    }
	
}
