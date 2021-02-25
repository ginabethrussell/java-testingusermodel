package com.lambdaschool.usermodel.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTesting.class,
    properties = {
    "command.line.runner.enabled=false"
})
public class UserServiceImplUnitTestNoDB
{

    // create mocks

    @Autowired
    UserService userService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleService roleService;

    @MockBean
    private HelperFunctions helperFunctions;

    private List <User> userList;
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
        u1.getUseremails().get(0).setUseremailid(11);

        u1.getUseremails()
            .add(new Useremail(u1,
                "admin@mymail.local"));
        u1.getUseremails().get(1).setUseremailid(12);

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
        u2.getUseremails().get(0).setUseremailid(21);

        u2.getUseremails()
            .add(new Useremail(u2,
                "hops@mymail.local"));
        u2.getUseremails().get(1).setUseremailid(22);

        u2.getUseremails()
            .add(new Useremail(u2,
                "bunny@email.local"));
        u2.getUseremails().get(2).setUseremailid(23);

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
        u3.getUseremails().get(0).setUseremailid(31);

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

        MockitoAnnotations.initMocks(this);

    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void findUserById()
    {
        System.out.println("Test findUserById()");
        Mockito.when(userrepos.findById(10L))
            .thenReturn(Optional.of(userList.get(0)));
        assertEquals("admin", userService.findUserById(10).getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notFindUserById()
    {
        System.out.println("Test NotFindUserById()");
        Mockito.when(userrepos.findById(10L))
            .thenThrow(ResourceNotFoundException.class);

        assertEquals("admin", userService.findUserById(10).getUsername());
    }

    @Test
    public void findByNameContaining()
    {
        System.out.println("Test findByNameContaining()");
        Mockito.when(userrepos.findByUsernameContainingIgnoreCase("kitty"))
            .thenReturn(userList);
        assertEquals("misskitty", userService.findByNameContaining("kitty").get(4).getUsername());
    }

    @Test
    public void findAll()
    {
        System.out.println("Test findAll()");
        Mockito.when(userrepos.findAll())
            .thenReturn(userList);
        System.out.println(userService.findAll());
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void delete()
    {
        System.out.println("Test delete");
        Mockito.when(userrepos.findById(40L))
            .thenReturn(Optional.of(userList.get(3)));
        Mockito.doNothing()
            .when(userrepos)
            .deleteById(40L);

        userService.delete(40);
        assertEquals(5, userList.size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void noDelete()
    {
        System.out.println("Test noDelete");
        Mockito.when(userrepos.findById(40L))
            .thenThrow(ResourceNotFoundException.class);
        Mockito.doNothing()
            .when(userrepos)
            .deleteById(40L);

        userService.delete(40);
        assertEquals(5, userList.size());
    }

    @Test
    public void findByName()
    {
        System.out.println("Test findByName");
        Mockito.when(userrepos.findByUsername("admin"))
            .thenReturn(userList.get(0));
        assertEquals("admin", userService.findByName("admin").getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notFindByName()
    {
        System.out.println("Test NotFindByName()");
        Mockito.when(userrepos.findByUsername("molly"))
            .thenThrow(ResourceNotFoundException.class);

        assertEquals("admin", userService.findByName("molly").getUsername());
    }

    @Test
    public void save()
    {
        User u = new User("dexter",
            "bananas",
            "dexter@lambdaschool.local");

        Role roleType = new Role("consumer");
        roleType.setRoleid(1);

        u.getRoles()
            .add(new UserRoles(u,
                roleType));

        u.getUseremails().add(new Useremail(u,"moody@lambdaschool.com" ));
        u.getUseremails().get(0).setUseremailid(34);

        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);
        Mockito.when(roleService.findRoleById(1L))
           .thenReturn(roleType);

        User newUser = userService.save(u);
        assertNotNull(newUser);
        assertEquals("dexter", newUser.getUsername());

    }

    @Test
    public void update() throws Exception
    {

        User u = new User("molly",
            "timothyhay",
            "molly@lambdaschool.local");
        u.setUserid(50);
        Role roleType = new Role("master");
        roleType.setRoleid(9);
        u.getRoles().add(new UserRoles(u, roleType));


        // I need a copy of r2 to send to update so the original r2 is not changed.
        // I am using Jackson to make a clone of the object
        ObjectMapper objectMapper = new ObjectMapper();
        User u3 = objectMapper
            .readValue(objectMapper.writeValueAsString(u), User.class);

        Mockito.when(helperFunctions.isAuthorizedToMakeChange("molly"))
            .thenReturn(true);
        Mockito.when(userrepos.findById(50L))
            .thenReturn(Optional.of(u3));
        Mockito.when(roleService.findRoleById(1L))
            .thenReturn(roleType);

        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        User updateUser = userService.update(u3, 50);

        assertNotNull(updateUser);
        assertEquals("molly",
            updateUser.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notAuthorizedupdate() throws Exception
    {

        User u = new User("molly",
            "timothyhay",
            "molly@lambdaschool.local");
        u.setUserid(50);
        Role roleType = new Role("master");
        roleType.setRoleid(9);
        u.getRoles().add(new UserRoles(u, roleType));


        // I need a copy of r2 to send to update so the original r2 is not changed.
        // I am using Jackson to make a clone of the object
        ObjectMapper objectMapper = new ObjectMapper();
        User u3 = objectMapper
            .readValue(objectMapper.writeValueAsString(u), User.class);

        Mockito.when(helperFunctions.isAuthorizedToMakeChange("molly"))
            .thenThrow(ResourceNotFoundException.class);
        Mockito.when(userrepos.findById(50L))
            .thenReturn(Optional.of(u3));
        Mockito.when(roleService.findRoleById(1L))
            .thenReturn(roleType);

        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        User updateUser = userService.update(u3, 50);

        assertNotNull(updateUser);
        assertEquals("molly",
            updateUser.getUsername());
    }


    @Test
    public void deleteAll()
    {
        System.out.println("Test deleteAll");
        Mockito.doNothing()
            .when(userrepos)
            .deleteAll();

        userService.deleteAll();
        assertEquals(5, userList.size());
    }
}