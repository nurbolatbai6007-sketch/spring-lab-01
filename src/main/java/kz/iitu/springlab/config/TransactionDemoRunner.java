package kz.iitu.springlab.config;

import kz.iitu.springlab.catalog.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionDemoRunner implements CommandLineRunner {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;

    public TransactionDemoRunner(AccountService accountService,
                                 AccountRepository accountRepository,
                                 AuditLogRepository auditLogRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Account alice = accountRepository.findByOwnerName("Alice").orElse(null);
        Account bob = accountRepository.findByOwnerName("Bob").orElse(null);

        if (alice == null || bob == null) {
            System.out.println("TransactionDemoRunner: Alice or Bob not found, skipping.");
            return;
        }

        if (alice.getBalance().compareTo(new BigDecimal("1000.00")) != 0) {
            System.out.println("TransactionDemoRunner: demo already executed (Alice balance = "
                    + alice.getBalance() + "), skipping.");
            return;
        }

        Long aliceId = alice.getId();
        Long bobId = bob.getId();

        // ===================== TASK A =====================
        System.out.println("\n========== TASK A: TRANSACTION DEMO ==========\n");

        System.out.println(">>> CASE 1: Successful transfer 100 from Alice to Bob");
        System.out.println("    before: Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        accountService.transfer(aliceId, bobId, new BigDecimal("100.00"));
        System.out.println("    after:  Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        System.out.println("    expected: Alice -100, Bob +100\n");

        System.out.println(">>> CASE 2: Transfer more than balance (should rollback)");
        System.out.println("    before: Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        try {
            accountService.transfer(aliceId, bobId, new BigDecimal("999999.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("    after:  Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        System.out.println("    expected: BOTH unchanged\n");

        System.out.println(">>> CASE 3: RuntimeException after debit (should rollback)");
        System.out.println("    before: Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        try {
            accountService.transferWithFailure(aliceId, bobId, new BigDecimal("50.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("    after:  Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        System.out.println("    expected: BOTH unchanged\n");

        System.out.println(">>> CASE 4: Checked exception WITHOUT rollbackFor (WILL NOT rollback)");
        System.out.println("    before: Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        try {
            accountService.transferWithCheckedFailure(aliceId, bobId, new BigDecimal("30.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("    after:  Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        System.out.println("    expected: Alice -30 (NOT rollback)\n");

        System.out.println(">>> CASE 5: Checked exception WITH rollbackFor (WILL rollback)");
        System.out.println("    before: Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        try {
            accountService.transferWithCheckedFailureAndRollback(aliceId, bobId, new BigDecimal("40.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("    after:  Alice=" + accountRepository.findById(aliceId).orElseThrow().getBalance()
                + ", Bob=" + accountRepository.findById(bobId).orElseThrow().getBalance());
        System.out.println("    expected: BOTH unchanged\n");

        // ===================== TASK B =====================
        System.out.println("\n========== TASK B: PROPAGATION DEMO ==========\n");

        System.out.println(">>> CASE B1: Outer @Transactional + Inner REQUIRED (fail after audit)");
        long auditBeforeB1 = auditLogRepository.count();
        System.out.println("    audit rows before = " + auditBeforeB1);
        try {
            accountService.transferWithRequiredAudit(aliceId, bobId, new BigDecimal("10.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getMessage());
        }
        long auditAfterB1 = auditLogRepository.count();
        System.out.println("    audit rows after = " + auditAfterB1);
        System.out.println("    expected: audit NOT saved (REQUIRED rolls back with outer)\n");

        System.out.println(">>> CASE B2: Outer @Transactional + Inner REQUIRES_NEW (fail after audit)");
        long auditBeforeB2 = auditLogRepository.count();
        System.out.println("    audit rows before = " + auditBeforeB2);
        try {
            accountService.transferWithRequiresNewAudit(aliceId, bobId, new BigDecimal("10.00"));
        } catch (Exception e) {
            System.out.println("    caught: " + e.getMessage());
        }
        long auditAfterB2 = auditLogRepository.count();
        System.out.println("    audit rows after = " + auditAfterB2);
        System.out.println("    expected: audit SAVED (REQUIRES_NEW commits independently)\n");

        // ===================== TASK C =====================
        System.out.println("\n========== TASK C: ISOLATION DEMO ==========\n");

        System.out.println(">>> CASE C1: READ_COMMITTED — second read sees change from another tx");
        BigDecimal c1Start = accountRepository.findById(aliceId).orElseThrow().getBalance();
        System.out.println("    Alice balance at start: " + c1Start);

        Thread c1Thread = new Thread(() -> {
            try {
                accountService.readTwiceReadCommitted(aliceId, 500L);
            } catch (Exception e) {
                System.out.println("    [READ_COMMITTED] error: " + e.getMessage());
            }
        });
        c1Thread.start();

        Thread.sleep(100);
        accountService.setBalanceDirectly(aliceId, new BigDecimal("777.00"));
        System.out.println("    [Other tx] changed Alice balance to 777.00 (committed)");
        c1Thread.join();

        accountService.setBalanceDirectly(aliceId, c1Start);
        System.out.println("    Alice balance restored to " + c1Start + "\n");

        System.out.println(">>> CASE C2: REPEATABLE_READ — second read sees OLD value (snapshot)");
        BigDecimal c2Start = accountRepository.findById(aliceId).orElseThrow().getBalance();
        System.out.println("    Alice balance at start: " + c2Start);

        Thread c2Thread = new Thread(() -> {
            try {
                accountService.readTwiceRepeatableRead(aliceId, 500L);
            } catch (Exception e) {
                System.out.println("    [REPEATABLE_READ] error: " + e.getMessage());
            }
        });
        c2Thread.start();

        Thread.sleep(100);
        accountService.setBalanceDirectly(aliceId, new BigDecimal("888.00"));
        System.out.println("    [Other tx] changed Alice balance to 888.00 (committed)");
        c2Thread.join();

        accountService.setBalanceDirectly(aliceId, c2Start);
        System.out.println("    Alice balance restored to " + c2Start + "\n");

        // ===================== TASK D =====================
        System.out.println("\n========== TASK D: OPTIMISTIC LOCKING DEMO ==========\n");

        // Загружаем две копии одной и той же строки в разных транзакциях
        System.out.println(">>> Loading the same Alice account twice (two independent transactions)");
        Account copy1 = accountService.readFresh(aliceId);
        Account copy2 = accountService.readFresh(aliceId);
        Long version1 = copy1.getVersion();
        Long version2 = copy2.getVersion();
        BigDecimal originalBalance = copy1.getBalance();
        System.out.println("    copy1: balance=" + copy1.getBalance() + ", version=" + version1);
        System.out.println("    copy2: balance=" + copy2.getBalance() + ", version=" + version2);

        System.out.println(">>> Update copy1 (committed)");
        accountService.updateWithVersion(aliceId, version1, new BigDecimal("555.00"));
        System.out.println("    copy1 updated to 555.00, committed");

        System.out.println(">>> Attempt to update copy2 with stale version (should FAIL)");
        try {
            accountService.updateWithVersion(aliceId, version2, new BigDecimal("111.00"));
            System.out.println("    ERROR: expected OptimisticLockException but none was thrown");
        } catch (Exception e) {
            System.out.println("    caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        Account finalAccount = accountRepository.findById(aliceId).orElseThrow();
        System.out.println("    final balance = " + finalAccount.getBalance()
                + ", final version = " + finalAccount.getVersion());
        System.out.println("    expected: copy1's change (555.00) is preserved — the second update was rejected\n");

        // Восстановим исходный баланс
        accountService.setBalanceDirectly(aliceId, originalBalance);
        System.out.println("    Alice balance restored to " + originalBalance + "\n");

        System.out.println("========== END OF DEMOS ==========\n");
    }
}