package com.dicap.views;

import com.dicap.model.Photographer;
import com.dicap.views.image.ImageView;
import com.dicap.views.photographe.PhotographeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create(), event -> logout());

    public MainLayout() {

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void logout() {
        SecurityContextHolder.clearContext();
        VaadinServletService.getCurrentServletRequest().getSession().invalidate();
        getUI().ifPresent(ui -> ui.getPage().setLocation("login"));
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        HorizontalLayout h= new HorizontalLayout();
        h.setAlignItems(FlexComponent.Alignment.END);
        //h.add(logoutButton);
        h.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle,h);
    }

    private void addDrawerContent() {

        Span appName = new Span("tpJavaEE");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Image ", ImageView.class, LineAwesomeIcon.TH_LIST_SOLID.create()));
        nav.addItem(new SideNavItem("Photographer ", PhotographeView.class, LineAwesomeIcon.USER_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        var user = VaadinSession.getCurrent().getAttribute(Photographer.class);
        Footer layout = new Footer(
                logoutButton,
                new H1(VaadinIcon.USER.create())
        );

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
