/*
 * 
 *  This file contains all the classes for HAR specification. Where ever the specification contains elements that are not
 *  required for HTD file cretion, I have skipped those elements (or have commented them out). I figured why increase the code bases
 *  unnecessarily. However, in the future if the requirement arises, here is the place that you need to make the changes. Also in these classes
 *  I might have added an element or two (like isHttps in the Request Class).
 *  
 *  The flow is essentially is like this:
 *  
 *  READ .HAR FILE (JSON DATA) ----> Unmarshall into Objects that have been specified in this file ---> Give the request/response to TrafficViewerSDK
 *  to create a HTD file
 *  
 * I accessed these specifications @
 * 
 *  Jan Odvarko (Software is hard) "HAR 1.2 Spec" (www.softwareishard.com/blog/har-12-spec/) accessed on June 19, 2013
 * 
 *  @author Kiran Shirali (kshirali@ebay.com)
 * */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


using Newtonsoft.Json;

namespace HtdConvertor
{

    /*
     *  Class for High level 'Har' Object.
     *  
     * Summary of HAR object types:
         * log
         * creator
         * browser
         * pages
         * pageTimings
         * entries
         * request
         * response
         * cookies
         * headers
         * queryString
         * postData
         * params
         * content
         * cache
         * timings
     * 
     */
    public class Har
    {
        public Log log { get; set; }

    }

    /**
     * The specification for a Log object is as follows:
     *    
     * "log": {        
     * "version" : "1.2",     
     * "creator" : {},     
     * "browser" : {},      
     * "pages": [],      
     * "entries": [],     
     * "comment": ""
        
     * }
     * 
     * 
     * The only important attribute for us is: 
     * 
     * entries [array] - List of all exported (tracked) requests.
     * 
     */
    public class Log
    {
        public Entry[] entries { get; set; }
    }


    /*
     * 
     * The specification for a Entries object is as follows:
     * 
     * "entries": [ 
     * {     
     * "pageref": "page_0",  
     * "startedDateTime": "2009-04-16T12:07:23.596Z",       
     * "time": 50,       
     * "request": {...},      
     * "response": {...},       
     * "cache": {...},       
     * "timings": {},       
     * "serverIPAddress": "10.0.0.1",       
     * "connection": "52492",
     * "comment": ""  
     * }
     * ]
     * 
     * I am going to create an Enry object and make it a list in 'Log' Object. 
     * 
     * request [object] - Detailed info about the request.
     * response [object] - Detailed info about the response.
     * 
     */
    public class Entry
    {

        public Request request { get; set; }
        public Response response { get; set; }

    }

    /*
     * 
     * 
     * This object contains detailed info about performed request.
     * 
     * "request": {
         * "method": "GET",    
         * "url": "http://www.example.com/path/?param=value",  
         * "httpVersion": "HTTP/1.1",  
         * "cookies": [], 
         * "headers": [],
         * "queryString" : [],
         * "postData" : {},
         * "headersSize" : 150, 
         * "bodySize" : 0,
         * "comment" : ""
         * }
     * 
     * 
     * 
     */
    public class Request
    {
        public String method { get; set; }
        public String url { get; set; }
        public String httpVersion { get; set; }
            // Currently not required. Check a little below in the code for the reason.
            //public QueryString[] queryString { get; set; }
        public Header[] headers { get; set; }
            // Currently not required. Check a little below in the code for the reason.
            //public Cookie[] cookies { get; set; }
        public PostData postData { get; set; }
            // Currently not required
            //public int headersSize { get; set; }
            // Currently not required
            //public int bodySize { get; set; }
            // Currently not required
            //public String comment { get; set; }

        // This is not part of the HAR Specification. I need this boolean to denote whether this request
        // is a https request or not. One of the numerous problems converting a HAR to HTD file. Wonder why did IBM
        // decide to go for a proprietary specification??? :(
        public Boolean isHttps { get; set; }


        // And it is here that I am reconstructing the entire Request. It is going to be a simple text with each of the entry
        // in new lines and relevant spaces. That is apparently how the SDK of IBM requires it (TODO: Chuck HTD files in the future)
        // An example GET request:
        //      GET / HTTP/1.1
        //      Host: exampleSite.com
        //      User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0
        //      Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
        //      Accept-Language: en-US,en;q=0.5
        //      Connection: keep-alive
        public override String ToString()
        {

            StringBuilder tempString = new StringBuilder("");

            // Append the GET or POST request method.
            // So a request would look like:
            // GET 
            if (method != null && method != "")
            {
                tempString.Append(method);
                tempString.Append(" ");
            }

            // Append the URL.
            // So a request would look like:
            // GET /
            if (url != null && url != "")
            {
                tempString.Append(url);
                tempString.Append(" ");
            }


            /***********************************************************************************************************************/

            // Apparently the query string is unnecessary. Because the query string is also present in the relative 'url' <see above>
            // So if you have a request like: http://www.example.com/addfield?name=name&desig=CoolDeveloper
            // then the above field 'url' would hold "/addfield?name=name&desig=CoolDeveloper"
            // AppScan will extract this data here it self. So commenting out the below code.

                //if (queryString != null && queryString.Length > 0)
                //{
                //    tempString.Append("?");
                //    Boolean firstParameter = true;

                //    foreach (QueryString tempQueryString in queryString)
                //    {
                //        if (firstParameter)
                //        {
                //            firstParameter = false;
                //        }
                //        else
                //        {
                //            tempString.Append("&");
                //        }
                //       
                //        if (tempQueryString.ToString() != null)
                //        {
                //            tempString.Append(tempQueryString.ToString());
                //        }
                //    }
                //}

                //tempString.Append(" ");

            /***********************************************************************************************************************/

            // Append the HTTP Version. Browser mob is not sending the HTTP version. It sends HTTP and not HTTP/1.1 Weird !! 
            // I am manually appending '/1.1' here. Need to look into browser mob.
            //
            // So a request would look like:
            // GET / HTTP/1.1
            if (httpVersion != null && httpVersion != "")
            {
                tempString.AppendLine(httpVersion + "/1.1");
            }


            // Append the each of the header values that come in the request.
            // So a request would look like:
            //      GET / HTTP/1.1
            //      Host: exampleSite.com
            //      User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0
            //      Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
            //      Accept-Language: en-US,en;q=0.5
            //      Connection: keep-alive
            if (headers != null && headers.Length > 0)
            {

                foreach (Header tempHeader in headers)
                {
                    if (tempHeader.ToString() != null)
                    {
                        tempString.AppendLine(tempHeader.ToString());



                        if (tempHeader.name != null
                                && tempHeader.name.Equals("Host", StringComparison.InvariantCultureIgnoreCase)
                                    && tempHeader.value != null)
                        {
                            String requestStringToBeClipped = null;

                            if (url.StartsWith("http://"))
                            {
                                requestStringToBeClipped = "http://" + tempHeader.value;
                            }
                            else if (url.StartsWith("https://"))
                            {
                                requestStringToBeClipped = "https://" + tempHeader.value;
                                isHttps = true;
                            }
                            else if (url.StartsWith(tempHeader.value))
                            {
                                requestStringToBeClipped = tempHeader.value;
                            }

                            tempString = tempString.Replace(requestStringToBeClipped, "");
                        }


                    }
                }
            }

            /********************************************************************************************************************/
            // Currently all the Cookie information is being recorded in the headers. So this section is irrelevant. Kepping this
            // code commented. (TODO: test and ensure that this section need not be appended to the 'request')

            //if (cookies != null && cookies.Length > 0)
            //{
            //    tempString.Append("Cookie: ");
            //    foreach (Cookie tempCookie in cookies)
            //    {
            //        if (tempCookie.ToString() != null && tempCookie.ToString() != "")
            //        {
            //            tempString.Append(tempCookie.ToString());
            //        }
            //    }
            //}

            /********************************************************************************************************************/

            tempString.AppendLine();

            if (postData != null && postData.ToString() != "")
            {
                
                //tempString.AppendLine();
                tempString.AppendLine(postData.ToString());
                //tempString.AppendLine();
            }

            return tempString.ToString();
        }
    }

    public class Header
    {
        public String name { get; set; }
        public String value { get; set; }
        public String comment { get; set; }


        public override String ToString()
        {

            if (name != null && name != "")
            {
                return name + ": " + value;
            }
            return null;
        }

    }

    public class Cookie
    {
        public String name { get; set; }
        public String value { get; set; }
        public String path { get; set; }
        public String domain { get; set; }
        public String expires { get; set; }
        public Boolean httpOnly { get; set; }
        public Boolean secure { get; set; }
        public String comment { get; set; }

        public override string ToString()
        {
            StringBuilder tempString = new StringBuilder("");

            if (name != null && name != "")
            {
                tempString.Append(name + "=" + value + "; ");

                if (path != null && path != "")
                {
                    tempString.Append("Path=" + path + "; ");
                }

                if (domain != null && domain != "")
                {
                    tempString.Append("Domain=" + domain + "; ");
                }

                if (expires != null && expires != "")
                {
                    tempString.Append("Expires=" + expires + "; ");
                }

                if (secure)
                {
                    tempString.Append("Secure; ");
                }

                if (httpOnly)
                {
                    tempString.Append("HttpOnly; ");
                }
            }

            return tempString.ToString();
        }

    }


    public class QueryString
    {

        public String name { get; set; }
        public String value { get; set; }
        public String comment { get; set; }

        public override String ToString()
        {

            if (name != null && name != "")
            {
                return name + "=" + value;
            }
            return null;
        }
    }


    /**
     * 
     * This object describes posted data, if any (embedded in request object)
     * "postData": {
     *      "mimeType": "multipart/form-data",   
     *      "params": [],  
     *      "text" : "plain posted data",  
     *      "comment": ""
     * }
     * 
     * 
     * The only relevant information is params [array] - List of posted parameters (in case of URL encoded parameters).
     * 
     */
    public class PostData
    {
        [JsonProperty("params")]
        public Param[] parameters { get; set; }

        public override string ToString()
        {
            StringBuilder tempString = new StringBuilder("");

            if (parameters != null && parameters.Length > 0)
            {
                Boolean firstElement = true;
                foreach (Param tempParam in parameters)
                {
                    if (tempParam.ToString() != null)
                    {
                        if (firstElement)
                        {
                            tempString.Append(tempParam.ToString());
                            firstElement = false;
                        }
                        else
                        {
                            tempString.Append("&"+tempParam.ToString());
                        }
                        
                    }
                }
            }

            return tempString.ToString();
        }
    }


    /**
     * 
     * List of posted parameters, if any (embedded in postData object)
     * 
     * "params": [  
         * {
         * "name": "paramName",
         * "value": "paramValue",
         * 
         * "fileName": "example.pdf",
         * "contentType": "application/pdf",
         * "comment": ""
         * }
     * ]
     * 
     * Currently the important spec information is
     *      name [string] - name of a posted parameter.
     *      value [string, optional] - value of a posted parameter or content of a posted file.
     * 
     */
    public class Param
    {
        public String name { get; set; }
        public String value { get; set; }

        public override string ToString()
        {
            if (name != null && name != "")
            {

                StringBuilder tempString = new StringBuilder(name);

                if (value != null && value != "")
                {
                    tempString.Append("=" + HtdConvertorUtil.encodeString(value));
                }
                else
                {
                    tempString.Append("=%00");
                }
                return tempString.ToString();
            }
            else
            {
                return null;
            }
        }
    }

    public class Response
    {
        public String httpVersion { get; set; }
        public int status { get; set; }
        public String statusText { get; set; }
        public Header[] headers { get; set; }
        public Cookie[] cookies { get; set; }
        public String redirectURL { get; set; } // Not going to use
        public int headersSize { get; set; } // Not going to use this
        public int bodySize { get; set; } // Not going to use this
        public String comment { get; set; } // Not going to use this

        public Content content { get; set; }

        public override string ToString()
        {
            StringBuilder tempString = new StringBuilder("");

            if (httpVersion != null && httpVersion != ""
                //&& status != null
                    && statusText != null && statusText != "")
            {

                tempString.AppendLine(httpVersion + "/1.1" + " " + status + " " + statusText);

                if (headers != null && headers.Length > 0)
                {

                    foreach (Header tempHeader in headers)
                    {
                        if (tempHeader.ToString() != null)
                        {
                            tempString.AppendLine(tempHeader.ToString());
                        }
                    }
                }

                //if (cookies != null && cookies.Length > 0)
                //{
                //    tempString.Append("Cookie: ");
                //    foreach (Cookie tempCookie in cookies)
                //    {
                //        if (tempCookie.ToString() != null && tempCookie.ToString() != "")
                //        {
                //            tempString.Append(tempCookie.ToString());
                //        }
                //    }
                //}

                if (content != null)
                {

                    tempString.AppendLine(content.ToString());

                }
                //tempString.AppendLine("");
                
            }
            return tempString.ToString();
        }
    }


    public class Content
    {

        public String mimeType { get; set; }
        public int size { get; set; }
        public int compression { get; set; } // Not going to use this
        public String text { get; set; }
        public String comment { get; set; } // Not going to use this

        public override string ToString()
        {
            StringBuilder tempString = new StringBuilder("");

            if (mimeType != null && mimeType != "")
            {

                tempString.AppendLine("Content-Type: " + mimeType);

            }

            if (size > 0)
            {

                tempString.AppendLine("Content-Length: " + size);
            }

            if (text != null && text != "")
            {

                tempString.AppendLine("");
                tempString.AppendLine(text);
            }


            return tempString.ToString();
        }
    }
}
