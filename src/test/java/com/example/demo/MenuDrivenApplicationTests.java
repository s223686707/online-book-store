package com.example.demo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.online.book.store.Main;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class MenuDrivenApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAdminHomePage() throws Exception {
        mockMvc.perform(get("/Admin_Home"))
               .andExpect(status().isOk())
               .andExpect(view().name("Admin_View"));
    }

    @Test
    public void testBookManagementPage() throws Exception {
        mockMvc.perform(get("/Book_Management"))
               .andExpect(status().isOk())
               .andExpect(view().name("Book_Management"));
    }

    @Test
    public void testUserHomePage() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("Home"));
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/Login").param("User", "testUser").param("Pass", "testPassword"))
               .andExpect(status().isOk())
               .andExpect(view().name("Login_Form"));
    }

    // Add more test cases for other endpoints and controllers as needed
}

