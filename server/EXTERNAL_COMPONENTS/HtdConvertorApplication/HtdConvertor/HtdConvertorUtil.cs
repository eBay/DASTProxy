using System;
using System.Collections.Generic;
using System.Linq;
/**
 *  This file contains the Util class. This class would contain all the necessary functions that would be used through out the application.
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 */

using System.Text;

namespace HtdConvertor
{
    static class HtdConvertorUtil
    {
        public static Boolean isRelevantRequest(Entry entryToBeChecked)
        {

            Boolean isRelevant = true;
            String requestUrl = entryToBeChecked.request.url.ToLower();

            if (requestUrl.Contains(".js")
                    || requestUrl.Contains(".ico")
                        || requestUrl.Contains(".png")
                            || requestUrl.Contains(".gif")
                                    || requestUrl.Contains(".css"))
            {
                isRelevant = false;
            }

            return isRelevant;
            //return true;
        }

        /*
         * This function is to encode strings for AppScan Enterprise to read. It is my observation that ASE throws an error when
         * certain codes are not encoded (especially null and space characters).
         */
        public static String encodeString(String stringToBeEncoded)
        {
            StringBuilder tempString = new StringBuilder(stringToBeEncoded);
            tempString.Replace(" ", "%20");
            tempString.Replace("^", "%5E");
            tempString.Replace("$", "%24");
            tempString.Replace("@", "%40");

            /*
            tempString.Replace("!", "%21");
            tempString.Replace("\"", "%22");
            tempString.Replace("#", "%23");
            tempString.Replace("%", "%25");
            tempString.Replace("&", "%26");
            tempString.Replace("\'", "%27");
            tempString.Replace("(", "%28");
            tempString.Replace(")", "%29");
            tempString.Replace("*", "%2A");
            tempString.Replace("+", "%2B");
            tempString.Replace(",", "%2C");
            tempString.Replace("-", "%2D");
            tempString.Replace(".", "%2E");
            tempString.Replace("/", "%2F");
             * */

            return tempString.ToString();
        }
    }

        
   
}
