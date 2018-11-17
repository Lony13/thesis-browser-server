package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServerInfoDemoService implements IServerInfoService {

    @Override
    public List<ServerInfo> getInfo() {
        ArrayList<ServerInfo> infos = new ArrayList<>();
        infos.add(new ServerInfo(new Date(),
                "Theses for Piotr Faliszewski downloaded. Downloaded 24 new theses and updated 67."));
        infos.add(new ServerInfo(new Date(),
                "LDA finished word for 12 theses. Time 17 min 42 seconds."));
        infos.add(new ServerInfo(new Date(),
                "Updated quoatation number for theses"));
        infos.add(new ServerInfo(new Date(),
                "Download thesis with title How to program with python for author Python Master"));
        return infos;
    }

}
