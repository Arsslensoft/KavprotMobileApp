/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kavprot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import org.netbeans.microedition.lcdui.LoginScreen;
import org.netbeans.microedition.lcdui.SplashScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 * @author arsslen
 */
public class HelloMIDlet extends MIDlet implements CommandListener, ItemCommandListener {
    
    private boolean midletPaused = false;
//<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private java.util.Hashtable __previousDisplayables = new java.util.Hashtable();
    private Command exitCommand;
    private Command ReceiveMsgCMD;
    private Command SendMsgCMD;
    private Command sendCMD;
    private Command receiveCMD;
    private Command okCommand;
    private Command exitCommand1;
    private Command backCommand;
    private Command helpCommand;
    private Command backCommand1;
    private Command helpCommand1;
    private Command Maincommand;
    private Command backCommand2;
    private SplashScreen splashScreen;
    private Form form;
    private TextField sendtxt;
    private Spacer spacer2;
    private Gauge gauge;
    private Spacer spacer1;
    private TextField acceptxt;
    private TextField cmdop;
    private TextBox ReceiveMsgBox;
    private Form MainFORM;
    private TextField kpavatxt;
    private TextField kpavmtxt;
    private TextField servertxt;
    private TextField enctxt;
    private TextField enckeytxt;
    private Form HelpForm;
    private StringItem stringItem;
    private Image image;
    private SimpleCancellableTask task;
//</editor-fold>//GEN-END:|fields|0|

    public String replaceAll(String text, String searchString, String replacementString) {
        StringBuffer sBuffer = new StringBuffer();
        int pos = 0;
        while ((pos = text.indexOf(searchString)) != -1) {
                sBuffer.append(text.substring(0, pos) + replacementString);
                text = text.substring(pos + searchString.length());
        }
        sBuffer.append(text);
        return sBuffer.toString();
    }
    
    Player player;
public void PlayMediaFile(String url) throws IOException, MediaException {
     
         player = Manager.createPlayer(replaceAll(url, " ", ""));

        player.realize();

      VolumeControl  vCtrl = (VolumeControl) player.getControl("VolumeControl");

        if (vCtrl != null) {
            vCtrl.setLevel(100);
        }

        player.prefetch();
        player.start();
    }

 

  
  public void GetMessage() throws IOException, InterruptedException
   {
       try
               {
                   gauge.setLabel("Looking for last message");
                   gauge.setValue(10);
                   // S=source&D=destination&U=trustkey&A=action
                   String resp = this.sendPostRequest("S=" +this.kpavmtxt.getString()+"&D=" +this.kpavatxt.getString()+"&U=ATKOCAPCRTPBR&A=GETMSG");
                   
       if(!resp.equals("ACCESS DENIED") && resp != null && !resp.equals("") && !resp.equals(" "))
       {
       gauge.setLabel("Unpacking Packet...");
       gauge.setValue(30);
       
        // decrypt message from base 16 (hex)
        String decodedresponse = new String(EncodingProvider.DecodeB16(resp));

      // get headers
     String source = decodedresponse.substring(0, 14);// 14 bytes
     String Destin = decodedresponse.substring(14, 28);//14 bytes
     String EncAlgo = decodedresponse.substring(28, 36);// 8 bytes
     String CID = decodedresponse.substring(36, 40);// 4 bytes
     String command = decodedresponse.substring(40, 48);// 8 bytes
     //String accept = decodedresponse.substring(48, 52);// 4 bytes
     String DEVICE = decodedresponse.substring(52, 56);// 4 bytes 
     // verify (source and destination and device and KID)
     if(source.equals(this.kpavatxt.getString()) && Destin.equals(this.kpavmtxt.getString()) && DEVICE.equals("KSSA") && CID.equals("KPAV"))
     {
                  // initialize encryption algorithm
             gauge.setLabel("Initializing SEA...");
             gauge.setValue(60);
             String Key = this.enckeytxt.getString();
               // 128 or 256 or 512 or 1024 bytes
    if(EncAlgo.equals("SEA-1024")) 
        SEA.InitializeKey(1024, Key.getBytes("UTF-8"), (byte)64);
    else if(EncAlgo.equals("SEA-2048")) 
        SEA.InitializeKey(2048, Key.getBytes("UTF-8"), (byte)64);
    else  if(EncAlgo.equals("SEA-4096")) 
        SEA.InitializeKey(4096, Key.getBytes("UTF-8"), (byte)64);
    else  if(EncAlgo.equals("SEA-8192")) 
        SEA.InitializeKey(8192, Key.getBytes("UTF-8"), (byte)64);
    else
        SEA.InitializeKey(2048, Key.getBytes("UTF-8"), (byte)64);
    
    
       gauge.setLabel("Extracting Informations...");
       gauge.setValue(80);
       
        // decrypt content                 
     String MSG = new String(SEA.DecryptFromBase64(decodedresponse.substring(56, decodedresponse.length())));
       
            gauge.setLabel("Message Received. Processing...");
            gauge.setValue(90);
            
            // process message
 if(command.equals("PLAYAUDI"))
     {
         // play audio
        this.PlayMediaFile(MSG);
     }
     else if(command.equals("SHOWTEXT"))
     {
         // show text
     this.ReceiveMsgBox.setString(MSG);
     this.switchDisplayable(null,ReceiveMsgBox);
      
     }      
    else if(command.equals("ALERTUSR"))
     {
           this.ReceiveMsgBox.setString(MSG);
           this.switchDisplayable(null,ReceiveMsgBox);
         // alert user 
         this.getDisplay().vibrate(15000);
         this.getDisplay().flashBacklight(15000);
                
     }
  else   
     {
         // no action
           gauge.setLabel("No Action for this Packet, Destroying it...");
          gauge.setValue(0);
       resp = null;
             Thread.sleep(2000);
            gauge.setLabel(" ");
     }
 
     

     }
     else
     {
             gauge.setLabel("ARCP Violation : AUTH_FAILED");
                   gauge.setValue(100);
                   Thread.sleep(1000);
                    gauge.setLabel(" ");
                   gauge.setValue(0);
         
     }
     
       }
       else
       {
           
                   gauge.setLabel("No new message");
                   gauge.setValue(100);
                   Thread.sleep(1000);
                    gauge.setLabel(" ");
                   gauge.setValue(0);
       }
              
        }
       catch(Exception ex)
       {
       gauge.setLabel(ex.getMessage());
       gauge.setValue(0);
        
       }
       finally
       {
           
       }
       
   }
   
     
    public void SendMessage()
    {
        try
        {
               String source = this.kpavmtxt.getString();
               String Destin = this.kpavatxt.getString();
               String command = this.cmdop.getString();
               String msg = this.sendtxt.getString();
               String encalgo = this.enctxt.getString();
               String accept = this.acceptxt.getString();
               String Key = this.enckeytxt.getString();
               // verify message integrity
                if(source.length() == 14 && Destin.length() == 14 && command.length() == 8 && encalgo.length() == 8 && accept.length() == 4)
                {
                     this.gauge.setValue(10);
               this.gauge.setLabel("Preparing Data...");
               String MSGC = source + Destin + encalgo + "KPAV" + command + accept + "KSSM";
                
               this.gauge.setValue(30);
               this.gauge.setLabel("Initializing SEA engine...");
                     
                    // 128 or 256 or 512 or 1024 bytes
    if(encalgo.equals("SEA-1024")) 
        SEA.InitializeKey(1024, Key.getBytes("UTF-8"), (byte)64);
    else if(encalgo.equals("SEA-2048")) 
        SEA.InitializeKey(2048, Key.getBytes("UTF-8"), (byte)64);
    else  if(encalgo.equals("SEA-4096")) 
        SEA.InitializeKey(4096, Key.getBytes("UTF-8"), (byte)64);
    else  if(encalgo.equals("SEA-8192")) 
        SEA.InitializeKey(8192, Key.getBytes("UTF-8"), (byte)64);
    else
        SEA.InitializeKey(2048, Key.getBytes("UTF-8"), (byte)64);
    

                this.gauge.setValue(50);
               this.gauge.setLabel("Encrypting Message...");
               MSGC =  MSGC + SEA.EncryptToBase64(msg.getBytes("UTF-8"));
       
this.gauge.setValue(70);
               this.gauge.setLabel("Sending Packet...");
               String resp =this.sendPostRequest("D=" +Destin+"&S="+source+"&U=ATKOCAPCRTPBR&A=SETMSG&M=" + EncodingProvider.EncodeB16(MSGC.getBytes()));
              if(resp.equals("Message sent"))
              {
               this.gauge.setValue(100);
              this.gauge.setLabel("Your message was sent");
              Thread.sleep(1000);
               this.gauge.setValue(0);
              this.gauge.setLabel(" ");
              }
              else
              {
                   this.gauge.setValue(100);
              this.gauge.setLabel(resp);
                Thread.sleep(1000);
               this.gauge.setValue(0);
              this.gauge.setLabel(" ");
              }
              


                }
                else
                {
                     this.gauge.setValue(100);
              this.gauge.setLabel("ARCP Violation");
          
                Thread.sleep(2000);
                      this.gauge.setValue(0);
              this.gauge.setLabel(" ");
          
         
                } 
            
        }
        
        catch(Exception ex)
        {
             this.gauge.setValue(100);
              this.gauge.setLabel("Sending Failed : " + ex.getMessage());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex1) {
                ex1.printStackTrace();
            }
            
        }
        finally
        {
            this.gauge.setValue(0);
          this.gauge.setLabel(" ");
     
        }
    }
    
public String sendPostRequest(String requeststring) throws IOException 
{ 
	HttpConnection hc = null; 
	DataInputStream dis = null; 
	DataOutputStream dos = null; 
	StringBuffer messagebuffer = new StringBuffer(); 
	try 
	{ //open up a http connection with the Web server for both send and receive operations 
	    hc = (HttpConnection) Connector.open(servertxt.getString(), Connector.READ_WRITE); // Set the request method to POST 				
    	    hc.setRequestMethod(HttpConnection.POST); 
            hc.setRequestProperty("User-Agent", "ARSSLENSOFT_REMOTE_CONTROL_PROTOCOL_AGENT/1.0");
             hc.setRequestProperty(
                         "Content-Type", 
                         "application/x-www-form-urlencoded");
  	    //send the string entered by user byte by byte 
	    dos = hc.openDataOutputStream(); 
	    byte[] request_body = requeststring.getBytes("UTF-8"); 
	    for (int i = 0; i < request_body.length; i++) 
  	    { 
  	        dos.writeByte(request_body[i]); 
	    } 
            dos.flush(); 
	    dos.close(); 
	    //retrieve the response back from the server
            dis = new DataInputStream(hc.openInputStream()); 
	    int ch; 
	    //check the content length first 
	    long len = hc.getLength(); 
	    if(len!=-1) 
	    { 
	         for(int i = 0;i<len;i++) 
    		     if((ch = dis.read())!= -1) 
 	                 messagebuffer.append((char)ch); 
	    } 
	    else 
	     { // if the content length is not available 
	          while ((ch = dis.read()) != -1) 
                      messagebuffer.append((char) ch); 
	    } 
	
	dis.close(); 
	}
	catch (IOException ioe) 
	{ 
		
	} 
	finally 
	{ // Free up i/o streams and http connection 
		try 
		{ 
		    if (hc != null) 
			hc.close(); 
		}catch (IOException ignored) {} 
		try 
		{ 
    		    if (dis != null) 
			dis.close(); 
      	        }catch (IOException ignored) {} 
		try 
		{ 
  		    if (dos != null) 
			dos.close(); 
	        } 
		catch (IOException ignored) {} 
	} 
	return messagebuffer.toString(); 
	}

    /**
     * The HelloMIDlet constructor.
     */
    public HelloMIDlet() {
    }

     
//<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    /**
     * Switches a display to previous displayable of the current displayable.
     * The
     * <code>display</code> instance is obtain from the
     * <code>getDisplay</code> method.
     */
    private void switchToPreviousDisplayable() {
        Displayable __currentDisplayable = getDisplay().getCurrent();
        if (__currentDisplayable != null) {
            Displayable __nextDisplayable = (Displayable) __previousDisplayables.get(__currentDisplayable);
            if (__nextDisplayable != null) {
                switchDisplayable(null, __nextDisplayable);
            }
        }
    }
//</editor-fold>//GEN-END:|methods|0|
//<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize

    /**
     * Initializes the application. It is called only once when the MIDlet is
     * started. The method is called before the
     * <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
//</editor-fold>//GEN-END:|0-initialize|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
        this.switchDisplayable(null, this.getSplashScreen());
    }//GEN-BEGIN:|3-startMIDlet|2|
//</editor-fold>//GEN-END:|3-startMIDlet|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
//</editor-fold>//GEN-END:|4-resumeMIDlet|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The
     * <code>display</code> instance is taken from
     * <code>getDisplay</code> method. This method is used by all actions in the
     * design for switching displayable.
     *
     * @param alert the Alert which is temporarily set to the display; if
     * <code>null</code>, then
     * <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        Displayable __currentDisplayable = display.getCurrent();
        if (__currentDisplayable != null && nextDisplayable != null) {
            __previousDisplayables.put(nextDisplayable, __currentDisplayable);
        }
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
//</editor-fold>//GEN-END:|5-switchDisplayable|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a
     * particular displayable.
     *
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == MainFORM) {//GEN-BEGIN:|7-commandAction|1|68-preAction
            if (command == exitCommand1) {//GEN-END:|7-commandAction|1|68-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|2|68-postAction
                // write post-action user code here
            } else if (command == helpCommand) {//GEN-LINE:|7-commandAction|3|74-preAction
                // write pre-action user code here
                switchDisplayable(null, getHelpForm());//GEN-LINE:|7-commandAction|4|74-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|5|71-preAction
                // write pre-action user code here
                switchDisplayable(null, getForm());//GEN-LINE:|7-commandAction|6|71-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|7|87-preAction
        } else if (displayable == ReceiveMsgBox) {
            if (command == backCommand1) {//GEN-END:|7-commandAction|7|87-preAction
                // write pre-action user code here
                switchToPreviousDisplayable();//GEN-LINE:|7-commandAction|8|87-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|93-preAction
        } else if (displayable == form) {
            if (command == Maincommand) {//GEN-END:|7-commandAction|9|93-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainFORM());//GEN-LINE:|7-commandAction|10|93-postAction
                // write post-action user code here
            } else if (command == backCommand2) {//GEN-LINE:|7-commandAction|11|90-preAction
                // write pre-action user code here
                switchToPreviousDisplayable();//GEN-LINE:|7-commandAction|12|90-postAction
                // write post-action user code here
            } else if (command == helpCommand1) {//GEN-LINE:|7-commandAction|13|82-preAction
                // write pre-action user code here
                switchDisplayable(null, getHelpForm());//GEN-LINE:|7-commandAction|14|82-postAction
                // write post-action user code here
            } else if (command == receiveCMD) {//GEN-LINE:|7-commandAction|15|57-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|16|57-postAction
                // write post-action user code here
                Thread t = new Thread()
                {
                    public void run()
                    {
                        try {
                            GetMessage();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                
                };
                t.start();
            } else if (command == sendCMD) {//GEN-LINE:|7-commandAction|17|59-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|18|59-postAction
                // write post-action user code here
                   Thread t = new Thread()
                {
                    public void run()
                    {
                      
                            SendMessage();
                       
                    }
                
                };
                t.start();
            }//GEN-BEGIN:|7-commandAction|19|24-preAction
        } else if (displayable == splashScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|19|24-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainFORM());//GEN-LINE:|7-commandAction|20|24-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|21|7-postCommandAction
        }//GEN-END:|7-commandAction|21|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|22|
//</editor-fold>//GEN-END:|7-commandAction|22|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initialized instance of exitCommand component.
     *
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|18-getter|0|18-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|18-getter|1|18-postInit
            // write post-init user code here
        }//GEN-BEGIN:|18-getter|2|
        return exitCommand;
    }
//</editor-fold>//GEN-END:|18-getter|2|





//<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashScreen ">//GEN-BEGIN:|22-getter|0|22-preInit
    /**
     * Returns an initialized instance of splashScreen component.
     *
     * @return the initialized component instance
     */
    public SplashScreen getSplashScreen() {
        if (splashScreen == null) {//GEN-END:|22-getter|0|22-preInit
            // write pre-init user code here
            splashScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|22-getter|1|22-postInit
            splashScreen.setTitle("Kavprot Security System");
            splashScreen.setCommandListener(this);
            splashScreen.setFullScreenMode(true);
            splashScreen.setImage(getImage());
            splashScreen.setText("Kavprot Security System");
            splashScreen.setTimeout(2000);//GEN-END:|22-getter|1|22-postInit
            // write post-init user code here
        }//GEN-BEGIN:|22-getter|2|
        return splashScreen;
    }
//</editor-fold>//GEN-END:|22-getter|2|



//<editor-fold defaultstate="collapsed" desc=" Generated Getter: image ">//GEN-BEGIN:|25-getter|0|25-preInit
    /**
     * Returns an initialized instance of image component.
     *
     * @return the initialized component instance
     */
    public Image getImage() {
        if (image == null) {//GEN-END:|25-getter|0|25-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|25-getter|1|25-@java.io.IOException
                image = Image.createImage("/shield.png");
            } catch (java.io.IOException e) {//GEN-END:|25-getter|1|25-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|25-getter|2|25-postInit
            // write post-init user code here
        }//GEN-BEGIN:|25-getter|3|
        return image;
    }
//</editor-fold>//GEN-END:|25-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: form ">//GEN-BEGIN:|38-getter|0|38-preInit
    /**
     * Returns an initialized instance of form component.
     *
     * @return the initialized component instance
     */
    public Form getForm() {
        if (form == null) {//GEN-END:|38-getter|0|38-preInit
            // write pre-init user code here
            form = new Form("Kavprot Security System", new Item[]{getSendtxt(), getCmdop(), getSpacer1(), getAcceptxt(), getSpacer2(), getGauge()});//GEN-BEGIN:|38-getter|1|38-postInit
            form.addCommand(getReceiveCMD());
            form.addCommand(getSendCMD());
            form.addCommand(getHelpCommand1());
            form.addCommand(getBackCommand2());
            form.addCommand(getMaincommand());
            form.setCommandListener(this);//GEN-END:|38-getter|1|38-postInit
            // write post-init user code here
        }//GEN-BEGIN:|38-getter|2|
        return form;
    }
//</editor-fold>//GEN-END:|38-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendtxt ">//GEN-BEGIN:|40-getter|0|40-preInit
    /**
     * Returns an initialized instance of sendtxt component.
     *
     * @return the initialized component instance
     */
    public TextField getSendtxt() {
        if (sendtxt == null) {//GEN-END:|40-getter|0|40-preInit
            // write pre-init user code here
            sendtxt = new TextField("Send Message", null, 20000, TextField.ANY);//GEN-BEGIN:|40-getter|1|40-postInit
            sendtxt.addCommand(getSendMsgCMD());
            sendtxt.setItemCommandListener(this);
            sendtxt.setDefaultCommand(getSendMsgCMD());//GEN-END:|40-getter|1|40-postInit
            // write post-init user code here
        }//GEN-BEGIN:|40-getter|2|
        return sendtxt;
    }
//</editor-fold>//GEN-END:|40-getter|2|





//<editor-fold defaultstate="collapsed" desc=" Generated Getter: task ">//GEN-BEGIN:|37-getter|0|37-preInit
    /**
     * Returns an initialized instance of task component.
     *
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask() {
        if (task == null) {//GEN-END:|37-getter|0|37-preInit
            // write pre-init user code here
            task = new SimpleCancellableTask();//GEN-BEGIN:|37-getter|1|37-execute
            task.setExecutable(new org.netbeans.microedition.util.Executable() {

                public void execute() throws Exception {//GEN-END:|37-getter|1|37-execute
// write task-execution user code here
                }//GEN-BEGIN:|37-getter|2|37-postInit
            });//GEN-END:|37-getter|2|37-postInit
            // write post-init user code here
        }//GEN-BEGIN:|37-getter|3|
        return task;
    }
//</editor-fold>//GEN-END:|37-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Items ">//GEN-BEGIN:|17-itemCommandAction|0|17-preItemCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a
     * particular item.
     *
     * @param command the Command that was invoked
     * @param displayable the Item where the command was invoked
     */
    public void commandAction(Command command, Item item) {//GEN-END:|17-itemCommandAction|0|17-preItemCommandAction
        // write pre-action user code here
        if (item == acceptxt) {//GEN-BEGIN:|17-itemCommandAction|1|97-preAction
            if (command == SendMsgCMD) {//GEN-END:|17-itemCommandAction|1|97-preAction
                // write pre-action user code here
//GEN-LINE:|17-itemCommandAction|2|97-postAction
                // write post-action user code here
                             Thread t = new Thread()
                {
                    public void run()
                    {
                      
                            SendMessage();
                       
                    }
                
                };
                              t.start();
            }//GEN-BEGIN:|17-itemCommandAction|3|96-preAction
        } else if (item == cmdop) {
            if (command == SendMsgCMD) {//GEN-END:|17-itemCommandAction|3|96-preAction
                // write pre-action user code here
//GEN-LINE:|17-itemCommandAction|4|96-postAction
                // write post-action user code here
                             Thread t = new Thread()
                {
                    public void run()
                    {
                      
                            SendMessage();
                       
                    }
                
                };
                              t.start();
            }//GEN-BEGIN:|17-itemCommandAction|5|49-preAction
        } else if (item == sendtxt) {
            if (command == SendMsgCMD) {//GEN-END:|17-itemCommandAction|5|49-preAction
                // write pre-action user code here
//GEN-LINE:|17-itemCommandAction|6|49-postAction
                // write post-action user code here
                             Thread t = new Thread()
                {
                    public void run()
                    {
                      
                            SendMessage();
                       
                    }
                
                };
                             t.start();
            }//GEN-BEGIN:|17-itemCommandAction|7|79-preAction
        } else if (item == stringItem) {
            if (command == backCommand) {//GEN-END:|17-itemCommandAction|7|79-preAction
                // write pre-action user code here
                switchToPreviousDisplayable();//GEN-LINE:|17-itemCommandAction|8|79-postAction
                // write post-action user code here
            }//GEN-BEGIN:|17-itemCommandAction|9|17-postItemCommandAction
        }//GEN-END:|17-itemCommandAction|9|17-postItemCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|17-itemCommandAction|10|
//</editor-fold>//GEN-END:|17-itemCommandAction|10|


//<editor-fold defaultstate="collapsed" desc=" Generated Getter: SendMsgCMD ">//GEN-BEGIN:|48-getter|0|48-preInit
    /**
     * Returns an initialized instance of SendMsgCMD component.
     *
     * @return the initialized component instance
     */
    public Command getSendMsgCMD() {
        if (SendMsgCMD == null) {//GEN-END:|48-getter|0|48-preInit
            // write pre-init user code here
            SendMsgCMD = new Command("Send Message", "<null>", Command.ITEM, 0);//GEN-LINE:|48-getter|1|48-postInit
            // write post-init user code here
        }//GEN-BEGIN:|48-getter|2|
        return SendMsgCMD;
    }
//</editor-fold>//GEN-END:|48-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: spacer1 ">//GEN-BEGIN:|44-getter|0|44-preInit
    /**
     * Returns an initialized instance of spacer1 component.
     *
     * @return the initialized component instance
     */
    public Spacer getSpacer1() {
        if (spacer1 == null) {//GEN-END:|44-getter|0|44-preInit
            // write pre-init user code here
            spacer1 = new Spacer(16, 1);//GEN-LINE:|44-getter|1|44-postInit
            // write post-init user code here
        }//GEN-BEGIN:|44-getter|2|
        return spacer1;
    }
//</editor-fold>//GEN-END:|44-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: acceptxt ">//GEN-BEGIN:|45-getter|0|45-preInit
    /**
     * Returns an initialized instance of acceptxt component.
     *
     * @return the initialized component instance
     */
    public TextField getAcceptxt() {
        if (acceptxt == null) {//GEN-END:|45-getter|0|45-preInit
            // write pre-init user code here
            acceptxt = new TextField("Accept", "TEXT", 32, TextField.ANY);//GEN-BEGIN:|45-getter|1|45-postInit
            acceptxt.addCommand(getSendMsgCMD());
            acceptxt.setItemCommandListener(this);
            acceptxt.setDefaultCommand(getSendMsgCMD());//GEN-END:|45-getter|1|45-postInit
            // write post-init user code here
        }//GEN-BEGIN:|45-getter|2|
        return acceptxt;
    }
//</editor-fold>//GEN-END:|45-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: spacer2 ">//GEN-BEGIN:|46-getter|0|46-preInit
    /**
     * Returns an initialized instance of spacer2 component.
     *
     * @return the initialized component instance
     */
    public Spacer getSpacer2() {
        if (spacer2 == null) {//GEN-END:|46-getter|0|46-preInit
            // write pre-init user code here
            spacer2 = new Spacer(16, 1);//GEN-LINE:|46-getter|1|46-postInit
            // write post-init user code here
        }//GEN-BEGIN:|46-getter|2|
        return spacer2;
    }
//</editor-fold>//GEN-END:|46-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: gauge ">//GEN-BEGIN:|47-getter|0|47-preInit
    /**
     * Returns an initialized instance of gauge component.
     *
     * @return the initialized component instance
     */
    public Gauge getGauge() {
        if (gauge == null) {//GEN-END:|47-getter|0|47-preInit
            // write pre-init user code here
            gauge = new Gauge("Initializing", false, 100, 0);//GEN-LINE:|47-getter|1|47-postInit
            // write post-init user code here
        }//GEN-BEGIN:|47-getter|2|
        return gauge;
    }
//</editor-fold>//GEN-END:|47-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: ReceiveMsgCMD ">//GEN-BEGIN:|50-getter|0|50-preInit
    /**
     * Returns an initialized instance of ReceiveMsgCMD component.
     *
     * @return the initialized component instance
     */
    public Command getReceiveMsgCMD() {
        if (ReceiveMsgCMD == null) {//GEN-END:|50-getter|0|50-preInit
            // write pre-init user code here
            ReceiveMsgCMD = new Command("Item", Command.ITEM, 0);//GEN-LINE:|50-getter|1|50-postInit
            // write post-init user code here
        }//GEN-BEGIN:|50-getter|2|
        return ReceiveMsgCMD;
    }
//</editor-fold>//GEN-END:|50-getter|2|



//<editor-fold defaultstate="collapsed" desc=" Generated Getter: receiveCMD ">//GEN-BEGIN:|56-getter|0|56-preInit
    /**
     * Returns an initialized instance of receiveCMD component.
     *
     * @return the initialized component instance
     */
    public Command getReceiveCMD() {
        if (receiveCMD == null) {//GEN-END:|56-getter|0|56-preInit
            // write pre-init user code here
            receiveCMD = new Command("Receive Message", Command.ITEM, 0);//GEN-LINE:|56-getter|1|56-postInit
            // write post-init user code here
        }//GEN-BEGIN:|56-getter|2|
        return receiveCMD;
    }
//</editor-fold>//GEN-END:|56-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendCMD ">//GEN-BEGIN:|58-getter|0|58-preInit
    /**
     * Returns an initialized instance of sendCMD component.
     *
     * @return the initialized component instance
     */
    public Command getSendCMD() {
        if (sendCMD == null) {//GEN-END:|58-getter|0|58-preInit
            // write pre-init user code here
            sendCMD = new Command("Send Message", Command.ITEM, 0);//GEN-LINE:|58-getter|1|58-postInit
            // write post-init user code here
        }//GEN-BEGIN:|58-getter|2|
        return sendCMD;
    }
//</editor-fold>//GEN-END:|58-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdop ">//GEN-BEGIN:|55-getter|0|55-preInit
    /**
     * Returns an initialized instance of cmdop component.
     *
     * @return the initialized component instance
     */
    public TextField getCmdop() {
        if (cmdop == null) {//GEN-END:|55-getter|0|55-preInit
            // write pre-init user code here
            cmdop = new TextField("Command Operator", null, 32, TextField.ANY);//GEN-BEGIN:|55-getter|1|55-postInit
            cmdop.addCommand(getSendMsgCMD());
            cmdop.setItemCommandListener(this);
            cmdop.setDefaultCommand(getSendMsgCMD());//GEN-END:|55-getter|1|55-postInit
            // write post-init user code here
        }//GEN-BEGIN:|55-getter|2|
        return cmdop;
    }
//</editor-fold>//GEN-END:|55-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: ReceiveMsgBox ">//GEN-BEGIN:|54-getter|0|54-preInit
    /**
     * Returns an initialized instance of ReceiveMsgBox component.
     *
     * @return the initialized component instance
     */
    public TextBox getReceiveMsgBox() {
        if (ReceiveMsgBox == null) {//GEN-END:|54-getter|0|54-preInit
            // write pre-init user code here
            ReceiveMsgBox = new TextBox("textBox", null, 20000, TextField.ANY);//GEN-BEGIN:|54-getter|1|54-postInit
            ReceiveMsgBox.addCommand(getBackCommand1());
            ReceiveMsgBox.setCommandListener(this);//GEN-END:|54-getter|1|54-postInit
            // write post-init user code here
        }//GEN-BEGIN:|54-getter|2|
        return ReceiveMsgBox;
    }
//</editor-fold>//GEN-END:|54-getter|2|



//<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand1 ">//GEN-BEGIN:|67-getter|0|67-preInit
    /**
     * Returns an initialized instance of exitCommand1 component.
     *
     * @return the initialized component instance
     */
    public Command getExitCommand1() {
        if (exitCommand1 == null) {//GEN-END:|67-getter|0|67-preInit
            // write pre-init user code here
            exitCommand1 = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|67-getter|1|67-postInit
            // write post-init user code here
        }//GEN-BEGIN:|67-getter|2|
        return exitCommand1;
    }
//</editor-fold>//GEN-END:|67-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|70-getter|0|70-preInit
    /**
     * Returns an initialized instance of okCommand component.
     *
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|70-getter|0|70-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|70-getter|1|70-postInit
            // write post-init user code here
        }//GEN-BEGIN:|70-getter|2|
        return okCommand;
    }
//</editor-fold>//GEN-END:|70-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: MainFORM ">//GEN-BEGIN:|62-getter|0|62-preInit
    /**
     * Returns an initialized instance of MainFORM component.
     *
     * @return the initialized component instance
     */
    public Form getMainFORM() {
        if (MainFORM == null) {//GEN-END:|62-getter|0|62-preInit
            // write pre-init user code here
            MainFORM = new Form("Configuration", new Item[]{getKpavatxt(), getKpavmtxt(), getServertxt(), getEnctxt(), getEnckeytxt()});//GEN-BEGIN:|62-getter|1|62-postInit
            MainFORM.addCommand(getExitCommand1());
            MainFORM.addCommand(getOkCommand());
            MainFORM.addCommand(getHelpCommand());
            MainFORM.setCommandListener(this);//GEN-END:|62-getter|1|62-postInit
            // write post-init user code here
        }//GEN-BEGIN:|62-getter|2|
        return MainFORM;
    }
//</editor-fold>//GEN-END:|62-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: kpavatxt ">//GEN-BEGIN:|63-getter|0|63-preInit
    /**
     * Returns an initialized instance of kpavatxt component.
     *
     * @return the initialized component instance
     */
    public TextField getKpavatxt() {
        if (kpavatxt == null) {//GEN-END:|63-getter|0|63-preInit
            // write pre-init user code here
            kpavatxt = new TextField("Kavprot Application", "test-user@KSSA", 100, TextField.ANY);//GEN-LINE:|63-getter|1|63-postInit
            // write post-init user code here
        }//GEN-BEGIN:|63-getter|2|
        return kpavatxt;
    }
//</editor-fold>//GEN-END:|63-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: kpavmtxt ">//GEN-BEGIN:|64-getter|0|64-preInit
    /**
     * Returns an initialized instance of kpavmtxt component.
     *
     * @return the initialized component instance
     */
    public TextField getKpavmtxt() {
        if (kpavmtxt == null) {//GEN-END:|64-getter|0|64-preInit
            // write pre-init user code here
            kpavmtxt = new TextField("Kavprot Mobile", "test-user@KSSM", 100, TextField.ANY);//GEN-LINE:|64-getter|1|64-postInit
            // write post-init user code here
        }//GEN-BEGIN:|64-getter|2|
        return kpavmtxt;
    }
//</editor-fold>//GEN-END:|64-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: servertxt ">//GEN-BEGIN:|65-getter|0|65-preInit
    /**
     * Returns an initialized instance of servertxt component.
     *
     * @return the initialized component instance
     */
    public TextField getServertxt() {
        if (servertxt == null) {//GEN-END:|65-getter|0|65-preInit
            // write pre-init user code here
            servertxt = new TextField("Arsslensoft Communication Server", "http://arsslenserv.eb2a.com/ARCP.php", 100, TextField.ANY);//GEN-LINE:|65-getter|1|65-postInit
            // write post-init user code here
        }//GEN-BEGIN:|65-getter|2|
        return servertxt;
    }
//</editor-fold>//GEN-END:|65-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: enctxt ">//GEN-BEGIN:|66-getter|0|66-preInit
    /**
     * Returns an initialized instance of enctxt component.
     *
     * @return the initialized component instance
     */
    public TextField getEnctxt() {
        if (enctxt == null) {//GEN-END:|66-getter|0|66-preInit
            // write pre-init user code here
            enctxt = new TextField("Encryption Algorithm", "SEA-2048", 100, TextField.ANY);//GEN-LINE:|66-getter|1|66-postInit
            // write post-init user code here
        }//GEN-BEGIN:|66-getter|2|
        return enctxt;
    }
//</editor-fold>//GEN-END:|66-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: helpCommand ">//GEN-BEGIN:|73-getter|0|73-preInit
    /**
     * Returns an initialized instance of helpCommand component.
     *
     * @return the initialized component instance
     */
    public Command getHelpCommand() {
        if (helpCommand == null) {//GEN-END:|73-getter|0|73-preInit
            // write pre-init user code here
            helpCommand = new Command("Help", Command.HELP, 0);//GEN-LINE:|73-getter|1|73-postInit
            // write post-init user code here
        }//GEN-BEGIN:|73-getter|2|
        return helpCommand;
    }
//</editor-fold>//GEN-END:|73-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: HelpForm ">//GEN-BEGIN:|75-getter|0|75-preInit
    /**
     * Returns an initialized instance of HelpForm component.
     *
     * @return the initialized component instance
     */
    public Form getHelpForm() {
        if (HelpForm == null) {//GEN-END:|75-getter|0|75-preInit
            // write pre-init user code here
            HelpForm = new Form("Kavprot SS - Help", new Item[]{getStringItem()});//GEN-LINE:|75-getter|1|75-postInit
            // write post-init user code here
        }//GEN-BEGIN:|75-getter|2|
        return HelpForm;
    }
//</editor-fold>//GEN-END:|75-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|77-getter|0|77-preInit
    /**
     * Returns an initialized instance of stringItem component.
     *
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|77-getter|0|77-preInit
            // write pre-init user code here
            stringItem = new StringItem("Commands", "RECOTEXT : this command is used for speech recognition e.g  (text to recognize)  \nEVALEXPR : this command is used for mathematical expressions evaluations  e.g (expression)  \nSOLVEQUA : this command is used for polynominal equation solving e.g  (a,b,c,d...,n)  \nFILEOPER  : this command is used for file operations e.g  (write/append/create/read ,file,data)  \nWINCONTR : this command is used to control windows e.g  (SHUTDOWN/REBOOT/LOGOFF/ABORT ,time)  \nHARDCONT : this command is used to control a hardware e.g (EJECT,drive letter)  \nAVOPERAT : this command is used for Antivirus operation e.g (SCANDRV, drive letter)  \nLAUNALER : this command is used for user protection  \nCUSTCOMD : this command is used for user set commands\n", Item.PLAIN);//GEN-BEGIN:|77-getter|1|77-postInit
            stringItem.addCommand(getBackCommand());
            stringItem.setItemCommandListener(this);//GEN-END:|77-getter|1|77-postInit
            // write post-init user code here
        }//GEN-BEGIN:|77-getter|2|
        return stringItem;
    }
//</editor-fold>//GEN-END:|77-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|78-getter|0|78-preInit
    /**
     * Returns an initialized instance of backCommand component.
     *
     * @return the initialized component instance
     */
    public Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|78-getter|0|78-preInit
            // write pre-init user code here
            backCommand = new Command("Back", Command.BACK, 0);//GEN-LINE:|78-getter|1|78-postInit
            // write post-init user code here
        }//GEN-BEGIN:|78-getter|2|
        return backCommand;
    }
//</editor-fold>//GEN-END:|78-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: helpCommand1 ">//GEN-BEGIN:|81-getter|0|81-preInit
    /**
     * Returns an initialized instance of helpCommand1 component.
     *
     * @return the initialized component instance
     */
    public Command getHelpCommand1() {
        if (helpCommand1 == null) {//GEN-END:|81-getter|0|81-preInit
            // write pre-init user code here
            helpCommand1 = new Command("Help", Command.HELP, 0);//GEN-LINE:|81-getter|1|81-postInit
            // write post-init user code here
        }//GEN-BEGIN:|81-getter|2|
        return helpCommand1;
    }
//</editor-fold>//GEN-END:|81-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: enckeytxt ">//GEN-BEGIN:|85-getter|0|85-preInit
    /**
     * Returns an initialized instance of enckeytxt component.
     *
     * @return the initialized component instance
     */
    public TextField getEnckeytxt() {
        if (enckeytxt == null) {//GEN-END:|85-getter|0|85-preInit
            // write pre-init user code here
            enckeytxt = new TextField("Encryption Key", "seaenckey", 255, TextField.ANY | TextField.PASSWORD);//GEN-BEGIN:|85-getter|1|85-postInit
            enckeytxt.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|85-getter|1|85-postInit
            // write post-init user code here
        }//GEN-BEGIN:|85-getter|2|
        return enckeytxt;
    }
//</editor-fold>//GEN-END:|85-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand1 ">//GEN-BEGIN:|86-getter|0|86-preInit
    /**
     * Returns an initialized instance of backCommand1 component.
     *
     * @return the initialized component instance
     */
    public Command getBackCommand1() {
        if (backCommand1 == null) {//GEN-END:|86-getter|0|86-preInit
            // write pre-init user code here
            backCommand1 = new Command("Back", Command.BACK, 0);//GEN-LINE:|86-getter|1|86-postInit
            // write post-init user code here
        }//GEN-BEGIN:|86-getter|2|
        return backCommand1;
    }
//</editor-fold>//GEN-END:|86-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand2 ">//GEN-BEGIN:|89-getter|0|89-preInit
    /**
     * Returns an initialized instance of backCommand2 component.
     *
     * @return the initialized component instance
     */
    public Command getBackCommand2() {
        if (backCommand2 == null) {//GEN-END:|89-getter|0|89-preInit
            // write pre-init user code here
            backCommand2 = new Command("Back", Command.BACK, 0);//GEN-LINE:|89-getter|1|89-postInit
            // write post-init user code here
        }//GEN-BEGIN:|89-getter|2|
        return backCommand2;
    }
//</editor-fold>//GEN-END:|89-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: Maincommand ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initialized instance of Maincommand component.
     *
     * @return the initialized component instance
     */
    public Command getMaincommand() {
        if (Maincommand == null) {//GEN-END:|92-getter|0|92-preInit
            // write pre-init user code here
            Maincommand = new Command("Main Menu", Command.ITEM, 0);//GEN-LINE:|92-getter|1|92-postInit
            // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return Maincommand;
    }
//</editor-fold>//GEN-END:|92-getter|2|

    /**
     * Returns a display instance.
     *
     * @return the display instance.
     */
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started. Checks whether the MIDlet have been
     * already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     *
     * @param unconditional if true, then the MIDlet has to be unconditionally
     * terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }
}
