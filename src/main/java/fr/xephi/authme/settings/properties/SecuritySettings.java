package fr.xephi.authme.settings.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import fr.xephi.authme.security.HashAlgorithm;
import fr.xephi.authme.settings.EnumSetProperty;

import java.util.List;
import java.util.Set;

import static ch.jalu.configme.properties.PropertyInitializer.newLowercaseListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class SecuritySettings implements SettingsHolder {

    @Comment({"Stop the server if we can't contact the sql database",
        "Take care with this, if you set this to false,",
        "AuthMe will automatically disable and the server won't be protected!"})
    public static final Property<Boolean> STOP_SERVER_ON_PROBLEM =
        newProperty("Security.SQLProblem.stopServer", true);

    @Comment("Remove passwords from console?")
    public static final Property<Boolean> REMOVE_PASSWORD_FROM_CONSOLE =
        newProperty("Security.console.removePassword", true);

    @Comment("Copy AuthMe log output in a separate file as well?")
    public static final Property<Boolean> USE_LOGGING =
        newProperty("Security.console.logConsole", true);

    @Comment("Enable captcha when a player uses wrong password too many times")
    public static final Property<Boolean> USE_CAPTCHA =
        newProperty("Security.captcha.useCaptcha", false);

    @Comment("Max allowed tries before a captcha is required")
    public static final Property<Integer> MAX_LOGIN_TRIES_BEFORE_CAPTCHA =
        newProperty("Security.captcha.maxLoginTry", 5);

    @Comment("Captcha length")
    public static final Property<Integer> CAPTCHA_LENGTH =
        newProperty("Security.captcha.captchaLength", 5);

    @Comment("Minimum length of password")
    public static final Property<Integer> MIN_PASSWORD_LENGTH =
        newProperty("settings.security.minPasswordLength", 5);

    @Comment("Maximum length of password")
    public static final Property<Integer> MAX_PASSWORD_LENGTH =
        newProperty("settings.security.passwordMaxLength", 30);

    @Comment({
        "This is a very important option: every time a player joins the server,",
        "if they are registered, AuthMe will switch him to unLoggedInGroup.",
        "This should prevent all major exploits.",
        "You can set up your permission plugin with this special group to have no permissions,",
        "or only permission to chat (or permission to send private messages etc.).",
        "The better way is to set up this group with few permissions, so if a player",
        "tries to exploit an account they can do only what you've defined for the group.",
        "After, a logged in player will be moved to his correct permissions group!",
        "Please note that the group name is case-sensitive, so 'admin' is different from 'Admin'",
        "Otherwise your group will be wiped and the player will join in the default group []!",
        "Example unLoggedinGroup: NotLogged"
    })
    public static final Property<String> UNLOGGEDIN_GROUP =
        newProperty("settings.security.unLoggedinGroup", "unLoggedinGroup");

    @Comment({
        "Possible values: SHA256, BCRYPT, BCRYPT2Y, PBKDF2, SALTEDSHA512, WHIRLPOOL,",
        "MYBB, IPB3, PHPBB, PHPFUSION, SMF, XENFORO, XAUTH, JOOMLA, WBB3, WBB4, MD5VB,",
        "PBKDF2DJANGO, WORDPRESS, ROYALAUTH, CUSTOM (for developers only). See full list at",
        "https://github.com/AuthMe/AuthMeReloaded/blob/master/docs/hash_algorithms.md"
    })
    public static final Property<HashAlgorithm> PASSWORD_HASH =
        newProperty(HashAlgorithm.class, "settings.security.passwordHash", HashAlgorithm.SHA256);

    @Comment("Salt length for the SALTED2MD5 MD5(MD5(password)+salt)")
    public static final Property<Integer> DOUBLE_MD5_SALT_LENGTH =
        newProperty("settings.security.doubleMD5SaltLength", 8);

    @Comment({
        "If a password check fails, AuthMe will also try to check with the following hash methods.",
        "Use this setting when you change from one hash method to another.",
        "AuthMe will update the password to the new hash. Example:",
        "legacyHashes:",
        "- 'SHA1'"
    })
    public static final Property<Set<HashAlgorithm>> LEGACY_HASHES =
        new EnumSetProperty<>(HashAlgorithm.class, "settings.security.legacyHashes");

    @Comment("Number of rounds to use if passwordHash is set to PBKDF2. Default is 10000")
    public static final Property<Integer> PBKDF2_NUMBER_OF_ROUNDS =
        newProperty("settings.security.pbkdf2Rounds", 10000);

    @Comment({"Prevent unsafe passwords from being used; put them in lowercase!",
        "You should always set 'help' as unsafePassword due to possible conflicts.",
        "unsafePasswords:",
        "- '123456'",
        "- 'password'",
        "- 'help'"})
    public static final Property<List<String>> UNSAFE_PASSWORDS =
        newLowercaseListProperty("settings.security.unsafePasswords", "123456", "password", "qwerty", "12345", "54321", "123456789", "help");

    @Comment("Tempban a user's IP address if they enter the wrong password too many times")
    public static final Property<Boolean> TEMPBAN_ON_MAX_LOGINS =
        newProperty("Security.tempban.enableTempban", false);

    @Comment("How many times a user can attempt to login before their IP being tempbanned")
    public static final Property<Integer> MAX_LOGIN_TEMPBAN =
        newProperty("Security.tempban.maxLoginTries", 10);

    @Comment({"The length of time a IP address will be tempbanned in minutes",
        "Default: 480 minutes, or 8 hours"})
    public static final Property<Integer> TEMPBAN_LENGTH =
        newProperty("Security.tempban.tempbanLength", 480);

    @Comment({"How many minutes before resetting the count for failed logins by IP and username",
        "Default: 480 minutes (8 hours)"})
    public static final Property<Integer> TEMPBAN_MINUTES_BEFORE_RESET =
        newProperty("Security.tempban.minutesBeforeCounterReset", 480);

    @Comment("Number of characters a recovery code should have (0 to disable)")
    public static final Property<Integer> RECOVERY_CODE_LENGTH =
        newProperty("Security.recoveryCode.length", 8);

    @Comment("How many hours is a recovery code valid for?")
    public static final Property<Integer> RECOVERY_CODE_HOURS_VALID =
        newProperty("Security.recoveryCode.validForHours", 4);

    private SecuritySettings() {
    }

}
