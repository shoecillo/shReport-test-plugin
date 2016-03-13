package extract;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import shoe.utilFiles.ReadDir;
import shoe.utilFiles.WriteReaderF;
import shoe.xmlFunc.LeerXml;
import util.Utils;
import beans.TestCaseBean;
import beans.TestSuiteBean;

public class ExtractXml 
{
	
	private File dir = new File("site/data/");
	private File dirXml = null;
	
	private HashMap<String, TestSuiteBean> mapa = new HashMap<String, TestSuiteBean>();
	
	public ExtractXml(File dirXml) 
	{
		this.dirXml = dirXml;
	}
	
	public ExtractXml(File dirXml,File dir) 
	{
		this.dirXml = dirXml;
		this.dir = new File(dir.getAbsolutePath()+File.separator+"site"+File.separator+"data"+File.separator);
	}
	
	public static void main(String[] args) 
	{
		try {
			new ExtractXml(new File("surefire-reports")).exec();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
	public void exec() throws IOException
	{
		if(!dir.exists())
		{
			dir.mkdir();
		}
		
		
		
		if(dirXml.exists())
		{
			ReadDir rd = new ReadDir(dirXml.getAbsolutePath(), ".xml");
			for(File f : rd.getRes())
			{
				LeerXml xml = new LeerXml();
				Element root = xml.rXml(f.getAbsolutePath());
				mapa.put(f.getName().substring(0, f.getName().lastIndexOf(".")), readXmlReport(root));
			}
			
			processPackages();
			processTestCases();
			processSummary();
		}
		else
		{
			System.out.println("surefire-reports folder do not exists.");
		}
	}
	
	private TestSuiteBean readXmlReport(Element root)
	{
		// <testsuite failures="2" time="0.007" errors="0" skipped="0" tests="3" name="beans.TestBean">
		List<Element> lsTestCases = root.getChildren("testcase");
		List<TestCaseBean> lsCases = new ArrayList<TestCaseBean>();
		for(Element e : lsTestCases)
		{
			TestCaseBean bean = new TestCaseBean();
			bean.setTime(e.getAttributeValue("time"));
			bean.setClassname(e.getAttributeValue("classname"));
			
			String name = e.getAttributeValue("name");
			if(e.getChild("failure") != null)
			{
				Element err = e.getChild("failure");
				name += "##"+err.getAttributeValue("message")+"##"+err.getText();
				bean.setName(name.replaceAll("\n", "<BR>"));
				
			}
			else
			{
				bean.setName(name);
			}
			
			lsCases.add(bean);
		}
		TestSuiteBean suite = new TestSuiteBean();
		suite.setFailures(root.getAttributeValue("failures"));
		suite.setTime(root.getAttributeValue("time"));
		suite.setErrors(root.getAttributeValue("errors"));
		suite.setSkipped(root.getAttributeValue("skipped"));
		suite.setTests(root.getAttributeValue("tests"));
		suite.setName(root.getAttributeValue("name"));
		suite.setLsCases(lsCases);
		
		return suite;
	}
	
	private void processSummary() throws IOException
	{
		StringBuffer summary = new StringBuffer();
		
		int Tests_  = 0 ;
		int Errors_ = 0;
		int Failures_ = 0;
		int Skipped_ = 0;
		float Time_ = 0;
		float rate_ = 0;
		String Success_Rate_ = "";
		
		
		summary.append("var summary = ");
		summary.append("\n{");
		summary.append("\n\tdisclaimer : 'Note: failures are anticipated and checked for with assertions while errors are unanticipated.',");
		summary.append("\n\ttable : ");
		summary.append("\n\t[{");
		for(String k : mapa.keySet())
		{
			TestSuiteBean bean = mapa.get(k);
			Tests_  += Integer.parseInt(bean.getTests());
			Errors_ += Integer.parseInt(bean.getErrors());
			Failures_ += Integer.parseInt(bean.getFailures());
			Skipped_ += Integer.parseInt(bean.getSkipped());
			Time_ += Float.parseFloat(bean.getTime());
		}
		
		int stat_ = Tests_ - Failures_ - Errors_ - Skipped_;
		
		// Calculate Success Rate
		rate_ = ((float)stat_*100)/(float)Tests_;
		DecimalFormat fr_ = new DecimalFormat("0.###");
		DecimalFormatSymbols otherSymbols_ = new DecimalFormatSymbols();
		otherSymbols_.setDecimalSeparator('.');
		fr_.setDecimalFormatSymbols(otherSymbols_);
		Success_Rate_ = fr_.format(rate_)+"%";
		
		summary.append("\n\t\tTests : '"+Tests_+"',");
		summary.append("\n\t\tErrors : '"+Errors_+"',");
		summary.append("\n\t\tFailures : '"+Failures_+"',");
		summary.append("\n\t\tSkipped : '"+Skipped_+"',");
		summary.append("\n\t\tSuccess_Rate : '"+Success_Rate_+"',");
		summary.append("\n\t\tTime : '"+Time_+"'");
		summary.append("\n\t}],");
		
		summary.deleteCharAt(summary.lastIndexOf(","));
		summary.append("\n}");
		
		
		new WriteReaderF().Wfile(summary.toString(), dir.getAbsolutePath()+"/summary.js");
	}
	
	private void processTestCases() throws IOException
	{
		StringBuffer buff = new StringBuffer();
		buff.append("var testCases = ");
		buff.append("\n[");
		for(String k : mapa.keySet())
		{
			TestSuiteBean bean = mapa.get(k);
			buff.append("{");
			buff.append("\n\ttestSuite : '"+bean.getName().substring(bean.getName().indexOf(".")+1)+"',");
			buff.append("\n\ttable : [");
			for(TestCaseBean b : bean.getLsCases())
			{
				buff.append("{");
				if(b.getName().indexOf("##") != -1)
				{
					buff.append("\n\t\tResult :'KO',");
				}
				else
				{
					buff.append("\n\t\tResult :'OK',");
				}
				buff.append("\n\t\tnMethod : '"+b.getName()+"',");
				buff.append("\n\t\tTime : '"+b.getTime()+"'");
				buff.append("\n\t},");
			}
			buff.deleteCharAt(buff.lastIndexOf(","));
			buff.append("]");
			buff.append("\n},");
		}
		buff.deleteCharAt(buff.lastIndexOf(","));
		buff.append("]");

		new WriteReaderF().Wfile(buff.toString(), dir.getAbsolutePath()+"/testCases.js");
	}
	
	private void processPackages() throws IOException
	{
		
		List<String> lsPackages = new ArrayList<String>();
		for(String k : mapa.keySet())
		{
			TestSuiteBean bean = mapa.get(k);
			lsPackages.add(bean.getName().substring(0,bean.getName().lastIndexOf(".")));
		}
		// add elements to al, including duplicates
		Set<String> hs = new HashSet<String>();
		hs.addAll(lsPackages);
		lsPackages.clear();
		lsPackages.addAll(hs);
		
		int total = 0;
		int errors = 0;
		int failures = 0;
		int skipped = 0;
		float time = 0;
		
		StringBuffer pkgSummary = new StringBuffer();
		StringBuffer pkgsList = new StringBuffer(); 	
		
		StringBuffer packages = new StringBuffer(); 	
		
		pkgSummary.append("\npkgSummary : \n[");
		pkgsList.append("\npkgsList : \n[");
		for(String pkg : lsPackages)
		{
			pkgSummary.append("{");
			pkgsList.append("{");
			pkgsList.append("\n\tPackage : '"+pkg+"',");
			pkgsList.append("\n\ttable : \n\t[\n");
			for(String k : mapa.keySet())
			{
				
				TestSuiteBean bean = mapa.get(k);
				if(bean.getName().startsWith(pkg))
				{
					pkgsList.append("\t\t{");
					total += Integer.parseInt(bean.getTests());
					errors += Integer.parseInt(bean.getErrors());
					failures += Integer.parseInt(bean.getFailures());
					skipped += Integer.parseInt(bean.getSkipped());
					time += Float.parseFloat(bean.getTime());
					
					
					int Tests_  =Integer.parseInt(bean.getTests());
					int Errors_ = Integer.parseInt(bean.getErrors());
					int Failures_ = Integer.parseInt(bean.getFailures());
					int Skipped_ = Integer.parseInt(bean.getSkipped());
					float Time_ = Float.parseFloat(bean.getTime());
					float rate_ = 0;
					String Success_Rate_ = "";
					
					int stat_ = Tests_ - Failures_ - Errors_ - Skipped_;
					
					// Calculate Success Rate
					rate_ = ((float)stat_*100)/(float)Tests_;
					DecimalFormat fr_ = new DecimalFormat("0.###");
					DecimalFormatSymbols otherSymbols_ = new DecimalFormatSymbols();
					otherSymbols_.setDecimalSeparator('.');
					fr_.setDecimalFormatSymbols(otherSymbols_);
					Success_Rate_ = fr_.format(rate_)+"%";
					
					if(stat_ == Tests_)
						pkgsList.append("\n\t\t\tResult : 'OK',");
					else
						pkgsList.append("\n\t\t\tResult : 'KO',");
					
					pkgsList.append("\n\t\t\tClass : '"+bean.getName().substring(bean.getName().lastIndexOf(".")+1)+"',");
					pkgsList.append("\n\t\t\tTests : '"+Tests_+"',");
					pkgsList.append("\n\t\t\tErrors : '"+Errors_+"',");
					pkgsList.append("\n\t\t\tFailures : '"+Failures_+"',");
					pkgsList.append("\n\t\t\tSkipped : '"+Skipped_+"',");
					pkgsList.append("\n\t\t\tSuccess_Rate : '"+Success_Rate_+"',");
					pkgsList.append("\n\t\t\tTime : '"+Time_+"'");
					pkgsList.append("\n\t\t},\n");
				}
			}
			pkgsList.deleteCharAt(pkgsList.lastIndexOf(","));
			pkgsList.append("\t]");
			pkgsList.append("\n},");
			
			// Calculate Success Rate			
			int stat = total - failures - errors - skipped;
			float rate = 0;
			String Success_Rate = "";
			
			rate = ((float)stat*100)/(float)total;
			DecimalFormat fr = new DecimalFormat("0.###");
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
			otherSymbols.setDecimalSeparator('.');
			fr.setDecimalFormatSymbols(otherSymbols);
			Success_Rate = fr.format(rate)+"%";

			pkgSummary.append("\n\tPackage : '"+pkg+"',");
			pkgSummary.append("\n\tTests : '"+total+"',");
			pkgSummary.append("\n\tErrors : '"+errors+"',");
			pkgSummary.append("\n\tFailures : '"+failures+"',");
			pkgSummary.append("\n\tSkipped : '"+skipped+"',");
			pkgSummary.append("\n\tSuccess_Rate : '"+Success_Rate+"',");
			pkgSummary.append("\n\tTime : '"+time+"'");
			pkgSummary.append("\n},");
			
			total = 0;
			errors = 0;
			failures = 0;
			skipped = 0;
			time = 0;
		}
		pkgSummary.deleteCharAt(pkgSummary.lastIndexOf(","));
		pkgSummary.append("]");
		
		pkgsList.deleteCharAt(pkgsList.lastIndexOf(","));
		pkgsList.append("]");
		
		packages.append("var packages = ");
		packages.append("\n{\n");
		packages.append(Utils.addTabs(pkgSummary.toString(), 1));
		packages.append(",\n");
		packages.append(Utils.addTabs(pkgsList.toString(), 1));
		packages.append("\n}");
		
		new WriteReaderF().Wfile(packages.toString(), dir.getAbsolutePath()+"/pkgList.js");
	}
	
	public boolean isFailed()
	{
		boolean res = false;
		for(String k : mapa.keySet())
		{
			TestSuiteBean bean = mapa.get(k);
			int failures = Integer.valueOf(bean.getFailures());
			int errors = Integer.valueOf(bean.getErrors());
			int skipped = Integer.valueOf(bean.getSkipped());
			int total = failures + errors + skipped;
			if(total>0)
			{
				res = true;
				break;
			}
		}
		return res;
	}
	
}
