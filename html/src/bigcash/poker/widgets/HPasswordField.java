package bigcash.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import bigcash.poker.dialogs.gwt.HBaseDialog;

public class HPasswordField extends HorizontalPanel {
    private boolean passwordVisible;
    private String showPasswordImage, hidePasswordImage;
    private Image eyeImage;
    public TextBox textBox;

    public HPasswordField(String placeholder) {
        super();
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        setStyleName("input-box");
        Image iconImage = new Image("img/password.png");
        iconImage.setHeight("10px");
        textBox = new TextBox();
        textBox.setStyleName("input-field");
        textBox.getElement().setAttribute("placeholder", placeholder);
        textBox.setWidth("100%");
        add(iconImage);
        setCellVerticalAlignment(iconImage, HasVerticalAlignment.ALIGN_MIDDLE);
        setCellHorizontalAlignment(iconImage, HasHorizontalAlignment.ALIGN_CENTER);
        setCellWidth(iconImage, "5%");
        SimplePanel separator = new SimplePanel();
        separator.setStyleName("separator");
        add(separator);
        setCellVerticalAlignment(separator, HasVerticalAlignment.ALIGN_MIDDLE);
        add(textBox);

        setCellVerticalAlignment(textBox, HasVerticalAlignment.ALIGN_MIDDLE);
        setCellWidth(textBox, "90%");


        textBox.getElement().setAttribute("type", "password");
        passwordVisible = false;
        hidePasswordImage = "img/password_visible.png";
        showPasswordImage = "img/password_hidden.png";
        eyeImage = new Image(showPasswordImage);
        eyeImage.setHeight("10px");

        eyeImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (passwordVisible) {
                    textBox.getElement().setAttribute("type", "password");
                    eyeImage.setUrl(showPasswordImage);
                    passwordVisible = false;
                } else {
                    textBox.getElement().setAttribute("type", "text");
                    eyeImage.setUrl(hidePasswordImage);
                    passwordVisible = true;
                }
            }
        });
        add(eyeImage);
        setCellVerticalAlignment(eyeImage, HasVerticalAlignment.ALIGN_MIDDLE);
        setCellWidth(eyeImage, "5%");
    }

    public String getText() {
        return textBox.getText();
    }

}
