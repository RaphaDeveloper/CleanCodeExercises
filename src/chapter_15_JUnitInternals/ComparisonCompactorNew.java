package chapter_15_JUnitInternals;

import junit.framework.Assert;

public class ComparisonCompactorNew {
    private int contextLength;
    private String expected;
    private String actual;
    private int commonPrefix;
    private int commonSuffix;

    public ComparisonCompactorNew(int contextLength,
                                  String expected,
                                  String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    public String compact(String message) {
        if (!shouldCompact())
            return Assert.format(message, expected, actual);

        return computeCompactation(message);
    }

    private boolean shouldCompact() {
        return expected != null && actual != null && !expected.equals(actual);
    }

    private String computeCompactation(String message)
    {
        defineCommonPrefix();
        defineCommonSuffix();

        return Assert.format(message, computeResult(expected), computeResult(actual));
    }

    private void defineCommonPrefix() {
        int end = Math.min(expected.length(), actual.length());

        for (commonPrefix = 0; commonPrefix < end; commonPrefix++) {
            if (expected.charAt(commonPrefix) != actual.charAt(commonPrefix))
                break;
        }
    }

    private void defineCommonSuffix() {
        int expectedSuffix = expected.length() - 1;
        int actualSuffix = actual.length() - 1;

        for (; actualSuffix >= commonPrefix && expectedSuffix >= commonPrefix; actualSuffix--, expectedSuffix--) {
            if (expected.charAt(expectedSuffix) != actual.charAt(actualSuffix))
                break;
        }

        commonSuffix = expected.length() - expectedSuffix;
    }

    private String computeResult(String source) {
        return computeCommonPrefix() + computeDifference(source) + computeCommonSuffix();
    }

    private String computeCommonPrefix() {
        String commonPrefix = getCommonPrefix();

        if (anyPrefixCharacterIsNotVisible())
            commonPrefix = "..." + commonPrefix;

        return commonPrefix;
    }

    private String getCommonPrefix() {
        int start = Math.max(commonPrefix - contextLength, 0);

        return expected.substring(start, commonPrefix);
    }

    private boolean anyPrefixCharacterIsNotVisible() {
        return commonPrefix > contextLength;
    }

    private String computeDifference(String source) {
        int countOfCharactersBeforeSuffix = getCountOfCharactersBeforeSuffix(source);

        return "[" + source.substring(commonPrefix, countOfCharactersBeforeSuffix) + "]";
    }

    private String computeCommonSuffix() {
        String commonSuffix = getCommonSuffix();

        if (anySuffixCharacterIsNotVisible())
            commonSuffix += "...";

        return commonSuffix;
    }

    private String getCommonSuffix() {
        int countOfCharactersBeforeSuffix = getCountOfCharactersBeforeSuffix(expected);
        int end = Math.min(countOfCharactersBeforeSuffix + contextLength, expected.length());

        return expected.substring(countOfCharactersBeforeSuffix, end);
    }

    private boolean anySuffixCharacterIsNotVisible() {
        return getCountOfCharactersBeforeSuffix(expected) < expected.length() - contextLength;
    }

    private int getCountOfCharactersBeforeSuffix(String source) {
        return source.length() - commonSuffix + 1;
    }
}
