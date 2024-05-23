package com.steps.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class WebServer {
    public static void main(String[] args) {
        Server myserver = new Server(1116);
        myserver.setHandler(new DefaultHandler());
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder defaultServletHolder = new ServletHolder("default", DefaultServlet.class);

        context.addServlet(defaultServletHolder, "/");
        myserver.setHandler(context);

        ServletContextHandler apiHandler = apiHandler();
        myserver.setHandler(apiHandler);

        try {
            myserver.start();
            myserver.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static ServletContextHandler apiHandler() {
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("com.steps.api");
        resourceConfig.register(JacksonFeature.class);
//        resourceConfig.register(Hello.class);

        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/api/*");

        return handler;

    }



}
