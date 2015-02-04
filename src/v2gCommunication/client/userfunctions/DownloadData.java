/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import v2gCommunication.client.ServerConnection;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.tasks.Task;
import v2gcommunication.commonclasses.tasks.TaskState;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to download data from the server.
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class DownloadData implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    StartTaskTransmit taskReply;
    
    /**
     * Run method must be overridden as it is defined in TaskWorker.
     * 
     * Server will return a Request including the IDs of the tasks being 
     * transmitted.
     * 
     * This method reads the request waits for the tasks to be downloaded 
     * completely and stores the information in file
     * 
     * 
     */
    @Override
    public void run() {
        // Evaluate Request
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("DOWNLOAD DATA");
            System.out.println("=============");
            System.out.println("Receiving Tasks:");
            int i = 0;
            ArrayList<String> tasksIDs = new ArrayList<String>();
            ServerConnection sc = ServerConnection.getInstance();
            String fileName = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("task") && para.parameterType.equals(String.class.getName())){
                    i++;
                    String taskID = para.parameterValue;
                    tasksIDs.add(taskID);
                    System.out.println("TaskID Task " + i + ": " + taskID);
                }
                if (para.parameterName.equals("fileName") && para.parameterType.equals(String.class.getName())){
                    i++;
                    fileName = para.parameterValue;
                    System.out.println("FileName to be Stored: " + fileName);
                }
            }
            System.out.println("***************************************************");
            System.out.println();
            
            // Create a list of Tasks
            int i1 = 0;
            ArrayList<Task> tasks = new ArrayList<Task>();
            while (i1<tasksIDs.size()){
                Task ta = sc.getReceiveTask(tasksIDs.get(i1));
                if (ta!=null){
                    tasks.add(ta);
                    i1++;
                }
                else{
                    try {
                        //Wait if Task has not arrived on server.
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            // wait for completion of download
            boolean waitForData = true;
            while (waitForData) {
                boolean taskStatesEnd = true;
                for (Task ta: tasks){
                    if (ta.getTaskState()==TaskState.END){
                        taskStatesEnd = taskStatesEnd && true;
                    }
                    else {
                        taskStatesEnd = taskStatesEnd && false;
                    }
                }
                waitForData = !taskStatesEnd;
                if (waitForData) try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //store data into a file
            try {
                FileOutputStream foo = new FileOutputStream(fileName);
                for (Task ta:tasks){
                    ArrayList<ParameterSet> ps = new ArrayList<ParameterSet>();
                    int len;
                    len = ta.getDataElements(ps, ta.size());
                    int a = 0;
                    for (a = 0;a<len;a++){
                        String line = ta.getTaskID().replace("|", "\t") +"\t"+ ps.get(a).parameterName +"\t"+ ps.get(a).parameterValue +"\t"+ ps.get(a).timeStamp+"\n";
                        foo.write(line.getBytes());
                    }
                    ta.deleteDataElements(len);
                    sc.dataStored(ta);
                }
                foo.close();
                System.out.println();
                System.out.println("***************************************************");
                System.out.println("DATA STORED");
                System.out.println("===========");
                System.out.println("");
                System.out.println("***************************************************");
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        this.taskReply = taskReply;
        
    }
    
    /**
     * Static method to generate a download data request. The returned 
     * Request can be added to a Session and will then 
     * be transmitted to the server
     * 
     * @param username      username of logged in user
     * @param vehnames      vehiclenames of vehicles to be queried
     * @param functions     functions to be queried
     * @param fullpath      path of file
     * @return 
     */
    public static Request getDownloadDataRequest(String username, 
            ArrayList<String> vehnames, ArrayList<String> functions,
            String fullpath){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        for (String vehname:vehnames){ 
            ParameterSet vehicleName = new ParameterSet();
            vehicleName.parameterName = "vehname";
            vehicleName.parameterType = String.class.getName();
            vehicleName.parameterValue = vehname;
            vehicleName.timeStamp = null;
            parameters.add(vehicleName);
        }
        
        for (String function:functions){ 
            ParameterSet veFu = new ParameterSet();
            veFu.parameterName = "function";
            veFu.parameterType = String.class.getName();
            veFu.parameterValue = function;
            veFu.timeStamp = null;
            parameters.add(veFu);
        }
        
        ParameterSet file = new ParameterSet();
        file.parameterName = "fileName";
        file.parameterType = String.class.getName();
        file.parameterValue = fullpath;
        file.timeStamp = null;
        parameters.add(file);
        
        Request createUserRequest = new Request(username, "DownloadData", parameters);
        return createUserRequest;
    }
    
}
