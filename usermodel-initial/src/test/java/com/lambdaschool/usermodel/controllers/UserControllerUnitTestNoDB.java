package com.lambdaschool.usermodel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WithMockUser(username = "admin",
 roles = {"USER", "ADMIN"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = UserModelApplicationTesting.class,
    properties = {
    "command.line.runner.enabled=false"
})
@AutoConfigureMockMvc
public class UserControllerUnitTestNoDB
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private List<User> userList;

    @Before
    public void setUp() throws Exception
    {
        // create a list of users for mocks
        userList = new ArrayList<>();

        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        r1.setRoleid(1);
        r2.setRoleid(2);
        r3.setRoleid(3);

        User u1 = new User("admin",
            "password",
            "admin@lambdaschool.local");
        u1.setUserid(10);

        u1.getRoles()
            .add(new UserRoles(u1,
                r1));
        u1.getRoles()
            .add(new UserRoles(u1,
                r2));
        u1.getRoles()
            .add(new UserRoles(u1,
                r3));

        u1.getUseremails()
            .add(new Useremail(u1,
                "admin@email.local"));
        u1.getUseremails()
            .get(0)
            .setUseremailid(11);

        u1.getUseremails()
            .add(new Useremail(u1,
                "admin@mymail.local"));
        u1.getUseremails()
            .get(1)
            .setUseremailid(12);

        userList.add(u1);

        // data, user
        User u2 = new User("cinnamon",
            "1234567",
            "cinnamon@lambdaschool.local");
        u2.setUserid(20);

        u2.getRoles()
            .add(new UserRoles(u2,
                r2));
        u2.getRoles()
            .add(new UserRoles(u2,
                r3));

        u2.getUseremails()
            .add(new Useremail(u2,
                "cinnamon@mymail.local"));
        u2.getUseremails()
            .get(0)
            .setUseremailid(21);

        u2.getUseremails()
            .add(new Useremail(u2,
                "hops@mymail.local"));
        u2.getUseremails()
            .get(1)
            .setUseremailid(22);

        u2.getUseremails()
            .add(new Useremail(u2,
                "bunny@email.local"));
        u2.getUseremails()
            .get(2)
            .setUseremailid(23);

        userList.add(u2);

        // user
        User u3 = new User("barnbarn",
            "ILuvM4th!",
            "barnbarn@lambdaschool.local");
        u3.setUserid(30);

        u3.getRoles()
            .add(new UserRoles(u3,
                r2));
        u3.getUseremails()
            .add(new Useremail(u3,
                "barnbarn@email.local"));
        u3.getUseremails()
            .get(0)
            .setUseremailid(31);

        userList.add(u3);

        User u4 = new User("puttat",
            "password",
            "puttat@school.lambda");
        u4.setUserid(40);

        u4.getRoles()
            .add(new UserRoles(u4,
                r2));

        userList.add(u4);

        User u5 = new User("misskitty",
            "password",
            "misskitty@school.lambda");
        u5.setUserid(50);

        u5.getRoles()
            .add(new UserRoles(u5,
                r2));

        userList.add(u5);

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void listAllUsers() throws Exception
    {
        String apiUrl = "/users/users";
        Mockito.when(userService.findAll())
            .thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn();
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        System.out.println(er);
        assertEquals(er,
            tr);
    }

    @Test
    public void getUserById() throws Exception
    {
        String apiUrl = "/users/user/10";
        Mockito.when(userService.findUserById(10))
            .thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn();
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(0));

        System.out.println(tr);
        assertEquals(er,
            tr);
    }

    @Test
    public void getUserByIdNotFound() throws Exception
    {
        String apiUrl = "/users/user/100";
        Mockito.when(userService.findUserById(100))
            .thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn();
        String tr = r.getResponse()
            .getContentAsString();

        String er = "";

        System.out.println(er);
        assertEquals(er,
            tr);
    }

    @Test
    public void getUserByName() throws Exception
    {
        String apiUrl = "/users/user/name/puttat";

        Mockito.when(userService.findByName("puttat"))
            .thenReturn(userList.get(3));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn(); // this could throw an exception
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(3));

        System.out.println("Expect: " + er);
        System.out.println("Actual: " + tr);

        Assert.assertEquals("Rest API Returns List",
            er,
            tr);
    }

    @Test
    public void getUserByNameNotFound() throws Exception
    {
        String apiUrl = "/users/user/name/dexter";

        Mockito.when(userService.findByName("dexter"))
            .thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn(); // this could throw an exception
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = "";

        System.out.println("Expect: " + er);
        System.out.println("Actual: " + tr);

        Assert.assertEquals("Rest API Returns List",
            er,
            tr);
    }

    @Test
    public void getUserLikeName() throws Exception
    {

        String apiUrl = "/users/user/name/like/barn";

        Mockito.when(userService.findByNameContaining("barn"))
            .thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn(); // this could throw an exception
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        System.out.println("Expect: " + er);
        System.out.println("Actual: " + tr);

        Assert.assertEquals("Rest API Returns List",
            er,
            tr);
    }

    @Test
    public void addNewUser() throws Exception
    {
        String apiUrl = "/users/user";

        User u = new User("dexter",
            "bananas",
            "dexter@lambdaschool.local");
        u.setUserid(99);

        Role roleType = new Role("consumer");
        roleType.setRoleid(1);

        u.getRoles()
            .add(new UserRoles(u,
                roleType));

        u.getUseremails()
            .add(new Useremail(u,
                "bananas@lambdaschool.com"));
        u.getUseremails()
            .get(0)
            .setUseremailid(34);

        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u);

        Mockito.when(userService.save(any(User.class)))
            .thenReturn(u);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userString);

        mockMvc.perform(rb)
            .andExpect(status().isCreated())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullUser() throws Exception
    {
        String apiUrl = "/users/user/{userid}";
        String userName = "MoodyMonsterMolly";
        User u = new User(userName,
            "bananas",
            "dexter@lambdaschool.local");
        u.setUserid(99);

        Role roleType = new Role("consumer");
        roleType.setRoleid(1);

        u.getRoles()
            .add(new UserRoles(u,
                roleType));

        u.getUseremails()
            .add(new Useremail(u,
                "bananas@lambdaschool.com"));
        u.getUseremails()
            .get(0)
            .setUseremailid(34);

        Mockito.when(userService.update(u,
            10L))
            .thenReturn(u);
        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u);

        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl,
            10L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(userString);

        mockMvc.perform(rb)
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateUser() throws Exception
    {
        String apiUrl = "/users/user/{userid}";
        String userName = "MoodyMonsterMolly";
        User u = new User(userName,
            "bananas",
            "dexter@lambdaschool.local");
        u.setUserid(99);

        Role roleType = new Role("consumer");
        roleType.setRoleid(1);

        u.getRoles()
            .add(new UserRoles(u,
                roleType));

        u.getUseremails()
            .add(new Useremail(u,
                "bananas@lambdaschool.com"));
        u.getUseremails()
            .get(0)
            .setUseremailid(34);

        Mockito.when(userService.update(u,
            10L))
            .thenReturn(u);
        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(u);

        RequestBuilder rb = MockMvcRequestBuilders.patch(apiUrl,
            10L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(userString);

        mockMvc.perform(rb)
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteUserById() throws Exception
    {
        String apiUrl = "/users/user/{userid}";
        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl,
            "13")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getCurrentUserInfo() throws Exception
    {
        String apiUrl = "/users/getuserinfo";
        Mockito.when(userService.findByName("admin"))
            .thenReturn(userList.get(2));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn();
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(2));

        System.out.println(tr);
        assertEquals(er,
            tr);
    }
}