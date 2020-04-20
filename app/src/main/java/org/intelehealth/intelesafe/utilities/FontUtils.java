package org.intelehealth.intelesafe.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;

public class FontUtils {

    public static SpannableString typeface(Context context, int font, CharSequence string) {
        Typeface typeface = ResourcesCompat.getFont(context, font);
        SpannableString s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(typeface), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }
}
