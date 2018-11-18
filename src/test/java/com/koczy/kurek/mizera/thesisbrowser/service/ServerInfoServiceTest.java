package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ServerInfoDAO;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerInfoServiceTest {

    private ServerInfoService serverInfoService;
    private ServerInfoDAO serverInfoDAO;

    private List<ServerInfo> serverInfos;

    @Before
    public void setUp() {
        serverInfos = new ArrayList<>();
        serverInfos.add(new ServerInfo(new Date(), "test info"));
        serverInfos.add(new ServerInfo(new Date(), "test info 2"));
    }

    @Test
    public void getInfo() {
        serverInfoDAO = mock(ServerInfoDAO.class);
        serverInfoService = new ServerInfoService(serverInfoDAO);

        when(serverInfoDAO.getServerInfo()).thenReturn(serverInfos);

        List<ServerInfo> infos = serverInfoService.getInfo();

        assertEquals(serverInfos, infos);
        assertNotEquals(new ArrayList<>(), infos);
    }
}