package com.dextrys.utils;

//import com.google.common.base.Function;
//import com.dextrys.config.Const;
//import org.openqa.selenium.TimeoutException;
//import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.annotation.Nullable;
import javax.mail.*;
//import javax.mail.search.FlagTerm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
//import java.util.concurrent.TimeUnit;

public class MailUtils {
    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);
    public static final String HOST = "webmail.dextrys.com";
    public static final int PORT = 993;
    public static final String USER = "ivakhrushev";
    public static final String MAILBOX = "SEMautomation";
    public static final String LOGIN = "SFO/" + USER + "/" + MAILBOX;
    public static final String PASSWORD = "Potato42*";

    public static Map<String, String> getLastMessage(){
        try {
            Store store = getStore();
            Map<String, String> result = readLastMessage(store);
            store.close();
            return result;
        } catch (Exception mex) {
            throw new RuntimeException("Unable to read email: " + mex.getMessage(), mex);
        }
    }
/*
    public static Map<String, String> waitForNewMessage(final int countBefore){
        Store store = null;
        try {
            store = getStore();
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect: " + e.getMessage(), e);
        }
        try {
            Message[] newMessages = new FluentWait<Store>(store)
                    .withTimeout(Const.MAIL_TIMEOUT, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .until(new Function<Store, Message[]>() {
                        @Nullable
                        public Message[] apply(Store store) {
                            try {
                                Folder inbox = store.getFolder("INBOX");
                                inbox.open(Folder.READ_ONLY);
                                int count = inbox.getMessageCount();
                                if (count > countBefore) {
                                    return inbox.getMessages(countBefore + 1, count);
                                } else {
                                    return null;
                                }
                            } catch (MessagingException e) {
                                return null;
                            }
                        }
                    });
            if (newMessages.length > 1) {
                throw new RuntimeException("More than one new e-mails were received.");
            } else {
                return toMap(newMessages[0]);
            }
        } catch (TimeoutException te) {
            try {
                store.close();
            } catch (MessagingException e) {
                logger.error("Unable to close the store");
            }
            throw new RuntimeException("No new messages were received", te);
        }
    }
*/
    /*
    Not robust implementation, because there are a lot of situations when message couldn't be marked as SEEN.
    Also it is not multi-account approach with existent resources - shared mailbox
     */
    /*
    @Deprecated
    public static Map<String, String> waitForNewMessage(){
        Store store = null;
        try {
            store = getStore();
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect: " + e.getMessage(), e);
        }
        try {
            Message[] uMessages = new FluentWait<Store>(store)
                    .withTimeout(Const.MAIL_TIMEOUT, TimeUnit.SECONDS)
                    .pollingEvery(3, TimeUnit.SECONDS)
                    .until(new Function<Store, Message[]>() {
                        @Nullable
                        public Message[] apply(Store store) {
                            try {
                                Folder inbox = store.getFolder("INBOX");
                                inbox.open(Folder.READ_ONLY);
                                // seems getNewMessagesCount always returns all messages
                                Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                                if (messages.length > 0) {
                                    return messages;
                                } else {
                                    return null;
                                }
                            } catch (MessagingException e) {
                                return null;
                            }
                        }
                    });
            if (uMessages.length > 1) {
                String markingResult = "\nMessages were marked SEEN, please restart the test.";
                try {
                    for (Message msg : uMessages) {
                        msg.setFlag(Flags.Flag.SEEN, true);
                        msg.saveChanges();
                    }
                } catch (MessagingException mex) {
                    markingResult = "\nUnable to mark messages as SEEN, please do it manually and restart the test.";
                }
                throw new RuntimeException("More than one new e-mails were found: " + uMessages.length + markingResult);
            }
        } catch (TimeoutException te) {
            try {
                store.close();
            } catch (MessagingException e) {
                logger.error("Unable to close the store");
            }
            throw new RuntimeException("No new messages were received", te);
        }
        try {
            Map<String, String> result = readLastMessage(store);
            store.close();
            return result;
        } catch (Exception mex) {
            throw new RuntimeException("Unable to read email: " + mex.getMessage(), mex);
        }
    }
*/
    private static Map<String, String> readLastMessage(Store store) throws MessagingException, IOException {
        Folder inbox = store.getFolder("INBOX");
        // message will be marked as SEEN after the content will be received
        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(inbox.getMessageCount());
        return toMap(msg);
    }

    private static Map<String, String> toMap(Message msg) {
        try {
            Map<String, String> result = new HashMap<String, String>();
            Address[] in = msg.getFrom();
            result.put("from", Arrays.deepToString(in));
            result.put("sent", msg.getSentDate().toString());
            result.put("subject", msg.getSubject());
            Object content = msg.getContent();
            if (content instanceof String) {
                result.put("content", (String) content);
            } else {
                Multipart mp = (Multipart) content;
                String text = "";
                for (int i = 0; i < mp.getCount(); i++) {
                    text += mp.getBodyPart(i);
                }
                result.put("content", text);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Unable to read message", e);
        }
    }

    public static int countMessages(){
        try {
            Store store = getStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            int count = inbox.getMessageCount();
            store.close();
            return count;
        } catch (Exception mex) {
            throw new RuntimeException("Unable to read email: " + mex.getMessage(), mex);
        }
    }

    public static int countNewMessages(){
        try {
            Store store = getStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            int count = inbox.getNewMessageCount();
            store.close();
            return count;
        } catch (Exception mex) {
            throw new RuntimeException("Unable to read email: " + mex.getMessage(), mex);
        }
    }

    private static Store getStore() throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getInstance(props, null);

        // disable all to use old-style imap format in connection
        // we need it to connect to shared mailbox
        props.put("mail.imaps.auth.plain.disable", "true");
        props.put("mail.imaps.auth.ntlm.disable", "true");
        props.put("mail.imaps.auth.gssapi.disable", "true");

        Store store = session.getStore("imaps");
        store.connect(HOST, PORT, LOGIN, PASSWORD);
        return store;
    }
}
