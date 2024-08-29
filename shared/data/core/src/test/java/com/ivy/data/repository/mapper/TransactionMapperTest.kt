package com.ivy.data.repository.mapper

import arrow.core.Some
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.AssetCode.Companion.EUR
import com.ivy.data.model.primitive.AssetCode.Companion.USD
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.model.testing.account
import com.ivy.data.repository.AccountRepository
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.UUID

@RunWith(TestParameterInjector::class)
class TransactionMapperTest {

    private val accountRepo = mockk<AccountRepository>()

    private lateinit var mapper: TransactionMapper

    @Before
    fun setup() {
        mapper = TransactionMapper(accountRepository = accountRepo)
    }

    // region entity -> domain
    @Test
    fun `maps domain income to entity`(
        @TestParameter settled: Boolean,
    ) {
        // given
        val income = Income(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Income"),
            description = NotBlankTrimmedString.unsafe("Income desc"),
            category = CategoryId,
            time = InstantNow,
            settled = settled,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                paidForDateTime = PaidForDateTime,
                loanRecordId = LoanRecordId
            ),
            value = PositiveValue(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            account = AccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { income.toEntity() }

        // then
        val dateTime = InstantNow
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.INCOME,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Income",
            description = "Income desc",
            dateTime = dateTime.takeIf { settled },
            categoryId = CategoryId.value,
            dueDate = dateTime.takeIf { !settled },
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = true,
            isDeleted = false,
            id = TransactionId.value
        )
    }

    @Test
    fun `maps domain expense to entity`(
        @TestParameter settled: Boolean,
    ) {
        // given
        val expense = Expense(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Expense"),
            description = NotBlankTrimmedString.unsafe("Expense desc"),
            category = CategoryId,
            time = InstantNow,
            settled = settled,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                paidForDateTime = PaidForDateTime,
                loanRecordId = LoanRecordId
            ),
            value = PositiveValue(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            account = AccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { expense.toEntity() }

        // then
        val dateTime = InstantNow
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.EXPENSE,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Expense",
            description = "Expense desc",
            dateTime = dateTime.takeIf { settled },
            categoryId = CategoryId.value,
            dueDate = dateTime.takeIf { !settled },
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = true,
            isDeleted = false,
            id = TransactionId.value
        )
    }

    @Test
    fun `maps domain transfer to entity`(
        @TestParameter settled: Boolean,
    ) {
        // given
        val transfer = Transfer(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Transfer"),
            description = NotBlankTrimmedString.unsafe("Transfer desc"),
            category = CategoryId,
            time = InstantNow,
            settled = settled,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                paidForDateTime = PaidForDateTime,
                loanRecordId = LoanRecordId
            ),
            fromValue = PositiveValue(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            fromAccount = AccountId,
            toValue = PositiveValue(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            toAccount = ToAccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { transfer.toEntity() }

        // then
        val dateTime = InstantNow
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.TRANSFER,
            amount = 100.0,
            toAccountId = ToAccountId.value,
            toAmount = 100.0,
            title = "Transfer",
            description = "Transfer desc",
            dateTime = dateTime.takeIf { settled },
            categoryId = CategoryId.value,
            dueDate = dateTime.takeIf { !settled },
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = true,
            isDeleted = false,
            id = TransactionId.value
        )
    }
    // endregion

    // domain -> entity
    @Test
    fun `maps income entity to domain - valid income`(
        @TestParameter settled: Boolean,
        @TestParameter removed: Boolean,
    ) = runTest {
        // given
        val entity = ValidIncome.copy(
            dateTime = InstantNow.takeIf { settled },
            dueDate = InstantNow.takeIf { !settled },
            isDeleted = removed,
        )
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { entity.toDomain() }

        // then
        if (removed) {
            income.shouldBeLeft()
        } else {
            income.shouldBeRight() shouldBe Income(
                id = TransactionId,
                title = NotBlankTrimmedString.unsafe("Income"),
                description = NotBlankTrimmedString.unsafe("Income desc"),
                category = CategoryId,
                time = InstantNow,
                settled = settled,
                metadata = TransactionMetadata(
                    recurringRuleId = RecurringRuleId,
                    loanId = LoanId,
                    paidForDateTime = PaidForDateTime,
                    loanRecordId = LoanRecordId
                ),
                value = PositiveValue(
                    amount = PositiveDouble.unsafe(100.0),
                    asset = EUR
                ),
                account = AccountId,
                tags = persistentListOf()
            )
        }
    }

    @Test
    fun `maps income entity to domain - blank title is okay`() = runTest {
        val blankTitleEntity = ValidIncome.copy(title = "")
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { blankTitleEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidIncome.copy(description = "")
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no category is okay`() = runTest {
        val noCategoryEntity = ValidIncome.copy(categoryId = null)
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { noCategoryEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no recurringId is okay`() = runTest {
        val noRecurringId = ValidIncome.copy(recurringRuleId = null)
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { noRecurringId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanId is okay`() = runTest {
        // given
        val noLoanId = ValidIncome.copy(loanId = null)
        mockkAccounts(account = USD)

        // when
        val income = with(mapper) { noLoanId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanRecordId is okay`() = runTest {
        // given
        val noLoanRecordId = ValidIncome.copy(loanRecordId = null)
        mockkAccounts(account = EUR)

        // when
        val income = with(mapper) { noLoanRecordId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `income entity to domain - missing source account is failure`() = runTest {
        // given
        coEvery { accountRepo.findById(AccountId) } returns null

        // when
        val transfer = with(mapper) { ValidIncome.toDomain() }

        // then
        transfer.shouldBeLeft()
    }

    @Test
    fun `expense entity to domain - valid expense`(
        @TestParameter settled: Boolean,
        @TestParameter removed: Boolean,
    ) = runTest {
        // given
        val entity = ValidExpense.copy(
            dateTime = InstantNow.takeIf { settled },
            dueDate = InstantNow.takeIf { !settled },
            isDeleted = removed
        )
        mockkAccounts(account = EUR)

        // when
        val expense = with(mapper) { entity.toDomain() }

        // then
        if (removed) {
            expense.shouldBeLeft()
        } else {
            expense.shouldBeRight() shouldBe Expense(
                id = TransactionId,
                title = NotBlankTrimmedString.unsafe("Expense"),
                description = NotBlankTrimmedString.unsafe("Expense desc"),
                category = CategoryId,
                time = InstantNow,
                settled = settled,
                metadata = TransactionMetadata(
                    recurringRuleId = RecurringRuleId,
                    loanId = LoanId,
                    paidForDateTime = PaidForDateTime,
                    loanRecordId = LoanRecordId
                ),
                value = PositiveValue(
                    amount = PositiveDouble.unsafe(100.0),
                    asset = EUR
                ),
                account = AccountId,
                tags = persistentListOf()
            )
        }
    }

    @Test
    fun `expense entity to domain - blank title is okay`() = runTest {
        val blankTitleEntity = ValidExpense.copy(title = "")
        mockkAccounts(account = USD)

        // when
        val expense = with(mapper) { blankTitleEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidExpense.copy(description = "")
        mockkAccounts(account = EUR)

        // when
        val expense = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no category is okay`() = runTest {
        // given
        val noCategoryEntity = ValidExpense.copy(categoryId = null)
        mockkAccounts(account = EUR)

        // when
        val expense = with(mapper) { noCategoryEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no recurringId is okay`() = runTest {
        val noRecurringId = ValidExpense.copy(recurringRuleId = null)
        mockkAccounts(account = EUR)

        // when
        val expense = with(mapper) { noRecurringId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanId is okay`() = runTest {
        val noLoanId = ValidExpense.copy(loanId = null)
        mockkAccounts(account = USD)

        // when
        val expense = with(mapper) { noLoanId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanRecordId is okay`() = runTest {
        // given
        val noLoanRecordId = ValidExpense.copy(loanRecordId = null)
        mockkAccounts(account = EUR)

        // when
        val expense = with(mapper) { noLoanRecordId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - missing source account is failure`() = runTest {
        // given
        coEvery { accountRepo.findById(AccountId) } returns null

        // when
        val transfer = with(mapper) { ValidExpense.toDomain() }

        // then
        transfer.shouldBeLeft()
    }

    @Test
    fun `transfer entity to domain - valid transfer`(
        @TestParameter settled: Boolean,
        @TestParameter removed: Boolean,
    ) = runTest {
        // given
        val entity = ValidTransfer.copy(
            dateTime = InstantNow.takeIf { settled },
            dueDate = InstantNow.takeIf { !settled },
            isDeleted = removed,
            amount = 50.0,
            toAmount = 55.0,
        )
        mockkAccounts(
            account = EUR,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { entity.toDomain() }

        // then
        if (removed) {
            transfer.shouldBeLeft()
        } else {
            transfer.shouldBeRight() shouldBe Transfer(
                id = TransactionId,
                title = NotBlankTrimmedString.unsafe("Transfer"),
                description = NotBlankTrimmedString.unsafe("Transfer desc"),
                category = CategoryId,
                time = InstantNow,
                settled = settled,
                metadata = TransactionMetadata(
                    recurringRuleId = RecurringRuleId,
                    loanId = LoanId,
                    paidForDateTime = PaidForDateTime,
                    loanRecordId = LoanRecordId
                ),
                fromValue = PositiveValue(
                    amount = PositiveDouble.unsafe(50.0),
                    asset = EUR
                ),
                fromAccount = AccountId,
                toValue = PositiveValue(
                    amount = PositiveDouble.unsafe(55.0),
                    asset = USD
                ),
                toAccount = ToAccountId,
                tags = persistentListOf()
            )
        }
    }

    @Test
    fun `transfer entity to domain - blank title is okay`() = runTest {
        // given
        val blankTitleEntity = ValidTransfer.copy(title = "")
        mockkAccounts(
            account = EUR,
            toAccount = EUR
        )

        // when
        val transfer = with(mapper) { blankTitleEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidTransfer.copy(description = "")
        mockkAccounts(
            account = USD,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no category is okay`() = runTest {
        // given
        val noCategoryEntity = ValidTransfer.copy(categoryId = null)
        mockkAccounts(
            account = USD,
            toAccount = EUR
        )

        // when
        val transfer = with(mapper) { noCategoryEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no recurringId is okay`() = runTest {
        // given
        val noRecurringId = ValidTransfer.copy(recurringRuleId = null)
        mockkAccounts(
            account = EUR,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { noRecurringId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanId is okay`() = runTest {
        // given
        val noLoanId = ValidTransfer.copy(loanId = null)
        mockkAccounts(
            account = USD,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { noLoanId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanRecordId is okay`() = runTest {
        // given
        val noLoanRecordId = ValidTransfer.copy(loanRecordId = null)
        mockkAccounts(
            account = EUR,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { noLoanRecordId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no toAmount is okay`() = runTest {
        // given
        val noLoanRecordId = ValidTransfer.copy(toAmount = null)
        mockkAccounts(
            account = EUR,
            toAccount = USD
        )

        // when
        val transfer = with(mapper) { noLoanRecordId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - missing source account is failure`() = runTest {
        // given
        coEvery { accountRepo.findById(AccountId) } returns null
        coEvery {
            accountRepo.findById(ToAccountId)
        } returns Arb.account(asset = Some(EUR)).next()

        // when
        val transfer = with(mapper) { ValidTransfer.toDomain() }

        // then
        transfer.shouldBeLeft()
    }

    @Test
    fun `transfer entity to domain - missing destination account is failure`() = runTest {
        // given
        coEvery {
            accountRepo.findById(AccountId)
        } returns Arb.account(asset = Some(EUR)).next()
        coEvery { accountRepo.findById(ToAccountId) } returns null

        // when
        val transfer = with(mapper) { ValidTransfer.toDomain() }

        // then
        transfer.shouldBeLeft()
    }
    // endregion

    private fun mockkAccounts(
        account: AssetCode,
        toAccount: AssetCode? = null,
    ) {
        coEvery {
            accountRepo.findById(AccountId)
        } returns Arb.account(asset = Some(account)).next()
        if (toAccount != null) {
            coEvery {
                accountRepo.findById(ToAccountId)
            } returns Arb.account(asset = Some(toAccount)).next()
        }
    }

    companion object {
        val AccountId = AccountId(UUID.randomUUID())
        val ToAccountId = AccountId(UUID.randomUUID())
        val CategoryId = CategoryId(UUID.randomUUID())
        val RecurringRuleId = UUID.randomUUID()
        val PaidForDateTime: Instant = Instant.now()
        val LoanId = UUID.randomUUID()
        val LoanRecordId = UUID.randomUUID()
        val TransactionId = TransactionId(UUID.randomUUID())
        val InstantNow = Instant.now()

        val ValidIncome = TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.INCOME,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Income",
            description = "Income desc",
            dateTime = InstantNow,
            categoryId = CategoryId.value,
            dueDate = null,
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = true,
            isDeleted = false,
            id = TransactionId.value
        )

        val ValidExpense = TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.EXPENSE,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Expense",
            description = "Expense desc",
            dateTime = InstantNow,
            categoryId = CategoryId.value,
            dueDate = null,
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = true,
            isDeleted = false,
            id = TransactionId.value
        )

        val ValidTransfer = TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.TRANSFER,
            amount = 100.0,
            toAccountId = ToAccountId.value,
            toAmount = 100.0,
            title = "Transfer",
            description = "Transfer desc",
            dateTime = InstantNow,
            categoryId = CategoryId.value,
            dueDate = null,
            paidForDateTime = PaidForDateTime,
            recurringRuleId = RecurringRuleId,
            attachmentUrl = null,
            loanId = LoanId,
            loanRecordId = LoanRecordId,
            isSynced = false,
            isDeleted = false,
            id = TransactionId.value
        )
    }
}