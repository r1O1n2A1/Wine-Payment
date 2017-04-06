package com.paypal.api.payments.wine.api;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

/**
 * 
 * @author ronan
 *
 */
@WebService(targetNamespace="http://paypal.wine.afcepf.fr")
public interface IExpressCheckout {
	@WebMethod(operationName = "checkoutPaypal")
	@WebResult(name = "checkoutDone")
	Payment expressCheckoutService(@WebParam(name="detailsPayment")
		Map<String,String> detailsPayment)  throws PayPalRESTException;
	@WebMethod(operationName = "retrievePayment")
	@WebResult(name = "paymentDone")
	Payment retrievePaymentObject(@WebParam(name = "paymentID")
		String paymentID)  throws PayPalRESTException;
}
