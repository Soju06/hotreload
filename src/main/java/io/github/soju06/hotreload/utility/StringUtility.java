package io.github.soju06.hotreload.utility;

public class StringUtility {
    public static String format(String f, Object... args) {
        var len = f.length();
        int status = 0, argsIndex = 0;
        var sb = new StringBuilder();
        var inFormat = new StringBuilder();
        for (int i = 0; i < len; i++) {
            var c = f.charAt(i);

            if (c == '{' && status == 0) status = 1;
            else if (status == 1) {
                if (c == '}') {
//                    var format = inFormat.toString();
//                    if (format.length() > 2) {
//                        if (format.charAt(0) == '?' && Character.isDigit(format.charAt(1))) {
//                            d
//                        }
//                    }
                    var obj = args[argsIndex++];
                    // OBJECT FORMAT HERE <=
                    sb.append(obj);
                    status = 0;
                    inFormat.setLength(0);
                } else inFormat.append(c);
            } else sb.append(c);
        }
        return sb.toString();
    }
}
