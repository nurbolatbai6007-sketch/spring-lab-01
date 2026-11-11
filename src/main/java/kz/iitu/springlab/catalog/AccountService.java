package kz.iitu.springlab.catalog;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository repository;
    private final AuditService auditService;
    private final EntityManager entityManager;

    public AccountService(AccountRepository repository,
                          AuditService auditService,
                          EntityManager entityManager) {
        this.repository = repository;
        this.auditService = auditService;
        this.entityManager = entityManager;
    }

    // ===== Task A =====
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
    }

    @Transactional
    public void transferWithFailure(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.save(from);
        if (true) throw new RuntimeException("Simulated failure after debit");
        to.credit(amount);
        repository.save(to);
    }

    @Transactional
    public void transferWithCheckedFailure(Long fromId, Long toId, BigDecimal amount) throws Exception {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.save(from);
        if (true) throw new Exception("Simulated checked exception");
        to.credit(amount);
        repository.save(to);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferWithCheckedFailureAndRollback(Long fromId, Long toId, BigDecimal amount) throws Exception {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.save(from);
        if (true) throw new Exception("Simulated checked exception with rollbackFor");
        to.credit(amount);
        repository.save(to);
    }

    // ===== Task B =====
    @Transactional
    public void transferWithRequiredAudit(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
        auditService.logRequired("TRANSFER", "from=" + fromId + " to=" + toId + " amount=" + amount);
        if (true) throw new RuntimeException("Simulated failure after REQUIRED audit");
    }

    @Transactional
    public void transferWithRequiresNewAudit(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();
        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
        auditService.logRequiresNew("TRANSFER", "from=" + fromId + " to=" + toId + " amount=" + amount);
        if (true) throw new RuntimeException("Simulated failure after REQUIRES_NEW audit");
    }

    // ===== Task C =====
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readTwiceReadCommitted(Long accountId, Long sleepMs) throws InterruptedException {
        Account firstRead = repository.findById(accountId).orElseThrow();
        BigDecimal firstBalance = firstRead.getBalance();
        System.out.println("    [READ_COMMITTED] First read: balance = " + firstBalance);
        Thread.sleep(sleepMs);
        entityManager.clear();
        BigDecimal secondBalance = repository.findById(accountId).orElseThrow().getBalance();
        System.out.println("    [READ_COMMITTED] Second read: balance = " + secondBalance);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void readTwiceRepeatableRead(Long accountId, Long sleepMs) throws InterruptedException {
        Account firstRead = repository.findById(accountId).orElseThrow();
        BigDecimal firstBalance = firstRead.getBalance();
        System.out.println("    [REPEATABLE_READ] First read: balance = " + firstBalance);
        Thread.sleep(sleepMs);
        entityManager.clear();
        BigDecimal secondBalance = repository.findById(accountId).orElseThrow().getBalance();
        System.out.println("    [REPEATABLE_READ] Second read: balance = " + secondBalance);
    }

    @Transactional
    public void setBalanceDirectly(Long accountId, BigDecimal newBalance) {
        Account a = repository.findById(accountId).orElseThrow();
        a.setBalance(newBalance);
        repository.save(a);
    }

    // ===== Task D: чтение (без изменений, для двух транзакций) =====
    @Transactional(readOnly = true)
    public Account readFresh(Long accountId) {
        return repository.findById(accountId).orElseThrow();
    }

    // ===== Task D: обновление с версией (может кинуть OptimisticLockException) =====
    @Transactional
    public void updateWithVersion(Long accountId, Long expectedVersion, BigDecimal newBalance) {
        Account a = repository.findById(accountId).orElseThrow();
        if (!a.getVersion().equals(expectedVersion)) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
                    Account.class, accountId);
        }
        a.setBalance(newBalance);
        repository.save(a);
    }
}