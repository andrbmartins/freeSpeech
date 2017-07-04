package org.academiadecodigo.bootcamp8.freespeech.client.service.cryptography;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;

/**
 * Created by Filipe on 02/07/2017.
 */
public interface CryptographyService extends Service {


    /**
     * /**
     * Establishes a connection the the server socket in the specified host and port.
     * @param host - the host.
     * @param port - the port.
     * @return connection status
     */
    boolean connect(String host, int port);

}
