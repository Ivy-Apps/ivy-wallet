package com.ivy.data.repository.mapper

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.repository.AccountRepository
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class TransactionMapperTest {

    private val accountRepo = mockk<AccountRepository>()

    private lateinit var mapper: TransactionMapper

    @Before
    fun setup() {
        mapper = TransactionMapper(accountRepo)
    }

    @Test
    fun `maps domain income to entity`() {
        // given
        val income = Income(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Income"),
            description = NotBlankTrimmedString.unsafe("Income desc"),
            category = CategoryId,
            time = InstantNow,
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = InstantNow,
            removed = false,
            value = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            account = AccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { income.toEntity() }

        // then
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.INCOME,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Income",
            description = "Income desc",
            dateTime = InstantNow.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = CategoryId.value,
            dueDate = null,
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
    fun `maps domain expense to entity`() {
        // given
        val expense = Expense(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Expense"),
            description = NotBlankTrimmedString.unsafe("Expense desc"),
            category = CategoryId,
            time = InstantNow,
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            account = AccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { expense.toEntity() }

        // then
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.EXPENSE,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Expense",
            description = "Expense desc",
            dateTime = InstantNow.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = CategoryId.value,
            dueDate = null,
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
    fun `maps domain transfer to entity`() {
        // given
        val transfer = com.ivy.data.model.Transfer(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Transfer"),
            description = NotBlankTrimmedString.unsafe("Transfer desc"),
            category = CategoryId,
            time = InstantNow,
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = Instant.EPOCH,
            removed = false,
            fromValue = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            fromAccount = AccountId,
            toValue = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.unsafe("NGN")
            ),
            toAccount = ToAccountId,
            tags = persistentListOf()
        )

        // when
        val entity = with(mapper) { transfer.toEntity() }

        // then
        entity shouldBe TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.TRANSFER,
            amount = 100.0,
            toAccountId = ToAccountId.value,
            toAmount = 100.0,
            title = "Transfer",
            description = "Transfer desc",
            dateTime = InstantNow.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = CategoryId.value,
            dueDate = null,
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
    fun `maps income entity to domain - valid income`() = runTest {
        // given
        val entity = ValidIncome
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val income = with(mapper) { entity.toDomain() }

        // then
        income.shouldBeRight() shouldBe Income(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Income"),
            description = NotBlankTrimmedString.unsafe("Income desc"),
            category = CategoryId,
            time = DateTime.atZone(ZoneId.systemDefault()).toInstant(),
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.EUR
            ),
            account = AccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `maps income entity to domain - blank title is okay`() = runTest {
        val blankTitleEntity = ValidIncome.copy(title = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val income = with(mapper) { blankTitleEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidIncome.copy(description = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val income = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no category is okay`() = runTest {
        val noCategoryEntity = ValidIncome.copy(categoryId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val income = with(mapper) { noCategoryEntity.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no recurringId is okay`() = runTest {
        val noRecurringId = ValidIncome.copy(recurringRuleId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val income = with(mapper) { noRecurringId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanId is okay`() = runTest {
        // given
        val noLoanId = ValidIncome.copy(loanId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val income = with(mapper) { noLoanId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanRecordId is okay`() = runTest {
        // given
        val noLoanRecordId = ValidIncome.copy(loanRecordId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val income = with(mapper) { noLoanRecordId.toDomain() }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - valid expense`() = runTest {
        // given
        val entity = ValidExpense
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val expense = with(mapper) { entity.toDomain() }

        // then
        expense.shouldBeRight() shouldBe Expense(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Expense"),
            description = NotBlankTrimmedString.unsafe("Expense desc"),
            category = CategoryId,
            time = DateTime.atZone(ZoneId.systemDefault()).toInstant(),
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.EUR
            ),
            account = AccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `expense entity to domain - blank title is okay`() = runTest {
        val blankTitleEntity = ValidExpense.copy(title = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val expense = with(mapper) { blankTitleEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidExpense.copy(description = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val expense = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no category is okay`() = runTest {
        // given
        val noCategoryEntity = ValidExpense.copy(categoryId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val expense = with(mapper) { noCategoryEntity.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no recurringId is okay`() = runTest {
        val noRecurringId = ValidExpense.copy(recurringRuleId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val expense = with(mapper) { noRecurringId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanId is okay`() = runTest {
        val noLoanId = ValidExpense.copy(loanId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val expense = with(mapper) { noLoanId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanRecordId is okay`() = runTest {
        // given
        val noLoanRecordId = ValidExpense.copy(loanRecordId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val expense = with(mapper) { noLoanRecordId.toDomain() }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - valid transfer`() = runTest {
        // given
        val entity = ValidTransfer
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { entity.toDomain() }

        // then
        transfer.shouldBeRight() shouldBe com.ivy.data.model.Transfer(
            id = TransactionId,
            title = NotBlankTrimmedString.unsafe("Transfer"),
            description = NotBlankTrimmedString.unsafe("Transfer desc"),
            category = CategoryId,
            time = DateTime.atZone(ZoneId.systemDefault()).toInstant(),
            settled = true,
            metadata = TransactionMetadata(
                recurringRuleId = RecurringRuleId,
                loanId = LoanId,
                loanRecordId = LoanRecordId
            ),
            lastUpdated = Instant.EPOCH,
            removed = false,
            fromValue = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.EUR
            ),
            fromAccount = AccountId,
            toValue = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = AssetCode.USD
            ),
            toAccount = ToAccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `transfer entity to domain - blank title is okay`() = runTest {
        // given
        val blankTitleEntity = ValidTransfer.copy(title = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val transfer = with(mapper) { blankTitleEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - blank description is okay`() = runTest {
        val blankDescriptionEntity = ValidTransfer.copy(description = "")
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { blankDescriptionEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no category is okay`() = runTest {
        // given
        val noCategoryEntity = ValidTransfer.copy(categoryId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }

        // when
        val transfer = with(mapper) { noCategoryEntity.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no recurringId is okay`() = runTest {
        // given
        val noRecurringId = ValidTransfer.copy(recurringRuleId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { noRecurringId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanId is okay`() = runTest {
        val noLoanId = ValidTransfer.copy(loanId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { noLoanId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanRecordId is okay`() = runTest {
        val noLoanRecordId = ValidTransfer.copy(loanRecordId = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { noLoanRecordId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no toAmount is okay`() = runTest {
        val noLoanRecordId = ValidTransfer.copy(toAmount = null)
        coEvery { accountRepo.findById(AccountId) } returns mockk {
            every { asset } returns AssetCode.EUR
        }
        coEvery { accountRepo.findById(ToAccountId) } returns mockk {
            every { asset } returns AssetCode.USD
        }

        // when
        val transfer = with(mapper) { noLoanRecordId.toDomain() }

        // then
        transfer.shouldBeRight()
    }

    companion object {
        val DateTime = LocalDateTime.now()
        val AccountId = com.ivy.data.model.AccountId(UUID.randomUUID())
        val ToAccountId = com.ivy.data.model.AccountId(UUID.randomUUID())
        val CategoryId = com.ivy.data.model.CategoryId(UUID.randomUUID())
        val RecurringRuleId = UUID.randomUUID()
        val LoanId = UUID.randomUUID()
        val LoanRecordId = UUID.randomUUID()
        val TransactionId = com.ivy.data.model.TransactionId(UUID.randomUUID())
        val InstantNow = Instant.now()

        val ValidIncome = TransactionEntity(
            accountId = AccountId.value,
            type = TransactionType.INCOME,
            amount = 100.0,
            toAccountId = null,
            toAmount = null,
            title = "Income",
            description = "Income desc",
            dateTime = DateTime,
            categoryId = CategoryId.value,
            dueDate = null,
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
            dateTime = DateTime,
            categoryId = CategoryId.value,
            dueDate = null,
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
            dateTime = DateTime,
            categoryId = CategoryId.value,
            dueDate = null,
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