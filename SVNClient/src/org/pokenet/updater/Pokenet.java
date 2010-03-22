package org.pokenet.updater;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNEventAdapter;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class Pokenet extends JFrame {
	private static final long serialVersionUID = -6147249683774678347L;
	public static final String SVN_URL = "pokenet-release.svn.sourceforge.net/svnroot/pokenet-release";
	public static final String FOLDER_NAME = "pokenet-release";
	private String HEADER_IMAGE_URL = "http://pokedev.org/header.png";;
	
	JTextArea outText;
	JScrollPane scrollPane;
	
	public Pokenet() {
		super("PokeNet Game Launcher and Updater");
		this.setSize(740, 470);

		/* Center the updater */
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() / 2) - this.getWidth() / 2);
		int y = (int) ((d.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		
		outText = new JTextArea();
		outText.setEditable(false);
		outText.append("Console Information:\n");
		
		scrollPane = new JScrollPane(outText);
		scrollPane.setAutoscrolls(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		ImageIcon m_logo;
		JLabel l = null;

		try {
			m_logo = new ImageIcon(new URL(HEADER_IMAGE_URL ));
			l = new JLabel(m_logo);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.add(scrollPane, BorderLayout.CENTER);		
        this.add(l, BorderLayout.PAGE_START);
		this.setVisible(true);
		
		DAVRepositoryFactory.setup();
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
	
		 /* 
	     * Creates an instance of SVNClientManager providing authentication 
	     * information (name, password) and an options driver 
	     */ 
		SVNClientManager ourClientManager = SVNClientManager.newInstance(options); 
	    SVNUpdateClient updateClient = ourClientManager.getUpdateClient(); 

	    /*
	     * Creates the event handler to display information
	     */
	    ourClientManager.setEventHandler(
	    		new SVNEventAdapter(){
	    			public void handleEvent(SVNEvent event, double progress){
	    				SVNEventAction action = event.getAction();
	    				File curFile = event.getFile();
	    				String curDir = System.getProperty("user.dir");
	    				
	    				String path = curFile.getAbsolutePath().substring(curDir.length());
	    				
						if (action == SVNEventAction.ADD ||
								action == SVNEventAction.UPDATE_ADD){
//		    					outText.append("Downloading " + event.getFile().getName() + '\n');
								
								outText.append("Downloading " + path + '\n');
							
							outText.setCaretPosition(outText.getDocument().getLength());
							System.out.println("Downloading " + curFile.getName());
						} if (action == SVNEventAction.STATUS_COMPLETED ||
								action == SVNEventAction.UPDATE_COMPLETED){
							outText.append("Download completed. Launching client!");
							outText.setCaretPosition(outText.getDocument().getLength());
							System.out.println("Download completed. Launching client!");
						}
	    			}
	    		}
	    );	    	

	    
	    /* 
	     * sets externals not to be ignored during the checkout 
	     */ 
	    updateClient.setIgnoreExternals(false); 
		
	    /* 
	     * A url of a repository to check out 
	     */ 
	    SVNURL url = null;
		try {
			url = SVNURL.parseURIDecoded("http://" + SVN_URL);
		} catch (SVNException e2) {
			e2.printStackTrace();
		} 
	    /* 
	     * A revision to check out 
	     */ 
	    SVNRevision revision = SVNRevision.HEAD; 

	    /* 
	     * A revision for which you're sure that the url you specify is 
	     * exactly what you need 
	     */ 
	    SVNRevision pegRevision = SVNRevision.HEAD; 

	    /* 
	     * A local path where a Working Copy will be ckecked out 
	     */ 
	    File destPath = new File(FOLDER_NAME); 

	    /* 
	     * returns the number of the revision at which the working copy is 
	     */ 
	    try {
			
			boolean exists = destPath.exists();
			if(!exists) {
				outText.append("Installing...\nPlease be patient while PokeNet is downloaded...\n");
				System.out.println("Installing...");
				updateClient.doCheckout(url, destPath, pegRevision, 
                        revision, SVNDepth.INFINITY, true);
			} else {
				ourClientManager.getWCClient().doCleanup(destPath);
				outText.append("Updating...\n");
				System.out.println("Updating...\n");
				updateClient.doUpdate(destPath, revision, SVNDepth.INFINITY, true, true);
			}
	    } catch (SVNException e1) {
			e1.printStackTrace();
		} 
		
		this.outText.append("Launching...\n");

		/* Launch the game */
		try {
			this.setVisible(false);
			runPokenet();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while running the game.");
			System.exit(0);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Pokenet();
	}
	
	public void runPokenet() throws Exception {
		String curDir = "\"" + System.getProperty("user.dir") + "\"";
		String libraryPath = "-Djava.library.path=" + curDir + "/" + FOLDER_NAME + "/lib/native" ;
		String resPath = "-Dres.path=" + curDir + "/" + FOLDER_NAME + "/";
		String specialArg ="-Xmx512m -Xms512m -jar"; 
		String javaArg = curDir + "/" + FOLDER_NAME + "/Pokenet.jar";
		String execComm = "java " + libraryPath + " " + resPath + " " + specialArg + " " + javaArg;
		Process p = Runtime.getRuntime().exec(execComm);
		StreamReader r1 = new StreamReader(p.getInputStream(), "OUTPUT");
		StreamReader r2 = new StreamReader(p.getErrorStream(), "ERROR");
		new Thread(r1).start();
		new Thread(r2).start();
	}
}