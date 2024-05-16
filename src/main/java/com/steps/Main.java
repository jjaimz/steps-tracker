package com.steps;

import com.steps.business.Steps;
import com.steps.business.User;
import java.util.List;
import java.sql.*;
import static com.steps.data.HikariUtil.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        String q1 = "SELECT * FROM users";

        List<User> users = fetch(q1);
        printList(users);

        String q2 = "SELECT * FROM steps";
        List<Steps> steps = fetch(q2);
        printList(steps);


    }


}
