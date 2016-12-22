using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.IO;
using TrafficViewerSDK;
using TrafficViewerSDK.Http;
using Newtonsoft.Json.Serialization;
using Newtonsoft.Json;

namespace HtdConvertor
{

    class Convertor
    {
        static void Main(string[] args)
        {

            TrafficViewerFile file = new TrafficViewerFile();
            file.Profile.SetExclusions((IEnumerable<string>)new string[2]
              {
                "\\.(js|axd|zip|Z|tar|t?gz|sit|cab|pdf|ps|doc|ppt|xls|rtf|dot|mp(p|t|d|e|a|3|4|ga)|m4p|mdb|csv|pp(s|a)|xl(w|a)|dbf|slk|prn|dif|avi|mpe?g|mov(ie)?|qt|moov|rmi?|as(f|x)|m1v|wm(v|f|a)|wav|ra|au|aiff|midi?|m3u|gif|jpe?g|bmp|png|tif?f|ico|pcx|css|xml|dll)\\b",
                ConvertorProperties.ExcludedDomainsFromRecordingPattern
              });


            try
            {

                // Create an instance of StreamReader to read from a file.
                // The using statement also closes the StreamReader.
                using (StreamReader sr = new StreamReader(args[0]))
                {

                    String line;
                    // Read and display lines from the file until the end of 
                    // the file is reached.
                    while ((line = sr.ReadLine()) != null)
                    {
                        Har har = JsonConvert.DeserializeObject<Har>(line);
                        int requestHeaderId = 0;
                        int counter = 0;
                        foreach (Entry tempEntry in har.log.entries)
                        {

                            if (HtdConvertorUtil.isRelevantRequest(tempEntry))
                            {
                                counter++;
                                requestHeaderId = file.AddRequestResponse(tempEntry.request.ToString(), tempEntry.response.ToString());

                                if (tempEntry.request.postData != null)
                                {
                                    Console.WriteLine(tempEntry.request.postData.ToString());
                                }

                                file.GetRequestInfo(requestHeaderId).Description = "AppScan Proxy Request to Server";

                                if (tempEntry.request.isHttps)
                                {
                                    file.GetRequestInfo(requestHeaderId).IsHttps = true;
                                }


                            }


                        }
                        Console.WriteLine(counter);

                    }
                }


                file.Save(args[1]);
                file.Close(false);
                Console.WriteLine("Recording has been done");
                //Console.ReadLine();
            }
            catch (Exception e)
            {
                // Let the user know what went wrong.
                Console.WriteLine("The file could not be read:");
                Console.WriteLine(e.Message);
            }
        }
    }
}
