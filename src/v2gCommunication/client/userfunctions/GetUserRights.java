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
import v2gcommunication.commonclasses.userrights.UserPrivilage;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to get userrights.
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class GetUserRights implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run method must be overridden as it is defined in TaskWorker.
     * 
     * This method reads the request and puts out the result in the command line
     * 
     * It calls the callback function {@code ServerConnection.userrightsReceived}
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            UserPrivilage userPrivilage = new UserPrivilage();
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("username") && para.parameterType.equals(String.class.getName())){
                    userPrivilage.username = para.parameterValue;
                }
                if (para.parameterName.equals("canModifyUsers") && para.parameterType.equals(boolean.class.getName())){
                    userPrivilage.canModifyUsers = Boolean.parseBoolean(para.parameterValue);
                }
                if (para.parameterName.equals("canModifyVehicles") && para.parameterType.equals(boolean.class.getName())){
                    userPrivilage.canModifyVehicles = Boolean.parseBoolean(para.parameterValue);
                }
                if (para.parameterName.equals("canSendRequest") && para.parameterType.equals(boolean.class.getName())){
                    userPrivilage.canSendRequests = Boolean.parseBoolean(para.parameterValue);
                }
            }
            
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO GET USER RIGHTS");
            System.out.println("=========================");
            System.out.println("Userrights for user: " + userPrivilage.username);
            System.out.println("canModifyUsers:      " + userPrivilage.canModifyUsers);
            System.out.println("canModifyVehicles:   " + userPrivilage.canModifyVehicles);
            System.out.println("canSendRequest:      " + userPrivilage.canSendRequests);
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.userrightsReceived(userPrivilage);
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
     * Static method to generate a get user rights request. The returned 
     * Request will be added can be added to a Session and will then 
     * be transmitted to the server.
     * 
     * @param username              username of user logged in
     * @param userToGetUserRights   user for which rights should be transferred
     * @return 
     */
    public static Request getGetUserRightsRequest(String username, String userToGetUserRights){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        ParameterSet psUser = new ParameterSet();
        psUser.parameterName="username";
        psUser.parameterType=String.class.getName();
        psUser.parameterValue=userToGetUserRights;
        psUser.timeStamp = null;
        parameters.add(psUser);
        Request logonRequest = new Request(username, "GetUserRights", parameters);
        return logonRequest;
    }
    
}
