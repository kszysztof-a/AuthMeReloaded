package fr.xephi.authme.command.executable.changepassword;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.cache.auth.PlayerCache;
import fr.xephi.authme.command.CommandService;
import fr.xephi.authme.command.PlayerCommand;
import fr.xephi.authme.output.MessageKey;
import fr.xephi.authme.settings.Settings;
import fr.xephi.authme.task.ChangePasswordTask;
import fr.xephi.authme.util.Wrapper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The command for a player to change his password with.
 */
public class ChangePasswordCommand extends PlayerCommand {

    @Override
    public void runCommand(Player player, List<String> arguments, CommandService commandService) {
        String oldPassword = arguments.get(0);
        String newPassword = arguments.get(1);

        String name = player.getName().toLowerCase();
        Wrapper wrapper = Wrapper.getInstance();
        final PlayerCache playerCache = wrapper.getPlayerCache();
        if (!playerCache.isAuthenticated(name)) {
            commandService.send(player, MessageKey.NOT_LOGGED_IN);
            return;
        }

        // Make sure the password is allowed
        String playerPassLowerCase = newPassword.toLowerCase();
        // TODO #308: Remove SQL keywords check
        if (playerPassLowerCase.contains("delete") || playerPassLowerCase.contains("where")
            || playerPassLowerCase.contains("insert") || playerPassLowerCase.contains("modify")
            || playerPassLowerCase.contains("from") || playerPassLowerCase.contains("select")
            || playerPassLowerCase.contains(";") || playerPassLowerCase.contains("null")
            || !playerPassLowerCase.matches(Settings.getPassRegex)) {
            commandService.send(player, MessageKey.PASSWORD_MATCH_ERROR);
            return;
        }
        if (playerPassLowerCase.equalsIgnoreCase(name)) {
            commandService.send(player, MessageKey.PASSWORD_IS_USERNAME_ERROR);
            return;
        }
        if (playerPassLowerCase.length() < Settings.getPasswordMinLen
            || playerPassLowerCase.length() > Settings.passwordMaxLength) {
            commandService.send(player, MessageKey.INVALID_PASSWORD_LENGTH);
            return;
        }
        if (!Settings.unsafePasswords.isEmpty() && Settings.unsafePasswords.contains(playerPassLowerCase)) {
            commandService.send(player, MessageKey.PASSWORD_UNSAFE_ERROR);
            return;
        }

        AuthMe plugin = AuthMe.getInstance();
        commandService.runTaskAsynchronously(new ChangePasswordTask(plugin, player, oldPassword, newPassword));
    }
}
