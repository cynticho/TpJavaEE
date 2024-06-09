package com.dicap.views.photographe;

import com.dicap.model.Photographer;
import com.dicap.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;

@PermitAll
@PageTitle("Photographer's details")
@Route(value = "show",layout = MainLayout.class)
@RouteAlias(value = "detail", layout = MainLayout.class)
public class ShowView extends VerticalLayout {

    public ShowView() {
        Div content = new Div();

        Image img = new Image();
        Button backButton = new Button("Retour", VaadinIcon.LEVEL_LEFT.create(),e ->
                UI.getCurrent().getPage().getHistory().back()
        );
        add(backButton);
        Photographer photographer = (Photographer) VaadinSession.getCurrent().getAttribute("photographer");
        if (photographer != null) {
            if (photographer.getImagecontent() != null) {
                img = new Image(new StreamResource(photographer.getImageName(), () -> new ByteArrayInputStream(photographer.getImagecontent())), photographer.getImageName());
                img.setHeight("150px");
                img.setWidth("200px");
            }else {
                img.setAlt(" No profile Image found !!!");
            }

            content.add(
                    backButton,
                    new H1("Full Name : "+photographer.getNomComplet()),
                    new Div(img),
                    new Paragraph("Email : "+photographer.getEmail()),
                    new H3("Phone : "+photographer.getPhone()),
                    new Paragraph("Country : "+photographer.getCountry()),
                    new Paragraph(" City : "+photographer.getCity()),
                    new H2("Price Per Hour : "+photographer.getHourPrice())
            );
        } else {
            content.add("Photographer not found !!!");
        }

        add(content);
    }

}
