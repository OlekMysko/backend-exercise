package co.ynd.BackendExercise.monitoring;

import co.ynd.BackendExercise.service.TorExitNodeService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TorExitNodeHealthIndicator implements HealthIndicator {

    private final TorExitNodeService torExitNodeService;

    public TorExitNodeHealthIndicator(final TorExitNodeService torExitNodeService) {
        this.torExitNodeService = torExitNodeService;
    }

    @Override
    public Health health() {
        if (torExitNodeService.getTorExitNodes().isEmpty()) {
            return Health.down().withDetail("error", "Tor exit nodes list is empty").build();
        }
        return Health.up().build();
    }
}
