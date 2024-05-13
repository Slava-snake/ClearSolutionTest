package solution.clear.test.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import solution.clear.test.entity.User;


/**
 * It's a fake repository.
 * It had been made for test only.
 * Thread-unsafe.
 */


@Service
public class UserRepository {

    private AtomicLong ids = new AtomicLong();
    private List<User> users = new ArrayList<>();
    
    
    public void set(List<User> newUsers) {
        users = newUsers;
    }
    
    
    protected int getIndexById(long id) {
        int index = -1;
        for(User user : users) {
            index++;
            if (user.getId() == id)
                break;
        }
        return index;
    }
    
    
    protected boolean dateIsBetween(LocalDate date, LocalDate from, LocalDate to) {
        return date.compareTo(from) >= 0 && date.compareTo(to) <= 0;
    }
    
    
    public boolean exists(long id) {
        int index = getIndexById(id);
        return index >= 0;
    }
    
    
    public boolean notExists(long id) {
        int index = getIndexById(id);
        return index < 0;
    }
    
    
    public Optional<User> findById(long id) {
        int index = getIndexById(id);
        return index >= 0 ? Optional.of(users.get(index)) : Optional.empty();
    }
    
    
    public List<User> findAll() {
        return users;
    }
    
    
    public User save(User user) {
        long id = user.getId();
        int index = id == 0 ? -1 : getIndexById(id);
        if (index >= 0)
            users.set(index, user);
        else {
            user.setId(ids.addAndGet(1)); 
            users.add(user);
        }
        return user;
    }
    
    
    public User update(User user) {
        long id = user.getId();
        int index = id == 0 ? -1 : getIndexById(id);
        if (index >= 0) {
            users.set(index, user);
            return user;
        }
        return null;
    }
    
    
    public User deleteById(long id) {
        int index = getIndexById(id);
        return index >= 0 ? users.remove(index) : null;
    }


    public List<User> findByBirthdayAfterAndBefore(LocalDate from, LocalDate to) {
        List<User> result = new ArrayList<>();
        users.stream().filter(user -> dateIsBetween(user.getBirthday(), from, to))
                .forEach(result::add);
        return result;
    }
    
}
