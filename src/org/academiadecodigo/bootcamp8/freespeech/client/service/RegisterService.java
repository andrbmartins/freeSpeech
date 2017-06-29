package org.academiadecodigo.bootcamp8.freespeech.client.service;

import org.academiadecodigo.bootcamp8.freespeech.client.utils.Navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */


//TODO documentation

public class RegisterService {

    private static RegisterService instance = null;
    private Map<String, Service> services;

    public static RegisterService getInstance() {

        if (instance == null) {
            synchronized (Navigation.class) {
                if (instance == null) {
                    instance = new RegisterService();
                }
            }
        }
        return instance;
    }

    private RegisterService() {
        services = new HashMap<>();
    }

    public void addService(Service service) {
        services.put(service.getName(), service);
    }

    public <T extends Service> T get(Class<T> serviceClass) {
        Service service = services.get(serviceClass.getSimpleName());

        return (service != null) ? serviceClass.cast(service) : null;
    }
}
