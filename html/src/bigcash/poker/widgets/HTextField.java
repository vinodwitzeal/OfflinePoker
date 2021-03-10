package bigcash.poker.widgets;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class HTextField extends HorizontalPanel {
    public TextBox textBox;

    public HTextField(String icon,String placeholder) {
        super();
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        setStyleName("input-box");
        Image iconImage = new Image("img/" + icon + ".png");
        iconImage.setHeight("10px");
        textBox = new TextBox();
        textBox.setStyleName("input-field");
        textBox.getElement().setAttribute("placeholder",placeholder);
        textBox.setWidth("100%");
        add(iconImage);
        setCellVerticalAlignment(iconImage,HasVerticalAlignment.ALIGN_MIDDLE);
        setCellHorizontalAlignment(iconImage,HasHorizontalAlignment.ALIGN_CENTER);
        setCellWidth(iconImage,"5%");
        SimplePanel separator=new SimplePanel();
        separator.setStyleName("separator");
        add(separator);
        setCellVerticalAlignment(separator,HasVerticalAlignment.ALIGN_MIDDLE);
        add(textBox);
        setCellVerticalAlignment(textBox,HasVerticalAlignment.ALIGN_MIDDLE);
        setCellWidth(textBox,"95%");
    }

    public String getText() {
        return textBox.getText();
    }
}
