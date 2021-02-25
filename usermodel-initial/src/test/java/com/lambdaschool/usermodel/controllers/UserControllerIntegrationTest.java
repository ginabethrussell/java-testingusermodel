package com.lambdaschool.usermodel.controllers;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.services.RoleService;
import com.lambdaschool.usermodel.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserModelApplicationTesting.class)
@AutoConfigureMockMvc
@WithUserDetails(value = "admin")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerIntegrationTest
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Before
    public void setUp() throws Exception
    {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        List<User> myUsers = userService.findAll();
        for (User u : myUsers )
        {
            System.out.println(u.getUserid() + " " + u.getUsername());
        }
        List<Role> myRoles = roleService.findAll();
        for(Role r : myRoles)
        {
            System.out.println(r.getRoleid() + " " + r.getRoleid());
        }

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void whenMeasuredResponseTime()
    {
        given().when()
            .get("/users/users")
            .then()
            .time(lessThan(5000L));
    }

    @Test
    public void a_listAllUsers()
    {
        given().when()
            .get("/users/users")
            .then()
            .statusCode(200)
            .and()
            .body(containsString("cinnamon"));
    }

    @Test
    public void b_getUserById()
    {
        long aUser = 4;

        given().when()
            .get("/users/user/" + aUser)
            .then()
            .statusCode(200)
            .and()
            .body(containsString("kitty"));
    }

    @Test
    public void c_getUserByIdNotFound()
    {
        long aUser = 99;

        given().when()
            .get("/users/user/" + aUser)
            .then()
            .statusCode(404)
            .and()
            .body(containsString("Resource Not"));
    }

    @Test
    public void d_getUserByName()
    {
        String aUser = "barnbarn";
        given().when()
            .get("users/user/name/" + aUser)
            .then()
            .statusCode(200)
            .and()
            .body(containsString("barnbarn"));
    }

    @Test
    public void e_getUserLikeName()
    {
        String aUser = "kit";
        given().when()
            .get("/users/user/name/like/" + aUser)
            .then()
            .statusCode(200)
            .and()
            .body(containsString("misskitty"));

    }

    @Test
    public void q_addNewUser() throws Exception
    {
        String jsonInput = "{\n" +
            "    \"username\": \"dexter\",\n" +
            "    \"primaryemail\": \"dexter@lambda.com\",\n" +
            "    \"password\": \"bananas\" \n" +
            "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/user")
            .content(jsonInput)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.header()
                .exists("location"));
    }

    @Test
    public void updateFullUser() throws Exception
    {
        String jsonInput = "{\n" +
            "    \"username\": \"dexter\",\n" +
            "    \"primaryemail\": \"dexter@lambda.com\",\n" +
            "    \"password\": \"bananas\" \n" +
            "}";
        mockMvc.perform(MockMvcRequestBuilders.put("/users/user/{userid}",
            7)
            .content(jsonInput)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

    }

    @Test
    public void h_updateUser() throws Exception
    {
        String jsonInput = "{\n" +
            "    \"username\": \"dexter\" \n" +
            "}";
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/user/{userid}",
            13)
            .content(jsonInput)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void i_deleteUserById()
    {
        long aUser = 11;
        given().when()
            .delete("/users/user/" + aUser)
            .then()
            .statusCode(200);
    }

    @Test
    public void j_deleteUserByIdNotFound()
    {
        long aUser = 7777;
        given().when()
            .delete("/users/user/" + aUser)
            .then()
            .statusCode(404);
    }

    @Test
    public void k_getCurrentUserInfo()
    {
        given().when()
            .get("/users/getuserinfo")
            .then()
            .statusCode(200)
            .and()
            .body(containsString("admin"));
    }


    @Test
    @WithUserDetails(value = "cinnamon")
    public void l_getOwnCurrentUserInfo()
    {
        given().when()
            .get("/users/getuserinfo")
            .then()
            .statusCode(200)
            .and()
            .body(containsString("cinnamon"));
    }

}