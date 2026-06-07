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
import com.library.models.Rental;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    // ========== НОМЫН БҮРТГЭЛ ==========
    @FXML private Button btnAddBook;
    @FXML private TableView<Book> tableBook;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String>  colAuthor;
    @FXML private TableColumn<Book, String>  colTitle;
    @FXML private TableColumn<Book, String>  colIsbn;
    @FXML private TableColumn<Book, Integer> colQty;
    @FXML private TableColumn<Book, Integer> colAv;

    @FXML private TextField txtAuthor;
    @FXML private TextField txtBookName;
    @FXML private TextField txtBookResearch;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtQty;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private FilteredList<Book> filteredBookList;
    private int selectedBookId = -1;

    // ========== УНШИГЧИЙН БҮРТГЭЛ ==========
    @FXML private TableView<Member> tableMember;
    @FXML private TableColumn<Member, Integer> colMId;
    @FXML private TableColumn<Member, String>  colMemberName;
    @FXML private TableColumn<Member, String>  colMemberSurname;
    @FXML private TableColumn<Member, String>  colPhone;
    @FXML private TableColumn<Member, String>  colEmail;

    @FXML private TextField txtMemberName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMemberSearch;

    private ObservableList<Member> memberList = FXCollections.observableArrayList();
    private FilteredList<Member> filteredMemberList;
    private int selectedMemberId = -1;

    // ========== НОМЫН ТҮРЭЭС ==========
    @FXML private Button btnRentBook;
    @FXML private Button btnReturnBook;
    @FXML private RadioButton radioAll;
    @FXML private RadioButton radioOverdue;
    @FXML private ToggleGroup filterGroup;

    @FXML private TableView<Rental> tableRental;
    @FXML private TableColumn<Rental, Integer> colRentalId;
    @FXML private TableColumn<Rental, String>  colRentMemberName;
    @FXML private TableColumn<Rental, String>  colRentBookTitle;
    @FXML private TableColumn<Rental, String>  colRentDate;
    @FXML private TableColumn<Rental, String>  colReturnDate;
    @FXML private TableColumn<Rental, String>  colRentalStatus;

    private ObservableList<Rental> rentalList = FXCollections.observableArrayList();
    private FilteredList<Rental> filteredRentalList;

    // ========== INITIALIZE ==========
    @FXML
    public void initialize() {
        // Номын багана
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colAv.setCellValueFactory(new PropertyValueFactory<>("availableQty"));

        // Уншигчийн багана
        colMId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colMemberSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Түрээсийн багана
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRentMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colRentBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colRentDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colRentalStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBooksFromDatabase();
        loadMembersFromDatabase();
        loadRentalsFromDatabase();

        // FilteredList
        filteredBookList = new FilteredList<>(bookList, b -> true);
        tableBook.setItems(filteredBookList);

        filteredMemberList = new FilteredList<>(memberList, m -> true);
        tableMember.setItems(filteredMemberList);

        filteredRentalList = new FilteredList<>(rentalList, r -> true);
        tableRental.setItems(filteredRentalList);

        // Номын хайлт - шивэх бүрд хайна
        txtBookResearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.toLowerCase().trim();
            filteredBookList.setPredicate(book -> {
                if (keyword.isEmpty()) return true;
                return String.valueOf(book.getId()).contains(keyword)
                    || book.getTitle().toLowerCase().contains(keyword)
                    || book.getAuthor().toLowerCase().contains(keyword)
                    || book.getIsbn().toLowerCase().contains(keyword)
                    || String.valueOf(book.getQuantity()).contains(keyword)
                    || String.valueOf(book.getAvailableQty()).contains(keyword);
            });
        });

        // Уншигчийн хайлт - шивэх бүрд хайна
        txtMemberSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.toLowerCase().trim();
            filteredMemberList.setPredicate(member -> {
                if (keyword.isEmpty()) return true;
                return String.valueOf(member.getId()).contains(keyword)
                    || member.getMemberName().toLowerCase().contains(keyword)
                    || member.getSurname().toLowerCase().contains(keyword)
                    || member.getPhone().toLowerCase().contains(keyword)
                    || member.getEmail().toLowerCase().contains(keyword);
            });
        });

        // Радио товчлуур
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (filterGroup.getSelectedToggle() == radioOverdue) {
                filteredRentalList.setPredicate(rental -> {
                    if ("буцаасан".equalsIgnoreCase(rental.getStatus())) return false;
                    try {
                        LocalDate dueDate = LocalDate.parse(rental.getDueDate());
                        return dueDate.isBefore(LocalDate.now());
                    } catch (Exception e) {
                        return false;
                    }
                });
            } else {
                filteredRentalList.setPredicate(r -> true);
            }
        });

        // Ном сонгоход талбарт мэдээлэл орно
        tableBook.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBookId = newVal.getId();
                txtBookName.setText(newVal.getTitle());
                txtAuthor.setText(newVal.getAuthor());
                txtIsbn.setText(newVal.getIsbn());
                txtQty.setText(String.valueOf(newVal.getQuantity()));
            }
        });

        // Уншигч сонгоход талбарт мэдээлэл орно
        tableMember.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedMemberId = newVal.getId();
                txtMemberName.setText(newVal.getMemberName());
                txtSurname.setText(newVal.getSurname());
                txtPhone.setText(newVal.getPhone());
                txtEmail.setText(newVal.getEmail());
            }
        });
    }

    // ========== НОМ ==========
    private void loadBooksFromDatabase() {
        bookList.clear();
        String query = "SELECT * FROM book";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                bookList.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("available_qty"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddBookClick(ActionEvent event) {
        String title  = txtBookName.getText();
        String author = txtAuthor.getText();
        String isbn   = txtIsbn.getText();
        String qty    = txtQty.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || qty.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Бүх талбарыг бөглөнө үү!");
            return;
        }

        String query = "INSERT INTO book (title, author, isbn, quantity, available_qty) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, isbn);
            ps.setInt(4, Integer.parseInt(qty));
            ps.setInt(5, Integer.parseInt(qty));
            ps.executeUpdate();
            loadBooksFromDatabase();
            clearBookFields();
            showAlert(Alert.AlertType.INFORMATION, "Ном амжилттай нэмэгдлээ!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ном нэмэхэд алдаа гарлаа!");
            e.printStackTrace();
        }
    }

    @FXML
    void onZasahButton(ActionEvent event) {
        if (selectedBookId == -1) {
            showAlert(Alert.AlertType.WARNING, "Засах номоо хүснэгтээс сонгоно уу!");
            return;
        }

        String title  = txtBookName.getText();
        String author = txtAuthor.getText();
        String isbn   = txtIsbn.getText();
        String qty    = txtQty.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || qty.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Бүх талбарыг бөглөнө үү!");
            return;
        }

        // available_qty = available_qty + (шинэ quantity - хуучин quantity)
        // Жишээ: хуучин=5, боломжит=3, шинэ=10 => 3 + (10-5) = 8
        String updateQuery = "UPDATE book SET title=?, author=?, isbn=?, " +
                             "available_qty = available_qty + (? - quantity), " +
                             "quantity=? WHERE book_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            int newQty = Integer.parseInt(qty);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, isbn);
            ps.setInt(4, newQty);
            ps.setInt(5, newQty);
            ps.setInt(6, selectedBookId);
            ps.executeUpdate();
            loadBooksFromDatabase();
            clearBookFields();
            showAlert(Alert.AlertType.INFORMATION, "Ном амжилттай засагдлаа!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Засахад алдаа гарлаа!");
            e.printStackTrace();
        }
    }

    @FXML
    void btnDelete(ActionEvent event) {
        if (selectedBookId == -1) {
            showAlert(Alert.AlertType.WARNING, "Устгах номоо хүснэгтээс сонгоно уу!");
            return;
        }

        // Түрээсэлсэн байгаа эсэхийг шалгах
        String checkQuery = "SELECT COUNT(*) FROM borrow_records WHERE bookid = ? AND status != 'буцаасан'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setInt(1, selectedBookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Энэ ном одоо түрээсэлсэн байгаа тул устгах боломжгүй!");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Номыг устгахдаа итгэлтэй байна уу?");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                String query = "DELETE FROM book WHERE book_id=?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setInt(1, selectedBookId);
                    ps.executeUpdate();
                    loadBooksFromDatabase();
                    clearBookFields();
                    showAlert(Alert.AlertType.INFORMATION, "Ном амжилттай устгагдлаа!");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Устгаж чадсангүй!");
                    e.printStackTrace();
                }
            }
        });
    }

    private void clearBookFields() {
        txtBookName.clear();
        txtAuthor.clear();
        txtIsbn.clear();
        txtQty.clear();
        selectedBookId = -1;
        tableBook.getSelectionModel().clearSelection();
    }

    // ========== УНШИГЧ ==========
    private void loadMembersFromDatabase() {
        memberList.clear();
        String query = "SELECT * FROM member";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                memberList.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("member_name"),
                    rs.getString("surname"),
                    rs.getString("phone"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnAddMember(ActionEvent event) {
        String name    = txtMemberName.getText();
        String surname = txtSurname.getText();
        String phone   = txtPhone.getText();
        String email   = txtEmail.getText();

        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Бүх талбарыг бөглөнө үү!");
            return;
        }

        String query = "INSERT INTO member (member_name, surname, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.executeUpdate();
            loadMembersFromDatabase();
            clearMemberFields();
            showAlert(Alert.AlertType.INFORMATION, "Уншигч амжилттай нэмэгдлээ!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Нэмэхэд алдаа гарлаа!");
            e.printStackTrace();
        }
    }

    @FXML
    void btnZasahMember(ActionEvent event) {
        if (selectedMemberId == -1) {
            showAlert(Alert.AlertType.WARNING, "Засах уншигчаа хүснэгтээс сонгоно уу!");
            return;
        }

        String name    = txtMemberName.getText();
        String surname = txtSurname.getText();
        String phone   = txtPhone.getText();
        String email   = txtEmail.getText();

        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Бүх талбарыг бөглөнө үү!");
            return;
        }

        String query = "UPDATE member SET member_name=?, surname=?, phone=?, email=? WHERE member_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, selectedMemberId);
            ps.executeUpdate();
            loadMembersFromDatabase();
            clearMemberFields();
            showAlert(Alert.AlertType.INFORMATION, "Уншигч амжилттай засагдлаа!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Засахад алдаа гарлаа!");
            e.printStackTrace();
        }
    }

    @FXML
    void btnDeleteMember(ActionEvent event) {
        if (selectedMemberId == -1) {
            showAlert(Alert.AlertType.WARNING, "Хасах уншигчаа хүснэгтээс сонгоно уу!");
            return;
        }

        // Түрээсэлсэн байгаа эсэхийг шалгах
        String checkQuery = "SELECT COUNT(*) FROM borrow_records WHERE memberid = ? AND status != 'буцаасан'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setInt(1, selectedMemberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Энэ уншигч одоо ном түрээсэлсэн байгаа тул хасах боломжгүй!");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Уншигчийг хасахдаа итгэлтэй байна уу?");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                String query = "DELETE FROM member WHERE member_id=?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setInt(1, selectedMemberId);
                    ps.executeUpdate();
                    loadMembersFromDatabase();
                    clearMemberFields();
                    showAlert(Alert.AlertType.INFORMATION, "Уншигч амжилттай хасагдлаа!");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Хасаж чадсангүй!");
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void memberSearch(ActionEvent event) {
        // txtMemberSearch listener дээр аль хэдийн хийгдсэн
    }

    private void clearMemberFields() {
        txtMemberName.clear();
        txtSurname.clear();
        txtPhone.clear();
        txtEmail.clear();
        selectedMemberId = -1;
        tableMember.getSelectionModel().clearSelection();
    }

    // ========== ТҮРЭЭС ==========
    private void loadRentalsFromDatabase() {
        rentalList.clear();
        String query = "SELECT br.record_id, m.member_name, b.title AS book_title, " +
                       "br.borrow_date, br.due_date, br.status " +
                       "FROM borrow_records br " +
                       "JOIN book b ON br.bookid = b.book_id " +
                       "JOIN member m ON br.memberid = m.member_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                java.sql.Date bDate = rs.getDate("borrow_date");
                java.sql.Date dDate = rs.getDate("due_date");
                rentalList.add(new Rental(
                    rs.getInt("record_id"),
                    rs.getString("member_name"),
                    rs.getString("book_title"),
                    bDate != null ? bDate.toString() : "",
                    dDate != null ? dDate.toString() : "",
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onRentBookClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/rent_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Шинэ номын түрээс бүртгэх");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnRentBook.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            RentDialogController controller = loader.getController();
            if (controller.isSaveClicked()) {
                loadRentalsFromDatabase();
                loadBooksFromDatabase();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Dialog нээхэд алдаа гарлаа!");
            e.printStackTrace();
        }
    }

    @FXML
    void onReturnBookClick(ActionEvent event) {
        Rental selected = tableRental.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Буцаах бүртгэлийг хүснэгтээс сонгоно уу!");
            return;
        }
        if ("буцаасан".equalsIgnoreCase(selected.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Энэ ном аль хэдийн буцаагдсан байна!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Номыг буцаахдаа итгэлтэй байна уу?");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                returnBookInDatabase(selected.getId());
            }
        });
    }

    @FXML
    void onEditRentalClick(ActionEvent event) {
        Rental selected = tableRental.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Засах түрээсийн бүртгэлийг сонгоно уу!");
            return;
        }
        if ("буцаасан".equalsIgnoreCase(selected.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Буцаагдсан бүртгэлийг засах боломжгүй!");
            return;
        }

        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog =
            new javafx.scene.control.Dialog<>();
        dialog.setTitle("Буцаах өдөр засах");
        dialog.setHeaderText("Бүртгэл ID: " + selected.getId() +
                             " | Ном: " + selected.getBookTitle());

        javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker();
        try {
            datePicker.setValue(LocalDate.parse(selected.getDueDate()));
        } catch (Exception e) {
            datePicker.setValue(LocalDate.now().plusDays(14));
        }

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10,
            new javafx.scene.control.Label("Шинэ буцаах өдөр:"),
            datePicker
        );
        vbox.setPadding(new javafx.geometry.Insets(20));
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(
            javafx.scene.control.ButtonType.OK,
            javafx.scene.control.ButtonType.CANCEL
        );

        dialog.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                LocalDate newDate = datePicker.getValue();
                if (newDate == null) {
                    showAlert(Alert.AlertType.WARNING, "Огноо сонгоно уу!");
                    return;
                }
                if (newDate.isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.WARNING, "Буцаах өдөр өнөөдрөөс өмнө байж болохгүй!");
                    return;
                }

                String updateQuery = "UPDATE borrow_records SET due_date=? WHERE record_id=?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(updateQuery)) {
                    ps.setDate(1, java.sql.Date.valueOf(newDate));
                    ps.setInt(2, selected.getId());
                    ps.executeUpdate();
                    loadRentalsFromDatabase();
                    showAlert(Alert.AlertType.INFORMATION, "Буцаах өдөр амжилттай засагдлаа!");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Засахад алдаа гарлаа!");
                    e.printStackTrace();
                }
            }
        });
    }

    private void returnBookInDatabase(int recordId) {
        String selectQuery       = "SELECT bookid FROM borrow_records WHERE record_id = ?";
        String updateRecordQuery = "UPDATE borrow_records SET status='буцаасан', return_date=CURDATE() WHERE record_id = ?";
        String updateBookQuery   = "UPDATE book SET available_qty = available_qty + 1 WHERE book_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int bookId;
            try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
                ps.setInt(1, recordId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Бичлэг олдсонгүй!");
                    conn.rollback();
                    return;
                }
                bookId = rs.getInt("bookid");
            }

            try (PreparedStatement ps = conn.prepareStatement(updateRecordQuery)) {
                ps.setInt(1, recordId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateBookQuery)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            loadRentalsFromDatabase();
            loadBooksFromDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Ном амжилттай буцаагдлаа!");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert(Alert.AlertType.ERROR, "Буцаалт хийхэд алдаа гарлаа!");
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
