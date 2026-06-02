package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.demo.model.TableSpot;

@Repository
public interface TableSpotRepository extends ReactiveMongoRepository<TableSpot, String> {
}

