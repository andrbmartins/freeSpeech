package org.academiadecodigo.bootcamp8.freespeech.client.service.connection;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;

/**
 * Developed
 *
 * @<Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface ConnectionService extends Service {


    /**
     * /**
     * Establishes a connection the the server socket in the specified host and port.
     *
     * @param host - the host.
     * @param port - the port.
     * @return connection status
     */
    boolean connect(String host, int port);

}
