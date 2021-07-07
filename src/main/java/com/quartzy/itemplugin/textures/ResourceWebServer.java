package com.quartzy.itemplugin.textures;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ResourceWebServer implements HttpHandler{
    
    @Getter
    @Setter
    private Path resourcePackFolder;
    @Getter
    private HttpServer server;
    
    public ResourceWebServer(HttpServer server, Path resourcePackFolder){
        server.createContext("/resources", this);
        this.resourcePackFolder = resourcePackFolder.normalize().toAbsolutePath();
        this.server = server;
    }
    
    public static ResourceWebServer createWebServer(Path resourcePackFolder){
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(getIp(), 48391), 0); //Use system default for backlog
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server.setExecutor(threadPoolExecutor);
            ResourceWebServer resourceWebServer = new ResourceWebServer(server, resourcePackFolder);
            server.start();
            return resourceWebServer;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String fullURIStr = exchange.getRequestURI().toString();
        String requestStr = fullURIStr.substring(fullURIStr.indexOf("/resources/") + "/resources/".length());
        Path requestFile = resourcePackFolder.resolve(requestStr).normalize().toAbsolutePath();
        if(isValidFile(resourcePackFolder, requestFile, 4)){
            OutputStream responseBody = exchange.getResponseBody();
            InputStream inputStream = Files.newInputStream(requestFile, StandardOpenOption.READ);
            byte[] bytes = inputStream.readAllBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            responseBody.write(bytes);
            responseBody.flush();
            responseBody.close();
        }else{
            OutputStream responseBody = exchange.getResponseBody();
            byte[] bytes = "404 not found".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, bytes.length);
            responseBody.write(bytes);
            responseBody.flush();
            responseBody.close();
        }
    }
    
    private static boolean isValidFile(Path parent, Path unknown, int iterations){
        //Check if file should be ignored (e.g. warning.txt)
        if(unknown.getFileName().toString().equalsIgnoreCase("warning.txt"))return false;
        
        File file = unknown.toFile();
        if(!file.exists() || !file.isFile() || !file.canRead()){
            return false;
        }
        Path checkPath = unknown.getParent();
        for(int i = 0; i < iterations; i++){
            if(checkPath.equals(parent))return true;
            checkPath = checkPath.getParent();
        }
        return false;
    }
    
    public static String getIp(){
        try{
            return Bukkit.getIp().isEmpty() || Bukkit.getIp().isBlank() ? InetAddress.getLocalHost().getHostAddress() : Bukkit.getIp();
        } catch(UnknownHostException e){
            e.printStackTrace();
        }
        return Bukkit.getIp();
    }
}
