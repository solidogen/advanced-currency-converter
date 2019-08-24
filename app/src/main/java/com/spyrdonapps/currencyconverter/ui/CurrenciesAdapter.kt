package com.spyrdonapps.currencyconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.util.GlideApp
import com.spyrdonapps.currencyconverter.util.extensions.addTextChangedListener
import com.spyrdonapps.currencyconverter.util.extensions.capitalizeWords
import com.spyrdonapps.currencyconverter.util.extensions.showKeyboard
import kotlinx.android.synthetic.main.item_currency.view.*
import timber.log.Timber
import java.util.Collections

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    // TODO scrolling after putting value from keyboard makes unexpected behaviors, unit test it

    private lateinit var recyclerView: RecyclerView
    private var currencies: MutableList<Currency> = mutableListOf()
    private var canUpdateList = true

    private val firstCurrency
        get() = currencies.first()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount() = currencies.count()

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(updatedCurrencies: List<Currency>) {
        if (!canUpdateList || updatedCurrencies.isEmpty()) {
            return
        }
        if (currencies.isEmpty()) {
            currencies = Collections.synchronizedList(updatedCurrencies)
            notifyDataSetChanged()
        } else {
            updatedCurrencies.forEach { updatedCurrency ->
                currencies
                    .firstOrNull { it.isoCode == updatedCurrency.isoCode }
                    ?.let {
                        it.rateBasedOnEuro = updatedCurrency.rateBasedOnEuro
                        if (it.canChangeDisplayedRate) {
                            it.setDisplayableValueBasedOnFirstCurrency(firstCurrency)
                        }
                    }
                // TODO check if I can spare this item changed callback and just change edittext value without redrawing
                currencies.filter { it.canChangeDisplayedRate }
                    .forEach {
                        notifyItemChanged(currencies.indexOf(it))
                    }
            }
        }
    }

    private fun moveItemToTopAndNotify(currency: Currency, position: Int) {
        currencies.apply {
            recyclerView.post {
                canUpdateList = false
                try {
                    currencies.removeAt(position)
                    currencies.add(0, currency)
                    notifyItemMoved(position, 0)
                    scrollToTop()
                } catch (e: Exception) {
                    Timber.e(e)
                } finally {
                    canUpdateList = true
                }
            }
        }
    }

    private fun scrollToTop() {
        (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 1)
    }

    private fun setCurrencyRateNotChangeable(currency: Currency) {
        currencies.forEach {
            it.canChangeDisplayedRate = it.isoCode != currency.isoCode
        }
    }

    private fun getCachedFormattedRateForCurrency(currency: Currency): String {
        return currencies.first { it.isoCode == currency.isoCode }.getFormattedDisplayableRateBasedOnEuro()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(currency: Currency) {
            with(view) {
                setOnClickListener {
                    onItemClicked(currency, adapterPosition, rateEditText)
                }

                isoCodeTextView.text = currency.isoCode
                fullNameTextView.text = currency.fullName.capitalizeWords()

                GlideApp.with(view)
                    .load(currency.flagImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(flagImageView)

                rateEditText.apply {
                    if (currency.canChangeDisplayedRate) {
                        setText(currency.getFormattedDisplayableRateBasedOnEuro())
                    } else {
                        setText(getCachedFormattedRateForCurrency(currency))
                    }
                    setOnTouchListener { _, _ ->
                        view.performClick()
                        true
                    }
                    addTextChangedListener { newText ->
                        currency.enteredValue = newText.toDoubleOrNull() ?: 0.0
                        // todo try to instantly update rates without destroying everything
                    }
                }
            }
        }

        private fun onItemClicked(currency: Currency, position: Int, rateEditText: EditText) {
            Timber.d("Currency clicked: ${currency.isoCode}")
            setCurrencyRateNotChangeable(currency)
            moveItemToTopAndNotify(currency, position)
            rateEditText.run {
                requestFocus()
                setSelection(text.length)
                showKeyboard()
            }
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}