/*
 * Copyright (C) 2023. Lazar Dobrota and Nemanja Radovanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.raf.gym.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.gym.UserMain;
import rs.raf.gym.commons.dto.client.ClientCreateDto;
import rs.raf.gym.commons.dto.client.ClientDto;
import rs.raf.gym.commons.dto.client.ClientUpdateDto;
import rs.raf.gym.commons.dto.gym.GymDto;
import rs.raf.gym.commons.dto.gym.GymUpdateManagerDto;
import rs.raf.gym.commons.dto.manager.ManagerCreateDto;
import rs.raf.gym.commons.dto.manager.ManagerDto;
import rs.raf.gym.commons.dto.manager.ManagerUpdateDto;
import rs.raf.gym.commons.dto.user.AdminCreateDto;
import rs.raf.gym.commons.dto.user.UserDto;
import rs.raf.gym.commons.dto.user.UserLoginDto;
import rs.raf.gym.commons.dto.user.UserTokenDto;
import rs.raf.gym.commons.dto.user.UserUpdateDto;
import rs.raf.gym.commons.exception.GymException;
import rs.raf.gym.commons.utils.NetworkUtils;
import rs.raf.gym.exception.ExceptionType;
import rs.raf.gym.mapper.UserMapper;
import rs.raf.gym.commons.model.Role;
import rs.raf.gym.model.User;
import rs.raf.gym.model.UserRole;
import rs.raf.gym.repository.IUserRepository;
import rs.raf.gym.repository.IUserRoleRepository;
import rs.raf.gym.security.SecurityAspect;
import rs.raf.gym.security.service.ITokenService;
import rs.raf.gym.service.IUserService;
import rs.raf.gym.specification.UserSpecification;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository     userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final UserMapper          userMapper;
    private final ITokenService       tokenService;
    private final SecurityAspect      securityAspect;
    private final NetworkUtils        networkUtils;

    @Override
    public Page<UserDto> getAllUsers(String role, String firstname, String lastname, String username, Pageable pageable) {
        UserSpecification specification = new UserSpecification(role, firstname, lastname, username);

        return userRepository.findAll(specification.filter(), pageable)
                .map(userMapper::userToUserDto);
    }

    @Override
    public UserDto findById(Long id) throws GymException {
        return userMapper.userToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new GymException(ExceptionType.FIND_ID_USER_NOT_FOUND_USER, id.toString())));
    }

    @Override
    public UserDto createAdmin(AdminCreateDto adminCreateDto) throws GymException {
        UserRole userRole = userRoleRepository.findByName(Role.ADMIN.getName())
                .orElseThrow(() -> new GymException(ExceptionType.CREATE_ADMIN_NOT_FOUND_USER_ROLE, Role.ADMIN.getName()));

        User user = new User();
        user.setUserRole(userRole);
        userMapper.mapUser(user, adminCreateDto);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public ClientDto createClient(ClientCreateDto clientCreateDto) throws GymException {
        UserRole userRole = userRoleRepository.findByName(Role.CLIENT.getName())
                .orElseThrow(() -> new GymException(ExceptionType.CREATE_CLIENT_NOT_FOUND_USER_ROLE, Role.CLIENT.getName()));
        User user = new User();
        user.setUserRole(userRole);
        userMapper.mapUser(user, clientCreateDto);
        return userMapper.userToClientDto(userRepository.save(user));
    }

    @Override
    public ManagerDto createManager(ManagerCreateDto managerCreateDto) throws GymException {
        UserRole userRole = userRoleRepository.findByName(Role.MANAGER.getName())
                .orElseThrow(() -> new GymException(ExceptionType.CREATE_MANAGER_NOT_FOUND_USER_ROLE, Role.MANAGER.getName()));

        User user = new User();
        user.setGymId(networkUtils.request(HttpMethod.GET, "/schedule/gym/id/" + managerCreateDto.getGymName(), UserMain.TOKEN, Long.class));
        user.setUserRole(userRole);
        userMapper.mapUser(user, managerCreateDto);

        User previousManager = userRepository.findUserByGymId(user.getGymId())
                                             .orElse(null);

        if (previousManager != null) { // if manager exists, he should be thrown out of gym
            previousManager.setGymId(null);
            previousManager.setAccess(false);
            userRepository.save(previousManager);
        }

        user = userRepository.save(user);

        networkUtils.asyncRequest(HttpMethod.PUT, "/schedule/gym/manager", UserMain.TOKEN,
                new GymUpdateManagerDto(managerCreateDto.getGymName(), user.getId()), GymDto.class);

        return userMapper.userToManagerDto(user);
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto) throws GymException {
        User user = userRepository.findUserByUsername(userUpdateDto.getOldUsername())
                .orElseThrow(() -> new GymException(ExceptionType.UPDATE_USER_NOT_FOUND_USERNAME, userUpdateDto.getOldUsername()));

        userMapper.mapUser(user, userUpdateDto);

        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public ClientDto updateClient(ClientUpdateDto clientUpdateDto) throws GymException {
        User user = userRepository.findUserByUsername(clientUpdateDto.getOldUsername())
                .orElseThrow(() -> new GymException(ExceptionType.UPDATE_CLIENT_NOT_FOUND_USERNAME, clientUpdateDto.getOldUsername()));

        userMapper.mapUser(user, clientUpdateDto);

        return userMapper.userToClientDto(userRepository.save(user));
    }

    //note: manager can't update its gym, only admin can
    @Override
    public ManagerDto updateManager(ManagerUpdateDto managerUpdateDto) throws GymException {
        User user = userRepository.findUserByUsername(managerUpdateDto.getOldUsername())
                .orElseThrow(() -> new GymException(ExceptionType.UPDATE_MANAGER_NOT_FOUND_USERNAME, managerUpdateDto.getOldUsername()));

        userMapper.mapUser(user, managerUpdateDto);

        return userMapper.userToManagerDto(userRepository.save(user));
    }

    @Override
    public UserTokenDto login(UserLoginDto userLoginDto) throws GymException {
        User user = userRepository.findUserByUsernameAndPassword(userLoginDto.getUsername(), userLoginDto.getPassword())
                .orElseThrow(() -> new GymException(ExceptionType.LOGIN_USER_NOT_FOUND_USERNAME_AND_PASSWORD, userLoginDto.getUsername(), userLoginDto.getPassword()));


        if (!user.isActivated()) throw new GymException(ExceptionType.LOGIN_USER_NOT_ACTIVATED, userLoginDto.getUsername());
        if (!user.isAccess()) throw new GymException(ExceptionType.LOGIN_USER_NOT_ACCESS, userLoginDto.getUsername());

        //Create token
        Map<String, Object> payload = new HashMap<>();
        payload.put(User.id(), user.getId());
        payload.put(User.userRole(), user.getUserRole().getName());

        return new UserTokenDto(userMapper.userToUserDto(user), tokenService.encrypt(payload));
    }

    @Override
    public Long findIdByToken(String token) {
        Long id = securityAspect.getId(token);
        if (id == null) throw new GymException(ExceptionType.FIND_ID_BY_TOKEN_INVALID_TOKEN);
        return id;
    }

    /**
     * Activate account when someone registers
     * @param token credentials of that person
     * @return UserDto if everything is alright
     */
    @Override
    public UserDto activateAccount(String token) {
        Long id = findIdByToken(token);
        User user = userRepository.findById(id).orElseThrow(() -> new GymException(ExceptionType.ACTIVATE_USER_NOT_FOUND_USER, id.toString()));
        return userMapper.userToUserDto(user);
    }

}
