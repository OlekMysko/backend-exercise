package co.ynd.BackendExercise.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TorExitNodeService {

    private static final Logger logger = LoggerFactory.getLogger(TorExitNodeService.class);

    private final RestTemplate restTemplate;
    private final String torExitNodesUrl;
    private Set<String> torExitNodes = new HashSet<>();

    public TorExitNodeService(final RestTemplate restTemplate, final String torExitNodesUrl) {
        this.restTemplate = restTemplate;
        this.torExitNodesUrl = torExitNodesUrl;
        logger.info("Initializing Tor Exit Node Service...");
    }

    @Cacheable("torExitNodes")
    public Set<String> getTorExitNodes() {
        return torExitNodes.isEmpty() ? getFallbackExitNodes() : torExitNodes;
    }

    @Scheduled(fixedRateString = "${tor.exit.nodes.refresh.rate}")
    @CacheEvict(value = "torExitNodes", allEntries = true)
    public void refreshTorExitNodes() {
        int retryAttempts = 3;
        for (int i = 1; i <= retryAttempts; i++) {
            try {
                logger.info("Attempting to refresh Tor Exit Nodes, attempt {}/{}", i, retryAttempts);
                String response = restTemplate.getForObject(torExitNodesUrl, String.class);
                torExitNodes = parseExitNodes(response);
                logger.info("Tor Exit Nodes successfully refreshed on attempt {}/{}.", i, retryAttempts);
                return;
            } catch (RestClientException e) {
                logger.error("Network error on attempt {}/{}: {}", i, retryAttempts, e.getMessage());
                if (i < retryAttempts) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry sleep interrupted: ", ie);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}/{}: {}", i, retryAttempts, e.getMessage());
                break;
            }
        }

        if (torExitNodes.isEmpty()) {
            logger.warn("All retry attempts failed, fallback to using the last known set of Tor exit nodes.");
            torExitNodes = getFallbackExitNodes();
        }
    }

    private Set<String> parseExitNodes(final String response) {
        if (response == null || response.isEmpty()) {
            logger.warn("Received an empty or null response from the Tor exit nodes URL.");
            return getFallbackExitNodes();
        }

        return Stream.of(response.split("\n"))
                .parallel()
                .filter(line -> line.startsWith("ExitAddress"))
                .map(line -> {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        return parts[1].trim();
                    } else {
                        logger.warn("Malformed ExitAddress line: {}", line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public boolean ipAddressIsNotValid(final String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return true;
        }
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return true;
        }
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getFallbackExitNodes() {
        return new HashSet<>(torExitNodes);
    }
}
