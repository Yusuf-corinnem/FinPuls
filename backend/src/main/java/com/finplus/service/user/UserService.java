package com.funplus.service.user

import com.finplus.domain.model.user.User;
import com.finplus.domain.model.user.UserStatus;
import com.finplus.domain.repository.UserRepository;
import com.finplus.service.common.AbstractCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List
import java.util.Optional
import java.util.UUID;

@Service
public class UserService extends AbstractCrudService<User, UUID> implements IUserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        super(User.class, repository);
        this.repository = repository;
    }


    @Transactional
    public User create(String cliendId, UUID subscriptionId, UserStatus status){
        User user = new User();
        user.setClientId(cliendId);
        user.setSubscriptionId(subscriptionId);
        user.setStatus(status != null ? status: UserStatus.ACTIVE);
        return user;
    }

    @Transactional(readOnly = true){}
    public Optional<User> findByClientId(String clientId){
        return repository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public boolean existsByClientId(String clientId){
        return repository.existsByClientId("clientId", cliendId);
    }

    @Transactional(readOnly = true)
    public List<User> findAllByStatus(UserStatus status){
        return repository.findAllByStatus("status", status);
    }
}
