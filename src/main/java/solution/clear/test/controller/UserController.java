package solution.clear.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solution.clear.test.entity.User;
import solution.clear.test.exception.UserNotFoundException;
import solution.clear.test.service.UserService;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    

    protected ResponseEntity<Collection<User>> formStatusCollection(Collection<User> result) {
        return new ResponseEntity<>(result, result.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }
    
    
    @PostMapping("/new")
    public ResponseEntity<User> newUser(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }
    
    
    @PostMapping
    public ResponseEntity<User> create(@RequestParam @NotBlank @Email String email, 
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName, 
            @RequestParam @Past LocalDate birthday,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phone) {
        return new ResponseEntity<>(
                userService.create(email, firstName, lastName, birthday, address, phone),
                HttpStatus.CREATED);
    }
    
    
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> patch(@PathVariable long id, @RequestBody JsonPatch patch) 
            throws JsonProcessingException, JsonPatchException {
        return new ResponseEntity<>(userService.patch(id, patch), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable long id,
            @RequestParam(required = false) @Email String email, 
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName, 
            @RequestParam(required = false) @Past LocalDate birthday,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phone) throws UserNotFoundException {
        return new ResponseEntity<>(
                userService.update(id, email, firstName, lastName, birthday, address, phone),
                HttpStatus.OK);
    }
    
    
    @PutMapping
    public ResponseEntity<User> update(@RequestBody User user) {
        return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable long id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.NO_CONTENT);
    }
    
    
    @GetMapping
    public ResponseEntity<Collection<User>> search(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return formStatusCollection(userService.search(from, to));
    }

}
