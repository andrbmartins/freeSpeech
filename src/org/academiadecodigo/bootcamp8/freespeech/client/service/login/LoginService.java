package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> Filipe Santos Sá
 */

public interface LoginService extends Service {

    public boolean getConnectionServer();

    public void makeConnection(String server, int port);

    void closeClientSocket();

    Message readObject();
}
