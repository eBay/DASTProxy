using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace HtdConvertor
{
    class ConvertorProperties
    {

        public static string ExcludedDomainsFromRecordingPattern
        {
            get
            {
                string[] strArray = "www.gstatic.com,mozilla.webahead.ibm.com".Split(new char[3]
        {
          ',',
          '\n',
          '\r'
        }, StringSplitOptions.RemoveEmptyEntries);
                string str1 = "";
                foreach (string str2 in strArray)
                    str1 = str1 + "(Host:\\s*" + Regex.Escape(str2.Trim()) + ")|";
                return str1.Substring(0, str1.Length - 1);
            }
        }
    }
}
