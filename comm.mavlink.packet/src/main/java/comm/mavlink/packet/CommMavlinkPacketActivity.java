package comm.mavlink.packet;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import interactivespaces.activity.impl.BaseActivity;

import interactivespaces.service.comm.serial.SerialCommunicationEndpoint;
import interactivespaces.service.comm.serial.SerialCommunicationEndpointService;
import interactivespaces.util.concurrency.CancellableLoop;
import interactivespaces.util.resource.ManagedResourceWithTask;

import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
/**
 * A simple Interactive Spaces Java-based activity.
 */
public class CommMavlinkPacketActivity extends BaseActivity {

	MAVLinkReader mavReader ;
	MAVLinkMessage mavMessage;
	private SerialCommunicationEndpoint serial;
	private InputStream serialInput;
	private byte [] serialData ;
    @Override
    public void onActivitySetup() {
        getLog().info("Activity comm.mavlink.packet setup");
        SerialCommunicationEndpointService serialService= getSpaceEnvironment().getServiceRegistry().getRequiredService(SerialCommunicationEndpointService.SERVICE_NAME);
        String portName = getConfiguration().getRequiredPropertyString("space.hardware.serial.port");
        serial = serialService.newSerialEndpoint(portName);
        serial.setBaud(115200);
        serial.setInputBufferSize(5000);
        serial.setOutputBufferSize(1000);
        
        ManagedResourceWithTask temp = new ManagedResourceWithTask(serial, new CancellableLoop() {
			
			@Override
			protected void loop() throws InterruptedException {
				// TODO Auto-generated method stub
				doTask();
			}
			protected void handleException(Exception e)
			{
				getLog().error("Error " +e);
			}
		}, getSpaceEnvironment());
        addManagedResource(temp);
    }

    @Override
    public void onActivityStartup() {
        getLog().info("Activity comm.mavlink.packet startup");
    }

    @Override
    public void onActivityPostStartup() {
        getLog().info("Activity comm.mavlink.packet post startup");
    }

    @Override
    public void onActivityActivate() {
        getLog().info("Activity comm.mavlink.packet activate");
        //serial.write(1);
        
    }

    @Override
    public void onActivityDeactivate() {
        getLog().info("Activity comm.mavlink.packet deactivate");
        //serial.shutdown();
    }

    @Override
    public void onActivityPreShutdown() {
        getLog().info("Activity comm.mavlink.packet pre shutdown");
    }

    @Override
    public void onActivityShutdown() {
        getLog().info("Activity comm.mavlink.packet shutdown");
    }

    @Override
    public void onActivityCleanup() {
        getLog().info("Activity comm.mavlink.packet cleanup");
    }
    
    private void doTask()
    {
		serialData =new byte[1000];
		serial.read(serialData);
		//getLog().info(serial.available());
		serialInput = new ByteArrayInputStream(serialData);
		mavReader = new MAVLinkReader(new DataInputStream(serialInput));
		try {
			while (serialInput.available() > 0) {
			    MAVLinkMessage msg = null;
				try {
					msg = mavReader.getNextMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					getLog().error("Error " +e);
				}
			   //MAVLinkMessage msg = mavReader.getNextMessageWithoutBlocking();
			    if (msg != null) {
			        getLog().info("SysId=" + msg.sysId + " CompId=" + msg.componentId + " seq=" + msg.sequence + " " + msg.toString());
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			getLog().error("Error " +e);
		}
		//String tempStr =new String(serialData);
		//getLog().info("Received :  " +  tempStr);
    }
}
