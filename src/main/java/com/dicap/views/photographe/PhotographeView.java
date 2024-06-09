package com.dicap.views.photographe;

import com.dicap.model.Photographer;
import com.dicap.service.PhotographerService;
import com.dicap.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@PermitAll
@PageTitle("photographer's management")
@Route(value = "photographe",layout = MainLayout.class)
@RouteAlias(value = "photographer", layout = MainLayout.class)
public class PhotographeView extends Div {

    private final PhotographerService photographerService;
    private Grid<Photographer> grid;
    private Dialog modal;
    private Dialog modal1;
    List<Photographer> photographers ;
    TextField searchField;

    private byte[] tempImageContent;
    private String tempImageName;
    private Image previewImage;

    List<String> countries = Arrays.asList(
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda",
            "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain",
            "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso",
            "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic",
            "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic of the",
            "Congo, Republic of the", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
            "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador",
            "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland",
            "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada",
            "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary",
            "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
            "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North",
            "Korea, South", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho",
            "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi",
            "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius",
            "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco",
            "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand",
            "Nicaragua", "Niger", "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan",
            "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines",
            "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis",
            "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe",
            "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
            "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Sudan",
            "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan",
            "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago",
            "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
            "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City",
            "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"
    );

    @Autowired
    public PhotographeView(PhotographerService photographerService) {
        this.photographerService = photographerService;

        photographers = photographerService.getAll();
        searchField = new TextField();
        searchField.setClearButtonVisible(true);
        searchField.setWidth("100%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));



        grid = new Grid<>(Photographer.class, false);
        //grid.setColumns("nomComplet", "phone", "email");
        grid.addColumn(creatRenderer()).setHeader("Photographer")
                .setAutoWidth(true).setFlexGrow(0).setSortable(true);;
        grid.addColumn(Photographer::getPhone).setHeader("Phone")
                .setAutoWidth(true).setSortable(true);;
        grid.addColumn(Photographer::getCountry).setHeader("Country")
                .setAutoWidth(true).setSortable(true);;
        grid.addColumn(Photographer::getCity).setHeader("City")
                .setAutoWidth(true).setSortable(true);;
        grid.addComponentColumn(photographe -> {
            MenuBar menuBar = new MenuBar();
            MenuItem actions = menuBar.addItem(VaadinIcon.MENU.create());
            actions.getSubMenu().addItem(new Button("Show",VaadinIcon.LIST.create()), e -> {
                VaadinSession.getCurrent().setAttribute("photographer", photographe);
                UI.getCurrent().navigate("show");
            });
            actions.getSubMenu().addItem(new Button("Update",VaadinIcon.EDIT.create()), e -> openModal(photographe));
            actions.getSubMenu().addItem(new Button("Delete",VaadinIcon.TRASH.create()), e -> openDelete(photographe));
            return menuBar;
        }).setHeader("Actions").setAutoWidth(true);
        updateList();

        Button addPhotographerButton = new Button("New",new Icon("lumo", "plus"));
        addPhotographerButton.addClickListener(e -> openModal(new Photographer()));

        add(new VerticalLayout(addPhotographerButton,searchField), grid);
    }

    private void openShow(Photographer photographe) {
        modal1 = new Dialog();
        modal1.setModal(false);
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> modal1.close());
        modal1.getHeader().add(closeButton);
        modal1.open();
    }

    private void openDelete(Photographer photographe) {
        ConfirmDialog dialog = new ConfirmDialog();
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        dialog.setHeader("Delete this photographer ?");
        dialog.setText(
                "Are you sure you want to permanently delete this photographer ?");
        dialog.add(closeButton,new Paragraph("Are you sure you want to permanently delete this photographer ?"));
        dialog.setCancelable(true);
        dialog.setCancelText("No, Cancel");
        dialog.addCancelListener(cancelEvent -> Notification.show("Cancelled, there is no deleted photographer",5000, Notification.Position.BOTTOM_CENTER));
        dialog.setConfirmText("Yes, Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(confirmEvent -> {
            photographerService.delete(photographe);
            Notification.show("Photographer's Deleted successfully !!!",5000, Notification.Position.BOTTOM_CENTER);
            updateList();
        });
        dialog.open();
    }

    private static Renderer<Photographer> creatRenderer() {
        return LitRenderer.<Photographer> of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span> ${item.fullName} </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.email}" + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("pictureUrl", Photographer::getImageName)
                .withProperty("fullName", Photographer::getNomComplet)
                .withProperty("email", Photographer::getEmail);
    }

    private void openModal(Photographer photographe) {
        modal = new Dialog();
        modal.setModal(false);
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> modal.close());
        modal.getHeader().add(closeButton);
        VerticalLayout layoutComposite= new VerticalLayout();

        HorizontalLayout l1 = new HorizontalLayout();
        l1.setWidth("100%");

        TextField firstName = new TextField("Full Name");
        firstName.setClearButtonVisible(true);
        firstName.setRequired(true);
        firstName.setWidth("100%");
        firstName.setValue(photographe.getNomComplet() != null ? photographe.getNomComplet() : "");
        l1.add(firstName);

        TextField phone = new TextField("Phone");
        phone.setWidth("560px");
        phone.setRequired(true);
        phone.setClearButtonVisible(true);
        phone.setValue(photographe.getPhone() != null ? photographe.getPhone() : "");

        PasswordField password = new PasswordField("Password");
        password.setRequired(true);
        password.setClearButtonVisible(true);
        password.setWidth("560px");
        password.setValue(photographe.getPassword() != null ? photographe.getPassword() : "");

        HorizontalLayout l2 = new HorizontalLayout();
        l1.setWidth("100%");
        l2.add(phone,password);

        EmailField email = new EmailField("Email");
        email.setClearButtonVisible(true);
        email.setRequired(true);
        email.setWidth("100%");
        email.setValue(photographe.getEmail() != null ? photographe.getEmail() : "");

        HorizontalLayout l3 = new HorizontalLayout();
        l3.setWidth("100%");
        l3.add(email);

        NumberField hourPrice = new NumberField("Price Per Hour");
        hourPrice.setMin(0);
        hourPrice.setRequired(true);
        hourPrice.setClearButtonVisible(true);
        hourPrice.setWidth("370px");
        hourPrice.setStep(1);
        hourPrice.setValue(photographe.getHourPrice() != 0 ? photographe.getHourPrice() : hourPrice.getMin());

        TextField city = new TextField("City");
        city.setClearButtonVisible(true);
        city.setWidth("370px");
        city.setValue(photographe.getCity() != null ? photographe.getCity() : "");

        Select<String> country = new Select<>();
        country.setLabel("Country");
        country.setItems(countries);
        country.setWidth("370px");
        country.setValue(photographe.getCountry() != null ? photographe.getCountry() : "");

        HorizontalLayout l4 = new HorizontalLayout();
        l1.setWidth("100%");
        l4.add(hourPrice,city,country);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        if (photographe.getImagecontent() != null) {
            tempImageContent = photographe.getImagecontent(); // Set the image content to the existing image
            tempImageName = photographe.getImageName();  // Set the image name to the existing image title

            // Display the existing image in the previewImage component
            previewImage = new Image(new StreamResource(tempImageName, () -> new ByteArrayInputStream(tempImageContent)), "Uploaded image");
        } else {
            previewImage = new Image();
        }

        Button saveButton = new Button("Save",VaadinIcon.UPLOAD.create(), e -> {
            photographe.setNomComplet(firstName.getValue());
            photographe.setPhone(phone.getValue());
            photographe.setCountry(country.getValue());
            photographe.setEmail(email.getValue());
            photographe.setCity(city.getValue());
            photographe.setPassword(password.getValue());
            photographe.setHourPrice(hourPrice.getValue());

            if (tempImageContent != null && tempImageName != null) {

                photographe.setImageName(tempImageName);
                photographe.setImagecontent(tempImageContent);

                Notification.show("Image saved successfully");
                tempImageContent = null;
                tempImageName = null;
                previewImage.setSrc(""); // Clear preview

            } else {
                Notification.show("No image to save",4000, Notification.Position.MIDDLE);
            }
            photographerService.save(photographe);
            updateList();
            modal.close();
            Notification.show("Photographer saved",4000, Notification.Position.MIDDLE);
        });
        saveButton.setEnabled(tempImageContent != null);


        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bufferData = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(bufferData)) != -1) {
                    outputStream.write(bufferData, 0, bytesRead);
                }
                tempImageContent = outputStream.toByteArray();
                tempImageName = event.getFileName();
                previewImage.setSrc(new StreamResource(tempImageName, () -> new ByteArrayInputStream(tempImageContent)));
                Notification.show("Image ready to be saved. Click 'Save' to store it.",5000, Notification.Position.BOTTOM_CENTER);
                saveButton.setEnabled(true);
            } catch (IOException e) {
                Notification.show("Error uploading image",5000, Notification.Position.BOTTOM_CENTER);
            }
        });

        previewImage.setHeight("100px");
        previewImage.setWidth("100px");

        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");


        HorizontalLayout l5 = new HorizontalLayout(upload,previewImage);
        layoutComposite.add(l1,l2,l3,l4,l5);
        modal.add(layoutComposite,saveButton);
        modal.setHeightFull();
        modal.setWidthFull();
        modal.open();
    }

    private void updateList() {
        grid.setItems(photographerService.getAll());
    }
}
