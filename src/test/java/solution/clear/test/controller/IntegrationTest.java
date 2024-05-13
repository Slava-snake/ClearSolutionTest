package solution.clear.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import solution.clear.test.entity.User;

/**
 * Integration scenario test. It's created specifically against UserRepository.class.
 */

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Value("${ageLimit}")
    private long ageLimit;

    private static final String REQUEST_MAPPING = "/api/users";

    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String BIRTHDAY_FIELD = "birthday";
    private static final String PHONE_FIELD = "phone";

    private static final String GOOD_EMAIL = "JohnCena@gmail.com";
    private static final String GOOD_BIRTHDAY = LocalDate.of(1970, 1, 1).toString();

    private static final TypeReference<List<User>> LIST_TYPE = new TypeReference<List<User>>() {};

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    private LocalDate getLimitBirthday() {
        return LocalDate.now().minusYears(ageLimit);
    }


    private void testEmpty() throws Exception {
        mvc.perform(get(REQUEST_MAPPING))
            .andExpect(status().isNoContent())
            .andExpect(content().string("[]"));
    }


    private User json2user(String json) throws Exception {
        return objectMapper.readValue(json, User.class);
    }


    private List<User> json2userList(String json) throws Exception {
        return objectMapper.readValue(json, LIST_TYPE);
    }


    private String user2json(User user) throws Exception {
        return objectMapper.writeValueAsString(user);
    }


    private User getUser(long id) throws Exception {
        return json2user(
                mvc.perform(get(REQUEST_MAPPING + "/{id}", id))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
    }


    private long testCreateAndGetId(String userName, String birthday) throws Exception {
        User user = json2user(mvc
                .perform(post(REQUEST_MAPPING)
                        .param(EMAIL_FIELD, GOOD_EMAIL)
                        .param(FIRST_NAME_FIELD, userName)
                        .param(LAST_NAME_FIELD, "last name")
                        .param(BIRTHDAY_FIELD, birthday))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString());
        assertEquals(user, getUser(user.getId()));
        return user.getId();
    }


    private String getFirstName(long id) {
        return "user " + id;
    }


    private void testCreate(long... ids) throws Exception {
        List<Long> result = new ArrayList<>();
        for (long id : ids)
            result.add(testCreateAndGetId(getFirstName(id), GOOD_BIRTHDAY));
        assertEquals(Arrays.stream(ids).boxed().toList(), result);
    }


    private void testNewUser(long id) throws Exception {
        User user = json2user(
            mvc.perform(post(REQUEST_MAPPING + "/new")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(user2json(
                            new User(0, GOOD_EMAIL, getFirstName(id), "last name", getLimitBirthday(), null, null))))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType("application/json")).andReturn().getResponse()
                .getContentAsString());
        assertEquals(user, getUser(id));
        assertEquals(id, user.getId());
    }


    private void testGet(long id) throws Exception {
        User user = getUser(id);
        assertEquals(id, user.getId());
        assertEquals(getFirstName(id), user.getFirstName());
    }


    private void testUpdate(long id) throws Exception {
        User user = new User(id, "EMAIL@GOOD", "FIRST", "LAST", LocalDate.of(2000, 01, 01), "Anywhere", "103");
        assertEquals(user, json2user(
                mvc.perform(put(REQUEST_MAPPING)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(user2json(user)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString()));
    }


    private void testUpdateSomeFields(long id) throws Exception {
        User user = json2user(
                mvc.perform(put(REQUEST_MAPPING + "/{id}", id)
                            .param(EMAIL_FIELD, "EMAIL@GOOD"))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals("EMAIL@GOOD", user.getEmail());
        user = json2user(
                mvc.perform(put(REQUEST_MAPPING + "/{id}", id)
                            .param(FIRST_NAME_FIELD, FIRST_NAME_FIELD)
                            .param(LAST_NAME_FIELD, LAST_NAME_FIELD)
                            .param(BIRTHDAY_FIELD, "0001-01-01")
                            .param(PHONE_FIELD, "+380960000000"))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(FIRST_NAME_FIELD, user.getFirstName());
        assertEquals(LAST_NAME_FIELD, user.getLastName());
        assertEquals(LocalDate.of(1, 1, 1), user.getBirthday());
        assertEquals("+380960000000", user.getPhone());
    }


    void testPatch(long id) throws Exception {
        User user = json2user(
                mvc.perform(patch(REQUEST_MAPPING + "/{id}", id)
                            .contentType("application/json-patch+json")
                            .content("[{\"op\":\"replace\",\"path\":\"/email\",\"value\":\"EMAIL@GOOD\"}]"))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals("EMAIL@GOOD", user.getEmail());
        user = json2user(
                mvc.perform(patch(REQUEST_MAPPING + "/{id}", id)
                            .contentType("application/json-patch+json")
                            .content("[{\"op\":\"replace\",\"path\":\"/" + FIRST_NAME_FIELD + "\",\"value\":\""
                                    + FIRST_NAME_FIELD + "\"}," + "{\"op\":\"replace\",\"path\":\"/"
                                    + LAST_NAME_FIELD + "\",\"value\":\"" + LAST_NAME_FIELD + "\"},"
                                    + "{\"op\":\"replace\",\"path\":\"/" + BIRTHDAY_FIELD
                                    + "\",\"value\":\"0001-01-01\"}," + "{\"op\":\"replace\",\"path\":\"/"
                                    + PHONE_FIELD + "\",\"value\":\"+380960000000\"}]"))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType("application/json")).andReturn().getResponse()
                    .getContentAsString());
        assertEquals(FIRST_NAME_FIELD, user.getFirstName());
        assertEquals(LAST_NAME_FIELD, user.getLastName());
        assertEquals(LocalDate.of(1, 1, 1), user.getBirthday());
        assertEquals("+380960000000", user.getPhone());
    }


    void compare(long id1, long id2) throws Exception {
        User user1 = json2user(
                mvc.perform(get(REQUEST_MAPPING + "/{id}", id1))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        User user2 = json2user(
                mvc.perform(get(REQUEST_MAPPING + "/{id}", id2))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(user1, user2);
    }


    void testSearch() throws Exception {
        List<User> users = new ArrayList<>();
        for (long id = 1; id <= 4; id++)
            users.add(getUser(id));
        List<User> responsedAll = json2userList(
                mvc.perform(get(REQUEST_MAPPING))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(users, responsedAll);
        List<User> responsed2 = json2userList(
                mvc.perform(get(REQUEST_MAPPING)
                            .param("to", GOOD_BIRTHDAY))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(List.of(users.get(0), users.get(2)), responsed2);
        List<User> responsedLast = json2userList(
                mvc.perform(get(REQUEST_MAPPING)
                            .param("from", getLimitBirthday().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(users.subList(3, 4), responsedLast);
        responsedAll = json2userList(
                mvc.perform(get(REQUEST_MAPPING)
                            .param("from", "0001-01-01")
                            .param("to", getLimitBirthday().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
        assertEquals(users, responsedAll);
    }


    void testDelete(long... ids) throws Exception {
        for (long id : ids) {
            User user = json2user(
                mvc.perform(delete(REQUEST_MAPPING + "/{id}", id))
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(content().contentType("application/json"))
                    .andReturn().getResponse().getContentAsString());
            assertEquals(id, user.getId());
        }
    }


    @Test
    void testScenario() throws Exception {
        testEmpty();
        testCreate(1, 2, 3);
        testNewUser(4);
        testGet(3);
        testUpdate(2);
        testUpdateSomeFields(3);
        testPatch(1);
        compare(1, 3);
        testSearch();
        testDelete(1, 2, 3, 4);
        testEmpty();
    }

}
