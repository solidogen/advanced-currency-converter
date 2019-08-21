package com.spyrdonapps.currencyconverter.data.remote

data class CurrenciesResponse(
    val base: String,
    val date: String,
    val rates: ApiCurrencies
)

// todo map to list,  map name
data class ApiCurrencies(val AUD: Double, val BGN: Double, val BRL: Double, val CAD: Double,
                         val CHF: Double, val CNY: Double, val CZK: Double, val DKK: Double,
                         val GBP: Double, val HKD: Double, val HRK: Double, val HUF: Double,
                         val IDR: Double, val ILS: Double, val INR: Double, val ISK: Double,
                         val JPY: Double, val KRW: Double, val MXN: Double, val MYR: Double,
                         val NOK: Double, val NZD: Double, val PHP: Double, val PLN: Double,
                         val RON: Double, val RUB: Double, val SEK: Double, val SGD: Double,
                         val THB: Double, val TRY: Double, val USD: Double, val ZAR: Double)

// todo finish this
data class CurrencyUiModel(val isoCode: String, val currencyName: String)