package ai.labs.nlp;

import ai.labs.bootstrap.UserModule;
import ai.labs.caching.bootstrap.CachingModule;
import ai.labs.core.bootstrap.CoreModule;
import ai.labs.expressions.bootstrap.ExpressionModule;
import ai.labs.memory.bootstrap.ConversationMemoryModule;
import ai.labs.parser.bootstrap.SemanticParserModule;
import ai.labs.permission.bootstrap.PermissionModule;
import ai.labs.persistence.bootstrap.PersistenceModule;
import ai.labs.resources.RepositoryModule;
import ai.labs.rest.bootstrap.RestInterfaceModule;
import ai.labs.runtime.DependencyInjector;
import ai.labs.runtime.bootstrap.RuntimeModule;
import ai.labs.runtime.bootstrap.SwaggerModule;
import ai.labs.serialization.bootstrap.SerializationModule;
import ai.labs.server.IServerRuntime;
import ai.labs.server.bootstrap.ServerRuntimeModule;
import ai.labs.staticresources.bootstrap.StaticResourcesModule;
import ai.labs.testing.bootstrap.AutomatedtestingModule;
import ai.labs.utilities.FileUtilities;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

import java.io.FileInputStream;

/**
 * @author ginccc
 */
public class NlpServer {
    private static final String ENVIRONMENT_KEY = "EDDI_ENV";
    private static final String USER_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        String eddiEnv = System.getProperty(ENVIRONMENT_KEY);
        if (eddiEnv == null || eddiEnv.isEmpty()) {
            System.err.println("Environment Variable must not be null nor empty! (e.g. -DEDDI_ENV=[development/production])");
            System.exit(1);
        }

        final String configDir = FileUtilities.buildPath(USER_DIR, "config", eddiEnv);

        //bootstrapping modules
        DependencyInjector.Environment environment = DependencyInjector.Environment.valueOf(eddiEnv.toUpperCase());
        //TODO check why production mode does not work
        final DependencyInjector injector = DependencyInjector.init(DependencyInjector.Environment.DEVELOPMENT,
                new RuntimeModule(
                        new FileInputStream(configDir + "threads.properties"),
                        new FileInputStream(configDir + "systemRuntime.properties")),
                new RequestScopeModule(),
                new RestInterfaceModule(),
                new SerializationModule(),
                new PersistenceModule(new FileInputStream(configDir + "mongodb.properties")),
                new ConversationMemoryModule(),
                new PermissionModule(),
                new ExpressionModule(),
                new RepositoryModule(),
                new ConversationMemoryModule(),
                new UserModule(),
                new CachingModule(new FileInputStream(configDir + "infinispan.xml")),
                new SemanticParserModule(),
                new AutomatedtestingModule(),
                new StaticResourcesModule(),
                new CoreModule(),
                new SwaggerModule(new FileInputStream(configDir + "swagger.properties")),
                new ServerRuntimeModule(new FileInputStream(configDir + "webServer.properties"),
                        new FileInputStream(configDir + "keycloak.properties")));

        //init webserver
        injector.getInstance(IServerRuntime.class).startup();
    }
}
