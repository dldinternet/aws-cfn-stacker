package com.dldinternet.aws.cfn.stacker.ui;

import com.dldinternet.aws.cfn.stacker.api.model.OrderConfirmation;
import com.dldinternet.aws.cfn.stacker.api.model.Pizza;
import com.dldinternet.aws.cfn.stacker.api.model.PizzaOrder;
import com.dldinternet.aws.cfn.stacker.api.model.Topping;
import com.dldinternet.aws.cfn.stacker.api.service.GreetingService;
import com.dldinternet.aws.cfn.stacker.api.service.GreetingServiceAsync;
import com.dldinternet.aws.cfn.stacker.api.service.PizzaService;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class UI implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Label errorLabel = new Label();

        TextButton gxtButton = new TextButton("Verify GXT Works");
        gxtButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                MessageBox messageBox = new MessageBox("GXT Works.");
                messageBox.show();
            }
        });

        TextButton flexButton = new TextButton("RestyGWT");

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("errorLabelContainer").add(errorLabel);
        RootPanel.get("gxtButtonContainer").add(gxtButton);
        RootPanel.get("flexButtonContainer").add(flexButton);

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        final Button closeButton = new Button("Close");
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        final HTML messageToServerLabel = new HTML();
        final HTML serverResponseLabel = new HTML();
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending to the server:</b>"));
        dialogVPanel.add(messageToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });

        // Create a handler for the sendButton and nameField
        class RPCHandler implements SelectEvent.SelectHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onSelect(SelectEvent event) {
                // makeRPCcall();
                placeOrder();
            }

            private void placeOrder() {
                PizzaService service = GWT.create(PizzaService.class);
                Resource resource = new Resource(GWT.getModuleBaseURL() + "pizza-service");
                ((RestServiceProxy) service).setResource(resource);

                messageToServerLabel.setHTML("listToppings");
                service.listToppings(new MethodCallback<List<Topping>>() {
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Error: " + exception);
                    }

                    @Override
                    public void onSuccess(Method method, List<Topping> response) {
                        dialogBox.setText("Remote Procedure Call. " + "Got back results: ");
                        serverResponseLabel.removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("");
                        for (Topping topping : response)
                             // RootPanel.get().add(new Label("got topping: " + topping.name + " $" + topping.price));
                             serverResponseLabel.setHTML(serverResponseLabel.getHTML() + "<br>\n" + "got topping: " + topping.name + " $" + topping.price);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }
                });

                PizzaOrder order = new PizzaOrder();
                order.delivery = true;
                order.delivery_address.add("3434 Pinerun Ave.");
                order.delivery_address.add("Tampa, FL  33734");

                Pizza pizza = new Pizza();
                pizza.crust = "thin";
                pizza.quantity = 1;
                pizza.size = 16;
                pizza.toppings.add("ham");
                pizza.toppings.add("pineapple");
                order.pizzas.add(pizza);

                pizza = new Pizza();
                pizza.crust = "thin";
                pizza.quantity = 1;
                pizza.size = 16;
                pizza.toppings.add("extra cheese");
                order.pizzas.add(pizza);

                messageToServerLabel.setHTML(messageToServerLabel.getHTML()+"<hr>\n<b>Order:</b><br>\n"+order.toString("<br>\n"));
                service.order(order, new MethodCallback<OrderConfirmation>() {
                    public void onSuccess(Method method, OrderConfirmation receipt) {
                        // RootPanel.get().add(new Label("got receipt: " + receipt));
                        dialogBox.setText("Remote Procedure Call. " + "Got back results: ");
                        serverResponseLabel.removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(serverResponseLabel.getHTML() + "<hr>\n");
                        serverResponseLabel.setHTML(serverResponseLabel.getHTML() + "<br>\n" + "got receipt: <br>\n" + receipt.toHTML());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onFailure(Method method, Throwable exception) {
//                        Window.alert("Error: " + exception);
                        dialogBox.setText("Remote Procedure Call - Failure: " + exception);
                        serverResponseLabel.addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(SERVER_ERROR);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        RPCHandler rpchandler = new RPCHandler();
        flexButton.addSelectHandler(rpchandler);

        Element body = RootPanel.getBodyElement();
        Element loading = RootPanel.get("loading").getElement();
        body.removeChild(loading);
//        DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));
    }
}
