/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gCommunication.client.ServerConnection;
import v2gcommunication.commonclasses.encryption.SHA_1_hash;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to register a user on the
 * server
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class CreateUser implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run mehthod must be overridden as it is defined in TaskWorker.
     * 
     * Server will return a request indicating whether the vehicle 
     * was added successfully
     * 
     * This method reads the request and puts out true or false to the 
     * commandline.
     * 
     * It also calls the callback method {@code ServerConnection.userCreated}
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            boolean userCreated = false;
            String nameOfCreatedUser = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("userCreated") && para.parameterType.equals(boolean.class.getName())){
                    userCreated = Boolean.valueOf(para.parameterValue);
                }
                if (para.parameterName.equals("nameOfCreatedUser") && para.parameterType.equals(String.class.getName())){
                    nameOfCreatedUser = para.parameterValue;
                }
            }
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO CREATE USER");
            System.out.println("=====================");
            System.out.println("Request for creating user: " + nameOfCreatedUser + " processed.");
            System.out.println("Request processed successfully: " + userCreated);
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.userCreated();
        }
    }

    /**
     * Method defined in Taskworker is used for a the runMethod to reply using
     * Requests or Tasks.
     * 
     * @param requestProcessed  The Request which is currently handled
     * @param requestReply      The Request used to reply
     * @param taskReply         The Task used to reply.
     */
    @Override
    public void inputForRunnable(Request requestProcessed, AddRequest requestReply, StartTaskTransmit taskReply) {
        this.request = requestProcessed;
        this.requestReply = requestReply;
    }
    
    /**
     * 
     * Static method to generate a add user request. The returned 
     * Request will be added to a Session and will then be transmitted 
     * to the server.
     * 
     * @param username              username of user logged in
     * @param newUsername           username of user to be created
     * @param password              password for the user to be created
     * @param canModifyUsers        True if user can manage users
     * @param canModifyVehicles     True if user can manage vehicles
     * @param canSendRequest        True if user can send request
     * @return                      Request to be sent
     */
    public static Request getCreateUserRequest(String username, String newUsername ,String password, 
            boolean canModifyUsers, boolean canModifyVehicles, boolean canSendRequest){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        //Create Parameter set for username
        ParameterSet psUser = new ParameterSet();
        psUser.parameterName="newUsername";
        psUser.parameterType=String.class.getName();
        psUser.parameterValue=newUsername;
        psUser.timeStamp = null;
        parameters.add(psUser);
        
        //Create Parameter set for password
        ParameterSet psPassword = new ParameterSet();
        psPassword.parameterName="password";
        psPassword.parameterType=String.class.getName();
        SHA_1_hash hash = new SHA_1_hash();
        psPassword.parameterValue=hash.doHash(password);
        psPassword.timeStamp = null;
        parameters.add(psPassword);
        
        //Create Parameter set for canModifyUser
        ParameterSet psCanModifyUser = new ParameterSet();
        psCanModifyUser.parameterName="canModifyUsers";
        psCanModifyUser.parameterType=boolean.class.getName();
        psCanModifyUser.parameterValue=String.valueOf(canModifyUsers);
        psCanModifyUser.timeStamp = null;
        parameters.add(psCanModifyUser);
        
        //Create Parameter set for canModifyVehicle
        ParameterSet psCanModifyVehcicles = new ParameterSet();
        psCanModifyVehcicles.parameterName="canModifyVehicles";
        psCanModifyVehcicles.parameterType=boolean.class.getName();
        psCanModifyVehcicles.parameterValue=String.valueOf(canModifyVehicles);
        psCanModifyVehcicles.timeStamp = null;
        parameters.add(psCanModifyVehcicles);
        
        //Create Parameter set for canModifySendRequest
        ParameterSet psCanSendRequest = new ParameterSet();
        psCanSendRequest.parameterName="canSendRequest";
        psCanSendRequest.parameterType=boolean.class.getName();
        psCanSendRequest.parameterValue=String.valueOf(canSendRequest);
        psCanSendRequest.timeStamp = null;
        parameters.add(psCanSendRequest);
        
        
        Request createUserRequest = new Request(username, "CreateUser", parameters);
        return createUserRequest;
    }
    
}
