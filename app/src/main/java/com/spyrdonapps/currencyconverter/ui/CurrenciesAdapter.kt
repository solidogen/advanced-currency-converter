package com.spyrdonapps.currencyconverter.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.util.GlideApp
import kotlinx.android.synthetic.main.item_currency.view.*
import timber.log.Timber
import java.util.Collections

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private var currentTopCurrencyIsoCode: String = EURO_ISO_CODE
    private var canUpdateList = true

    private var curriencies: MutableList<Currency> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(curriencies[position])
    }

    override fun getItemCount() = curriencies.count()

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(newCurrencies: List<Currency>) {
        if (!canUpdateList) {
            return
        }
        if (curriencies.isEmpty()) {
            if (newCurrencies.isNotEmpty()) {
                curriencies = Collections.synchronizedList(newCurrencies)
                notifyDataSetChanged()
            }
        } else {
            if (newCurrencies.isNotEmpty()) {
                newCurrencies.forEach { updatedCurrency ->
                    curriencies.firstOrNull { it.isoCode == updatedCurrency.isoCode }?.let {
                        it.rateBasedOnEuro = updatedCurrency.rateBasedOnEuro
                        notifyItemChanged(curriencies.indexOf(it))
                    }
                }
            }
        }
    }

    private fun moveItemToTopAndNotify(item: Currency) {
        curriencies.apply {
            recyclerView.post {
                canUpdateList = false
                try {
                    val selectedItemIndex = indexOf(item)
                    /*
                     *TODO
                     * this swap is not good enough - it causes upcoming sortBy to lag the screen
                     * item has to go directly to it's new sorted position, I need to calculate it right here, to spare sorting
                     */
                    Collections.swap(this, selectedItemIndex, 0)
//                    notifyItemChanged(selectedItemIndex)
                    notifyItemMoved(selectedItemIndex, 0)
                    // TODO sort items besides first one
//                    subList(1, lastIndex).sortBy { it.isoCode } // this won't work
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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Currency) {
            with(view) {
                isoCodeTextView.text = item.isoCode
                fullNameTextView.text = item.fullName

                // todo find out how to save first item from getting new rate values
                // maybe they need new values, but can't show them when first
                /*if (item.isoCode != currentTopCurrencyIsoCode) {

                }*/

                GlideApp.with(view)
                    .load(item.flagImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(flagImageView)

                rateEditText.apply {
                    setText(item.rateBasedOnEuro.toString())
                    setOnTouchListener { _, _ ->
                        view.performClick()
                        true
                    }
                }

                setOnClickListener {
                    onItemClicked(item, rateEditText)
                }
            }
        }

        private fun onItemClicked(item: Currency, rateEditText: EditText) {
            currentTopCurrencyIsoCode = item.isoCode
            moveItemToTopAndNotify(item)
            rateEditText.run {
                requestFocus()
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.showSoftInput(rateEditText, InputMethodManager.SHOW_FORCED)
            }
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}