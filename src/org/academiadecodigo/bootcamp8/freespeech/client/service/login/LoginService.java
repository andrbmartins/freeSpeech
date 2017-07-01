package org.academiadecodigo.bootcamp8.freespeech.client.service.login;

import org.academiadecodigo.bootcamp8.freespeech.client.service.Service;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Message;
import org.academiadecodigo.bootcamp8.freespeech.shared.message.Sendable;

/**
 * Developed @ <Academia de Código_>
 * Created by
 * <Code Cadet> JPM Ramos
 * <Code Cadet> PedroMAlves
 */

public interface LoginService extends Service {

    void makeConnection(String server, int port);
}
