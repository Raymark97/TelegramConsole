# TelegramConsole
A Spigot plugin that allows Minecraft server owners to interact with their console through Telegram. It can **read the output** and **execute** commands remotely — these features alone make TelegramConsole the perfect companion when quick changes are needed for your server (you might as well forget about the classic console and switch to TelegramConsole altogether! 😁).

### Main features
- Read output
- Execute commands
- Format output appropriately (i.e. adequate usage of **bold**, *italic* and `code`)
- Smart multiple-lines detection (i.e. exceptions are stacked up to form a single message, rather than multiple ones due to line breaks)
- Forward exceptions to a separate and dedicated chat (extremely useful for keeping track of errors)

### Installation
1. Make sure you are running the latest release of TelegramCore, you can download it [here](http://)
2. Put the .jar file in the 'plugin' folder (both TelegramCore and TelegramConsole)
3. Start the server to generate config.yml
4. Create a group on Telegram and obtain its chat ID ([How to get the chatID](http://google.com))
5. Create a bot if you haven't already ([Ping @BotFather on Telegram](https://t.me/BotFather))
6. Insert the token of your bot in `plugins/TelegramConsole/config.yml`
7. Restart the server
8. Done! you can start interacting with your server console on Telegram

### Commands
Here's a list of commands avaiable (only for admins)

Command | Description
------------ | -------------
**In-game**
command1 | description
command2 | description
command3 | description
**Telegram**
tgmenu | opens settings' menu
command2 | description
command3 | description


### TODO List
- [x] Develop this plugin
