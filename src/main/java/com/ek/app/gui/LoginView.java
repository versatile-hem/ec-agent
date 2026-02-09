package com.ek.app.gui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Sign in | NextraERP")
@CssImport(value = "./themes/nextra/styles.css") // see section 3
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final EmailField email = new EmailField("Email address");
    private final PasswordField password = new PasswordField("Password");
    private final Checkbox rememberMe = new Checkbox("Keep me signed in");
    private final Anchor forgotPassword = new Anchor("#", "Forgot password?");
    private final Button signIn = new Button("Sign in");
    private final Anchor startTrial = new Anchor("#", "Start 14‑day free trial");

    /*
     * 
     * public LoginView() {
     * LoginForm login = new LoginForm();
     * login.setAction("login");
     * add(new H1("Login"), login);
     * setAlignItems(Alignment.CENTER);
     * setJustifyContentMode(JustifyContentMode.CENTER);
     * setSizeFull();
     * }
     */

    public LoginView()
    {
        addClassName("login-view");

        // ===== Left Pane (Form) =====
        final var left = new Div();
        left.addClassName("left-pane");

        // Logo row
        final var logoRow = new HorizontalLayout();
        logoRow.addClassName("logo-row");
        final var logo = new Div();
        logo.addClassName("app-logo"); // small rounded square
        final var brand = new Span("NextraERP");
        brand.addClassName("brand");
        logoRow.add(logo, brand);
        logoRow.setAlignItems(FlexComponent.Alignment.CENTER);

        // Headings
        final var heading = new H1("Welcome back");
        heading.addClassName("heading");
        final var sub = new Paragraph("Sign in to manage your business operations");
        sub.addClassName("subheading");

        // Form controls
        email.setPlaceholder("john@acmecorp.io");
        email.setClearButtonVisible(true);
        email.setWidthFull();
       // email.setAutocapitalize("off");
        //email.setAutocomplete("username");
        email.setRequiredIndicatorVisible(true);

        password.setPlaceholder("••••••••");
        password.setRevealButtonVisible(false);
        password.setWidthFull();
        //password.setAutocomplete("current-password");
        password.setRequiredIndicatorVisible(true);

        // Email & password validation
        email.setErrorMessage("Please enter a valid email");
        email.setPattern("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

        // Forgot link aligned right
        final var forgotRow = new HorizontalLayout();
        forgotRow.addClassName("forgot-row");
        forgotRow.setWidthFull();
        forgotRow.add(new Div(), forgotPassword);
        forgotPassword.getElement().setAttribute("aria-label", "Forgot password");

        // Remember me + (optional) SSO icon
        final var rememberRow = new HorizontalLayout();
        rememberRow.addClassName("remember-row");
        rememberRow.setWidthFull();
        rememberRow.setAlignItems(FlexComponent.Alignment.CENTER);
        rememberRow.add(rememberMe);

        // Sign in button
        signIn.addClassName("primary");
        signIn.setWidthFull();
        signIn.addClickListener(e -> attemptLogin());
        signIn.addClickShortcut(Key.ENTER);

        // Divider + trial
        final var divider = new Div();
        divider.addClassName("divider");
        final var smallRow = new HorizontalLayout();
        smallRow.addClassName("small-row");
        final var smallText = new Span("Don’t have an account?");
        startTrial.addClassName("trial-link");
        smallRow.add(smallText, startTrial);
        smallRow.setAlignItems(FlexComponent.Alignment.BASELINE);
        smallRow.setSpacing(true);

        // Group form
        final var form = new VerticalLayout(
                logoRow, heading, sub,
                email, password, forgotRow,
                rememberRow, signIn, divider, smallRow);
        form.addClassName("form-wrap");
        form.setSpacing(false);
        form.setPadding(false);
        left.add(form);

        // ===== Right Pane (Image + Testimonial) =====
        final var right = new Div();
        right.addClassName("right-pane");

        // Testimonial card
        final var card = new Div();
        card.addClassName("testimonial-card");

        final var stars = new HorizontalLayout(
                new Icon(VaadinIcon.STAR), new Icon(VaadinIcon.STAR),
                new Icon(VaadinIcon.STAR), new Icon(VaadinIcon.STAR),
                new Icon(VaadinIcon.STAR_HALF_LEFT_O));
        stars.addClassName("stars");
        stars.getChildren().forEach(c -> ((Icon) c).setColor("var(--lumo-warning-color)"));
        card.add(stars);

        final var quote = new Paragraph(
                "“NextraERP has completely transformed how my " +
                        "multi‑site operations run. The automations are tidy " +
                        "and set up took 15 min.”");
        quote.addClassName("quote");
        final var author = new Span("Jonathan Avery");
        author.addClassName("author");
        final var role = new Span("COO, Aleron Logistics");
        role.addClassName("role");

        final var meta = new VerticalLayout(quote, author, role);
        meta.addClassName("card-meta");
        meta.setSpacing(false);
        meta.setPadding(false);
        card.add(meta);

        right.add(card);

        // ===== Root Layout =====
        final var root = new Div(left, right);
        root.addClassName("login-root");

        add(root);

        // Focus first field
        UI.getCurrent().beforeClientResponse(this, ctx -> email.focus());
    }

    private void attemptLogin() {
        boolean valid = email.getValue() != null && email.getValue().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                && password.getValue() != null && !password.getValue().isBlank();

        if (!valid) {
            Notification.show("Please provide a valid email and password.", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        // TODO: Replace with real authentication call
        boolean authOk = "demo@nextra.io".equalsIgnoreCase(email.getValue())
                && "demo123".equals(password.getValue());

        if (authOk) {
            // Persist 'remember me' as needed (cookie/token)
            UI.getCurrent().navigate("dashboard");
        } else {
            Notification.show("Invalid credentials.", 3000, Notification.Position.TOP_CENTER);
        }
    }
}