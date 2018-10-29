package com.koczy.kurek.mizera.thesisbrowser.schedule;

import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component
public class TasksWorker {

    private static final Logger log = Logger.getLogger(DownloadService.class.getName());

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("Worker: The time is now " + new Date());
    }

}
