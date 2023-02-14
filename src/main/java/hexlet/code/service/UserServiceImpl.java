package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();
    private final byte[] salt = new byte[16];

    @Override
    public void createNewUser(final UserDto userDto) {

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(getPasswordHash(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void updateUser(long id, UserDto userDto) {

        User userForUpdate = userRepository.findById(id).get();

        userForUpdate.setFirstName(userDto.getFirstName());
        userForUpdate.setLastName(userDto.getLastName());
        userForUpdate.setEmail(userDto.getEmail());
        userForUpdate.setPassword(getPasswordHash(userDto.getPassword()));
        userRepository.save(userForUpdate);

    }

    private String getPasswordHash(String pass) {

        try {
            secureRandom.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Arrays.toString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.getCause();
            return null;
        }

    }

}
