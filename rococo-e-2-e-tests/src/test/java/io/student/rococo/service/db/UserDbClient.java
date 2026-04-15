package io.student.rococo.service.db;

import io.qameta.allure.Step;
import io.student.rococo.config.Config;
import io.student.rococo.data.entity.auth.AuthUserEntity;
import io.student.rococo.data.entity.auth.Authority;
import io.student.rococo.data.entity.auth.AuthorityEntity;
import io.student.rococo.data.entity.userdata.UserDataEntity;
import io.student.rococo.data.repository.auth.AuthUserRepository;
import io.student.rococo.data.repository.userdata.UserdataRepository;
import io.student.rococo.data.tpl.XaTransactionTemplate;
import io.student.rococo.model.UserJson;
import io.student.rococo.service.UserClient;
import io.student.rococo.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class UserDbClient implements UserClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepository();
    private final UserdataRepository udUserRepository = new UserdataRepository();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );
    @Override
    @Step("Create user by SQL")
    public @Nonnull UserJson createUser(@Nonnull String username, @Nonnull String password) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(username);
                    authUser.setPassword(pe.encode(password));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUser(authUser);
                                        ae.setAuthority(e);
                                        return ae;
                                    }
                            ).toList()
                    );
                    authUserRepository.createUser(authUser);

                    UserDataEntity userdataUser = new UserDataEntity();
                    userdataUser.setUsername(username);
                    userdataUser.setFirstname(RandomDataUtils.randomName());
                    userdataUser.setLastname(RandomDataUtils.randomSurname());

                    return UserJson.fromEntity(
                            udUserRepository.createUserData(userdataUser)
                    );
                }
        ));
    }
}
