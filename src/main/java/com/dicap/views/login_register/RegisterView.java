package com.dicap.views.login_register;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@AnonymousAllowed
public class RegisterView extends Composite {
    @Override
    protected Component initContent() {
        return new VerticalLayout(
                new H1("Can't add photographer now, please try after !!!"),
                new H6("made by Dicaprio")
        );
    }
}
