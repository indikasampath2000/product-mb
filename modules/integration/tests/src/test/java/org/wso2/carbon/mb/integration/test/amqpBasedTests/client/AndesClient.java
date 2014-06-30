package org.wso2.carbon.mb.integration.test.amqpBasedTests.client;

import org.wso2.carbon.mb.integration.test.amqpBasedTests.queue.QueueMessageBrowser;
import org.wso2.carbon.mb.integration.test.amqpBasedTests.queue.QueueMessageReceiver;
import org.wso2.carbon.mb.integration.test.amqpBasedTests.queue.QueueMessageSender;
import org.wso2.carbon.mb.integration.test.amqpBasedTests.topic.TopicMessagePublisher;
import org.wso2.carbon.mb.integration.test.amqpBasedTests.topic.TopicMessageReceiver;
import org.wso2.carbon.mb.integration.test.amqpBasedTests.utils.AndesClientOutputParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AndesClient {

    private AtomicInteger queueMessageCounter = new AtomicInteger(0);
    private AtomicInteger topicMessageCounter = new AtomicInteger(0);
    public String filePathToWriteReceivedMessages = System.getProperty("resources.dir") + File.separator +"receivedMessages.txt";

    private String mode = "";

    private String hostInformation = "localhost:5673";
    private String destinations = "topic:myTopic";
    private String printNumberOfMessagesPerAsString = "1";
    private String isToPrintEachMessageAsString = "false";
    private String numOfSecondsToRunAsString = "200";
    private String messageCountAsString = "100";
    private String numberOfThreadsAsString = "1";
    private String parameters = "listener=true,ackMode=1,delayBetweenMsg=0,stopAfter=100";
    private String connectionString = "";

    private String analyticOperation = "";
    private String numberOfMessagesExpectedForAnalysis = "";

    private List<QueueMessageReceiver> queueListeners = new ArrayList<QueueMessageReceiver>();
    private List<TopicMessageReceiver> topicListeners = new ArrayList<TopicMessageReceiver>();
    private List<QueueMessageSender> queueMessageSenders = new ArrayList<QueueMessageSender>();
    private List<TopicMessagePublisher> topicMessagePublishers = new ArrayList<TopicMessagePublisher>();

    public AndesClient(String mode, String hostInformation, String destinations, String printNumberOfMessagesPerAsString, String isToPrintEachMessageAsString, String numOfSecondsToRunAsString, String messageCountAsString, String numberOfThreadsAsString, String parameters, String connectionString) {
        this.mode = mode;
        this.hostInformation = hostInformation;
        this.destinations = destinations;
        this.printNumberOfMessagesPerAsString = printNumberOfMessagesPerAsString;
        this.isToPrintEachMessageAsString = isToPrintEachMessageAsString;
        this.numOfSecondsToRunAsString = numOfSecondsToRunAsString;
        this.messageCountAsString = messageCountAsString;
        this.numberOfThreadsAsString = numberOfThreadsAsString;
        this.parameters = parameters;
        this.connectionString = connectionString;
    }

    public AndesClient(String mode, String analiticOperation, String numberOfMessagesExpeactedForAnalysis) {
        this.mode = mode;
        this.analyticOperation = analiticOperation;
        this.numberOfMessagesExpectedForAnalysis = numberOfMessagesExpeactedForAnalysis;
    }

    public void startWorking() {

        queueMessageCounter.set(0);
        topicMessageCounter.set(0);
        //mode: send/receive/browse/purge/analyse
        //hosts host1:port,host2:port;
        //destinations queue:q1,q2,q3|topic:t1,t2,t3;
        //printNumberOfMessagesPer 100
        //isToPrintEachMessage false
        //numOfSecondsToRun
        //count 1000;
        //numOfThreads 5;
        //params listener=true,durable=false,subscriptionID=sub1,file="",ackMode=AUTO,delayBetweenMsg=200,stopAfter=12,ackAfterEach=300,commitAfterEach=300,rollbackAfterEach=400,unsubscribeAfter=500 (all parameters are optional)
        //connectionString (optional)

        //hardCode username and password
        String userName = "admin";
        String passWord = "admin";

        //String mode = "receive";

        if (mode.equals("send") || mode.equals("receive")) {


/*            String hostInformation ="localhost:5673";
            String destinations = "topic:myTopic";
            String printNumberOfMessagesPerAsString = "1";
            String isToPrintEachMessageAsString = "false";
            String numOfSecondsToRunAsString = "200";
            String messageCountAsString = "100";
            String numberOfThreadsAsString = "1";
            String parameters = "listener=true,ackMode=1,delayBetweenMsg=0,stopAfter=100";
            String connectionString = "";*/

            //print input information
            System.out.println("hosts==>" + hostInformation);
            System.out.println("destinations==>" + destinations);
            System.out.println("mode==>" + mode);
            System.out.println("number of seconds to run==>" + numOfSecondsToRunAsString);
            System.out.println("message count==>" + messageCountAsString);
            System.out.println("num of threads==>" + numberOfThreadsAsString);
            System.out.println("print message count per==>" + printNumberOfMessagesPerAsString);
            System.out.println("is to print message==>" + isToPrintEachMessageAsString);
            System.out.println("parameters==>" + parameters);
            System.out.println("connectionString(optional)==>" + connectionString);


            //decode the host information
            String[] hostsAndPorts = hostInformation.split(",");

            //decode destinations
            String[] destinationList = destinations.split("\\|");

            String[] queues = null;
            String[] topics = null;

            for (int count = 0; count < destinationList.length; count++) {
                String destinationString = destinationList[count];
                if (destinationString.startsWith("queue")) {
                    String queueString = destinationString.split(":")[1];
                    queues = queueString.split(",");

                } else if (destinationString.startsWith("topic")) {
                    String topicString = destinationString.split(":")[1];
                    topics = topicString.split(",");
                }
            }


            //get mode of operation
            String modeOfOperation = mode;

            //get numberOfSecondsToRun
            int numberOfSecondsToWaitForMessages = Integer.MAX_VALUE;
            if (numOfSecondsToRunAsString != null && !numOfSecondsToRunAsString.equals("")) {
                numberOfSecondsToWaitForMessages = Integer.parseInt(numOfSecondsToRunAsString);
            }

            //decode message count
            int messageCount = 1;
            if (messageCountAsString != null && !messageCountAsString.equals("")) {
                messageCount = Integer.parseInt(messageCountAsString);
            }
            if (topics == null || topics.length == 0) {
                topicMessageCounter.set(messageCount);
            }
            if (queues == null || queues.length == 0) {
                queueMessageCounter.set(messageCount);
            }

            //decode thread count
            int numberOfThreads = 1;
            if (numberOfThreadsAsString != null && !numberOfThreadsAsString.equals("")) {
                numberOfThreads = Integer.parseInt(numberOfThreadsAsString);
            }

            //decode how often we should print message count
            int printNumberOfMessagesPer = 1;
            if (printNumberOfMessagesPerAsString != null && !printNumberOfMessagesPerAsString.equals("")) {
                printNumberOfMessagesPer = Integer.parseInt(printNumberOfMessagesPerAsString);
            }

            //decode if we should print each message
            boolean isToPrintEachMessage = false;
            if (isToPrintEachMessageAsString != null && !isToPrintEachMessageAsString.equals("")) {
                isToPrintEachMessage = Boolean.parseBoolean(isToPrintEachMessageAsString);
            }


            //decode parameters
            boolean isToUseListerner = true;
            boolean isDurable = false;
            String subscriptionID = "";
            String filePath = null;
            //default AUTO_ACK
            int ackMode = 1;
            int delayBetWeenMessages = 0;
            int stopAfter = Integer.MAX_VALUE;
            int ackAfterEach = Integer.MAX_VALUE;
            int commitAfterEach = Integer.MAX_VALUE;
            int rollbackAfterEach = Integer.MAX_VALUE;
            int unsubscribeAfter = Integer.MAX_VALUE;

            String[] parameterStrings = parameters.split(",");
            for (int count = 0; count < parameterStrings.length; count++) {
                String[] keyValues = parameterStrings[count].split("=");
                String key = keyValues[0];
                String value = keyValues[1];
                if (key.equals("")) {

                } else if (key.equals("listener")) {
                    isToUseListerner = Boolean.parseBoolean(value);
                } else if (key.equals("durable")) {
                    isDurable = Boolean.parseBoolean(value);
                } else if (key.equals("subscriptionID")) {
                    subscriptionID = value;
                } else if (key.equals("file")) {
                    filePath = value;
                } else if (key.equals("ackMode")) {
                    ackMode = Integer.parseInt(value);
                } else if (key.equals("delayBetweenMsg")) {
                    delayBetWeenMessages = Integer.parseInt(value);
                } else if (key.equals("stopAfter")) {
                    stopAfter = Integer.parseInt(value);
                } else if (key.equals("ackAfterEach")) {
                    ackAfterEach = Integer.parseInt(value);
                } else if (key.equals("commitAfterEach")) {
                    commitAfterEach = Integer.parseInt(value);
                } else if (key.equals("rollbackAfterEach")) {
                    rollbackAfterEach = Integer.parseInt(value);
                } else if (key.equals("unsubscribeAfter")) {
                    unsubscribeAfter = Integer.parseInt(value);
                }
            }

            //*************************************************************************************************8


            //according to information start threads
            if (modeOfOperation.equals("send")) {
                System.out.println("===============Sending Messages====================");
                for (int count = 0; count < numberOfThreads; count++) {
                    int hostIndex = count % hostsAndPorts.length;
                    String host = hostsAndPorts[hostIndex].split(":")[0];
                    String port = hostsAndPorts[hostIndex].split(":")[1];
                    if (queues != null && queues.length != 0) {
                        int queueIndex = count % queues.length;
                        String queue = queues[queueIndex];

                        //start a queue sender
                        QueueMessageSender queueMessageSender = new QueueMessageSender(connectionString, host, port, userName, passWord,
                                queue, queueMessageCounter, messageCount, delayBetWeenMessages, filePath, printNumberOfMessagesPer, isToPrintEachMessage);
                        queueMessageSenders.add(queueMessageSender);
                        new Thread(queueMessageSender).start();

                    }
                    if (topics != null && topics.length != 0) {
                        int topicIndex = count % topics.length;
                        String topic = topics[topicIndex];

                        //start a topic sender
                        TopicMessagePublisher topicMessagePublisher = new TopicMessagePublisher(connectionString, host, port, userName, passWord,
                                topic, topicMessageCounter, messageCount, delayBetWeenMessages, filePath, printNumberOfMessagesPer, isToPrintEachMessage);
                        topicMessagePublishers.add(topicMessagePublisher);
                        new Thread(topicMessagePublisher).start();

                    }

                }

            } else if (modeOfOperation.equals("receive")) {

                System.out.println("===============Receiving Messages====================");

                File fileToWriteReceivedMessages = new File(filePathToWriteReceivedMessages);
                if (isToPrintEachMessage) {
                    try {
                        if (fileToWriteReceivedMessages.exists()) {
                            fileToWriteReceivedMessages.delete();
                        }
                        fileToWriteReceivedMessages.createNewFile();
                    } catch (IOException e) {
                        System.out.println("Cannot create a file to append receive messages" + e);
                    }
                }
                for (int count = 0; count < numberOfThreads; count++) {
                    int hostIndex = count % hostsAndPorts.length;
                    String host = hostsAndPorts[hostIndex].split(":")[0];
                    String port = hostsAndPorts[hostIndex].split(":")[1];
                    if (queues != null && queues.length != 0) {
                        int queueIndex = count % queues.length;
                        String queue = queues[queueIndex];

                        //start a queue receiver
                        QueueMessageReceiver queueMessageReceiver = new QueueMessageReceiver
                                (connectionString, host, port, userName, passWord, queue, ackMode, isToUseListerner, queueMessageCounter, delayBetWeenMessages,
                                        printNumberOfMessagesPer, isToPrintEachMessage, filePathToWriteReceivedMessages, stopAfter, ackAfterEach, commitAfterEach, rollbackAfterEach);
                        queueListeners.add(queueMessageReceiver);
                        new Thread(queueMessageReceiver).start();
                    }
                    if (topics != null && topics.length != 0) {
                        int topicIndex = count % topics.length;
                        String topic = topics[topicIndex];

                        //start a topic receiver
                        TopicMessageReceiver topicMessageReceiver = new TopicMessageReceiver(connectionString, host, port, userName, passWord, topic, isDurable,
                                subscriptionID, ackMode, isToUseListerner, topicMessageCounter, delayBetWeenMessages, printNumberOfMessagesPer, isToPrintEachMessage, filePathToWriteReceivedMessages, stopAfter, unsubscribeAfter, ackAfterEach, commitAfterEach, rollbackAfterEach);
                        topicListeners.add(topicMessageReceiver);
                        new Thread(topicMessageReceiver).start();

                    }
                }

            } else {
                System.out.println("ERROR: Unknown mode of operation");
            }
        } else if (mode.equals("browse")) {

            /*mode browse
              host Name And Port localhost:5672
              destination myQueue
              print Number Of Messages Per 100
              is To Print Each Message false
              */

            System.out.println("===============Browsing Messages====================");
            //only applies to queues

/*          String hostnameandPort =args[1];
            String destination = args[2];
            String printNumberOfMessagesPerAsString = args[3];
            String isToPrintEachMessageAsString = args[4];*/

            //print input information
            System.out.println("mode==>" + mode);
            System.out.println("host and port==>" + hostInformation);
            System.out.println("destination==>" + destinations);
            System.out.println("print message count per==>" + printNumberOfMessagesPerAsString);
            System.out.println("is to print message==>" + isToPrintEachMessageAsString);

            //decode host and port
            String hostName = hostInformation.split(":")[0];
            String port = hostInformation.split(":")[1];

            //decode how often we should print message count
            int printNumberOfMessagesPer = 1;
            if (printNumberOfMessagesPerAsString != null && !printNumberOfMessagesPerAsString.equals("")) {
                printNumberOfMessagesPer = Integer.parseInt(printNumberOfMessagesPerAsString);
            }

            //decode if we should print each message
            boolean isToPrintEachMessage = false;
            if (isToPrintEachMessageAsString != null && !isToPrintEachMessageAsString.equals("")) {
                isToPrintEachMessage = Boolean.parseBoolean(isToPrintEachMessageAsString);
            }

            int messageCount = browseQueue(hostName, port, userName, passWord, destinations, printNumberOfMessagesPer, isToPrintEachMessage);

            System.out.println("Browser Message Count: " + messageCount);


        } else if (mode.equals("purge")) {

            System.out.println("===============Purging Messages====================");

            //only applies to queues
/*          String hostnameandPort =args[1];
            String destination = args[2];*/

            //print input information
            System.out.println("mode==>" + mode);
            System.out.println("host and port==>" + hostInformation);

            //decode host and port
            String hostName = hostInformation.split(":")[0];
            String port = hostInformation.split(":")[1];

            //browse and get the message count
            int messageCount = browseQueue(hostName, port, userName, passWord, destinations, Integer.MAX_VALUE, false);

            //activate receiver for the queue(if specified more first one/ if specified more than one host get the first one)
            //start a queue receiver
            QueueMessageReceiver queueMessageReceiver = new QueueMessageReceiver
                    ("", hostName, port, userName, passWord, destinations, 1, true, queueMessageCounter, 0,
                            Integer.MAX_VALUE, false, "", messageCount, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            queueListeners.add(queueMessageReceiver);
            new Thread(queueMessageReceiver).start();

            topicMessageCounter.set(messageCount);

        } else if (mode.equals("analyse")) {

            /**
             * mode analyse
             *     printMessages/printDuplicates/printMissing/printSorted/checkOrder/clearFile
             */
            String operation = analyticOperation;
            String numberOfMessagesSentAsString = "";
            int numberOfMessagesSent = 0;

            if (operation.equals("printMissing")) {

                numberOfMessagesSentAsString = numberOfMessagesExpectedForAnalysis;
                numberOfMessagesSent = Integer.parseInt(numberOfMessagesSentAsString);
            }

            //print input information
            System.out.println("mode==>" + mode);
            System.out.println("operation==>" + operation);

            AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);

            if (operation.equals("printMessages")) {
                andesClientOutputParser.printMessagesMap();
            } else if (operation.equals("printDuplicates")) {
                andesClientOutputParser.printDuplicateMessages();
            } else if (operation.equals("printMissing")) {
                andesClientOutputParser.printMissingMessages(numberOfMessagesSent);
            } else if (operation.equals("printSorted")) {
                andesClientOutputParser.printMessagesSorted();
            } else if (operation.equals("checkOrder")) {
                System.out.println("MESSAGE ORDER PRESERVED: " + andesClientOutputParser.checkIfMessagesAreInOrder());
            } else if (operation.equals("clearFile")) {
                andesClientOutputParser.clearFile();
            } else {
                System.out.println("analyse operation not found...");
            }
        } else {
            System.out.println("ERROR: Unknown mode of operation");
        }
    }

    private int browseQueue(String host, String port, String userName, String password, String destination, int printNumberOfMessagesPer, boolean isToPrintMessage) {
        int messageCount = 0;
        //create a browser - one threaded app blocking
        //make an enumeration and get the count
        QueueMessageBrowser queueMessageBrowser = new QueueMessageBrowser(host, port, userName, password, destination, printNumberOfMessagesPer, isToPrintMessage);
        messageCount = queueMessageBrowser.getMessageCount();
        return messageCount;
    }


    //******************************************************************************************************************

    public void shutDownClient() {

        if (mode.equals("send")) {

            for (QueueMessageSender qSender : queueMessageSenders) {
                qSender.stopSending();
            }
            for (TopicMessagePublisher tPublisher : topicMessagePublishers) {
                tPublisher.stopPublishing();
            }

        } else if (mode.equals("receive")) {

            for (QueueMessageReceiver qListener : queueListeners) {
                qListener.stopListening();
            }
            for (TopicMessageReceiver tListener : topicListeners) {
                tListener.stopListening();
            }

        } else if (mode.equals("purge")) {

            for (QueueMessageReceiver qListener : queueListeners) {
                qListener.stopListening();
            }
        }

    }

    public int getReceivedqueueMessagecount() {
        return queueMessageCounter.get();
    }

    public int getReceivedTopicMessagecount() {
        return topicMessageCounter.get();
    }

    public Map<Long, Integer> checkIfMessagesAreDuplicated() {
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.checkIfMessagesAreDuplicated();
    }

    public boolean checkIfMessagesAreInOrder() {
        AndesClientOutputParser andesClientOutputParser = new AndesClientOutputParser(filePathToWriteReceivedMessages);
        return andesClientOutputParser.checkIfMessagesAreInOrder();
    }


}
