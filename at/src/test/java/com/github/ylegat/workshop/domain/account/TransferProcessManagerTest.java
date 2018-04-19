package com.github.ylegat.workshop.domain.account;

import static com.github.ylegat.workshop.domain.account.BankAccount.registerBankAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class TransferProcessManagerTest extends AbstractBankAccountTesting {

    private TransferProcessManager transferProcessManager;
    private BankAccount origin;

    @Before
    public void setUp() throws Exception {
        transferProcessManager = new TransferProcessManager(eventStore);
        eventBus.register(transferProcessManager);
        origin = registerBankAccount("origin", eventStore);
        origin.provisionCredit(1);
    }

    @Test
    public void should_cancel_transfer_when_destination_does_not_exist() throws InterruptedException {
        String transfer = origin.requestTransfer("nonRegisteredAccount", 1);

        Thread.sleep(100);
        assertThatEvents("origin").containsExactly(
                new BankAccountRegistered("origin"),
                new CreditProvisioned("origin", 1, 1),
                new TransferRequested("origin", transfer, "nonRegisteredAccount", 1, 0)
        );
    }

    @Test
    public void should_complete_transfer_when_destination_exist() throws InterruptedException {
         registerBankAccount("destination", eventStore);

        String transfer = origin.requestTransfer("destination", 1);

        Thread.sleep(100);
        assertThatEvents("origin").containsExactly(
                new BankAccountRegistered("origin"),
                new CreditProvisioned("origin", 1, 1),
                new TransferRequested("origin", transfer, "destination", 1, 0),
                new TransferCompleted("origin", transfer, "destination")
        );
        assertThatEvents("destination").containsExactly(
                new BankAccountRegistered("destination"),
                new TransferReceived("destination", transfer, "origin", 1, 1)
        );
    }

}
