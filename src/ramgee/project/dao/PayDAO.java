package ramgee.project.dao;

import ramgee.project.db.DBProperties;
import ramgee.project.vo.PayVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayDAO {
    private String url = DBProperties.URL;
    private String uid = DBProperties.UID;
    private String upw = DBProperties.UPW;
    private Connection connection;

    public PayDAO() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, uid, upw);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPay(PayVO payVO) {
        String sql = "INSERT INTO pays (pay_no, order_no, amountpaid, paymentdate) VALUES (pay_no_seq.NEXTVAL, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payVO.getOrder_no());
            statement.setDouble(2, payVO.getAmountPaid());
            statement.setDate(3, payVO.getPaymentDate());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void updatePay(PayVO payVO) {
        String sql = "UPDATE pays SET order_no = ?, amountpaid = ?, paymentdate = ? WHERE pay_no = ?";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payVO.getOrder_no());
            statement.setDouble(2, payVO.getAmountPaid());
            statement.setDate(3, payVO.getPaymentDate());
            statement.setInt(4, payVO.getPay_no());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void deletePay(int pay_no) {
        String sql = "DELETE FROM pays WHERE pay_no = ?";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pay_no);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public List<PayVO> findPaymentsByUsername(String username) {
        List<PayVO> payments = new ArrayList<>();
        String sql = "SELECT * FROM pays WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PayVO payment = new PayVO(
                            resultSet.getInt("pay_no"),
                            resultSet.getInt("order_no"),
                            resultSet.getDouble("amount_paid"),
                            resultSet.getDate("payment_date")
                    );
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return payments;
    }


    public PayVO findPayByPayNo(int pay_no) {
        String sql = "SELECT * FROM pays WHERE pay_no = ?";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pay_no);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PayVO payVO = new PayVO(
                            resultSet.getInt("pay_no"),
                            resultSet.getInt("order_no"),
                            resultSet.getDouble("amount_paid"),
                            resultSet.getDate("payment_date")
                    );
                    return payVO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return null;
    }

    public List<PayVO> findAllPays() {
        List<PayVO> pay_list = new ArrayList<>();
        String sql = "SELECT * FROM pays";
        try (Connection connection = DriverManager.getConnection(url, uid, upw);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int pay_no = resultSet.getInt("pay_no");
                int order_no = resultSet.getInt("order_no");
                double amountPaid = resultSet.getDouble("amount_paid");
                Date paymentDate = resultSet.getDate("payment_date");

                PayVO payVO = new PayVO(pay_no, order_no, amountPaid, paymentDate);
                pay_list.add(payVO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return pay_list;
    }

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
