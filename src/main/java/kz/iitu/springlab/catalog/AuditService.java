package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    // REQUIRED (по умолчанию) — присоединится к транзакции вызывающего
    @Transactional(propagation = Propagation.REQUIRED)
    public void logRequired(String action, String details) {
        repository.save(new AuditLog(action, details));
    }

    // REQUIRES_NEW — всегда создаёт новую транзакцию
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequiresNew(String action, String details) {
        repository.save(new AuditLog(action, details));
    }
}