package by.ese.web.app;

import com.sun.faces.config.ConfigureListener;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.logging.LogManager;

public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WEBAPP_LOCATION = "webapp";

    public static void main(String[] args) {
        System.setProperty("com.atomikos.icatch.registered", "true");

        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        QueuedThreadPool connectionThreadPool = new QueuedThreadPool();
        connectionThreadPool.setName("JETTY_CONNECTIONS");
        connectionThreadPool.setMinThreads(20);
        connectionThreadPool.setMaxThreads(500);
        connectionThreadPool.setStopTimeout(30 * 1000);

        Server server = new Server(connectionThreadPool);

        // HTTP connector
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);

        // HTTPS configuration
        LOG.debug("HTTPS Configuration");
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);

        server.setConnectors(new Connector[]{connector});

        try {//Start server
            LOG.debug("Initialize webapp context");
            // WebApp Context Handler
            WebAppContext webAppContext = initializeWebAppContext();

            Listener weldListener = new Listener();
            ConfigureListener facesListener = new ConfigureListener();

            webAppContext.addEventListener(weldListener);
            webAppContext.addEventListener(facesListener);

            MetaInfConfiguration mic = new MetaInfConfiguration();
            final Resource target = Resource.newResource(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            mic.scanForResources(webAppContext, target, null);
            mic.scanForFragment(webAppContext, target, null);
            mic.scanForTlds(webAppContext, target, null);

            LOG.debug("Initialize context security");
            // Config security ...
            ConstraintSecurityHandler securityHandler = initializeSecurity(webAppContext);
            LOG.debug("Start server");
            // Add security handler to server ...
            server.setHandler(securityHandler);
            //WebSocket
            WebSocketServerContainerInitializer.configureContext(webAppContext);

            server.start();
            server.join();
        } catch (Exception ex) {
            LOG.error("Exception in server routines", ex);
        } finally {//Destroy server
            server.destroy();
        }
    }

    private static ConstraintSecurityHandler initializeSecurity(WebAppContext webappContext) {
        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

        // deny downloading xhtml files ..
        Constraint xhtmlConstraint = new Constraint();
        xhtmlConstraint.setName("JSF Source Code Security Constraint");
        xhtmlConstraint.setAuthenticate(false);
        xhtmlConstraint.setRoles(null);

        ConstraintMapping xhtmlConstraintMapping = new ConstraintMapping();
        xhtmlConstraintMapping.setPathSpec("*.xhtml");
        xhtmlConstraintMapping.setConstraint(xhtmlConstraint);
        securityHandler.setConstraintMappings(Collections.singletonList(xhtmlConstraintMapping));

        // Add webapp context to security handler ...
        securityHandler.setHandler(webappContext);
        return securityHandler;
    }

    private static WebAppContext initializeWebAppContext() throws URISyntaxException {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        webAppContext.setContextPath("/");
        webAppContext.setDescriptor(WEBAPP_LOCATION + "/WEB-INF/web.xml");

        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource(WEBAPP_LOCATION);
        if (webAppDir == null) {
            throw new RuntimeException(String.format("No %s directory was found into the JAR file", WEBAPP_LOCATION));
        }
        webAppContext.setResourceBase(webAppDir.toURI().toString());
        webAppContext.setParentLoaderPriority(true);
//        webAppContext.setExtractWAR(true);


        webAppContext.setDisplayName("Hello World on Jetty Embedded 9");
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        LOG.info("Serving application from: " + WEBAPP_LOCATION);
        return webAppContext;
    }

}


