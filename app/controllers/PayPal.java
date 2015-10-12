//package controllers;
//
//import com.paypal.api.payments.*;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.OAuthTokenCredential;
//import com.paypal.base.rest.PayPalRESTException;
//import play.Logger;
//import play.Play;
//import play.mvc.Controller;
//import play.mvc.Result;
//
//import java.util.*;
//
///**
// * Created by User on 10/12/2015.
// */
//
//public class PayPal extends Controller {
//    public Result payPal() {
//        try {
//            // Configuration
//            String clientid = Play.application().configuration().getString("clientid");
//            String secret = Play.application().configuration().getString("secret");
//
//            String token = new OAuthTokenCredential(clientid, secret).getAccessToken();
//
//            Map<String, String> config = new HashMap<>();
//            config.put("mode", "sandbox");
//
//            APIContext context = new APIContext(token);
//            context.setConfigurationMap(config);
//
//            // Process cart/payment information
//            double price = 1000;
//            String priceString = String.format("%1.2f", price);
//            String desc = "Bought: Bijela Voda\nTID: 01454972\nAmount: " + priceString;
//
//            // Configure payment
//            Amount amount = new Amount();
//            amount.setTotal(priceString);
//            amount.setCurrency("USD");
//
//            List<Transaction> transactionList = new ArrayList<>();
//            Transaction transaction = new Transaction();
//            transaction.setAmount(amount);
//            transaction.setDescription(desc);
//            transactionList.add(transaction);
//
//            Payer payer = new Payer();
//            payer.setPaymentMethod("paypal");
//
//            Payment payment = new Payment();
//            payment.setPayer(payer);
//            payment.setIntent("sale");
//            payment.setTransactions(transactionList);
//
//            RedirectUrls redirects = new RedirectUrls();
//            // Note: Dvije ruute ispod nisu napravljene!
//            redirects.setCancelUrl("http://localhost:9000/payment/fail");
//            redirects.setReturnUrl("http://localhost:9000/payment/approved");
//            payment.setRedirectUrls(redirects);
//
//            Payment madePayments = payment.create(context);
//            Iterator<Links> it = madePayments.getLinks().iterator();
//            while (it.hasNext()) {
//                Links link = it.next();
//                if (link.getRel().equals("approval_url")) {
//                    return redirect(link.getHref());
//                }
//            }
//        } catch (PayPalRESTException e) {
//            Logger.warn("PayPal Exception");
//            e.printStackTrace();
//        }
//
//        return redirect("/");
//    }
//
//
//}