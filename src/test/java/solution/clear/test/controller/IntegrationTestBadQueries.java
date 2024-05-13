package solution.clear.test.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTestBadQueries {

    @Autowired
    private MockMvc mvc;

    @Value("${ageLimit}")
    private long ageLimit;
    
    private static final String REQUEST_MAPPING = "/api/users";
    
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String BIRTHDAY_FIELD = "birthday";
    
    private static final String BAD_EMAIL = "1234567890";
    private static final String GOOD_EMAIL = "JohnCena@gmail.com";
    private static final String GOOD_BIRTHDAY = LocalDate.of(1970, 1, 1).toString();


    private String getToday() {
        return LocalDate.now().toString();
    }
    
    
    private LocalDate getLastBadBirthday() {
        return LocalDate.now().minusYears(ageLimit);
    }
    
    
    private String getBadBirthday() {
        return getLastBadBirthday().plusDays(1).toString();
    }
    
    
    @Test
    void testRequiredEmail() throws Exception {
        mvc.perform(post(REQUEST_MAPPING)
                // no email
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("String email : not present")));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, "")
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("create.email  : must not be blank")));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, BAD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

    
    @Test
    void testRequiredFirstName() throws Exception {
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                // no first name
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("String firstName : not present")));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("create.firstName  : must not be blank")));
    }

    
    @Test
    void testRequiredLastName() throws Exception {
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                // no last name
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("String lastName : not present")));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "")
                .param(BIRTHDAY_FIELD, GOOD_BIRTHDAY))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("create.lastName  : must not be blank")));
    }

    
    @Test
    void testBadBirthday() throws Exception {
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                // no birthday
                )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("LocalDate birthday : not present")));            
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, " "))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("LocalDate birthday : present but converted to null")));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, getToday()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
        mvc.perform(post(REQUEST_MAPPING)
                .param(EMAIL_FIELD, GOOD_EMAIL)
                .param(FIRST_NAME_FIELD, "first1")
                .param(LAST_NAME_FIELD, "last1")
                .param(BIRTHDAY_FIELD, getBadBirthday()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("Age not valid. Minimum value is " + ageLimit + ".")));
    }
    
    
    @Test
    void testBadUserId() throws Exception {
        long id = 1L;
        mvc.perform(get(REQUEST_MAPPING + "/{id}", id))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().string(containsString("User (id=" + id + ") not found")));
    }
 
    
    @Test
    void testBadDateRange() throws Exception {
        mvc.perform(get(REQUEST_MAPPING)
                    .param("from", "2222-02-01")
                    .param("to", "1111-11-11"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().string(containsString("End date must be after begin date.")));
    }
    
}
