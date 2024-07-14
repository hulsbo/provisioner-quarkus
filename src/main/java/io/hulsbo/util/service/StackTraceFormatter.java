package io.hulsbo.util.service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StackTraceFormatter {

    public static String formatStackTrace(Exception exception) {
        AtomicInteger counter = new AtomicInteger(1);
        String stackTrace = Arrays.stream(exception.getStackTrace())
                .map(element -> "<div class=\"stack-trace-item\">" +
                        "<span class=\"stack-trace-number\">" + counter.getAndIncrement() + "</span>" +
                        element.toString() +
                        "</div>")
                .collect(Collectors.joining("\n"));

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Exception Stack Trace</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "            margin: 0;\n" +
                "            background-color: #D2B48C;\n" +
                "            font-family: Helvetica; \n" +
                "            background-image: url('data:image/svg+xml;utf8,<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\"><rect width=\"100\" height=\"100\" fill=\"none\" stroke=\"%238B4513\" stroke-width=\"2\"/></svg>');\n" +
                "        }\n" +
                "        \n" +
                "        .runestone {\n" +
                "            max-width: 80%;\n" +
                "            text-align: center;\n" +
                "            background-color: #D2B48C;\n" +
                "            border: 20px solid #8B4513;\n" +
                "            border-radius: 10px;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 0 15px rgba(0,0,0,0.5);\n" +
                "        }\n" +
                "        \n" +
                "        .exception-message {\n" +
                "            font-size: 16px;\n" +
//                "            font-weight: bold;\n" +
//                "            color: #FF4500;\n" +
                "            color: black;\n" +
                "            margin-bottom: 20px;\n" +
                "            text-shadow: 2px 2px 4px rgba(0,0,0,0.5);\n" +
                "        }\n" +
                "        \n" +
                "        .stack-trace-item {\n" +
//                "            color: #FF4500;\n" +
                "            color: black;\n" +
                "            margin-bottom: 1em;\n" +
                "            padding: 0.5em;\n" +
                "            border: 2px solid #8B4513;\n" +
                "            border-radius: 2px; \n       "  +
//                "            background-color: rgba(139, 69, 19, 0.1);\n" +
                "            background-color: white;\n" +
                "            word-wrap: break-word;\n" +
                "            position: relative;\n" +
                "            padding-left: 40px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        \n" +
                "        .stack-trace-number {\n" +
                "            position: absolute;\n" +
                "            left: 5px;\n" +
                "            top: 50%;\n" +
                "            transform: translateY(-50%);\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            font-style: italic;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"runestone\">\n" +
                "        <div class=\"exception-message\">" + exception.toString() + "</div>\n" +
                "        " + stackTrace + "\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
