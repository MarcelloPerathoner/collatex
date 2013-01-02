package eu.interedition.collatex;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import eu.interedition.collatex.io.ObjectMapperMessageBodyReaderWriter;
import eu.interedition.collatex.io.ObjectMapperProvider;
import eu.interedition.collatex.io.TemplateConfigurationProvider;
import eu.interedition.collatex.io.TemplateMessageBodyWriter;
import eu.interedition.collatex.io.VariantGraphDotMessageBodyWriter;
import eu.interedition.collatex.io.VariantGraphMLMessageBodyWriter;
import eu.interedition.collatex.io.VariantGraphSVGMessageBodyWriter;
import eu.interedition.collatex.io.VariantGraphTEIMessageBodyWriter;
import freemarker.template.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class Server extends DefaultResourceConfig {

  private static final Logger LOG = Logger.getLogger(Server.class.getName());

  private final Injector injector;

  public Server(Injector injector) {
    super();
    this.injector = injector;
  }

  public static void main(String... args) throws IOException {
    final Injector injector = Guice.createInjector(new ConfigurationModule(), new AbstractModule() {
      @Override
      protected void configure() {
        bind(Configuration.class).toProvider(TemplateConfigurationProvider.class).asEagerSingleton();
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
      }
    });

    final URI context = UriBuilder.fromUri("http://localhost/")
            .port(Integer.parseInt(injector.getInstance(Key.get(String.class, Names.named("interedition.port")))))
            .path(injector.getInstance(Key.get(String.class, Names.named("interedition.context_path"))))
            .build();

    if (LOG.isLoggable(Level.INFO)) {
      LOG.info("Starting HTTP server at " + context.toString());
    }

    final HttpServer httpServer = GrizzlyServerFactory.createHttpServer(context, new Server(injector));

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (LOG.isLoggable(Level.INFO)) {
          LOG.info("Stopping HTTP server");
        }
        httpServer.stop();
      }
    }));

    httpServer.start();

    synchronized (httpServer) {
      try {
        httpServer.wait();
      } catch (InterruptedException e) {
      }
    }
  }


  @Override
  public Set<Class<?>> getProviderClasses() {
    return Sets.<Class<?>>newHashSet(
            TemplateMessageBodyWriter.class,
            VariantGraphDotMessageBodyWriter.class,
            VariantGraphMLMessageBodyWriter.class,
            VariantGraphTEIMessageBodyWriter.class
    );
  }

  @Override
  public Set<Object> getSingletons() {
    return Sets.newHashSet(
            injector.getInstance(CollateResource.class),
            injector.getInstance(StaticResource.class),
            injector.getInstance(ObjectMapperMessageBodyReaderWriter.class),
            injector.getInstance(VariantGraphSVGMessageBodyWriter.class)
    );
  }
}
