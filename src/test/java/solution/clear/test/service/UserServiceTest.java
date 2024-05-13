package solution.clear.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import solution.clear.test.entity.User;
import solution.clear.test.exception.AgeNotValidException;
import solution.clear.test.exception.UserNotFoundException;
import solution.clear.test.repository.UserRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    @Autowired
    private UserService service;
    
    @MockBean
    private UserRepository repository;
    
    @Value("${ageLimit}")
    private long ageLimit;
    
    private static String TODAY = LocalDate.now().toString();
    
    private static User user99 = 
            new User(99, "email@email.com", "First", "Last", LocalDate.of(2000, 2, 2), null, null); 
    
    private static User userInvalid = 
            new User(-1, "bad-email", "", null, LocalDate.now(), null, null);

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testGetUser() {
        when(repository.findById(0)).thenReturn(Optional.empty());
        when(repository.findById(-1)).thenReturn(Optional.empty());
        when(repository.findById(1)).thenReturn(Optional.empty());
        when(repository.findById(99)).thenReturn(Optional.of(user99)); 
        
        assertThrows(UserNotFoundException.class, () -> service.getUser(0), "User (id=0) not found");
        assertThrows(UserNotFoundException.class, () -> service.getUser(-1), "User (id=-1) not found");
        assertThrows(UserNotFoundException.class, () -> service.getUser(1), "User (id=1) not found");
        assertDoesNotThrow(() -> assertEquals(user99, service.getUser(99)));
    }
    
    
    @Test
    void testCreate() {
        when(repository.save(any())).thenReturn(user99);
        
        assertThrows(AgeNotValidException.class, () -> service.create(userInvalid),
                "Age not valid. Minimum value is " + ageLimit + ".");
        assertDoesNotThrow(() -> assertEquals(user99, service.create(user99)));
    }
    
    
    @Test
    void testCreateByFields() {
        when(repository.save(any())).thenReturn(user99);
        
        assertThrows(AgeNotValidException.class,
                () -> service.create("", "", "", LocalDate.now(), null, null),
                "Age not valid. Minimum value is " + ageLimit + ".");
        assertDoesNotThrow(() -> assertEquals(user99,
                service.create("", "", "", LocalDate.now().minusYears(ageLimit), null, null)));
    }
    
    
    @Test
    void testUpdate() {
        when(repository.save(any())).thenReturn(user99);
        
        assertThrows(AgeNotValidException.class, () -> service.update(userInvalid), 
                "Age not valid. Minimum value is " + ageLimit + ".");
        assertDoesNotThrow(() -> assertEquals(user99, service.update(user99)));
    }
    
    
    @Test
    void testUpdateSomeFields() {
        when(repository.findById(99)).thenReturn(Optional.of(user99));
        when(repository.save(any())).thenReturn(user99);
        
        assertThrows(AgeNotValidException.class,
                () -> service.update(99, "", "", "", LocalDate.now(), null, null),
                "Age not valid. Minimum value is " + ageLimit + ".");
        assertDoesNotThrow(() -> assertEquals(user99, 
                service.update(99, "", "", "", LocalDate.now().minusYears(ageLimit), null, null)));
    }
    
    
    @Test
    void testPatch() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        when(repository.findById(99)).thenReturn(Optional.of(user99));
        when(repository.findById(99)).thenReturn(Optional.of(user99));
        when(repository.save(any())).thenReturn(user99);
        
        assertThrows(UserNotFoundException.class, () -> 
                service.patch(1, JsonPatch.fromJson(objectMapper.readValue(
                        "[{\"op\":\"replace\",\"path\":\"/birthday\",\"value\":\"" + TODAY + "\"}]", JsonNode.class))),
                "User (id=1) not found");
        assertThrows(AgeNotValidException.class, () ->
                service.patch(99, JsonPatch.fromJson(objectMapper.readValue(
                        "[{\"op\":\"replace\",\"path\":\"/birthday\",\"value\":\"" + TODAY + "\"}]", JsonNode.class))),
                "Age not valid. Minimum value is " + ageLimit + "."); 
        assertDoesNotThrow(() -> assertEquals(user99, service.patch(99, JsonPatch.fromJson(objectMapper.readValue(
                "[{\"op\":\"replace\",\"path\":\"/birthday\",\"value\":\"0001-01-01\"}]", JsonNode.class)))
                ));
    }
    
    
    @Test
    void testDelete() {
        when(repository.notExists(1)).thenReturn(true);
        when(repository.notExists(99)).thenReturn(false);
        when(repository.deleteById(99)).thenReturn(user99);   
        
        assertThrows(UserNotFoundException.class, () -> service.delete(1), "User (id=1) not found");
        assertEquals(user99, service.delete(99));
    }
    
    
    @Test
    void testSearch() {
        List<User> all = List.of(user99, userInvalid);
        when(repository.findAll()).thenReturn(all);

        assertEquals(all, service.search(null, null));
        assertDoesNotThrow(() -> service.search(null, userInvalid.getBirthday()));
        assertDoesNotThrow(() -> service.search(user99.getBirthday(), null));
        assertThrows(ConstraintViolationException.class, () -> 
                service.search(LocalDate.of(2000, 1, 1), LocalDate.of(1000, 1, 1)));  
    }
    
}
