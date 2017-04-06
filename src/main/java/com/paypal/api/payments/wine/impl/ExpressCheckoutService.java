package com.paypal.api.payments.wine.impl;

import static com.paypal.api.payments.util.SampleConstants.MODE;
import static com.paypal.api.payments.util.SampleConstants.clientID;
import static com.paypal.api.payments.util.SampleConstants.clientSecret;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.api.payments.util.SampleConstants;
import com.paypal.api.payments.util.url.UrlOperationSystemUtile;
import com.paypal.api.payments.wine.api.IExpressCheckout;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@WebService(endpointInterface = "com.paypal.api.payments.wine.api.IExpressCheckout",
targetNamespace = "http://paypal.wine.afcepf.fr")
public class ExpressCheckoutService implements IExpressCheckout {

	private static Logger logger = Logger.getLogger(ExpressCheckoutService.class);

	private static final String REDIRECT_PAYPAL = "redirectPaypal";
	protected static Map<String, String> mapPayment = new HashMap<>();
	private Payment payment;
	@Override
	public Payment expressCheckoutService(Map<String,String> detailsPayment) 
			throws PayPalRESTException {
		if (detailsPayment != null) {
			payment = createPayment(detailsPayment);
		} else {
			throw new PayPalRESTException("payment can not be done | "
					+ "empty details payment ");
		}
		UrlOperationSystemUtile openUrlService = new UrlOperationSystemUtile();
		if (mapPayment.get(REDIRECT_PAYPAL) != null && 
				!mapPayment.get(REDIRECT_PAYPAL).equalsIgnoreCase("")) {
			try {
				openUrlService.openUrl(mapPayment.get(REDIRECT_PAYPAL));
				//			createHttpConnection(String url)
			} catch (IOException ioe) {
				logger.error(ioe);
			}
		} else {
			logger.error("no url has been defined...");
		}
		if(payment.getId() == null) {
			throw new PayPalRESTException("payment has not been correctly created");
		}
		return payment;
	}

	@Override
	public Payment retrievePaymentObject(String paymentID) throws PayPalRESTException {
		APIContext apiContext = new APIContext(clientID, clientSecret, MODE);
		payment = Payment.get(apiContext,paymentID);
		logger.info("Payment retrieved ID = " + payment.getId()
		+ ", status = " + payment.getState());
		return payment;
	}



	private Payment createPayment(Map<String,String> detailsPayment) throws
	PayPalRESTException {
		Map<String, String> props = SampleConstants.props;
		Payment createdPayment = null;
		APIContext apiContext = new APIContext(clientID, clientSecret, MODE);
		// create a new Payment
		// ###Details
		// Let's you specify details of a payment amount.
		Details details = new Details();
		details.setShipping(detailsPayment.get("shipping"));
		if (detailsPayment.get("total") != null) {
			details.setSubtotal(String.valueOf(Double.parseDouble(detailsPayment.get("total")) 
					- Double.parseDouble(detailsPayment.get("shipping"))));
			details.setTax("0");
		} else {
			throw new PayPalRESTException("empty order has been received..");
		}

		// ###Amount
		// Let's you specify a payment amount.
		Amount amount = new Amount();
		amount.setCurrency("EUR");
		// Total must be equal to sum of shipping, tax and subtotal.
		amount.setTotal(detailsPayment.get("total"));
		amount.setDetails(details);

		// ###Transaction
		// A transaction defines the contract of a
		// payment - what is the payment for and who
		// is fulfilling it. Transaction is created with
		// a `Payee` and `Amount` types
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction
		.setDescription("This is the payment transaction description using paypal.");

		// ### Items
		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<>();
		if (detailsPayment.size() >= 1) {
			for (int i = 1 ; i < detailsPayment.size()-1; i++) {
				String itemIncoming = detailsPayment.get(String.valueOf(i));
				String[] attributsItem = parserIncomingOrder(itemIncoming);
				Item item = new Item();
				item.setName(attributsItem[0]).setQuantity(attributsItem[2])
				.setCurrency("EUR").setPrice(attributsItem[1]);
				items.add(item);
				itemList.setItems(items);
			}
		} else {
			throw new PayPalRESTException("no items have been provided for checkout...");
		}
		transaction.setItemList(itemList);


		// The Payment creation API requires a list of
		// Transaction; add the created `Transaction`
		// to a List
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		// ###Payer
		// A resource representing a Payer that funds a payment
		// Payment Method
		// as 'paypal'
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		// ###Payment
		// A Payment Resource; create one using
		// the above types and intent as 'sale'
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		// ###Redirect URLs
		RedirectUrls redirectUrls = new RedirectUrls();
		String guid = UUID.randomUUID().toString().replaceAll("-", "");
		redirectUrls.setCancelUrl(SampleConstants.URL_WINE_APP);
		redirectUrls.setReturnUrl(SampleConstants.URL_WINE_APP);
		payment.setRedirectUrls(redirectUrls);

		// Create a payment by posting to the APIService
		// using a valid AccessToken
		// The return object contains the status;
		createdPayment = setPaymentPaypal(guid, apiContext, payment);
		return createdPayment;
	}

	private String[] parserIncomingOrder(String incomingItem) {
		return incomingItem.split(
				SampleConstants.INCOMIG_ITEM_REGX);
	}

	/**
	 * Set payment using Paypal + link creation redirection
	 * @param createdPayment
	 * @param guid
	 * @param apiContext
	 * @param payment
	 * @return
	 */
	private Payment setPaymentPaypal(String guid, APIContext apiContext, Payment payment) {
		Payment createdPayment = null;
		try {
			createdPayment = payment.create(apiContext);
			logger.info("Created payment with id = "
					+ createdPayment.getId() + " and status = "
					+ createdPayment.getState());
			// ###Payment Approval Url
			Iterator<Links> links = createdPayment.getLinks().iterator();
			String redirectPaypal = "";
			while (links.hasNext()) {
				Links link = links.next();
				if (link.getRel().equalsIgnoreCase("approval_url")) {
					redirectPaypal = link.getHref();
					logger.info("URL redirect paypal: " + redirectPaypal);
				}
			}
			mapPayment.put(guid, createdPayment.getId());
			mapPayment.put(REDIRECT_PAYPAL, redirectPaypal);
		} catch (PayPalRESTException e) {
			logger.error(e);
		}
		return createdPayment;
	}

	/**
	 * Another method to create an http connection
	 * to access paypal rest endpoint
	 * @param url
	 */
	private void createHttpConnection(String url) throws MalformedURLException {
		try {
			URL myURL = new URL(url);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.connect();
		} 
		catch (IOException e) { 
			logger.error(e);
		} 
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
