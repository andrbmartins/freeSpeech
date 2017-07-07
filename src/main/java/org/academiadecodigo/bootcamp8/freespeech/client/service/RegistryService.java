package org.academiadecodigo.bootcamp8.freespeech.client.service;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */


public class RegistryService {

    private static RegistryService instance = null;
    private Map<String, Service> services;

    /**
     * Singleton instance.
     *
     * @return - the instance.
     */
    public static RegistryService getInstance() {

        if (instance == null) {
            synchronized (Navigation.class) {
                if (instance == null) {
                    instance = new RegistryService();
                }
            }
        }
        return instance;
    }

    /**
     * Instantiates a RegistryService and a HashMap of service names and services.
     */
    private RegistryService() {
        services = new HashMap<>();
    }

    /**
     * Puts a service in the HashMap.
     *
     * @param service
     */
    public void addService(Service service) {
        services.put(service.getName(), service);
    }

    /**
     * Returns the service that corresponds to the  specified element.
     *
     * @param serviceClass - the specified element.
     * @param <T>          - the service type.
     * @return - the service.
     */
    public <T extends Service> T get(Class<T> serviceClass) {
        Service service = services.get(serviceClass.getSimpleName());

        return (service != null) ? serviceClass.cast(service) : null;
    }
}
