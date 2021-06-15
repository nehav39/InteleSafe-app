/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.intelehealth.intelesafe.utilities;

import android.widget.Spinner;

import java.io.File;
import java.util.List;

import org.intelehealth.intelesafe.app.IntelehealthApplication;
import org.intelehealth.intelesafe.R;

public final class StringUtils {
    private static final String NULL_AS_STRING = "null";
    private static final String SPACE_CHAR = " ";

    public static boolean notNull(String string) {
        return null != string && !NULL_AS_STRING.equals(string.trim());
    }

    public static boolean isBlank(String string) {
        return null == string || SPACE_CHAR.equals(string);
    }

    public static boolean notEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                    default:
                        // Do nothing
                        break;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String getValue(String value) {
        String val = "";
        if (value != null)
            val = value;
        return val;

    }

    public static String getValue1(String value) {
        String val = " ";
        if (value != null)
            val = value;
        return val;

    }

    public static String getProvided(Spinner spinner) {
        String val = "";
        if (spinner.getSelectedItemPosition() == 0)
            val = IntelehealthApplication.getAppContext().getString(R.string.not_provided);
        else
            val = spinner.getSelectedItem().toString();

        return val;
    }

    public static String getFileNameWithoutExtension(File file) {
        String fileName = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                fileName = name.replaceFirst("[.][^.]+$", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "";
        }

        return fileName;

    }

    public static String getFileNameWithoutExtensionString(String filename) {
        String fileName = "";

        try {
            if (filename.indexOf(".") > 0)
                fileName = filename.substring(0, filename.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
            fileName = "";
        }

        return fileName;

    }

    public static String convertUsingStringBuilder(List<String> names) {
        StringBuilder namesStr = new StringBuilder();
        for (String name : names) {
            namesStr = namesStr.length() > 0 ? namesStr.append("','").append(name) : namesStr.append(name);
        }
        return namesStr.toString();
    }

    public static String mobileNumberEmpty(String value) {
        String val = "N/A";
        if (value != null && !value.isEmpty())
            val = value;
        return val;
    }

    public static String switch_hi_state(String val) {
        switch (val) {
            case "झारखण्ड":
                val = "Jharkhand";
                break;
            case "मध्य प्रदेश":
                val = "Madhya Pradesh";
                break;
            case "चयन करें":
                val = "Select";
                break;
            default:
                return val;
        }
        return val;
    }
    public static String switch_hi_city(String val) {
        switch (val) {
            case "रांची":
                val = "Ranchi";
                break;
            case "पूर्वी सिंहभूम":
                val = "East Singhbhum";
                break;
            case "बोकारो":
                val = "Bokaro";
                break;
            case "धनबाद":
                val = "Dhanbad";
                break;
            case "खंडवा":
                val = "Khandwa";
                break;
            case "राजगढ़":
                val = "Rajgarh";
                break;
            case "चयन करें":
                val = "Select";
                break;
            default:
                return val;
        }
        return val;
    }

    public static String switch_hi_block(String val) {
        switch (val) {
            case "बर्मु":
                val = "Burmu";
                break;
            case "खेलेरी":
                val = "Khelari";
                break;
            case "कांके":
                val = "Kanke";
                break;
            case "ओरमांझी":
                val = "Ormanjhi";
                break;
            case "अंगारा":
                val = "Angara";
                break;
            case "रेह":
                val = "Rahe";
                break;
            case "सिल्ली":
                val = "Silli";
                break;
            case "सोनाहातु":
                val = "Sonahatu";
                break;
            case "नामकुम":
                val = "Namkum";
                break;
            case "रातू":
                val = "Ratu";
                break;
            case "नगरी":
                val = "Nagri";
                break;
            case "मंदारी":
                val = "Mandar";
                break;
            case "चान्हो":
                val = "Chanho";
                break;
            case "बेरो":
                val = "Bero";
                break;
            case "इटकि":
                val = "Itki";
                break;
            case "लापुंग":
                val = "Lapung";
                break;
            case "बुन्दु":
                val = "Bundu";
                break;
            case "तामार":
                val = "Tamar";
                break;
            case "बहरागोरा":
                val = "Baharagora";
                break;
            case "बोराम":
                val = "Boram";
                break;
            case "चाकुलिया":
                val = "Chakulia";
                break;
            case "धालभूमगढ़":
                val = "Dhalbhumgarh";
                break;
            case "डुमरिया":
                val = "Dumaria";
                break;
            case "घाटशिला":
                val = "Ghatshila";
                break;
            case "गोलमुरी सह जुगसलाई":
                val = "Golmuri cum Jugsalai";
                break;
            case "गुड़ाबांधा":
                val = "Gudabandha";
                break;
            case "मुसाबनी":
                val = "Musabani";
                break;
            case "पतमदा":
                val = "Patamda";
                break;
            case "पोटका":
                val = "Potka";
                break;
            case "बेरमो":
                val = "Bermo";
                break;
            case "चंदनकियारी":
                val = "Chandankiyari";
                break;
            case "चंद्रपुर":
                val = "Chandrapura";
                break;
            case "चास":
                val = "Chas";
                break;
            case "गुमिया":
                val = "Gumia";
                break;
            case "जरीडीह":
                val = "Jaridih";
                break;
            case "कसमारी":
                val = "Kasmar";
                break;
            case "नवादिह":
                val = "Nawadih";
                break;
            case "पीटरवार":
                val = "Peterwar";
                break;
            case "बाघमार":
                val = "Baghmara";
                break;
            case "बलियापुर":
                val = "Baliapur";
                break;
            case "धनबाद":
                val = "Dhanbad";
                break;
            case "एगरकुंड":
                val = "Egarkund";
                break;
            case "गोविंदपुर":
                val = "Govindpur";
                break;
            case "कलियासोल":
                val = "Kaliasole";
                break;
            case "निरसा":
                val = "Nirsa";
                break;
            case "पूर्वी टुंडी":
                val = "Purvi Tundi";
                break;
            case "तोपचांची":
                val = "Topchanchi";
                break;
            case "टुंडी":
                val = "Tundi";
                break;
            case "अमान्य":
                val = "Invalid";
                break;
            case "चयन करें":
                val = "Select";
                break;
            case "अन्य":
                val = "Other";
                break;
            default:
                return val;
        }
        return val;
    }
}
