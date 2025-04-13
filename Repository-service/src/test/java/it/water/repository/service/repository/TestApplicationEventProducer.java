package it.water.repository.service.repository;

import it.water.core.api.model.Resource;
import it.water.core.api.model.events.ApplicationEventProducer;
import it.water.core.api.model.events.Event;
import it.water.core.interceptors.annotations.FrameworkComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FrameworkComponent
public class TestApplicationEventProducer implements ApplicationEventProducer {
    private Logger logger = LoggerFactory.getLogger(TestApplicationEventProducer.class);

    @Override
    public <T extends Resource, K extends Event> void produceEvent(T t, Class<K> aClass) {
        logger.info("produceEvent {} on class {}", t, aClass);
    }

    @Override
    public <T extends Resource, K extends Event> void produceDetailedEvent(T t, T t1, Class<K> aClass) {
        logger.info("produceDedetailedEvent before update {} - after {} on class {}", t, t1, aClass);
    }
}
