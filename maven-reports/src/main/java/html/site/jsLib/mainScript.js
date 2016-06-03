/**
 * 
 */

var mainScript = 
{
	progress : function(pct)
	{
		pct = pct.substring(0,pct.indexOf("%"));
		var bar = "<div class='progress'><div class='progress-bar' role='progressbar' aria-valuenow='"+pct+"' aria-valuemin='0' aria-valuemax='100' style='width: "+pct+"%;'>"+pct+"%</div></div>";
		return bar;
	},
	summaryView : function()
	{
		$("#summary-content .panel-footer").html(summary.disclaimer);
		
		var obj = summary.table[0];
		
		$.each(obj,function(n,v)
		{
			$("#summary-content .panel-body table thead tr").append("<th>"+n+"</th>");
		});
		
		$.each(summary.table,function(i,v)
		{
			$("#summary-content .panel-body table tbody").append("<tr></tr>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+v.Tests+"</td>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+v.Errors+"</td>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+v.Failures+"</td>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+v.Skipped+"</td>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+mainScript.progress(v.Success_Rate)+"</td>");
			$("#summary-content .panel-body table tbody tr:last").append("<td>"+v.Time+"</td>");
			
		});
		
		//$("#summary-content .panel-body")
	},
	packageView : function()
	{
		var obj = packages.pkgSummary[0];
		$.each(obj,function(n,v)
		{
			$("#packages-content .panel-body table thead tr").append("<th>"+n+"</th>");
		});
		
		$.each(packages.pkgSummary,function(i,v)
		{
			
			$("#packages-content .panel-body table tbody").append("<tr></tr>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Package+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Tests+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Errors+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Failures+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Skipped+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+mainScript.progress(v.Success_Rate)+"</td>");
			$("#packages-content .panel-body table tbody tr:last").append("<td>"+v.Time+"</td>");
		});
		
		$("#packages-content .panel-body table tbody tr").click(function()
		{
			var pkg = $(this).find("td:first").html();
			var selected = "";
			$("#packages-name").html("Package : <b>"+ pkg+"</b>");
			$("#package-info").hide();
			$("#packages-detail").fadeIn(500);
			$.each(packages.pkgsList,function(i,v)
			{
				if(v.Package === pkg)
				{
					selected = v.table;
					return;
				}
			});
			
			$("#packages-detail thead tr").html("");
			$("#packages-detail tbody").html("");
			
			$.each(selected[0],function(n,v)
			{
				$("#packages-detail thead tr").append("<th>"+n+"</th>");	
			});
			
			$.each(selected,function(n,v)
			{
				var icon = "";
				$("#packages-detail  table tbody").append("<tr></tr>");
				if(v.Result === "OK")
				{
					icon = "<span class='fa fa-check-circle'></span>";
					$("#packages-detail  table tbody tr:last").append("<td style='color:green;padding-left:20px;'>"+icon+"</td>");
				}
				else
				{
					icon = "<span class='fa fa-exclamation-triangle'></span>";
					$("#packages-detail  table tbody tr:last").append("<td style='color:red;padding-left:20px;'>"+icon+"</td>");
				}
				
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Class+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Tests+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Errors+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Failures+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Skipped+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+mainScript.progress(v.Success_Rate)+"</td>");
				$("#packages-detail  table tbody tr:last").append("<td>"+v.Time+"</td>");
			});
			
		});
		$("#packages-content .panel-body table tbody tr").css("cursor","pointer")
		
	},
	testCaseView : function()
	{
		$.each(testCases,function(index,val)
		{
			var pctg = "";
			$.each(packages.pkgsList,function(n,v)
			{
				
				$.each(v.table,function(i,data)
				{
					if(data.Class === val.testSuite)
					{
						pctg = data.Success_Rate;
						
					}
				});
						
			});
			$("#testcase-list").append("<li data-suite-index='"+index+"'style='border-radius:4px;padding-left:10px;cursor:pointer;font-size:15px;border-bottom: thin solid #ddd; box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.05);'><span class='fa fa-cogs'></span>&nbsp;"+val.testSuite+"<span class='pull-right'>"+pctg+"</span></li>");
			
		});
		
		$("#testcase-list li").click(function()
		{
			var index = $(this).attr("data-suite-index");
			var obj = testCases[index];
			$("#testCases-result  .panel-title").html("<span class='fa fa-cogs'></span>&nbsp;"+obj.testSuite);
			$("#testCases-result table thead tr").html("");
			$("#testCases-result table tbody").html("");
			
			
			$.each(obj.table[0],function(n,v)
			{
				$("#testCases-result table thead tr").append("<th>"+n+"</th>");
			});
			
			$.each(obj.table,function(i,v)
			{
				$("#testCases-result  table tbody").append("<tr></tr>");
				if(v.Result === "OK")
				{
					icon = "<span class='fa fa-check-circle'></span>";
					$("#testCases-result  table tbody tr:last").append("<td style='color:green;padding-left:20px;'>"+icon+"</td>");
					$("#testCases-result  table tbody tr:last").append("<td>"+v.nMethod+"</td>");
				}
				else
				{
					icon = "<span class='fa fa-exclamation-triangle'></span>";
					$("#testCases-result  table tbody tr:last").append("<td style='color:red;padding-left:20px;'>"+icon+"</td>");
					var err = v.nMethod.split("##");
					$("#testCases-result  table tbody tr:last").append("<td>"+err[0]+"</td>");
					$("#testCases-result  table tbody tr:last").css("cursor","pointer");
					$("#testCases-result  table tbody tr:last").click(function()
					{
						$("#modalErrorDetail h4").html("<span class='fa fa-exclamation-triangle fa-2x' style='color:red;'></span>&nbsp;&nbsp;"+err[0]);
						$("#causeError").html(err[1]);
						$("#traceError").html(err[2]);
						$('#modalErrorDetail').modal('show');
					});
				}
				
				
				$("#testCases-result table tbody tr:last").append("<td>"+v.Time+"</td>");
				
			});
			
			var pctg = "";
			$.each(packages.pkgsList,function(n,v)
			{
				
				$.each(v.table,function(i,v)
				{
					if(v.Class === obj.testSuite)
					{
						console.log(v.Success_Rate);
						var bar = mainScript.progress(v.Success_Rate)
						$("#testSuite-bar").html(bar);
					}
				});
				
			});
			
			$("#testCases-result").fadeIn(500);
		});
		
		$("#testcase-list li").hover(function()
		{
			$(this).css({"color":"#428bca","font-size":"15px"});
		},
		function()
		{
			$(this).css({"color":"black","font-size":"15px"});
		});
	},
	viewErrors : function()
	{
		var listErrors = [];
		$.each(testCases,function(index,val)
		{
			var obj = {};
			obj.testSuite = val.testSuite;
			obj.table = [];
			$.each(val.table,function(i,data)
			{
				if(data.Result == "KO")
				{
					obj.table.push(data)
				}
			});
			listErrors.push(obj)
		});
		
		$.each(listErrors,function(index,val)
		{
			$.each(val.table,function(i,v)
			{
				$("#errors-content  table tbody").append("<tr></tr>");
				$("#errors-content  table tbody tr:last").append("<td>"+val.testSuite+"</td>");
				icon = "<span class='fa fa-exclamation-triangle'></span>";
				$("#errors-content  table tbody tr:last").append("<td style='color:red;padding-left:20px;'>"+icon+"</td>");
				var err = v.nMethod.split("##");
				$("#errors-content  table tbody tr:last").append("<td>"+err[0]+"</td>");
				$("#errors-content  table tbody tr:last").css("cursor","pointer");
				$("#errors-content  table tbody tr:last").click(function()
				{
					$("#modalErrorDetail h4").html("<span class='fa fa-exclamation-triangle fa-2x' style='color:red;'></span>&nbsp;&nbsp;"+err[0]);
					$("#causeError").html(err[1]);
					$("#traceError").html(err[2]);
					$('#modalErrorDetail').modal('show');
				});
				$("#errors-content table tbody tr:last").append("<td>"+v.Time+"</td>");
			});
			
		});
	},
	init : function()
	{
		$("#menuTabs li").click(function()
		{
			$("#menuTabs li").removeClass("active");
			$(this).addClass("active");
			$("#content > div").hide();
			var id = $(this).attr("data-link");
			$("#"+id).fadeIn(500);
		});
		
		mainScript.summaryView();
		mainScript.packageView();
		mainScript.testCaseView();
		mainScript.viewErrors();
	}
}