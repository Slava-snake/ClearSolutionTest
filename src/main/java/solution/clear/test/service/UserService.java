package solution.clear.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import solution.clear.test.entity.User;
import solution.clear.test.exception.AgeNotValidException;
import solution.clear.test.exception.BadRangeException;
import solution.clear.test.exception.UserNotFoundException;
import solution.clear.test.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Value("${ageLimit}")
    protected long ageLimit;
    
    private ObjectMapper objectMapper = new ObjectMapper(); 
    
    
    protected void userIdExists(long id) {
        if (userRepository.notExists(id))
            throw new UserNotFoundException(id);
    }
    
    
    protected void ageValid(LocalDate birthday) {
        if (birthday.plusYears(ageLimit).isAfter(LocalDate.now())) 
            throw new AgeNotValidException(ageLimit);
    }
    
    
    public User getUser(long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) 
            throw new UserNotFoundException(id);
        return user;
    }
    
    
    public User create(User user) {
        ageValid(user.getBirthday());
        user.setId(0);
        return userRepository.save(user);
    }
    
    
    public User create(String email, String firstName, String lastName, LocalDate birthday,
            String address, String phone) {
        ageValid(birthday);
        return userRepository.save(new User(0, email, firstName, lastName, birthday, address, phone));
    }


    public User update(long id, String email, String firstName, String lastName,
            LocalDate birthday, String address, String phone) {
        User user = getUser(id);
        if (email != null)
            user.setEmail(email);
        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        if (birthday != null) {
            ageValid(birthday);
            user.setBirthday(birthday);
        }
        if (address != null)
            user.setAddress(address);
        if (phone != null)
            user.setPhone(phone);
        return userRepository.save(user);
    }
    

    public User update(User user) {
        userIdExists(user.getId());
        ageValid(user.getBirthday());
        return userRepository.save(user);
    }
    
    
    public User patch(long id, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(getUser(id), JsonNode.class));
        return userRepository.save(objectMapper.treeToValue(patched, User.class));
    }
    
    
    public User delete(long id) {
        userIdExists(id);
        return userRepository.deleteById(id);
    }
    
    
    public List<User> search(LocalDate from, LocalDate to) {
        if (from == null && to == null)
            return userRepository.findAll();
        if (from != null && to != null && from.isAfter(to))
            throw new BadRangeException(from, to);
        if (from == null)
            from = LocalDate.MIN;
        if (to == null)
            to = LocalDate.MAX;
        return userRepository.findByBirthdayAfterAndBefore(from, to);
    }

}
