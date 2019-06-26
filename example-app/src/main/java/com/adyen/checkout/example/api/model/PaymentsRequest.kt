/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 25/3/2019.
 */

package com.adyen.checkout.example.api.model

import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.redirect.RedirectUtil

// TODO this class is just a mock for tests
@Suppress("MagicNumber")
data class PaymentsRequest(
    val paymentMethod: PaymentComponentData,
    val amount: Amount = Amount("EUR", 1337),
    val merchantAccount: String = "TestMerchantCheckout", // BuildConfig.MERCHANT_ACCOUNT,
    val reference: String = "no-reference",
    val returnUrl: String = RedirectUtil.REDIRECT_RESULT_SCHEME + "com.adyen.checkout.example",
    val channel: String = "android",
    val additionalData: AdditionalData = AdditionalData("true")
)