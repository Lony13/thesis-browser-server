package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ServerInfoDAO;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerInfoService implements IServerInfoService {

    private ServerInfoDAO serverInfoDAO;

    @Autowired
    public ServerInfoService(ServerInfoDAO serverInfoDAO) {
        this.serverInfoDAO = serverInfoDAO;
    }

    @Override
    public List<ServerInfo> getInfo(int from, int to) {
        return serverInfoDAO.getServerInfo();
    }

}
