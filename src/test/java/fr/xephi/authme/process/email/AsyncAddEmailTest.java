package fr.xephi.authme.process.email;

import fr.xephi.authme.TestHelper;
import fr.xephi.authme.data.auth.PlayerAuth;
import fr.xephi.authme.data.auth.PlayerCache;
import fr.xephi.authme.datasource.DataSource;
import fr.xephi.authme.message.MessageKey;
import fr.xephi.authme.process.register.RegisterSecondaryArgument;
import fr.xephi.authme.process.register.RegistrationType;
import fr.xephi.authme.service.CommonService;
import fr.xephi.authme.service.ValidationService;
import fr.xephi.authme.settings.properties.RegistrationSettings;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link AsyncAddEmail}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncAddEmailTest {

    @InjectMocks
    private AsyncAddEmail asyncAddEmail;

    @Mock
    private Player player;

    @Mock
    private DataSource dataSource;

    @Mock
    private PlayerCache playerCache;

    @Mock
    private CommonService service;

    @Mock
    private ValidationService validationService;

    @BeforeClass
    public static void setUp() {
        TestHelper.setupLogger();
    }

    @Test
    public void shouldAddEmail() {
        // given
        String email = "my.mail@example.org";
        given(player.getName()).willReturn("testEr");
        given(playerCache.isAuthenticated("tester")).willReturn(true);
        PlayerAuth auth = mock(PlayerAuth.class);
        given(auth.getEmail()).willReturn(null);
        given(playerCache.getAuth("tester")).willReturn(auth);
        given(dataSource.updateEmail(any(PlayerAuth.class))).willReturn(true);
        given(validationService.validateEmail(email)).willReturn(true);
        given(validationService.isEmailFreeForRegistration(email, player)).willReturn(true);

        // when
        asyncAddEmail.addEmail(player, email);

        // then
        verify(dataSource).updateEmail(auth);
        verify(service).send(player, MessageKey.EMAIL_ADDED_SUCCESS);
        verify(auth).setEmail(email);
        verify(playerCache).updatePlayer(auth);
    }

    @Test
    public void shouldReturnErrorWhenMailCannotBeSaved() {
        // given
        String email = "my.mail@example.org";
        given(player.getName()).willReturn("testEr");
        given(playerCache.isAuthenticated("tester")).willReturn(true);
        PlayerAuth auth = mock(PlayerAuth.class);
        given(auth.getEmail()).willReturn(null);
        given(playerCache.getAuth("tester")).willReturn(auth);
        given(dataSource.updateEmail(any(PlayerAuth.class))).willReturn(false);
        given(validationService.validateEmail(email)).willReturn(true);
        given(validationService.isEmailFreeForRegistration(email, player)).willReturn(true);

        // when
        asyncAddEmail.addEmail(player, email);

        // then
        verify(dataSource).updateEmail(auth);
        verify(service).send(player, MessageKey.ERROR);
    }

    @Test
    public void shouldNotAddMailIfPlayerAlreadyHasEmail() {
        // given
        given(player.getName()).willReturn("my_Player");
        given(playerCache.isAuthenticated("my_player")).willReturn(true);
        PlayerAuth auth = mock(PlayerAuth.class);
        given(auth.getEmail()).willReturn("another@mail.tld");
        given(playerCache.getAuth("my_player")).willReturn(auth);

        // when
        asyncAddEmail.addEmail(player, "some.mail@example.org");

        // then
        verify(service).send(player, MessageKey.USAGE_CHANGE_EMAIL);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

    @Test
    public void shouldNotAddMailIfItIsInvalid() {
        // given
        String email = "invalid_mail";
        given(player.getName()).willReturn("my_Player");
        given(playerCache.isAuthenticated("my_player")).willReturn(true);
        PlayerAuth auth = mock(PlayerAuth.class);
        given(auth.getEmail()).willReturn(null);
        given(playerCache.getAuth("my_player")).willReturn(auth);
        given(validationService.validateEmail(email)).willReturn(false);

        // when
        asyncAddEmail.addEmail(player, email);

        // then
        verify(service).send(player, MessageKey.INVALID_EMAIL);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

    @Test
    public void shouldNotAddMailIfAlreadyUsed() {
        // given
        String email = "player@mail.tld";
        given(player.getName()).willReturn("TestName");
        given(playerCache.isAuthenticated("testname")).willReturn(true);
        PlayerAuth auth = mock(PlayerAuth.class);
        given(auth.getEmail()).willReturn(null);
        given(playerCache.getAuth("testname")).willReturn(auth);
        given(validationService.validateEmail(email)).willReturn(true);
        given(validationService.isEmailFreeForRegistration(email, player)).willReturn(false);

        // when
        asyncAddEmail.addEmail(player, email);

        // then
        verify(service).send(player, MessageKey.EMAIL_ALREADY_USED_ERROR);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

    @Test
    public void shouldShowLoginMessage() {
        // given
        given(player.getName()).willReturn("Username12");
        given(playerCache.isAuthenticated("username12")).willReturn(false);
        given(dataSource.isAuthAvailable("Username12")).willReturn(true);

        // when
        asyncAddEmail.addEmail(player, "test@mail.com");

        // then
        verify(service).send(player, MessageKey.LOGIN_MESSAGE);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

    @Test
    public void shouldShowEmailRegisterMessage() {
        // given
        given(player.getName()).willReturn("user");
        given(playerCache.isAuthenticated("user")).willReturn(false);
        given(dataSource.isAuthAvailable("user")).willReturn(false);
        given(service.getProperty(RegistrationSettings.REGISTRATION_TYPE)).willReturn(RegistrationType.EMAIL);
        given(service.getProperty(RegistrationSettings.REGISTER_SECOND_ARGUMENT)).willReturn(RegisterSecondaryArgument.NONE);

        // when
        asyncAddEmail.addEmail(player, "test@mail.com");

        // then
        verify(service).send(player, MessageKey.REGISTER_EMAIL_NO_REPEAT_MESSAGE);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

    @Test
    public void shouldShowRegularRegisterMessage() {
        // given
        given(player.getName()).willReturn("user");
        given(playerCache.isAuthenticated("user")).willReturn(false);
        given(dataSource.isAuthAvailable("user")).willReturn(false);
        given(service.getProperty(RegistrationSettings.REGISTRATION_TYPE)).willReturn(RegistrationType.PASSWORD);
        given(service.getProperty(RegistrationSettings.REGISTER_SECOND_ARGUMENT)).willReturn(RegisterSecondaryArgument.CONFIRMATION);

        // when
        asyncAddEmail.addEmail(player, "test@mail.com");

        // then
        verify(service).send(player, MessageKey.REGISTER_MESSAGE);
        verify(playerCache, never()).updatePlayer(any(PlayerAuth.class));
    }

}
