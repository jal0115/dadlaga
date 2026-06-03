package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PleaseProvideControllerClassName {

    @FXML
    private TextField txtDisplay;

    private double firstNumber = 0;
    private String operator = "";
    private boolean newInput = true;

    @FXML
    public void initialize() {
        txtDisplay.setText("0");
    }

    @FXML
    void onNumberClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String value = btn.getText();
        if (newInput || txtDisplay.getText().equals("0")) {
            if (value.equals(".")) {
                txtDisplay.setText("0.");
            } else {
                txtDisplay.setText(value);
            }
            newInput = false;
        } else {
            txtDisplay.setText(txtDisplay.getText() + value);
        }
    }

    @FXML
    void onOperatorClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        firstNumber = Double.parseDouble(txtDisplay.getText());
        operator = btn.getText();
        newInput = true;
    }

    @FXML
    void onEqualClick(ActionEvent event) {
        double secondNumber = Double.parseDouble(txtDisplay.getText());
        double result = 0;
        switch (operator) {
            case "+": result = firstNumber + secondNumber; break;
            case "-": result = firstNumber - secondNumber; break;
            case "*": result = firstNumber * secondNumber; break;
            case "/":
                if (secondNumber != 0) {
                    result = firstNumber / secondNumber;
                } else {
                    txtDisplay.setText("Aldaa");
                    newInput = true;
                    return;
                }
                break;
            case "X²": result = firstNumber * firstNumber; break;
        }
        txtDisplay.setText(String.valueOf(result));
        newInput = true;
    }

    @FXML
    void onClearClick(ActionEvent event) {
        txtDisplay.setText("0");
        firstNumber = 0;
        operator = "";
        newInput = true;
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        String text = txtDisplay.getText();
        if (text.length() > 1) {
            txtDisplay.setText(text.substring(0, text.length() - 1));
        } else {
            txtDisplay.setText("0");
        }
    }

    @FXML
    void onPMClick(ActionEvent event) {
        String currentText = txtDisplay.getText();
        if (currentText.isEmpty() || currentText.equals("0") || currentText.equals("Aldaa")) {
            return;
        }
        double value = Double.parseDouble(currentText);
        value = value * -1;
        if (value % 1 == 0) {
            txtDisplay.setText(String.valueOf((long) value));
        } else {
            txtDisplay.setText(String.valueOf(value));
        }
    }

    @FXML
    void onSqrtClick(ActionEvent event) {
        double value = Double.parseDouble(txtDisplay.getText());
        if (value < 0) {
            txtDisplay.setText("Aldaa");
            return;
        }
        double result = Math.sqrt(value);
        if (result % 1 == 0) {
            txtDisplay.setText(String.valueOf((long) result));
        } else {
            txtDisplay.setText(String.valueOf(result));
        }
        newInput = true;
    }

    @FXML
    void onInverseClick(ActionEvent event) {
        double value = Double.parseDouble(txtDisplay.getText());
        if (value == 0) {
            txtDisplay.setText("Aldaa");
            return;
        }
        double result = 1 / value;
        if (result % 1 == 0) {
            txtDisplay.setText(String.valueOf((long) result));
        } else {
            txtDisplay.setText(String.valueOf(result));
        }
        newInput = true;
    }
}
