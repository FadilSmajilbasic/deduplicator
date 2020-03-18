package samt.smajilbasic.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.BeforeEnterHandler;

import org.springframework.http.HttpStatus;

import samt.smajilbasic.Resources;
import samt.smajilbasic.Validator;
import samt.smajilbasic.authentication.AccessControl;
import samt.smajilbasic.authentication.AccessControlFactory;
import samt.smajilbasic.communication.Client;

/**
 * The Login view that the user uses to authenticate to the deduplicator
 * service.
 */
@Route(value = "login")
@PageTitle(value = "Deduplicator - Login")
public class LoginView extends VerticalLayout {

    private static final long serialVersionUID = 4944489863331319773L;

    /**
     * The HTTP/HTTPS client uset for the authentication.
     */
    private Client client;
    /**
     * Flag used to switch between the advanced and basic view.
     */
    private boolean defaultView = true;
    /**
     * The text field for the host IP.
     */
    private TextField hostTextField;
    /**
     * The text field for the host port.
     */
    private NumberField portTextField;
    /**
     * The button that switches between the views.
     */
    private Button advancedViewButton;

    private FormLayout form;

    AccessControl accessControl;

    /**
     * Base constructor of the LoginView class. It creates the LoginView GUI with
     * all of it's elements.
     */
    public LoginView() {
        setSizeFull();
        accessControl = AccessControlFactory.getInstance().createAccessControl();
        hostTextField = new TextField("Host");
        portTextField = new NumberField("Port");
        TextField usernameTextField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");

        advancedViewButton = new Button("Advanced View", event -> {
            toggleView();
        });
        advancedViewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        portTextField.setStep(1);
        portTextField.setMin(1);
        portTextField.setMax(65535);
        portTextField.setValue(8443d);
        hostTextField.setValue("127.0.0.1");
        usernameTextField.setValue("admin");
        passwordField.setValue("administrator");

        Button button = new Button("Login", e -> tryLogin(hostTextField.getValue(), portTextField.getValue().intValue(),
                usernameTextField.getValue(), passwordField.getValue()));

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickShortcut(Key.ENTER);

        form = new FormLayout();
        passwordField.getStyle().set("margin-left","0px");
        form.setResponsiveSteps(new ResponsiveStep("10em", 1), new ResponsiveStep("20em", 2));
        form.add(usernameTextField, passwordField);
        Div container = new Div(form);
        container.setWidth("50%");
        setAlignItems(Alignment.CENTER);

        add(container, button, advancedViewButton);
        setMinWidth(Resources.SIZE_MOBILE_S);
    }

    /**
     * The toggleView method switches between the advanced and regular login view.
     * By switching to the advanced view it exposes the host and port fields.
     */
    private void toggleView() {
        if (!defaultView) {
            form.remove(hostTextField, portTextField);
        } else {
            form.add(hostTextField, portTextField);
        }
        advancedViewButton.setText(defaultView ? "Advanced View" : "Basic View");
        defaultView = !defaultView;
    }

    /**
     * The tyLogin method attempts to authenticate the {@link Client} to the
     * deduplicator service.
     * 
     * @param host the host IP to conenct to.
     * @param port the port to connect to.
     * @param user the username to try to authenticate with.
     * @param pass the user's password to try to authenticate with.
     */
    private void tryLogin(String host, int port, String user, String pass) {

        if (Validator.isValidIP(host)) {
            if (port > 0 && port <= 65535) {
                client = new Client(user, pass);
                HttpStatus resp = client.isAuthenticated(host, port);

                switch (resp) {
                    case OK:
                        accessControl.signedIn(user, client);
                        UI.getCurrent().navigate("");
                        break;
                    case UNAUTHORIZED:
                        Notification.show("Invalid credentials", 2000, Notification.Position.TOP_END)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        break;
                    case SERVICE_UNAVAILABLE:
                        Notification
                                .show("Server not reachable or an error occured", 2000, Notification.Position.TOP_END)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        break;
                    default:
                        Notification.show("Unknown error occured", 2000, Notification.Position.TOP_END)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        break;
                }
            } else {
                Notification.show("Invalid port set", 2000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Invalid IP set", 2000, Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}