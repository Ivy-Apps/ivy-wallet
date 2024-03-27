package com.ivy.data.repository.mapper

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class TransactionMapperTest {

    private lateinit var mapper: TransactionMapper

    @Before
    fun setup() {
        mapper = TransactionMapper()
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
        val transfer = Transfer(
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
    fun `maps income entity to domain - valid income`() {
        // given
        val entity = ValidIncome

        // when
        val income = with(mapper) { entity.toDomain(EUR) }

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
                asset = EUR
            ),
            account = AccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `maps income entity to domain - blank title is okay`() {
        val blankTitleEntity = ValidIncome.copy(title = "")

        // when
        val income = with(mapper) { blankTitleEntity.toDomain(USD) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - blank description is okay`() {
        val blankDescriptionEntity = ValidIncome.copy(description = "")

        // when
        val income = with(mapper) { blankDescriptionEntity.toDomain(EUR) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no category is okay`() {
        val noCategoryEntity = ValidIncome.copy(categoryId = null)

        // when
        val income = with(mapper) { noCategoryEntity.toDomain(USD) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no recurringId is okay`() {
        val noRecurringId = ValidIncome.copy(recurringRuleId = null)

        // when
        val income = with(mapper) { noRecurringId.toDomain(EUR) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanId is okay`() {
        val noLoanId = ValidIncome.copy(loanId = null)

        // when
        val income = with(mapper) { noLoanId.toDomain(USD) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `maps income entity to domain - no loanRecordId is okay`() {
        val noLoanRecordId = ValidIncome.copy(loanRecordId = null)

        // when
        val income = with(mapper) { noLoanRecordId.toDomain(EUR) }

        // then
        income.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - valid expense`() {
        // given
        val entity = ValidExpense

        // when
        val expense = with(mapper) { entity.toDomain(EUR) }

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
            value = Value(amount = PositiveDouble.unsafe(100.0), asset = EUR),
            account = AccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `expense entity to domain - blank title is okay`() {
        val blankTitleEntity = ValidExpense.copy(title = "")

        // when
        val expense = with(mapper) { blankTitleEntity.toDomain(USD) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - blank description is okay`() {
        val blankDescriptionEntity = ValidExpense.copy(description = "")

        // when
        val expense = with(mapper) { blankDescriptionEntity.toDomain(USD) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no category is okay`() {
        val noCategoryEntity = ValidExpense.copy(categoryId = null)

        // when
        val expense = with(mapper) { noCategoryEntity.toDomain(EUR) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no recurringId is okay`() {
        val noRecurringId = ValidExpense.copy(recurringRuleId = null)

        // when
        val expense = with(mapper) { noRecurringId.toDomain(EUR) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanId is okay`() {
        val noLoanId = ValidExpense.copy(loanId = null)

        // when
        val expense = with(mapper) { noLoanId.toDomain(USD) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `expense entity to domain - no loanRecordId is okay`() {
        val noLoanRecordId = ValidExpense.copy(loanRecordId = null)

        // when
        val expense = with(mapper) { noLoanRecordId.toDomain(EUR) }

        // then
        expense.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - valid transfer`() {
        // when
        val transfer = with(mapper) { ValidTransfer.toDomain(USD, EUR) }

        // then
        transfer.shouldBeRight() shouldBe Transfer(
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
                asset = USD
            ),
            fromAccount = AccountId,
            toValue = Value(
                amount = PositiveDouble.unsafe(100.0),
                asset = EUR
            ),
            toAccount = ToAccountId,
            tags = persistentListOf()
        )
    }

    @Test
    fun `transfer entity to domain - blank title is okay`() {
        val blankTitleEntity = ValidTransfer.copy(title = "")

        // when
        val transfer = with(mapper) { blankTitleEntity.toDomain(USD, USD) }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - blank description is okay`() {
        val blankDescriptionEntity = ValidTransfer.copy(description = "")

        // when
        val transfer = with(mapper) { blankDescriptionEntity.toDomain(EUR, USD) }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no category is okay`() {
        val noCategoryEntity = ValidTransfer.copy(categoryId = null)

        // when
        val transfer = with(mapper) { noCategoryEntity.toDomain(EUR, EUR) }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no recurringId is okay`() {
        val noRecurringId = ValidTransfer.copy(recurringRuleId = null)

        // when
        val transfer = with(mapper) { noRecurringId.toDomain(EUR, EUR) }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanId is okay`() {
        val noLoanId = ValidTransfer.copy(loanId = null)

        // when
        val transfer = with(mapper) { noLoanId.toDomain(USD, USD) }

        // then
        transfer.shouldBeRight()
    }

    @Test
    fun `transfer entity to domain - no loanRecordId is okay`() {
        val noLoanRecordId = ValidTransfer.copy(loanRecordId = null)

        // when
        val transfer = with(mapper) { noLoanRecordId.toDomain(USD, EUR) }

        // then
        transfer.shouldBeRight()
    }

    companion object {
        val DateTime = LocalDateTime.now()
        val AccountId = AccountId(UUID.randomUUID())
        val ToAccountId = AccountId(UUID.randomUUID())
        val CategoryId = CategoryId(UUID.randomUUID())
        val RecurringRuleId = UUID.randomUUID()
        val LoanId = UUID.randomUUID()
        val LoanRecordId = UUID.randomUUID()
        val TransactionId = TransactionId(UUID.randomUUID())
        val InstantNow = Instant.now()
        val USD = AssetCode.unsafe("USD")
        val EUR = AssetCode.unsafe("EUR")

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