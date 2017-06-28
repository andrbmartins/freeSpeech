package org.academiadecodigo.bootcamp8.freespeach.client.service;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> PedroMAlves
 */
public class Services {
    private static ClientService loginService = new LoginClientService();

    public static ClientService getLoginService() {
        return loginService;
    }

    public static EncryptionService getEncryptionService() {
        return new EncryptionService();
    }

    public static TempClientService getTempClientService() {
        return new TempClientService();
    }
}
