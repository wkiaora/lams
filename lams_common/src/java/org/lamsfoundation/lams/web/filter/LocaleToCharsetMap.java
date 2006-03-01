/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */
package org.lamsfoundation.lams.web.filter;

import java.util.Hashtable;
import java.util.Locale;


/** 
* A mapping to determine the (somewhat arbitrarily) preferred charset for 
* a given locale.  Supports all locales recognized in JDK 1.1.
* This class was originally written by Jason Hunter [jhunter@acm.org]
* as part of the book "Java Servlet Programming" (O'Reilly).
* See <a href="http://www.servlets.com/book">
* http://www.servlets.com/book</a> for more information.
* Used by Sun Microsystems with permission.
*/
  public class LocaleToCharsetMap {
  
    private static Hashtable map;
  
    static {
      map = new Hashtable();
  
      map.put("ar", "ISO-8859-6");
      map.put("be", "ISO-8859-5");
      map.put("bg", "ISO-8859-5");
      map.put("ca", "ISO-8859-1");
      map.put("cs", "ISO-8859-2");
      map.put("da", "ISO-8859-1");
      map.put("de", "ISO-8859-1");
     map.put("el", "ISO-8859-7");
     map.put("en", "ISO-8859-1");
     map.put("es", "ISO-8859-1");
     map.put("et", "ISO-8859-1");
     map.put("fi", "ISO-8859-1");
     map.put("fr", "ISO-8859-1");
     map.put("hr", "ISO-8859-2");
     map.put("hu", "ISO-8859-2");
     map.put("is", "ISO-8859-1");
     map.put("it", "ISO-8859-1");
     map.put("iw", "ISO-8859-8");
     map.put("ja", "Shift_JIS");
     map.put("ko", "EUC-KR");     // Requires JDK 1.1.6
     map.put("lt", "ISO-8859-2");
     map.put("lv", "ISO-8859-2");
     map.put("mk", "ISO-8859-5");
     map.put("nl", "ISO-8859-1");
     map.put("no", "ISO-8859-1");
     map.put("pl", "ISO-8859-2");
     map.put("pt", "ISO-8859-1");
     map.put("ro", "ISO-8859-2");
     map.put("ru", "ISO-8859-5");
     map.put("sh", "ISO-8859-5");
     map.put("sk", "ISO-8859-2");
     map.put("sl", "ISO-8859-2");
     map.put("sq", "ISO-8859-2");
     map.put("sr", "ISO-8859-5");
     map.put("sv", "ISO-8859-1");
     map.put("tr", "ISO-8859-9");
     map.put("uk", "ISO-8859-5");
     map.put("zh", "GB2312");
     map.put("zh_TW", "Big5");
 
   }
 
   /**
    * Gets the preferred charset for the given locale, or null if the locale
    * is not recognized.
    *
    * @param loc the locale
    * @return the preferred charset
    */
   public static String getCharset(Locale loc) {
     String charset;
 
     // Try for an full name match (may include country)
     charset = (String) map.get(loc.toString());
     if (charset != null) return charset;
 
     // If a full name didn't match, try just the language
     charset = (String) map.get(loc.getLanguage());
     return charset;  // may be null
   }
}