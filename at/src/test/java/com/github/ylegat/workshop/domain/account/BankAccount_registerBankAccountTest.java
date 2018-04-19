package com.github.ylegat.workshop.domain.account;

import com.github.ylegat.workshop.domain.common.ConflictingEventException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Fail.fail;

public class BankAccount_registerBankAccountTest extends AbstractBankAccountTesting {

    private String bankAccountId;

    @Before
    public void setUp() {
        bankAccountId = "aBankAccountId";
    }

    @Test
    public void should_register_bank_account_with_success() {
        BankAccount bankAccount = BankAccount.registerBankAccount(bankAccountId, eventStore);

        assertThatEvents(bankAccountId).containsExactly(new BankAccountRegistered(bankAccountId));
        assertThat(bankAccount).isEqualTo(new BankAccount(bankAccountId, eventStore, 0, 1));
    }

    @Test
    public void should_fail_registering_bank_account_with_already_used_id() {
        BankAccount.registerBankAccount(bankAccountId, eventStore);

        Throwable throwable = catchThrowable(() -> BankAccount.registerBankAccount(bankAccountId, eventStore));

        assertThat(throwable).isInstanceOf(ConflictingEventException.class);
        assertThatEvents(bankAccountId).containsExactly(new BankAccountRegistered(bankAccountId));
    }

}
