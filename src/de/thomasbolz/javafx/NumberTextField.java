package de.thomasbolz.javafx;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

/**
 * Textfield implementation that accepts formatted number and stores them in a
 * BigDecimal property The user input is formatted when the focus is lost or the
 * user hits RETURN.
 *
 * @author Thomas Bolz
 */
public class NumberTextField extends TextField {

    private final NumberFormat nf;
    private ObjectProperty<Double> number = new SimpleObjectProperty<>();

    public final Double getNumber() {
        return number.get();
    }

    public final void setNumber(Double value) {
        number.set(value);
    }

    public ObjectProperty<Double> numberProperty() {
        return number;
    }

    public NumberTextField() {
        this(0.0);
    }

    public NumberTextField(Double value) {
        this(value, NumberFormat.getInstance());
        initHandlers();
    }

    Double min, max;
    public NumberTextField(Double value, NumberFormat nf, Double min, Double max){
        this(value, nf);
        this.min = min;
        this.max = max;
    }

    public NumberTextField(Double value, NumberFormat nf) {
        super();
        this.nf = nf;
        initHandlers();
        setNumber(value);
    }

    private void initHandlers() {

        // try to parse when focus is lost or RETURN is hit
        setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                parseAndFormatInput();
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue.booleanValue()) {
                    parseAndFormatInput();
                }
            }
        });

        // Set text in field if BigDecimal property is changed from outside.
        numberProperty().addListener(new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> obserable, Double oldValue, Double newValue) {
                setText(nf.format(newValue));
            }
        });
    }

    /**
     * Tries to parse the user input to a number according to the provided
     * NumberFormat
     */
    private void parseAndFormatInput() {
        try {
            String input = getText();
            if (input == null || input.length() == 0) {
                return;
            }
            Number parsedNumber = nf.parse(input);
            Double newValue = new Double(parsedNumber.toString());
            if(min !=null && max !=null){
                setNumber(Math.min(Math.max(min, newValue), max));
            }else{
                setNumber(newValue);
            }
            selectAll();
        } catch (ParseException ex) {
            // If parsing fails keep old number
            setText(nf.format(number.get()));
        }
    }
}