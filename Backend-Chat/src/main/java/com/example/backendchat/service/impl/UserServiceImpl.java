package com.example.backendchat.service.impl;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.MessageConstant;
import com.example.backendchat.constant.SortByDataConstant;
import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.pagination.PaginationResponseDto;
import com.example.backendchat.domain.dto.pagination.PagingMeta;
import com.example.backendchat.domain.dto.request.ChangePasswordRequestDto;
import com.example.backendchat.domain.dto.request.UserUpdateDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.domain.dto.response.UserDto;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.domain.mapper.UserMapper;
import com.example.backendchat.exception.AlreadyExistException;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.security.UserPrincipal;
import com.example.backendchat.service.UserService;
import com.example.backendchat.util.PaginationUtil;
import com.example.backendchat.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final UploadFileUtil uploadFileUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        return userMapper.toUserDto(user);
    }

    @Override
    public PaginationResponseDto<UserDto> getCustomers(PaginationFullRequestDto request) {
        //Pagination
        Pageable pageable = PaginationUtil.buildPageable(request, SortByDataConstant.USER);
        //Create Output
        return new PaginationResponseDto<>(null, null);
    }

    @Override
    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.getUser(principal);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateInfo(String userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        userMapper.updateUser(user, userUpdateDto);

        if (userUpdateDto.getEmail() != null) {
            if (!userRepository.existsByEmail(userUpdateDto.getEmail())) {
                user.setEmail(userUpdateDto.getEmail());
            } else throw new AlreadyExistException(ErrorMessage.User.ERR_ALREADY_EXIST_USER,
                    new String[]{"email: " + userUpdateDto.getEmail()});
        }

        if (userUpdateDto.getUsername() != null) {
            if (!userRepository.existsByUsername(userUpdateDto.getUsername())) {
                user.setUsername(userUpdateDto.getUsername());
            } else throw new AlreadyExistException(ErrorMessage.User.ERR_ALREADY_EXIST_USER,
                    new String[]{"username: " + userUpdateDto.getUsername()});
        }

        if (userUpdateDto.getAvatar() != null) {
            if (user.getAvatar() != null) {
                uploadFileUtil.destroyFileWithUrl(user.getAvatar());
            }
            user.setAvatar(uploadFileUtil.uploadFile(userUpdateDto.getAvatar()));
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public CommonResponseDto changePassword(String userId, ChangePasswordRequestDto passwordRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        if (!passwordEncoder.matches(passwordRequestDto.getCurrentPassword(), user.getPassword())) {
            return new CommonResponseDto(false, MessageConstant.CURRENT_PASSWORD_INCORRECT);
        }
        if (passwordRequestDto.getCurrentPassword().equals(passwordRequestDto.getNewPassword())) {
            return new CommonResponseDto(false, MessageConstant.SAME_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(passwordRequestDto.getNewPassword()));
        userRepository.save(user);

        return new CommonResponseDto(true, MessageConstant.CHANGE_PASSWORD_SUCCESSFULLY);
    }

    @Override
    public PaginationResponseDto<UserDto> getAllUserConversation(PaginationFullRequestDto paginationFullRequestDto, String meId) {
        userRepository.findById(meId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{meId}));

        Pageable pageable = PaginationUtil.buildPageable(paginationFullRequestDto);

        Page<User> userPage = userRepository.getAllUserConversation(meId, pageable);

        PagingMeta meta = PaginationUtil.buildPagingMeta(paginationFullRequestDto, userPage);

        return new PaginationResponseDto<>(meta, userMapper.toUserDtos(userPage.getContent()));
    }

    @Override
    public PaginationResponseDto<UserDto> searchFriend(PaginationFullRequestDto paginationFullRequestDto, String meId, String searchText) {
        userRepository.findById(meId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{meId}));

        Pageable pageable = PaginationUtil.buildPageable(paginationFullRequestDto);

        Page<User> userPage = userRepository.searchFriend(meId,searchText, pageable);

        PagingMeta meta = PaginationUtil.buildPagingMeta(paginationFullRequestDto, userPage);

        return new PaginationResponseDto<>(meta, userMapper.toUserDtos(userPage.getContent()));
    }

    @Override
    public PaginationResponseDto<UserDto> searchOtherFriend(PaginationFullRequestDto paginationFullRequestDto, String meId, String searchText) {
        userRepository.findById(meId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{meId}));

        Pageable pageable = PaginationUtil.buildPageable(paginationFullRequestDto);

        Page<User> userPage = userRepository.searchOtherFriend(meId,searchText, pageable);

        PagingMeta meta = PaginationUtil.buildPagingMeta(paginationFullRequestDto, userPage);

        return new PaginationResponseDto<>(meta, userMapper.toUserDtos(userPage.getContent()));
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME,
                        new String[]{username}));
        return userMapper.toUserDto(user);
    }

}
