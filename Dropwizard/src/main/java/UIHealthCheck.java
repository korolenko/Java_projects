import com.codahale.metrics.health.HealthCheck;

public class UIHealthCheck extends HealthCheck {

    public UIHealthCheck(){
        super();
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}