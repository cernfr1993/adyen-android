/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 9/4/2019.
 */

package com.adyen.checkout.dropin.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import com.adyen.checkout.base.analytics.AnalyticEvent
import com.adyen.checkout.base.analytics.AnalyticsDispatcher
import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.core.log.LogUtil
import com.adyen.checkout.core.log.Logger
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.R
import com.adyen.checkout.dropin.ui.base.DropInBottomSheetDialogFragment
import com.adyen.checkout.dropin.ui.component.ComponentDialogFragment
import com.adyen.checkout.dropin.ui.paymentmethods.PaymentMethodListDialogFragment

/**
 * Activity that presents the available PaymentMethods to the Shopper.
 */
class DropInActivity : AppCompatActivity(), DropInBottomSheetDialogFragment.Protocol {

    companion object {
        private val TAG = LogUtil.getTag()

        private const val PAYMENT_METHOD_FRAGMENT_TAG = "PAYMENT_METHODS_DIALOG_FRAGMENT"
        private const val COMPONENT_FRAGMENT_TAG = "COMPONENT_DIALOG_FRAGMENT"

        private const val PAYMENT_METHODS_RESPONSE_KEY = "payment_methods_response"

        fun createIntent(context: Context, paymentMethodsApiResponse: PaymentMethodsApiResponse): Intent {
            val intent = Intent(context, DropInActivity::class.java)
            intent.putExtra(PAYMENT_METHODS_RESPONSE_KEY, paymentMethodsApiResponse)
            return intent
        }
    }

    private lateinit var mDropInViewModel: DropInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate - $savedInstanceState")
        setContentView(R.layout.activity_payment_method_picker)
        overridePendingTransition(0, 0)

        mDropInViewModel = ViewModelProviders.of(this).get(DropInViewModel::class.java)

        mDropInViewModel.paymentMethodsApiResponse =
                if (savedInstanceState != null && savedInstanceState.containsKey(PAYMENT_METHODS_RESPONSE_KEY)) {
                    savedInstanceState.getParcelable(PAYMENT_METHODS_RESPONSE_KEY)!!
                } else {
                    intent.getParcelableExtra(PAYMENT_METHODS_RESPONSE_KEY)
                }

        PaymentMethodListDialogFragment.newInstance(false).show(supportFragmentManager, PAYMENT_METHOD_FRAGMENT_TAG)

        sendAnalyticsEvent()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Logger.d(TAG, "onSaveInstanceState")
        // TODO save all possible DropIn state
        outState?.putParcelable(PAYMENT_METHODS_RESPONSE_KEY, mDropInViewModel.paymentMethodsApiResponse)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy")
    }

    override fun showComponentDialog(paymentMethod: PaymentMethod, wasInExpandMode: Boolean) {
        Logger.d(TAG, "showComponentDialog")
        ComponentDialogFragment.newInstance(paymentMethod, wasInExpandMode).show(supportFragmentManager, COMPONENT_FRAGMENT_TAG)
        hideFragmentDialog(PAYMENT_METHOD_FRAGMENT_TAG)
    }

    override fun showPaymentMethodsDialog(showInExpandStatus: Boolean) {
        Logger.d(TAG, "showPaymentMethodsDialog")
        PaymentMethodListDialogFragment.newInstance(showInExpandStatus).show(supportFragmentManager, PAYMENT_METHOD_FRAGMENT_TAG)
        hideFragmentDialog(COMPONENT_FRAGMENT_TAG)
    }

    override fun terminateDropIn() {
        Logger.d(TAG, "terminateDropIn")
        finish()
        overridePendingTransition(0, R.anim.fade_out)
    }

    private fun sendAnalyticsEvent() {
        Logger.d(TAG, "sendAnalyticsEvent")
        val analyticEvent = AnalyticEvent.create(this, AnalyticEvent.Flavor.DROPIN,
                "dropin", DropIn.INSTANCE.configuration.shopperLocale)
        AnalyticsDispatcher.dispatchEvent(this, DropIn.INSTANCE.configuration.environment, analyticEvent)
    }

    private fun hideFragmentDialog(tag: String) {
        getFragmentByTag(tag).let {
            it.dismiss()
        }
    }

    private fun getFragmentByTag(tag: String): DialogFragment {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        return fragment as DialogFragment
    }
}