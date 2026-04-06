package simplexity.simplehomes.configs;

public enum LocaleMessage {
    MUST_BE_PLAYER("errors.must-be-player", "<red>Sorry, only a player can run that command!</red>"),
    PROVIDE_HOME_NAME("errors.provide-home-name", "<gray>Please provide a home name</gray>"),
    HOME_ALREADY_EXISTS("errors.home-already-exists", "<red>Sorry, you already have a home with that name!</red> <gray>Use <yellow>/sethome <name> -o</yellow> to overwrite a home</gray>"),
    HOME_NOT_FOUND("errors.home-not-found", "<red>Home <yellow><name></yellow> not found</red>"),
    NULL_HOME("errors.null-home", "<red>Home '<name>' does not exist</red>"),
    CANNOT_SET_MORE_HOMES("errors.cannot-set-more-homes", "<red>You have already set <dark_red><bold><value></bold></dark_red> homes- you cannot set any more. Delete one of your current homes to set a new one.</red>"),
    CANNOT_USE_COMMAND("errors.cannot-use-command", "<red>You currently have more than <dark_red><bold><value></bold></dark_red> homes. In order to use this command, you must first delete some homes.</red>"),
    ERROR_HAS_OCCURRED("errors.error-has-occurred", "<red>An error has occurred while running this command. Please contact the server staff to let them know (-SimpleHomes Plugin)</red>"),
    NO_PERMISSION("errors.no-permission", "<red>You do not have permission to use <value></red>"),
    INSERT_NAME("inserts.name", "<yellow><name></yellow>"),
    INSERT_BED_NAME("inserts.bed-name", "<dark_gray><name></dark_gray>"),
    INSERT_WORLD("inserts.world", "<yellow><world></yellow>"),
    INSERT_X_LOC("inserts.x-loc", "<yellow><x-loc>x</yellow>,"),
    INSERT_Y_LOC("inserts.y-loc", "<yellow><y-loc>y</yellow>,"),
    INSERT_Z_LOC("inserts.z-loc", "<yellow><z-loc>z</yellow>"),
    INSERT_OVERRIDE("inserts.override", "<gray>You can type <yellow>/home <name> -o</yellow> in order to teleport anyways</gray>"),
    LIST_HEADER("messages.list-header", "<white><bold>[</bold><aqua>Homes</aqua><bold>]</bold></white>"),
    LIST_ITEM("messages.list-item", "<gray>  - <name> in <world> at <x-loc> <y-loc> <z-loc>"),
    LIST_NO_HOMES("messages.list-no-homes", "<gray>You have no homes set</gray>"),
    HOME_SET("messages.home-set", "<green>Home <name> has been set</green>"),
    HOME_DELETED("messages.home-deleted", "<gray>Home <name> in <world> at <x-loc> <y-loc> <z-loc> has been deleted</gray>"),
    HOME_TELEPORTED("messages.home-teleported", "<gray>Teleported successfully to <yellow><name></yellow></gray>"),
    PLUGIN_RELOADED("messages.plugin-reloaded", "<gold>SimpleHomes has been reloaded</gold>"),
    BLACKLISTED_WARNING("warnings.blacklisted", "<red>There is a <block> blocking your teleport location.</red> "),
    VOID_WARNING("warnings.void", "<red>Your home is currently above air.</red> "),
    FIRE_WARNING("warnings.fire", "<red>Your home is currently on fire</red> "),
    BLOCKS_WARNING("warnings.blocks", "<red>Your home is currently encased in blocks</red> "),
    LAVA_WARNING("warnings.lava", "<red>Your home is currently inside of lava</red> "),
    WATER_WARNING("warnings.water", "<red>Your home is currently under water</red> "),
    UNSUPPORTED_DESTRUCTIVE("console.unsupported-destructive", """
                <reset>------------------------------------------------------------------------
                <gold>You are about to run: <yellow>/<command>
                
                <red><bold>THIS ACTION IS DESTRUCTIVE AND WILL OVERWRITE EXISTING HOMES IN SIMPLEHOMES.
                <red><bold>THIS ACTION IS NOT SUPPORTED AND MAY CORRUPT SAVE DATA, USE AT YOUR OWN RISK.
                
                <gold>Please confirm this command using <yellow>/<command> confirm
                <reset>-----------------------------------------------------------------------------------------"""),
    IMPORT_HELP("console.import-help", """
                ------------------Import Homes Help------------------------------------
                
                <gold>This command is <red>DESTRUCTIVE</red> and will overwrite homes already set on SimpleHomes
                <gold>This command is <red>UNSUPPORTED</red> and may <red>CORRUPT SAVE DATA</red>.
                              <red>>>>>> USE AT YOUR OWN RISK <<<<<</red>.
                
                <aqua>Usage:</aqua> <yellow>/importhomes <plugin> [username]
                <yellow>[username] <aqua>is an optional argument to import specifically that user's homes.
                
                <green>Valid Plugins: Essentials
                
                <gray>Not all home plugins are supported, to add support submit an issue on GitHub - https://github.com/Simplexity-Development/SimpleHomes/issues
                <reset>-----------------------------------------------------------------------------------------"""),
    IMPORT_NOT_ENOUGH_ARGS("console.import-not-enough-args", """
                <red>Not enough arguments.</red>
                <gray>Try <yellow>importhomes help</yellow></gray>"""),
    ONLY_CONSOLE("console.only-console", "<red>This command can only be used by the Console."),
    CANNOT_CONFIRM("console.cannot-confirm", "<red>No command was executed recently to confirm."),
    TIMED_OUT("console.timed-out", "<red>Command timed out, please run again."),
    NO_VALID_PLUGIN("console.no-valid-plugin", "<red>No valid plugin was found."),
    ESSENTIALS_NOT_EXIST("console.essentials.not-exist", "<gold>There is no /plugins/Essentials/userdata folder!"),
    NOTHING_INSIDE_FOLDER("console.essentials.nothing-inside", "<gold>There is nothing inside of the /plugins/Essentials/userdata folder."),
    PLAYER_NOT_EXIST("console.essentials.player-not-exist", """
                <gold>This player does not exist or does not have an Essentials/userdata file!"),
                            <yellow>Player Name: </yellow><name>
                <yellow>Retrieved UUID: </yellow><uuid>
                <yellow>Searched File: </yellow><file>"""),
    IMPORT_COMPLETE("console.import-finished", "<green>Import complete</green>"),
    IMPORTED_HOMES("console.saved-homes", "<yellow>Imported all homes for <name></yellow>"),
    PLEASE_WAIT("delay.please-wait", "<green>Teleporting! Please wait <value> seconds!</green>"),
    YOU_MOVED("delay.you-moved", "<gray>You moved, teleportation has been cancelled</gray>");

    private final String path;
    private String message;

    LocaleMessage(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
