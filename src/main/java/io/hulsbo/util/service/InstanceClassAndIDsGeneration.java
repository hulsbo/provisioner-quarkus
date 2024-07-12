package io.hulsbo.util.service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InstanceClassAndIDsGeneration {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CLASS_LENGTH = 4;


    /**
     * Adds unique classes and IDs to HTML elements based on the template file name. <br>
     *
     * <p>NOTE: In template ID placeholders should have format: <pre>{@code __id_p{number}__}</pre></p>
     *
     * @param htmlInput HTML content as a string
     * @param componentName The name of the template file
     * @return A modified version of the HTML input with unique classes and IDs added to all elements
     *
     * @throws IllegalArgumentException if htmlInput or componentName is null or empty
     */
    public static String addInstanceClassAndIDs(String htmlInput, String componentName) {

        if (Objects.equals(componentName, "") || componentName == null)  {
            throw new IllegalArgumentException("componentName cannot be null or the empty string.");
        }

        if (Objects.equals(htmlInput, "") || htmlInput == null)  {
            throw new IllegalArgumentException("htmlInput cannot be null or the empty string.");
        }

        // Generate a unique class prefix
        String uniqueClassPrefix = generateUniqueClass(componentName);

        // Step 1: Generate unique ids for id-placeholders
        String htmlWithUniqueIds = replaceIdPlaceholders(htmlInput, uniqueClassPrefix);

        // Step 2: Process HTML and CSS to insert unique classes

        // Add the unique class prefix to HTML elements
        String htmlWithUniqueClasses = insertUniqueClassesToHtml(htmlWithUniqueIds, uniqueClassPrefix);

        // Add the unique class prefix to CSS selectors
        String finalHtml = insertUniqueClassesToCss(htmlWithUniqueClasses, uniqueClassPrefix);

        return finalHtml;
    }

    private static String replaceIdPlaceholders(String htmlInput, String uniqueClass) {
        Pattern pattern = Pattern.compile("__iid_([a-zA-Z\\d]+)__");
        Matcher matcher = pattern.matcher(htmlInput);

        // NOTE: don't know much about this, but StringBuffer ensures thread-safety whereas StringBuilder does not.
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String uniqueId = uniqueClass + "_iid_" + matcher.group(1);
            matcher.appendReplacement(sb, uniqueId);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String generateUniqueClass(String componentName) {
        StringBuilder sb = new StringBuilder("id_" + componentName + "_");
        Random random = new Random();

        for (int i = 0; i < CLASS_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static String insertUniqueClassesToHtml(String html, String uniqueClassPrefix) {
        // Generate a unique class prefix

        // Regex pattern to match HTML tags
        Pattern pattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)([^>]*)>");

        StringBuffer result = new StringBuffer();
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String tag = matcher.group(1);
            String attributes = matcher.group(2);

            // Check if there's already a class attribute
            if (attributes.contains("class=")) {
                // If class attribute exists, append our unique class
                attributes = attributes.replaceFirst("(class=['\"])", "$1" + uniqueClassPrefix + " ");
            } else {
                // If no class attribute, add one
                attributes += " class=\"" + uniqueClassPrefix + "\"";
            }

            // Replace the matched content with the modified tag
            matcher.appendReplacement(result, "<" + tag + attributes + ">");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static String insertUniqueClassesToCss(String html, String uniqueClassPrefix) {
        // Regex pattern to match the content of <style> tags
        Pattern stylePattern = Pattern.compile("<style[^>]*>(.*?)</style>", Pattern.DOTALL);
        Matcher styleMatcher = stylePattern.matcher(html);

        StringBuffer result = new StringBuffer();

        while (styleMatcher.find()) {
            String cssContent = styleMatcher.group(1);
            String processedCss = processCSS(cssContent, uniqueClassPrefix);
            styleMatcher.appendReplacement(result, "<style>" + processedCss + "</style>");
        }
        styleMatcher.appendTail(result);

        return result.toString();
    }

    private static String processCSS(String css, String uniqueClassPrefix) {
        // Regex pattern to match CSS selectors
        Pattern selectorPattern = Pattern.compile("([^{}]+)\\{");
        Matcher selectorMatcher = selectorPattern.matcher(css);

        StringBuffer processedCss = new StringBuffer();

        while (selectorMatcher.find()) {
            String selector = selectorMatcher.group(1).trim();
            String processedSelector = addUniqueClassToSelector(selector, uniqueClassPrefix);
            selectorMatcher.appendReplacement(processedCss, "\n\n" + processedSelector + " {");
        }
        selectorMatcher.appendTail(processedCss);

        return processedCss.toString();
    }

    public static String addUniqueClassToSelector(String selector, String uniqueClass) {
        // Split the selector into individual selectors (in case of multiple selectors separated by commas)
        String[] selectors = selector.split("\\s*,\\s*");

        // Process each selector
        String modifiedSelector = Arrays.stream(selectors)
                .map(s -> addUniqueClassToSingleSelector(s, uniqueClass))
                .collect(Collectors.joining(", "));

        return modifiedSelector;
    }

    private static String addUniqueClassToSingleSelector(String selector, String uniqueClass) {
        // Split the selector into parts based on combinators
        String[] parts = selector.split("(?<=[ >+~])");

        // Process the first part
        parts[0] = addUniqueClassToFirstPart(parts[0], uniqueClass);

        // Join the parts back together
        return String.join("", parts);
    }

    private static String addUniqueClassToFirstPart(String part, String uniqueClass) {
        // Patterns for different selector types
        Pattern elementPattern = Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*)");
        Pattern classPattern = Pattern.compile("^\\.");
        Pattern idPattern = Pattern.compile("^#");
        Pattern pseudoClassPattern = Pattern.compile("^:");
        Pattern attributePattern = Pattern.compile("(\\[.+?\\])");

        Matcher elementMatcher = elementPattern.matcher(part);
        Matcher classMatcher = classPattern.matcher(part);
        Matcher idMatcher = idPattern.matcher(part);
        Matcher pseudoClassMatcher = pseudoClassPattern.matcher(part);

        if (elementMatcher.find()) {
            // Handle element selectors
            String element = elementMatcher.group(1);
            String rest = part.substring(elementMatcher.end());

            // Handle attribute selectors
            Matcher attributeMatcher = attributePattern.matcher(rest);
            if (attributeMatcher.find()) {
                String attributes = attributeMatcher.group(1);
                rest = rest.substring(0, attributeMatcher.start()) +
                        rest.substring(attributeMatcher.end());
                return element + "." + uniqueClass + attributes + rest;
            }

            return element + "." + uniqueClass + rest;
        } else if (classMatcher.find()) {
            // Handle class selectors
            return "." + uniqueClass + part;
        } else if (idMatcher.find()) {
            // Handle ID selectors
            return "." + uniqueClass + part;
        } else if (pseudoClassMatcher.find()) {
            // Handle pseudo-classes
            return "." + uniqueClass + part;
        }

        // If no match found, return the original part
        return part;
    }

}