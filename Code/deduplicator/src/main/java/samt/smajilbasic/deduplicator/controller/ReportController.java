package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.DuplicateRepository;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

/**
 * ReportController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/report". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/report")
public class ReportController {

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella Report del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    ReportRepository reportRepository;

    /**
     * L'attributo duplicateRepository serve al controller per ricavare i duplicati dalla tabella File.
     * Usa l'annotazione @Autowired per indicare a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    DuplicateRepository duplicateRepository;

    /**
     * L'attributo fileRepository serve al controller per interfacciarsi con la
     * tabella File del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    FileRepository fileRepository;

    /**
     * Il metodo getAllReports risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/report/all</b>(localhost:8080/report/all/).
     * 
     * @return tutti i rapporti contenuti nella tabella Report.
     */
    @GetMapping(value = "/all")
    public @ResponseBody Iterable<Report> getAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Il metodo getLastReport risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/report</b>(localhost:8080/report/).
     * 
     * @return il rapporto più recente della tabella Report.
     */
    @GetMapping()
    public @ResponseBody Report getLastReport() {
        return reportRepository.getLastReport();
    }

    /**
     * Il metodo getReportById risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/report/&lt;id&gt;</b> (localhost:8080/report/18).
     * @param id l'id del rapporto da ricavare.
     * @return il rapporto della tabella Report, se l'id non è valido ritorna un messaggio d'errore.
     */
    @GetMapping(value = "/{id}")
    public @ResponseBody Object getReportById(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return reportRepository.findById(intId).get();
        else
            return new Message(HttpStatus.NOT_FOUND, "Invalid report id");
    }

    /**
     * Il metodo getDuplicateByReportId risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/report/duplicate/&lt;id&gt;</b> (localhost:8080/report/duplicate/98).
     * Il metodo ricava tutti i duplciati di un rapporto.
     * I duplicati vengono cercati in base alla grandezza del file e al hash dei contenuti dei file.
     * @param id l'id del rapporto dal quale verrano ricavati i duplicati.
     * @return tutti i duplicati del rapporto oppure un messaggio d'errore se l'id del rapporto non è valido.
     */
    @GetMapping(value = "/duplicate/{id}")
    public @ResponseBody Object getDuplicateByReportId(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return duplicateRepository.findDuplicatesFromReport((Report) getReportById(id));
        else
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid report id");
    }

    /**
     * Il metodo getFileByHash risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/report/duplicate/&lt;id&gt;/&lt;hash&gt;* (localhost:8080/report/duplicate/98/591153eccdcb574b9844c95736c340e7).
     * Il metodo ricava tutti i file di un duplicato di un rapporto.
     * @param id l'id del rapporto.
     * @param hash il hash del duplicato.
     * @return tutti i file che hanno l'hash passato come parametro e che sono contenuti nel rapporto con id specificato. 
     */
    @GetMapping(value = "/duplicate/{id}/{hash}")
    public @ResponseBody Object getFileByHash(@PathVariable String id, @PathVariable String hash) {
        Integer intId = Validator.isInt(id);
        hash = Validator.isHex(hash);
        if (hash != null && fileRepository.existsByHash(hash) && hash.length() == 30 && intId != null
                && reportRepository.existsById(intId) ) {
            return fileRepository.findFilesFromHashAndReport(reportRepository.findById(intId).get(), hash);
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid file path");
        }
    }
}
