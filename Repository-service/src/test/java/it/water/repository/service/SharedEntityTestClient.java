package it.water.repository.service;

import it.water.core.api.service.integration.SharedEntityIntegrationClient;
import it.water.core.interceptors.annotations.FrameworkComponent;

import java.util.Collection;
import java.util.List;

@FrameworkComponent(priority = 2)
public class SharedEntityTestClient implements SharedEntityIntegrationClient {
    @Override
    public Collection<Long> fetchSharingUsersIds(String s, long l) {
        return List.of(1l);
    }
}
