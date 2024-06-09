package com.dicap.views.login_register;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("login")
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout  implements BeforeEnterObserver{

    LoginOverlay login = new LoginOverlay();;

    public LoginView() {

        login.setTitle(new H1(VaadinIcon.SIGN_IN.create()));
        login.setDescription( "Sign in to your account !!!");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        login.addForgotPasswordListener(forgotPasswordEvent -> {
           Notification.show("Can't restore your password now, please try later !!!", 5000, Notification.Position.BOTTOM_CENTER);
        });

        login.setAction("login");

        add(  login, new H6("made by Dicaprio"));
        login.setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}

