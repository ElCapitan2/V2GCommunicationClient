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
 * Client side implementation to build a request to pass on request to vehicles.
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class InitiateRequestVehicleData implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run mehthod must be overridden as it is defined in TaskWorker.
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
            boolean requestInitiated = false;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("requestInitiated") && para.parameterType.equals(boolean.class.getName())){
                    requestInitiated = Boolean.parseBoolean(para.parameterValue);
                }
            }
            
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER INITIATE REQUEST");
            System.out.println("=========================");
            System.out.println("Requests initiated: " + requestInitiated);
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
     * Static method to generate a request to demand data from a vehicle 
     * request. The retruned Request can be added to a Session and will then 
     * be transmitted to the server
     * 
     * @param username              username of user logged in
     * @param vehnames              list of vehicles
     * @param vehicleFunctions      data/functions that shall be triggered
     * @param intervall             intervall in [ms]
     * @param iterations            iterations [-]
     * @return 
     */
    public static Request getInitiateRequestVehicleDataRequest(String username, ArrayList<String> vehnames, 
            ArrayList<String> vehicleFunctions, int intervall, int iterations){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        //parametersets for vehiclenames
        for (String vehname:vehnames){
            ParameterSet psVIN = new ParameterSet();
            psVIN.parameterName = "vehname";
            psVIN.parameterType = String.class.getName();
            psVIN.parameterValue = vehname;
            psVIN.timeStamp = null;
            parameters.add(psVIN);
        }
        
        // Add Functions to Request
        for (String vehicleFunction:vehicleFunctions){
            ParameterSet psFunction = new ParameterSet();
            psFunction.parameterName = "functionName";
            psFunction.parameterType = String.class.getName();
            psFunction.parameterValue = vehicleFunction;
            psFunction.timeStamp = null;
            parameters.add(psFunction);
        }
        
        //Create Parameterset for intervall
        ParameterSet psIntervall = new ParameterSet();
        psIntervall.parameterName="intervall";
        psIntervall.parameterType=int.class.getName();
        psIntervall.parameterValue=String.valueOf(intervall);
        psIntervall.timeStamp = null;
        parameters.add(psIntervall);
        
        //Create Parameter set for iterations
        ParameterSet psVehname = new ParameterSet();
        psVehname.parameterName="iterations";
        psVehname.parameterType=int.class.getName();
        psVehname.parameterValue=String.valueOf(iterations);
        psVehname.timeStamp = null;
        parameters.add(psVehname);
        
        
        Request createUserRequest = new Request(username, "InitiateRequestVehicleData", parameters);
        return createUserRequest;
    }
    
}
