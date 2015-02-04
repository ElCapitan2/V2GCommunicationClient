/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gCommunication.client.ServerConnection;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to delete a user on the
 * server
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class DeleteUser implements TaskWorker{
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
     * It also calls the callback method {@code ServerConnection.userDeleted}
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            boolean userDeleted = false;
            String nameOfUserDeleted = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("userDeleted") && para.parameterType.equals(boolean.class.getName())){
                    userDeleted = Boolean.valueOf(para.parameterValue);
                }
                if (para.parameterName.equals("nameOfUserDeleted") && para.parameterType.equals(String.class.getName())){
                    nameOfUserDeleted = para.parameterValue;
                }
            }
            
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO DELETE USER");
            System.out.println("=====================");
            System.out.println("Request for deleting user: " + nameOfUserDeleted + " processed.");
            System.out.println("Request processed successfully: " + userDeleted);
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.userDeleted();
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
     * Static method to generate a delete user request. The returned 
     * Request can be added to a Session and will then be transmitted to 
     * the server.
     * 
     * @param username          Username of user logged in
     * @param userTodelete      Username of user to be deleted
     * @return                  Request to be sent
     */
    public static Request getDeleteUserRequest(String username, String userTodelete){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        ParameterSet psUser = new ParameterSet();
        psUser.parameterName="username";
        psUser.parameterType=String.class.getName();
        psUser.parameterValue=userTodelete;
        psUser.timeStamp = null;
        parameters.add(psUser);
        Request logonRequest = new Request(username, "DeleteUser", parameters);
        return logonRequest;
    }
    
}
