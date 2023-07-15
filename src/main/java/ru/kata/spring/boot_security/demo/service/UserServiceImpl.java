package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Lazy
    public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUsername(user.getEmail());
        usersRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        if (usersRepository.findById(id).isPresent()) {
            usersRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User showUser(long id) {
        Optional<User> userFromDb = usersRepository.findById(id);
        return userFromDb.orElse(new User());
    }

    @Transactional
    @Override
    public void updateUser(User updateUser) {
        if (updateUser.getPassword().isEmpty()) {
            updateUser.setPassword(showUser(updateUser.getId()).getPassword());
        } else updateUser.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
        updateUser.setUsername(updateUser.getEmail());
        usersRepository.saveAndFlush(updateUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getListUsers() {
        return usersRepository.findAll();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String
                    .format("User '%s' not found", username));
        }
        return user;
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }
}
