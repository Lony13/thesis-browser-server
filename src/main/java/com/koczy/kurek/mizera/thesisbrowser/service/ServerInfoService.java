package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ServerInfoService implements IServerInfoService {

    public ServerInfoService() {
    }

    @Override
    public List<ServerInfo> getInfo(int from, int to) {
        return Collections.EMPTY_LIST;
    }

}
