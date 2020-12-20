import logging
import requests
from bs4 import BeautifulSoup
from telegram import Update
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters, CallbackContext
import os
PORT = int(os.environ.get('PORT', 5000))
url = 'http://bitsindri.ac.in'

# Enable logging
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO
)

logger = logging.getLogger(__name__)
TOKEN = "1460405964:AAFmwhebduQd67wyNeqzSfnHqlLAOUa3xlY"


# Define a few command handlers. These usually take the two arguments update and
# context. Error handlers also receive the raised TelegramError object in error.
def start(update: Update, context: CallbackContext) -> None:
    """Send a message when the command /start is issued."""
    update.message.reply_text('''Hi This bot was meant to keep us updated and that you don't miss an opportunity
    You can get past 10 notification from BIT's Official website using /get command
    Also you will get an update through this bot as soon as something new is posted on the website
    Use /help command anytime to know the possibilities what this bot can do
    Thanks, This bot is maintined by HnCC,BIT Sindri''')


def help_command(update: Update, context: CallbackContext) -> None:
    """Send a message when the command /help is issued."""
    help_text = '''Hi This bot was meant to keep us updated and that you don't miss an opportunity. You can get past 10 notification from BIT's Official website using /get command. Also you will get an update through this bot as soon as something new is posted on the website. Use /help command anytime to know the possibilities what this bot can do.\nThanks, This bot is maintined by HnCC,BIT Sindri'''
    update.message.reply_text(help_text)


def url_formatting(directory_url: str):
    http_string = url+directory_url.replace(" ", "%20")
    return http_string


def stop_sending(update: Update, context: CallbackContext) -> None:
    update.message.reply_text(
        "Removed from database...PENDING TASK TOBE COMPLETED")


def get(update: Update, context: CallbackContext) -> None:
    reply = None
    # update.message.reply_text("Hey")
    if update.message.text is not None:
        reply = ""
        r = requests.get(url)
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36(KHTML, like Gecko) Chrome/41.0.2272.0 Safari/537.36'}
        r = requests.get(url, headers=headers)
        htmlContent = r.content
        soup = BeautifulSoup(htmlContent, 'html.parser')
        list_ul = soup.find_all('ul', class_="nav menu menu-treemenu")
        s: str
        for i in range(1, 11):
            # reply += list_ul[0].contents[i].find('span').getText()+"\n\n"
            s = list_ul[0].contents[i].find('a').get('href')
            if s[0] == '/':
                s = url_formatting(s)
            reply += list_ul[0].contents[i].find('span').getText(
            ) + "\n"+s+"\n\n"
        update.message.reply_text(reply)


def action_message(update: Update, context: CallbackContext) -> None:
    update.message.reply_text("""Try /get or /help""")


def main():
    """Start the bot."""
    # Create the Updater and pass it your bot's token.
    # Make sure to set use_context=True to use the new context based callbacks
    # Post version 12 this will no longer be necessary
    updater = Updater(TOKEN, use_context=True)

    # Get the dispatcher to register handlers

    dispatcher = updater.dispatcher

    # on different commands - answer in Telegram
    dispatcher.add_handler(CommandHandler("start", start))
    dispatcher.add_handler(CommandHandler("help", help_command))
    dispatcher.add_handler(CommandHandler("get", get))
    dispatcher.add_handler(CommandHandler("stopsending", help_command))

    # on noncommand i.e message - choose action @action_message
    dispatcher.add_handler(MessageHandler(
        Filters.text & ~Filters.command, action_message))

    # Start the Bot
    updater.start_polling()
    # updater.start_webhook(listen="0.0.0.0",
    #                       port=int(PORT),
    #                       url_path=TOKEN)
    # updater.bot.setWebhook(
    #     'https://obscure-savannah-01072.herokuapp.com/' + TOKEN)
    # updater.start_
    # Run the bot until you press Ctrl-C or the process receives SIGINT,
    # SIGTERM or SIGABRT. This should be used most of the time, since
    # start_polling() is non-blocking and will stop the bot gracefully.
    updater.idle()


if __name__ == '__main__':
    main()
