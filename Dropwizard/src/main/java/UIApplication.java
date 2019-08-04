import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import resource.UIResource;


public class UIApplication extends Application<UIConfiguration> {

    public static void main(String[] args) throws Exception {
        new UIApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<UIConfiguration> bootstrap) {
        //добавляем bootstrap файлы
        bootstrap.addBundle(new AssetsBundle());
        //добавляем шаблоны mustache
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(UIConfiguration configuration,
                    Environment environment) {
        final UIResource resource = new UIResource(
        );

        final UIHealthCheck healthCheck =
                new UIHealthCheck();

        environment.jersey().register(resource);
        environment.healthChecks().register("UIHealthCheck", healthCheck);
    }

}