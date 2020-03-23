package com.example.demo.RoutingLayer;

import com.example.demo.ServiceLayer.CreatorService;
import com.sun.net.httpserver.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;


class Router {

    static class Configurator extends HttpsConfigurator {
        public Configurator(SSLContext ctx) {
            super(ctx);
        }

        @Override
        public void configure (HttpsParameters params) {
            params.setSSLParameters (getSSLContext().getSupportedSSLParameters());
        }
    }

    static HttpServer createHttpsServer(int port) throws IOException, NoSuchAlgorithmException {
        HttpsServer server = HttpsServer.create();
        String RESPONSE = "<html><body><p>Hello World!</body></html>";
        HttpContext context = server.createContext("/foo/");
        context.setHandler(he -> {
            he.getResponseHeaders().add("encoding", "UTF-8");
            he.sendResponseHeaders(200, RESPONSE.length());
            he.getResponseBody().write(RESPONSE.getBytes(StandardCharsets.UTF_8));
            he.close();
        });

        server.setHttpsConfigurator(new Configurator(SSLContext.getDefault()));
        server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        return server;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        int serverPort = 8000;
        CreatorService creator = new CreatorService();
        //HttpServer server = createHttpsServer(serverPort);
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        server.createContext("/manager", (exchange -> {
            System.out.println("someone is trying to connect to /manager");
            if (!exchange.getRequestMethod().equals("POST")) {
                throw new UnsupportedOperationException();
            }
            InputStream is = exchange.getRequestBody();

            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
                out.append(buffer, 0, charsRead);
            }
            String req = out.toString();
            JSONObject jsonReq = stringToJson(req);
            System.out.println("request: " + jsonReq.toJSONString());
            JSONObject jsonRes = creator.requestProcessor(jsonReq);
            String res = jsonRes.toJSONString();
            System.out.println("response: "+res);

            exchange.sendResponseHeaders(200, res.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(res.getBytes());
            output.flush();
            exchange.close();
        }));


        server.createContext("/api/hello", (exchange -> {
            System.out.println("someone is trying to connect to /api/hello");
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
                String noNameText = "Anonymous";
                String name = params.getOrDefault("name", List.of(noNameText)).stream().findFirst().orElse(noNameText);
                if (name.equals("gili")) name = "homo";
                String respText = String.format("{\"fuck\":\"%s!\"}", name);
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));


        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

    private static JSONObject stringToJson(String str) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}