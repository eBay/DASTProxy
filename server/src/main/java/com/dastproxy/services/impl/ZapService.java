/**
 * @author Srinivasa Rao (schirathanagandl@ebay.com)
 */

package com.dastproxy.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.dastproxy.model.Issue;
import com.dastproxy.model.Report;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.lightbody.bmp.core.har.copy.Har;
import net.lightbody.bmp.core.har.copy.HarEntry;

@Service
@Qualifier("zapService")
public class ZapService {

	private ClientApi clientApi = new ClientApi("localhost", 8080);
	
	private int statusToInt(ApiResponse response) {
		return Integer.parseInt(((ApiResponseElement)response).getValue());
	}
	
	
    public void activeScanSiteInScope(String apikey, String url, String method, String postData) throws Exception {
        ApiResponse response = clientApi.ascan.scan(apikey, url, "true", "true", "", method, postData);
        System.out.println("--------Inside ZapService ...------------------------response.getName()="+response.getName());
        // Poll until active scan is finished
        int status = 0;
        while ( status < 100) {
            status = statusToInt(clientApi.ascan.status(""));
            String format = "Scanning %s Progress: %d%%";
            System.out.println(String.format(format, url, status));
        }
    }
    public List<Alert> scan(String url, String method, String postData) {
    	List<Alert> alerts = null;
    
        try {
			System.out.println("--------Inside ZapService ...scan");
			clientApi.accessUrl(url);
			activeScanSiteInScope(null, url, method, postData);
			alerts = clientApi.getAlerts(url, -1, -1);
            System.out.println("--------Inside ZapService ...alerts.size()="+alerts.size());
            clientApi.core.deleteAllAlerts(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------Inside ZapService ...Done!");
        return alerts;
    }
  
    
    public void spider(String url, Integer maxChildren, boolean recurse, String contextName, String subTreeOnly) {
        // Defaulting the context to "Default Context" in ZAP
        
    	String contextNameString = contextName == null ? ""	 : contextName;
        String maxChildrenString = maxChildren == null ? null : String.valueOf(maxChildren);

        try {
            clientApi.spider.scan(null,url,"50","true","","");
            
            List<Alert> alerts = clientApi.getAlerts("", -1, -1);
            System.out.println("--------Inside ZapService ...alerts.size()="+alerts.size());
            System.out.println("--------Inside ZapService ...url="+url);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void scanWithZap(String fileName, Report report){
    	List<Alert> alerts = new ArrayList<Alert>();
		ObjectMapper jacksonObjectMapper = new ObjectMapper();
		String url = null, httpMethod = null, postData = null; 
		try {
			List<String> urls = new ArrayList<String>();
			Har har = jacksonObjectMapper.readValue(new File(fileName), Har.class);
			List<HarEntry> harEntries = har.getLog().getEntries();
			if (harEntries != null && harEntries.size() > 0){
				for (HarEntry harEntry : harEntries){
					url = harEntry.getRequest().getUrl();
					if (url!=null){
						String urlLower =url.toLowerCase(); 
						if (urlLower.endsWith(".gif") || urlLower.endsWith(".jsp") || urlLower.endsWith(".png") || urlLower.endsWith(".ico") || urlLower.contains("firefox.com/"))continue;
					}
					httpMethod = harEntry.getRequest().getMethod();
					if (httpMethod!=null && httpMethod.toLowerCase().equals("post")) postData = har.getLog().getEntries().get(0).getRequest().getPostData().getText();
					if (!urls.contains(url)){
						urls.add(url);
						alerts.addAll(scan(url, httpMethod, postData));
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		getZapAlertsToDastIssues(report, alerts);
     
    }
    public int getScanProgress() throws ClientApiException{
        ApiResponseList scans = (ApiResponseList) clientApi.ascan.scans();
        
        ApiResponseSet lastScan = (ApiResponseSet)scans.getItems().get(scans.getItems().size() -1);
        return Integer.parseInt(lastScan.getAttribute("progress"));
    }
    
    public List<Issue> getZapAlertsToDastIssues(Report report, List<Alert> alerts)
			{

		// List of issues for a scan
		List<Issue> listOfIssues = new LinkedList<Issue>();

		// create an issue object and set the properties
		int iter=1;
		List<String> uniqueAlerts = new ArrayList<String>();
		for (Alert alert: alerts){
			
			//Avoiding the duplicates
			if (!uniqueAlerts.contains(alert.getAlert()+alert.getUrl())){
				uniqueAlerts.add(alert.getAlert()+alert.getUrl());
			} else continue;
			 
			final Issue issue = new Issue();
			issue.setIssueType(alert.getAlert());
			issue.setIssueUrl(alert.getUrl());
			issue.setScanEngine("ZAP");
			issue.setSeverity(alert.getRisk().name());
			issue.setTestUrl(alert.getUrl());
			
			issue.setNativeIssueId(""+50000+alert.getCweId()*17+alert.getWascId()+iter*10);
			
			issue.setReport(report);
			iter++;
			listOfIssues.add(issue);
			if (iter > 200) break;
		}
		System.out.println("---------------------------listOfIssues.size()="+listOfIssues.size());
		report.setIssues(listOfIssues);
		return listOfIssues;
	}
	

    public static void main(String[] s){
    	ZapService service = new ZapService();
    	//service.s("https://signin.qa.ebay.com/ws/eBayISAPI.dll?SignIn&ru=http%3A%2F%2Fwww.qa.ebay.com%2F");
    	//service.spider("http://10.147.213.54/deal_management/", new Integer(50), true, null, "false");
    	service.spider("http://www.deals.fp.stratus.qa.ebay.com/deals/tech/cell-phones/%22", new Integer(50), true, null, "false");
    	//service.scan("http://www.qa.ebay.com");
    	//service.scan("http://10.147.213.54/deal_management/");
    	//List<Issue> issues = service.scanHarFile("C:\\HARtoHTDConvertor\\Users\\schirathanagandl\\Recordings-schirathanagandl-03-11-2016_07.56.17.har",new Report(), "testcaseName");
        //System.out.println("--------Inside ZapService...main ...issues.size()="+issues.size());
        /*
        for (Alert alert: alerts){
            System.out.println("--------alert.toString()..1="+alert.getAlert());
            System.out.println("--------alert.toString()..2="+alert.getAttack());
            System.out.println("--------alert.toString()..3="+alert.getCweId());
            System.out.println("--------alert.toString()..4="+alert.getOther());
            System.out.println("--------alert.toString()..5="+alert.getDescription());
            System.out.println("--------alert.toString()..6="+alert.getEvidence());
            System.out.println("--------alert.toString()..7="+alert.getParam());
            System.out.println("--------alert.toString()..8="+alert.getReference());
            System.out.println("--------alert.toString()..9="+alert.getSolution());
            System.out.println("--------alert.toString()..10="+alert.getUrl());
            System.out.println("--------alert.toString()..11="+alert.getWascId());
            System.out.println("--------alert.toString()..12="+alert.getConfidence().name());
            System.out.println("--------alert.toString()..13="+alert.getRisk().name());

        }
        */
    	//
    }

}