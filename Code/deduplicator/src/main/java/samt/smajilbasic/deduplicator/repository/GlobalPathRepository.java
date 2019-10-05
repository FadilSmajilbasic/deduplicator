package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.GlobalPath;


/**
 * GlobalPathRepository
 */
@Repository
public interface GlobalPathRepository extends CrudRepository<GlobalPath,String> {

}