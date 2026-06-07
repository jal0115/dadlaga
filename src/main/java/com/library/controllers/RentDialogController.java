package com.library.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.library.database.DBConnection;
import com.library.models.Book;
import com.library.models.Member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class RentDialogController {

    @FXML private ComboBox<Member> cmbMember;
    @FXML private ComboBox<Book> cmbBook;
    @FXML private DatePicker dpDueDate;
    @FXML private Button btnCancel;

    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        loadMembers();
        loadAvailableBooks();
        dpDueDate.setValue(LocalDate.now().plusDays(14)); // Анхны утгаар 14 хоног тохируулна
    }

    private void loadMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();
        String query = "SELECT * FROM member";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                members.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("member_name"),
                    rs.getString("surname"),
                    rs.getString("phone"),
                    rs.getString("email")
                ));
            }
            cmbMember.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        // Зөвхөн үлдэгдэл нь 0-ээс их номнуудыг уншина
        String query = "SELECT * FROM book WHERE available_qty > 0";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("available_qty"),
                    rs.getInt("quantity")
                ));
            }
            cmbBook.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSaveClick(ActionEvent event) {
        Member selectedMember = cmbMember.getSelectionModel().getSelectedItem();
        Book selectedBook = cmbBook.getSelectionModel().getSelectedItem();
        LocalDate dueDate = dpDueDate.getValue();

        if (selectedMember == null || selectedBook == null || dueDate == null) {
            showAlert(Alert.AlertType.WARNING, "Бүх талбарыг бөглөнө үү!");
            return;
        }

        if (dueDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Буцаах огноо өнөөдрөөс өмнө байж болохгүй!");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Түрээс + Үлдэгдэл хасах гүйлгээ (Transaction)

            // 1. Номын үлдэгдлийг дахин баталгаажуулж 1-ээр хасах
            String updateBookQuery = "UPDATE book SET available_qty = available_qty - 1 WHERE book_id = ? AND available_qty > 0";
            try (PreparedStatement psBook = conn.prepareStatement(updateBookQuery)) {
                psBook.setInt(1, selectedBook.getId());
                int affectedRows = psBook.executeUpdate();
                if (affectedRows == 0) {
                    showAlert(Alert.AlertType.WARNING, "Энэ ном дууссан байна!");
                    conn.rollback();
                    return;
                }
            }

            // 2. borrow_records хүснэгт рүү шинэ мөр INSERT хийх
            String insertQuery = "INSERT INTO borrow_records (bookid, memberid, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psRent = conn.prepareStatement(insertQuery)) {
                psRent.setInt(1, selectedBook.getId());
                psRent.setInt(2, selectedMember.getId());
                psRent.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                psRent.setDate(4, java.sql.Date.valueOf(dueDate));
                psRent.setString(5, "түрээсэлсэн");
                psRent.executeUpdate();
            }

            conn.commit();
            saveClicked = true;
            showAlert(Alert.AlertType.INFORMATION, "Түрээс амжилттай бүртгэгдлээ!");
            closeStage();

        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            showAlert(Alert.AlertType.ERROR, "Алдаа гарлаа: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    @FXML
    void onCancelClick(ActionEvent event) {
        closeStage();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
