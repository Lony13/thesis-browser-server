package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.service.DemoServiceResolver;
import com.koczy.kurek.mizera.thesisbrowser.service.IServerInfoService;
import com.koczy.kurek.mizera.thesisbrowser.service.ServerInfoDemoService;
import com.koczy.kurek.mizera.thesisbrowser.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ServerInfoController extends DemoServiceResolver<IServerInfoService>  {

    @Autowired
    public ServerInfoController(ServerInfoService serverInfoService, ServerInfoDemoService serverInfoDemoService) {
        super(serverInfoService, serverInfoDemoService);
    }

    @RequestMapping(value = "/server/info")
    public ResponseEntity<List<ServerInfo>> getServerInfo(@RequestParam(required = true) int from,
                                                          @RequestParam(required = true) int to,
                                                          HttpServletRequest request) {
        return new ResponseEntity<>(resolveService(request).getInfo(), HttpStatus.OK);
    }

}
