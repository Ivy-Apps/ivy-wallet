package com.ivy.wallet.logic.bankintegrations

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.base.capitalizeLocal
import com.ivy.wallet.model.bankintegrations.SETransaction
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import java.util.*

/*
{
  "data": {
    "business": {
      "equipment_and_materials": [
        "electronics",
        "software",
        "supplies_and_furniture",
        "raw_materials",
        "consumer_goods"
      ],
      "financials": [
        "dividends",
        "donations",
        "interest",
        "fees",
        "fines",
        "loans"
      ],
      "human_resources": [
        "wages",
        "bonus",
        "employee_benefits",
        "education_and_trainings",
        "staff_outsourcing",
        "travel",
        "entertainment",
        "meals"
      ],
      "income": [
        "investments",
        "sales",
        "returns",
        "prepayments"
      ],
      "insurance": [
        "business_insurance",
        "liability_insurance",
        "health_insurance",
        "equipment_insurance",
        "vehicle_insurance",
        "professional_insurance"
      ],
      "real_estate": [
        "office_rent",
        "mortgage",
        "construction_and_repair"
      ],
      "services": [
        "contractors",
        "accounting_and_auditing",
        "legal",
        "consulting",
        "storage",
        "marketing_and_media",
        "online_subscriptions",
        "it_services",
        "cleaning"
      ],
      "taxes": [
        "vat",
        "federal_taxes",
        "property_taxes",
        "income_taxes",
        "duty_taxes",
        "tax_return",
        "payroll_taxes"
      ],
      "transport": [
        "shipping",
        "leasing",
        "gas_and_fuel",
        "taxi",
        "service_and_parts"
      ],
      "uncategorized": [],
      "utilities": [
        "internet",
        "phone",
        "water",
        "gas",
        "electricity"
      ]
    },
    "personal": {
      "auto_and_transport": [
        "car_rental",
        "gas_and_fuel",
        "parking",
        "public_transportation",
        "service_and_parts",
        "taxi"
      ],
      "bills_and_utilities": [
        "internet",
        "phone",
        "television",
        "utilities"
      ],
      "business_services": [
        "advertising",
        "office_supplies",
        "shipping"
      ],
      "education": [
        "books_and_supplies",
        "student_loan",
        "tuition"
      ],
      "entertainment": [
        "amusement",
        "arts",
        "games",
        "movies_and_music",
        "newspapers_and_magazines"
      ],
      "fees_and_charges": [
        "provider_fee",
        "loans",
        "service_fee",
        "taxes"
      ],
      "food_and_dining": [
        "alcohol_and_bars",
        "cafes_and_restaurants",
        "groceries"
      ],
      "gifts_and_donations": [
        "charity",
        "gifts"
      ],
      "health_and_fitness": [
        "doctor",
        "personal_care",
        "pharmacy",
        "sports",
        "wellness"
      ],
      "home": [
        "home_improvement",
        "home_services",
        "home_supplies",
        "mortgage",
        "rent"
      ],
      "income": [
        "bonus",
        "investment_income",
        "paycheck"
      ],
      "insurance": [
        "car_insurance",
        "health_insurance",
        "life_insurance",
        "property_insurance"
      ],
      "kids": [
        "allowance",
        "babysitter_and_daycare",
        "baby_supplies",
        "child_support",
        "kids_activities",
        "toys"
      ],
      "pets": [
        "pet_food_and_supplies",
        "pet_grooming",
        "veterinary"
      ],
      "shopping": [
        "clothing",
        "electronics_and_software",
        "sporting_goods"
      ],
      "transfer": [],
      "travel": [
        "hotel",
        "transportation",
        "vacation"
      ],
      "uncategorized": []
    }
  }
}
 */

class SaltEdgeCategoryMapper(
    private val categoryDao: CategoryDao
) {

    fun mapSeAutoCategoryId(
        seTransaction: SETransaction
    ): UUID? {
        val existingCategory = categoryDao.findBySeCategoryName(
            seCategoryName = seTransaction.category
        )

        val seAutoCategory = if (existingCategory == null) {
            //Try to map and create category
            val seAutoCategory = mapSeCategoryName(seTransaction.category)?.copy(
                orderNum = categoryDao.findMaxOrderNum(),

                isSynced = false,
                isDeleted = false
            )

            if (seAutoCategory != null) {
                categoryDao.save(
                    seAutoCategory
                )
            }

            seAutoCategory
        } else existingCategory

        return seAutoCategory?.id
    }

    private fun mapSeCategoryName(seCategoryName: String): Category? {
        if (seCategoryName == "uncategorized") return null

        val colorIcon = mapCategoryColorIcon(seCategoryName = seCategoryName)

        return Category(
            name = seCategoryName
                .replace("_", " ")
                .capitalizeLocal(),
            seCategoryName = seCategoryName,
            color = (colorIcon?.first ?: IVY_COLOR_PICKER_COLORS_FREE.shuffled().first()).toArgb(),
            icon = colorIcon?.second
        )
    }

    private fun mapCategoryColorIcon(seCategoryName: String): Pair<Color, String?>? {
        return when (seCategoryName) {
            "electronics" -> Pair(Blue, "atom")
            "software" -> Pair(Blue3, "programming")
            "supplies_and_furniture" -> Pair(Orange2Dark, "label")
            "raw_materials" -> Pair(Green2Dark, "hike")
            "consumer_goods" -> Pair(Blue3Light, "shopping")
            "dividends" -> Pair(Orange3, "calculator")
            "donations" -> Pair(Yellow, "relationship")
            "interest" -> Pair(Purple1Light, "selfdevelopment")
            "fees" -> Pair(Orange, "document")
            "fines" -> Pair(Orange2Dark, "loan")
            "loans" -> Pair(Blue2Dark, "loan")
            "wages" -> Pair(IvyDark, "work")
            "bonus" -> Pair(YellowLight, "diamond")
            "employee_benefits" -> Pair(Blue2, "people")
            "education_and_trainings" -> Pair(Blue, "education")
            "staff_outsourcing" -> Pair(Green3, "people")
            "travel" -> Pair(BlueLight, "travel")
            "entertainment" -> Pair(Orange, "game")
            "meals" -> Pair(Green2, "restaurant")
            "investments" -> Pair(Green2Light, "document")
            "sales" -> Pair(Yellow, "label")
            "returns" -> Pair(OrangeLight, "category")
            "prepayments" -> Pair(GreenLight, "bills")
            "business_insurance" -> Pair(Blue2, "insurance")
            "liability_insurance" -> Pair(Blue2, "insurance")
            "health_insurance," -> Pair(Green2Light, "insurance")
            "equipment_insurance" -> Pair(Blue2, "insurance")
            "vehicle_insurance" -> Pair(Blue2, "insurance")
            "professional_insurance" -> Pair(Blue2, "insurance")
            "office_rent" -> Pair(BlueDark, "work")
            "mortgage" -> Pair(Orange2Dark, "house")
            "construction_and_repair" -> Pair(OrangeDark, "tools")
            "contractors" -> Pair(Blue3Dark, "document")
            "accounting_and_auditing" -> Pair(Blue3, "document")
            "legal" -> Pair(Blue3Dark, "document")
            "consulting" -> Pair(Blue2Light, "people")
            "storage" -> Pair(Orange2Dark, "furniture")
            "marketing_and_media" -> Pair(Orange, "rocket")
            "online_subscriptions" -> Pair(Orange3, "connect")
            "it_services" -> Pair(Blue, "programming")
            "cleaning" -> Pair(Blue2Light, "house")
            "vat" -> Pair(OrangeLight, "loan")
            "federal_taxes" -> Pair(RedDark, "loan")
            "property_taxes" -> Pair(RedDark, "loan")
            "income_taxes" -> Pair(RedDark, "loan")
            "duty_taxes" -> Pair(RedDark, "loan")
            "tax_return" -> Pair(GreenDark, "loan")
            "payroll_taxes" -> Pair(RedDark, "loan")
            "shipping" -> Pair(Yellow, "shopping2")
            "leasing" -> Pair(Green4, "house")
            "gas_and_fuel" -> Pair(Green3Dark, "vehicle")
            "taxi" -> Pair(YellowLight, "vehicle")
            "service_and_parts" -> Pair(Green2, "tools")
            "internet" -> Pair(BlueLight, "connect")
            "phone" -> Pair(BlueLight, "bills")
            "water" -> Pair(BlueLight, "bills")
            "gas" -> Pair(Orange3Light, "bills")
            "electricity" -> Pair(Blue2Light, "bills")
            "car_rental" -> Pair(BlueDark, "vehicle")
            "parking" -> Pair(GreenDark, "vehicle")
            "public_transportation" -> Pair(Purple1, "transport")
            "television" -> Pair(Green3Dark, "bills")
            "utilities" -> Pair(Blue, "bills")
            "advertising" -> Pair(Orange2, "rocket")
            "office_supplies" -> Pair(BlueLight, "work")
            "books_and_supplies" -> Pair(BlueLight, "document")
            "student_loan" -> Pair(Blue2Dark, "loan")
            "tuition" -> Pair(Blue2Dark, "education")
            "education" -> Pair(Blue2Dark, "education")
            "amusement" -> Pair(Orange, "rocket")
            "arts" -> Pair(Red, "palette")
            "games" -> Pair(Orange, "game")
            "movies_and_music" -> Pair(Red, "music")
            "newspapers_and_magazines" -> Pair(Orange, "document")
            "provider_fee" -> Pair(RedLight, "loan")
            "service_fee" -> Pair(RedDark, "loan")
            "taxes" -> Pair(RedDark, "bills")
            "alcohol_and_bars" -> Pair(Purple2Dark, "fooddrink")
            "cafes_and_restaurants" -> Pair(Green, "coffee")
            "groceries" -> Pair(Green4, "groceries")
            "charity" -> Pair(GreenLight, "selfdevelopment")
            "gifts" -> Pair(Red, "gift")
            "doctor" -> Pair(Blue2Light, "health")
            "personal_care" -> Pair(Blue2Light, "hairdresser")
            "pharmacy" -> Pair(Blue2Light, "farmacy")
            "sports" -> Pair(IvyLight, "sports")
            "wellness" -> Pair(GreenLight, "fitness")
            "home_improvement" -> Pair(Blue2Light, "house")
            "home_services" -> Pair(Blue2Light, "house")
            "home_supplies" -> Pair(Blue2Light, "house")
            "rent" -> Pair(BlueDark, "house")
            "investment_income" -> Pair(Green2Light, "leaf")
            "paycheck" -> Pair(Green, "work")
            "car_insurance" -> Pair(Blue2, "insurance")
            "health_insurance" -> Pair(Blue2, "insurance")
            "life_insurance" -> Pair(Green2Light, "insurance")
            "property_insurance" -> Pair(Blue2, "insurance")
            "allowance" -> Pair(GreenLight, "loan")
            "babysitter_and_daycare" -> Pair(BlueLight, "family")
            "baby_supplies" -> Pair(BlueLight, "shopping2")
            "child_support" -> Pair(BlueLight, "family")
            "kids_activities" -> Pair(Blue, "sports")
            "toys" -> Pair(Yellow, "rocket")
            "pet_food_and_supplies" -> Pair(Orange3Light, "pet")
            "pet_grooming" -> Pair(Orange3Light, "pet")
            "veterinary" -> Pair(Purple1, "pet")
            "clothing" -> Pair(Red3, "clothes2")
            "electronics_and_software" -> Pair(Blue, "atom")
            "sporting_goods" -> Pair(Blue, "sports")
            "hotel" -> Pair(Orange2Dark, "location")
            "transportation" -> Pair(Yellow, "transport")
            "vacation" -> Pair(Blue2Light, "travel")

            //Extra not documented categories
            "bills_and_utilities" -> Pair(Red, "bills")
            "business_services" -> Pair(BlueDark, "groceries")
            "shopping" -> Pair(BlueLight, "shopping")
            "income" -> Pair(Green, "work")
            "transfer" -> Pair(Ivy, "bank")
            else -> null
        }
    }
}