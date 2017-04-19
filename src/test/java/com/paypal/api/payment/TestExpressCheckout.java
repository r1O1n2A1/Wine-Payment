package com.paypal.api.payment;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.wine.impl.ExpressCheckoutService;
import com.paypal.base.rest.PayPalRESTException;

public class TestExpressCheckout {
	private ExpressCheckoutService checkout = new ExpressCheckoutService();
	private static Logger logger = Logger.getLogger(TestExpressCheckout.class);
	private Map<String,String> detailsPayment = null;
	private Payment nominalPayment = null;
	private Payment expectedPayment = null;
	private static String IDPAYMENT = "PAY-6F871714YK1376919LD3XMVA";
	
	public TestExpressCheckout() {
		detailsPayment = new HashMap<>();
		detailsPayment.put("total", "22515.0");
		detailsPayment.put("shipping", "15.0");
		detailsPayment.put("1", "Romanee|11250|2");
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		nominalPayment = new Payment();
		nominalPayment.setIntent("sale");
		nominalPayment.setPayer(payer);
		expectedPayment = new Payment();
		expectedPayment.setIntent("sale");
		expectedPayment.setPayer(payer);
	}
	@Test
	public void testNominal() throws PayPalRESTException {
		Payment returnPayment = checkout.expressCheckoutService(detailsPayment);
		Assert.assertNotNull(returnPayment);
		Assert.assertEquals(returnPayment.getIntent(), expectedPayment.getIntent());
	}
	@Test
	public void testGetNominal() throws PayPalRESTException {
		Payment returnPayment = checkout.retrievePaymentObject(IDPAYMENT);
	}
}
