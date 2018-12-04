package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;

import java.util.List;

public interface IServerInfoDao {

    List<ServerInfo> getServerInfo();

    void saveServerInfo(ServerInfo serverInfo);
}
