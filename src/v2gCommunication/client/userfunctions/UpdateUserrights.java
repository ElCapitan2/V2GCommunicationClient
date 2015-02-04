/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to update user rights.
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class UpdateUserrights implements TaskWorker{
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
            boolean userrightsUpdated = false;
            String nameOfUser = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("userrightsUpdated") && para.parameterType.equals(boolean.class.getName())){
                    userrightsUpdated = Boolean.parseBoolean(para.parameterValue);
                }
                if (para.parameterName.equals("nameOfUser") && para.parameterType.equals(String.class.getName())){
                    nameOfUser = para.parameterValue;
                }
            }
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO UPDATE USERRIGHTS");
            System.out.println("=====================");
            System.out.println("Request for updating userrights of user: " + nameOfUser + " processed.");
            System.out.println("Request processed successfully: " + userrightsUpdated);
            System.out.println("***************************************************");
            System.out.println();
        }
    }

    /**
     * Method defined in Taskworker is used for a the runMethod to reply using
     * Requests or Tasks.
     * 
     * @param requestProcessed
     * @param requestReply
     * @param taskReply 
     */
    @Override
    public void inputForRunnable(Request requestProcessed, AddRequest requestReply, StartTaskTransmit taskReply) {
        this.request = requestProcessed;
        this.requestReply = requestReply;
    }
    
    /**
     * Static method to generate a update user rights request. The retruned 
     * Request will can be added to a Session and will then 
     * be transmitted to the server
     * 
     * @param username                  username of user logged in
     * @param nameOfUser                username to be altered
     * @param canModifyUsers            True if user can manage users
     * @param canModifyVehicles         True if user can modify vehicles
     * @param canSendRequest            True if user can send request to vehicles
     * @return 
     */
    public static Request getUpdateUserrightsRequest(String username, String nameOfUser, 
            boolean canModifyUsers, boolean canModifyVehicles, boolean canSendRequest){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        //Create Parameter set for username
        ParameterSet psUser = new ParameterSet();
        psUser.parameterName="nameOfUser";
        psUser.parameterType=String.class.getName();
        psUser.parameterValue=nameOfUser;
        psUser.timeStamp = null;
        parameters.add(psUser);
        
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
        
        
        Request createUserRequest = new Request(username, "UpdateUserrights", parameters);
        return createUserRequest;
    }
    
}
