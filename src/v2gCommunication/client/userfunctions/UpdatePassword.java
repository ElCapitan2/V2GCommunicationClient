/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gcommunication.commonclasses.encryption.SHA_1_hash;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to update the user password
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class UpdatePassword implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run mehthod must be overridden as it is defined in TaskWorker.
     * 
     * Server will return a request indicating whether the operation was 
     * successful.
     * 
     * This method reads the request and puts out true or false to the 
     * commandline.
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            boolean passwordChanged = false;
            String nameOfUser = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("passwordUpdated") && para.parameterType.equals(boolean.class.getName())){
                    passwordChanged = Boolean.parseBoolean(para.parameterValue);
                }
                if (para.parameterName.equals("nameOfUser") && para.parameterType.equals(String.class.getName())){
                    nameOfUser = para.parameterValue;
                }
            }
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO CHANGE PASSWORD");
            System.out.println("=========================");
            System.out.println("Request for change password of user: " + nameOfUser + " processed.");
            System.out.println("Request processed successfully: " + passwordChanged);
            System.out.println("***************************************************");
            System.out.println();
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
     * Static method to generate a update password request. The retruned 
     * Request will be added can be added to a Session and will then 
     * be transmitted to the server
     * 
     * @param username          username of logged in user
     * @param nameOfUser        username of user to change password
     * @param password          new password
     * @return                  Request
     */
    public static Request getUpdatePasswordRequest(String username, 
            String nameOfUser, String password){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        //Create Parameter set for username
        ParameterSet psUser = new ParameterSet();
        psUser.parameterName="nameOfUser";
        psUser.parameterType=String.class.getName();
        psUser.parameterValue=nameOfUser;
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
        
        Request createUserRequest = new Request(username, "UpdatePassword", parameters);
        return createUserRequest;
    }
    
}
