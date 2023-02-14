package hexlet.code.service;

import hexlet.code.dto.UserDto;

public interface UserService {
    void createNewUser(UserDto userDto);

    void updateUser(long id, UserDto userDto);
}
