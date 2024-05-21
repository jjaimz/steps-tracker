package com.steps.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebServer {
    public static void main(String[] args) {
        Server myserver = new Server(1116);
        myserver.setHandler(new DefaultHandler());
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder defaultServletHolder = new ServletHolder("default", DefaultServlet.class);

        context.addServlet(defaultServletHolder, "/");
        myserver.setHandler(context);

        try {
            myserver.start();
            myserver.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
