package samt.smajilbasic.deduplicator.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanListener;
import samt.smajilbasic.deduplicator.scanner.ScanManager;

/**
 * ScanController
 */
@RestController
@RequestMapping(path = "/scan")
public class ScanController implements ScanListener{

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    AuthenticationDetailsRepository adr;

    ScanManager currentScan;

    @Autowired
    GlobalPathRepository gpr;

    @Autowired
    ApplicationContext context;
    private Report report = null; 

    @PostMapping("/start")
    public @ResponseBody Object start(@RequestParam(required = false) Integer threadCount) {
        
        if (gpr.count() > 0) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();

            AuthenticationDetails internalUser = adr.findById(authenticatedUser).get();
            System.out.println("user: " + authenticatedUser);
            currentScan = (ScanManager) context.getBean("scanManager");
            report = new Report(internalUser);
            report.setStart(System.currentTimeMillis());
            reportRepository.save(report);

            currentScan.setReportRepository(reportRepository);
            currentScan.setReportId(report.getId());
            currentScan.setThreadCount(threadCount);
            currentScan.setListener(this);
            currentScan.start();
            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No path to scan set");
        }
    }

    @PostMapping("/stop")
    public @ResponseBody Object stop() {
        if (currentScan != null) {
            currentScan.stopScan();

            System.out.println("Waiting finish");
            while (currentScan.isAlive()) {
                long time = System.currentTimeMillis();
                if (System.currentTimeMillis() - time > 100) {
                    System.out.print(".");
                }
            }

            Report report = currentScan.getReport();

            destroyScanManager();

            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

    @PostMapping("/pause")
    public @ResponseBody Message pause() {
        if (currentScan != null) {
            if (!currentScan.isPaused()) {
                currentScan.pauseAll();
                return new Message(HttpStatus.OK, "Scan paused");
            } else {
                return new Message(HttpStatus.OK, "Scan already paused");
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

    @PostMapping("/resume")
    public @ResponseBody Message resume() {
        if (currentScan != null) {
            if (currentScan.isPaused())
                currentScan.resumeAll();
            return new Message(HttpStatus.OK, "Scan resumed");
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently running");
        }
    }

    @GetMapping("/status")
    public @ResponseBody Object getStatus() {
        
        
        report = report != null? report : new Report();
        int count = report.getFilesScanned() == null ? 0 : report.getFilesScanned();
        Message response  = new Message(HttpStatus.OK, String.valueOf(count));
        LocalDateTime time = Instant.ofEpochMilli(report.getStart() == null ? 0 : report.getStart()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        response.setTimestamp(time);
        return response;

    }

    @Override
    public void scanFinished() {
        destroyScanManager();
    }

    private void destroyScanManager(){
        BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        ((DefaultListableBeanFactory) factory).destroySingleton("scanManager");

    }
}
