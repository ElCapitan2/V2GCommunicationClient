/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import v2gcommunication.commonclasses.encryption.DESDecode;
import v2gcommunication.commonclasses.encryption.DESEncode;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.sessions.Session;
import v2gcommunication.commonclasses.tasks.Task;
import v2gcommunication.commonclasses.transmission.ConvertJSON;
import v2gcommunication.commonclasses.transmission.ReceiveProtocol;
import v2gcommunication.commonclasses.transmission.TransmitProtocol;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.encryption.Decode;
import v2gcommunication.commonclasses.encryption.Encode;
import v2gcommunication.commonclasses.transmission.MessageBuilder;
import v2gcommunication.commonclasses.tasks.NewReceiveTask;
import v2gcommunication.commonclasses.tasks.NewSessionGetTasks;
import v2gcommunication.commonclasses.sessions.NewTransmitTask;
import v2gcommunication.commonclasses.transmission.ReadMessage;
import v2gcommunication.commonclasses.tasks.DataTransmitted;
import v2gcommunication.commonclasses.requests.NewSessionGetRequests;
import v2gcommunication.commonclasses.requests.RequestReceived;
import v2gcommunication.commonclasses.requests.RequestTransmitted;
import v2gcommunication.commonclasses.transmission.WriteMessage;

/**
 * SessionManagement for Client. Initializes and starts the session.
 * 
 * It holds an ExecutorService in which the session is executed.
 * 
 * It implements the interfaces {@code NewTransmitTask, AddRequest}
 * 
 * @author Alexander Forell
 */
public class SessionManagementClient implements NewTransmitTask, AddRequest{
    // Executor Service in which session is running
    private final ExecutorService executor;
    // Session for Server connection.
    private Session session;
    //MessageBuilder, readMessage, writeMessage to determine communication.
    private final MessageBuilder messageBuilder;
    private final ReadMessage readMessage;
    private final WriteMessage writeMessage;
    // Listener being informed when new Task is received
    private NewReceiveTask newReceiveTask;
    // Listener being informed when Task is completely transmitted
    private DataTransmitted taskTransmitted;
    // Listener being informed when Request has been Transmitted
    private RequestTransmitted requestTransmitted;
    // Listener being informed when new Request is received
    private RequestReceived requestReceived;
    // Listener being informed when new Session is started interface is used to get Tasks.
    private NewSessionGetTasks newSessionGetTasks;
     // Listener being informed when new Session is started interface is used to get Requests.
    private NewSessionGetRequests newSessionGetRequests;
    
    /**
     * constructor initiates classes.
     * Interfaces messageBuilder, readMessage and writeMessage are 
     * initialized with concrete implementations 
     * {@code ConverJSON, ReceiveProtocol, TransmitProtocol}
     * 
     */
    SessionManagementClient(){ 
        this.executor = Executors.newCachedThreadPool();
        this.messageBuilder = new ConvertJSON();
        this.readMessage = new ReceiveProtocol();
        this.writeMessage = new TransmitProtocol();
    }
    /**
     * Method takes a socket connection and, username and password and starts 
     * a new session. 
     * 
     * @param socket
     * @param username
     * @param password 
     */
    public void startSession(Socket socket, String username, String password){
        try {
            Encode enc = new DESEncode(); 
            Decode dec = new DESDecode();
            this.session = new Session(socket, executor, enc, dec, messageBuilder,
                    readMessage, writeMessage, newReceiveTask, taskTransmitted, 
                    requestTransmitted, requestReceived, newSessionGetTasks, 
                    newSessionGetRequests, username, password);
        } catch (IOException ex) {
            Logger.getLogger(SessionManagementClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Setter for listener being informed if new Task is received.
     * 
     * @param newReceiveTask 
     */
    public void setNewReceiveTaskListener(NewReceiveTask newReceiveTask){
        this.newReceiveTask = newReceiveTask;
    }
    
    /**
     * Setter for listener being informed if new Task is transmitted.
     * 
     * @param taskTransmitted 
     */
    public void setTaskTransmittedListener(DataTransmitted taskTransmitted){
        this.taskTransmitted = taskTransmitted;
    }
    
    /**
     * Setter for listener being informed if new Session is started.It is used 
     * to pass on existing buffered Tasks
     * 
     * @param newSessionGetTasks
     */
    public void setNewSessionGetTasksListener(NewSessionGetTasks newSessionGetTasks){
        this.newSessionGetTasks = newSessionGetTasks;
    }
    
    /**
     * Setter for listener being informed if new Session is started. It is used 
     * to pass on existing buffered Requests
     * 
     * @param newSession 
     */
    public void setNewSessionGetRequestsListener(NewSessionGetRequests newSession){
        this.newSessionGetRequests = newSession;
    }
    
    /**
     * Setter for listener being informed if Request was transmitted.
     * 
     * @param requestTransmitted 
     */
    public void setRequestTransmittedListener(RequestTransmitted requestTransmitted){
        this.requestTransmitted = requestTransmitted;
    }
    
    /**
     * Setter for listener being informed if a new Request was received. 
     * 
     * @param requestReceived 
     */
    public void setRequestReceivedListener(RequestReceived requestReceived){
        this.requestReceived = requestReceived;
    }
    
    /**
     * Adds TransmitTask to the session.
     * 
     * @param ta 
     */
    @Override
    public void addTransmitTask(Task ta) {
        if (session != null){
            session.addTransmitTask(ta);
        }
    }
    
    /**
     * Adds a Request to be transmitted to the session.
     * 
     * @param request 
     */
    @Override
    public void addRequest(Request request) {
        if (session != null){
            session.addRequest(request);
        }
    }
    
    
    
    
    
    
}
