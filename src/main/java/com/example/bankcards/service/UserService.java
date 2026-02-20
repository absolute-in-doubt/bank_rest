package com.example.bankcards.service;

import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.dto.UpdateRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ServerBusyException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Service
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper, PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
        this.transactionManager = transactionManager;
    }


    public UserDto getUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Couldn't find user with id: " + userId));
        return userDtoMapper.toDto(user);
    }

    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return userDtoMapper.toDtoList(users);
    }

    public void deleteUser(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Couldn't find user with id: " + userId));
        userRepository.delete(user);
    }

    public void updateUser(UpdateRequestDto request) throws UserNotFoundException, ServerBusyException {
        int maxRetries = 3;
        for(int i = maxRetries; i > 0; i--) {
            try {
                TransactionStatus txStat = transactionManager.getTransaction(new DefaultTransactionDefinition());
                try {
                    User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("Couldn't find user with id: " + request.getUserId()));
                    user.setFirstName(request.getFirstName());
                    user.setStatus(request.getStatus());
                    user.setUsername(request.getUsername());
                    user.setLastName(request.getLastName());
                    user.setRoles(request.getRoles());
                    transactionManager.commit(txStat);
                } catch (TransactionException e){
                    transactionManager.rollback(txStat);
                    log.warn("{} failed to perform update on user {} optimistically. Encountered {}", Thread.currentThread().getName(), request.getUserId(), e.getClass().getName());
                }
            } catch (ObjectOptimisticLockingFailureException e){
                log.trace("Failed to block card optimistically. Retries left: {}", i);
            }
        }
        throw new ServerBusyException("Failed to update user due to high amount of concurrent transactions. Try again later");


    }

}
