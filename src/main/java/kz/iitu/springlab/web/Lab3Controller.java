package kz.iitu.springlab.web;

import kz.iitu.springlab.config.AppProperties;
import kz.iitu.springlab.config.EnvironmentBanner;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/lab3")
public class Lab3Controller {

    private final AppProperties props;
    private final EnvironmentBanner banner;
    private final Environment environment;

    public Lab3Controller(AppProperties props,
                          EnvironmentBanner banner,
                          Environment environment) {
        this.props = props;
        this.banner = banner;
        this.environment = environment;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return Map.ofEntries(
                Map.entry("owner", props.owner()),
                Map.entry("group", props.group()),
                Map.entry("uiTheme", props.ui().theme()),
                Map.entry("itemsPerPage", props.ui().itemsPerPage()),
                Map.entry("mailFrom", props.mail().from()),
                Map.entry("mailRetryCount", props.mail().retryCount()),
                Map.entry("mailTimeout", props.mail().timeout().toString()),
                Map.entry("mailEnabled", props.mail().enabled()),
                Map.entry("serverPort", environment.getProperty("server.port")),
                Map.entry("activeProfiles", Arrays.asList(environment.getActiveProfiles())),
                Map.entry("banner", banner.describe())
        );
    }
}