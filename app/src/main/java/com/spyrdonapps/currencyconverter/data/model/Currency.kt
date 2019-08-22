package com.spyrdonapps.currencyconverter.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.spyrdonapps.currencyconverter.ui.CurrenciesAdapter
import java.util.Locale

@Entity(tableName = "currencies")
data class Currency(

    @PrimaryKey
    @ColumnInfo(name = "iso_code")
    val isoCode: String,

    @ColumnInfo(name = "rate_based_on_euro")
    var rateBasedOnEuro: Double
) {
    @Ignore
    val flagImageUrl = "https://fxtop.com/ico/${isoCode.toLowerCase(Locale.ROOT)}.gif"

    @Ignore
    val fullName = getCurrencyFullNameByIsoCode(isoCode)

    // todo ignored property of 'canChangeRate' with getter
    // + rateBasedOnEuro set will depend on this prop

    companion object {

        private fun getCurrencyFullNameByIsoCode(isoCode: String): String {
            return when (isoCode) {
                CurrenciesAdapter.EURO_ISO_CODE -> "Euro"
                "AUD" -> "Australian dollar"
                "BGN" -> "Bulgarian lev"
                "BRL" -> "Brazilian real"
                "CAD" -> "Canadian dollar"
                "CHF" -> "Swiss franc"
                "CNY" -> "Chinese yuan"
                "CZK" -> "Czech koruna"
                "DKK" -> "Danish krone"
                "GBP" -> "British pound"
                "HKD" -> "Hong Kong dollar"
                "HRK" -> "Croatian kuna"
                "HUF" -> "Hungarian forint"
                "IDR" -> "Indonesian rupiah"
                "ILS" -> "Israeli new shekel"
                "INR" -> "Indian rupee"
                "ISK" -> "Icelandic króna"
                "JPY" -> "Japanese yen"
                "KRW" -> "South Korean won"
                "MXN" -> "Mexican peso"
                "MYR" -> "Malaysian ringgit"
                "NOK" -> "Norwegian krone"
                "NZD" -> "New Zealand dollar"
                "PHP" -> "Philippine peso"
                "PLN" -> "Polish złoty"
                "RON" -> "Romanian leu"
                "RUB" -> "Russian ruble"
                "SEK" -> "Swedish krona"
                "SGD" -> "Singapore dollar"
                "THB" -> "Thai baht"
                "TRY" -> "Turkish lira"
                "USD" -> "United States dollar"
                "ZAR" -> "South African rand"
                else -> ""
            }
        }
    }
}