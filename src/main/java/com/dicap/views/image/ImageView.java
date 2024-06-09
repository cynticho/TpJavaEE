package com.dicap.views.image;

import com.dicap.model.ImageEntity;
import com.dicap.service.ImageService;
import com.dicap.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@PermitAll
@PageTitle("Image ")
@Route(value = "image", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class ImageView extends Main implements HasComponents, HasStyle {

    private final ImageService imageService;
    private OrderedList imageContainer;
    private Dialog modal;

    //private Dialog lightboxDialog;
    private Image lightboxImage;
    private int currentIndex;
    private List<ImageEntity> images;

    private byte[] tempImageContent;
    private String tempImageName;
    private Image previewImage;

    public ImageView(ImageService imageService) {
        this.imageService = imageService;
        this.images = imageService.findAll();
        constructUI();
        createLightbox();

        for (ImageEntity img : images) {
            Div content = new Div();
            Button delete = new Button(VaadinIcon.TRASH.create());
            Button update = new Button(VaadinIcon.EDIT.create());
            Button like = new Button(VaadinIcon.THUMBS_UP_O.create());
            like.addClickListener(buttonClickEvent -> {
                imageService.like(img);
                updateImageList();
                Notification.show(img.getLikes() + " Likes for this Image",5000, Notification.Position.BOTTOM_CENTER);
            });
            HorizontalLayout edit = new HorizontalLayout(delete, update, like);
            update.addClickListener(buttonClickEvent -> openModal(img));
            delete.addClickListener(buttonClickEvent -> openDelete(img));
            ImageViewCard card=new ImageViewCard(img);
            content.add(card, edit);
            card.addClickListener(event -> openLightbox(images.indexOf(img)));

            imageContainer.add(content);
        }
    }

    private void openDelete(ImageEntity imageEntity) {
        ConfirmDialog dialog = new ConfirmDialog();
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        dialog.setHeader("Delete this image ?");

        dialog.add(closeButton, new Paragraph("\"Are you sure you want to permanently delete this image ?\""));

        dialog.setCancelable(true);
        dialog.setCancelText("No, Cancel");
        dialog.addCancelListener(cancelEvent -> Notification.show("Cancelled, there is no deleted image", 5000, Notification.Position.BOTTOM_CENTER));
        dialog.setConfirmText("Yes, Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(confirmEvent -> {
            imageService.deleteById(imageEntity.getId());
            Notification.show("Image Deleted successfully !!!", 5000, Notification.Position.BOTTOM_CENTER);
            updateImageList();
        });
        dialog.open();
    }

    private void constructUI() {
        Button addBtn = new Button("New", VaadinIcon.PLUS.create());
        addBtn.addClickListener(e -> {
            openModal(new ImageEntity());
        });

        addClassNames("image-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Beautiful photos");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Royalty free photos and pictures, courtesy of Unsplash");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(addBtn, header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        container.add(headerContainer, sortBy);

        add(container, imageContainer, new Paragraph("made by Dicaprio"));

    }

    private void createLightbox() {
        modal = new Dialog();
        modal.setModal(true);
        modal.setWidth("800px");
        modal.setHeight("80%");
        lightboxImage = new Image();
        lightboxImage.setWidth("700px");
        lightboxImage.setHeight("350px");
        lightboxImage.addClassName("lightbox-content");
        lightboxImage.getStyle().set("object-fit", "contain");

        Button prevButton = new Button("Previous",VaadinIcon.LEVEL_LEFT_BOLD.create(), event -> showPreviousImage());
        Button nextButton = new Button("Next",VaadinIcon.LEVEL_RIGHT_BOLD.create(), event -> showNextImage());
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE), event -> modal.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(prevButton, nextButton, closeButton);

        Div img= new Div(lightboxImage);

        VerticalLayout dialogLayout = new VerticalLayout(img, buttonLayout);

        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        modal.add(dialogLayout);
    }

    private void openLightbox(int index) {
        currentIndex = index;
        lightboxImage.setSrc(new StreamResource(images.get(index).getTitle(), () -> new ByteArrayInputStream(images.get(index).getUrl())));
        modal.open();
    }

    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;

        }else currentIndex=images.size()-1;
        lightboxImage.setSrc(new StreamResource(images.get(currentIndex).getTitle(), () -> new ByteArrayInputStream(images.get(currentIndex).getUrl())));
    }

    private void showNextImage() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;

        }else currentIndex=0;
        lightboxImage.setSrc(new StreamResource(images.get(currentIndex).getTitle(), () -> new ByteArrayInputStream(images.get(currentIndex).getUrl())));
    }

    private void openModal(ImageEntity imageEntity) {
        modal = new Dialog();
        modal.setModal(false);
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> modal.close());
        modal.getHeader().add(closeButton);
        VerticalLayout layoutComposite = new VerticalLayout();

        TextField title = new TextField("Title");
        title.setClearButtonVisible(true);
        title.setRequired(true);
        title.setWidth("100%");
        title.setValue(imageEntity.getTitle() != null ? imageEntity.getTitle() : "");

        TextArea description = new TextArea();
        description.setClearButtonVisible(true);
        description.setRequired(true);
        description.setWidthFull();
        description.setHeight("100px");
        description.setLabel("Description");
        description.setPlaceholder("Enter the description for this image");
        description.setValue(imageEntity.getDescription() != null ? imageEntity.getDescription() : "");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        // If the image entity already has an image, display it
        if (imageEntity.getUrl() != null) {
            tempImageContent = imageEntity.getUrl(); // Set the image content to the existing image
            tempImageName = imageEntity.getTitle();  // Set the image name to the existing image title

            // Display the existing image in the previewImage component
            previewImage = new Image(new StreamResource(tempImageName, () -> new ByteArrayInputStream(tempImageContent)), "Uploaded image");
        } else {
            previewImage = new Image();
        }

        previewImage.setHeight("150px");
        previewImage.setWidth("200px");

        Button saveButton = new Button("Save", VaadinIcon.UPLOAD.create(), e -> {
            if (tempImageContent != null && tempImageName != null) {
                imageEntity.setTitle(title.getValue());
                imageEntity.setUrl(tempImageContent);
                imageEntity.setDescription(description.getValue());
                Long id = 2L;
                imageEntity.setPhototographer_id(id);
                imageEntity.setLikes(0L);
                imageService.save(imageEntity);
                updateImageList();
                Notification.show("Image saved successfully", 5000, Notification.Position.BOTTOM_CENTER);

                tempImageContent = null;
                tempImageName = null;
                previewImage.setSrc(""); // Clear preview
                modal.close();
            } else {
                Notification.show("No image to save", 5000, Notification.Position.BOTTOM_CENTER);
            }
        });

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
                Notification.show("Image ready to be saved. Click 'Save' to store it.");
                saveButton.setEnabled(true);
            } catch (IOException e) {
                Notification.show("Error uploading image");
            }
        });

        saveButton.setEnabled(tempImageContent != null);

        HorizontalLayout l1 = new HorizontalLayout(upload, previewImage);
        l1.setWidth("100%");
        layoutComposite.add(title, description, l1, saveButton);
        modal.setWidth("100%");
        modal.setHeightFull();
        modal.add(layoutComposite);
        modal.open();
    }

    private void updateImageList() {
        imageContainer.removeAll();
        images = imageService.findAll();
        for (ImageEntity img : images) {
            Div content = new Div();
            Button delete = new Button(VaadinIcon.TRASH.create());
            Button update = new Button(VaadinIcon.EDIT.create());
            Button like = new Button(VaadinIcon.THUMBS_UP_O.create());

            HorizontalLayout edit = new HorizontalLayout(delete, update, like);
            update.addClickListener(buttonClickEvent -> openModal(img));
            delete.addClickListener(buttonClickEvent -> openDelete(img));
            like.addClickListener(buttonClickEvent -> {
                imageService.like(img);
                updateImageList();
                Notification.show(img.getLikes() + " Likes for this Image");
            });
            content.add(new ImageViewCard(img), edit);
            content.addClickListener(event -> openLightbox(images.indexOf(img)));

            imageContainer.add(content);
        }
    }
}
