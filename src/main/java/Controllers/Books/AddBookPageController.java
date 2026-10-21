package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddBookPageController {
    Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<String> category(){
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        String sql = "Select category_id ,category_name FROM category ORDER BY category_id ASC";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String categoryId = resultSet.getString("category_id");
                String categoryName = resultSet.getString("category_name");
                categoryList.add(categoryId + " - " + categoryName);
        }

    }catch (SQLException e){

            System.out.println("Could Not Found the Category in DB" +e.getMessage());
        }

     if (categoryList.isEmpty()) {
         categoryList.addAll("Romance", "Fantasy","Science Fiction","Mystery & Crime","Thriller & Suspense","Horror",
                 "Historical Fiction","Literary Fiction","Young Adult (YA) & Children’s");
     }
        return categoryList;
    }

}
