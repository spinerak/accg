/*
 * CSGBuilderView.java
 */

package csgbuilder;
import com.sun.opengl.util.Animator;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.media.opengl.*;
import javax.swing.JFileChooser;

/**
 * The application's main frame.
 */
public class CSGBuilderView extends FrameView {

    // HACK
    private CSGTree lvTree;
    
    public OperandViewer mAOperandViewer;
    public OperandViewer mBOperandViewer;
    
    public CSGBuilderView(SingleFrameApplication app) {
        super(app);

        initComponents();
        
        // Init GLJPanlels
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        
        AOperandRenderer lvRenderer = new AOperandRenderer();
        mAOperandViewer = new OperandViewer(lvRenderer,
                new AOperandMIA(lvRenderer, this));
        
        canvas1 = mAOperandViewer.getCanvas();
        jSplitPane1.setLeftComponent(canvas1);
        
		
	// Create a CSG Tree
        lvTree = new CSGTree(new CSGEllipsoid(new double[]{0.5,0.0,0.0}, new double[]{1.0,1.0,1.0}));
        //lvTree.intersect(new CSGEllipsoid(new double[]{0.8,0.8,0.8}, new double[]{1.0,1.0,1.0}));
			
	// Get the mesh for this tree
    mAOperandViewer.setTree(lvTree);
    mAOperandViewer.startPolygonisation();
		
        // Start
	mAOperandViewer.start();
		
		
    OperandViewerRenderer lvRenderer2 = new OperandViewerRenderer();
    mBOperandViewer = new OperandViewer(lvRenderer2, new OperandViewerMIA(lvRenderer2));
        
        //CSGTree lvBOpTree = new CSGTree(new CSGEllipsoid(new double[]{0.0,0.0,0.0}, new double[]{0.5,0.5,0.5}));
        CSGTree lvBOpTree = new CSGTree(new CSGCuboid(new double[]{0.0,0.0,0.0}, new double[]{0.5,0.5,0.5}, new double[]{Math.PI/4, Math.PI/4, Math.PI/4}));
        
        jSplitPane2.setLeftComponent(mBOperandViewer.getCanvas());
        jSplitPane2.setRightComponent(new ObjectPropertyPanel(lvBOpTree, mBOperandViewer));
        jSplitPane1.setRightComponent(jSplitPane2);
        
//	mBOperandViewer.setTree(lvBOpTree);
//    mBOperandViewer.startPolygonisation();
    
	mBOperandViewer.start();
		
	// status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        
        canvas1.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                switch (e.getButton()) {
                    case java.awt.event.MouseEvent.BUTTON3:
                        jDialog1.setVisible(true);
                        break;
                }
            }
        });        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = CSGBuilderApp.getApplication().getMainFrame();
            aboutBox = new CSGBuilderAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        CSGBuilderApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jDialog1 = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridLayout(1, 0));

        jSplitPane1.setMinimumSize(new java.awt.Dimension(150, 150));
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setPreferredSize(new java.awt.Dimension(150, 150));

        jSplitPane2.setName("jSplitPane2"); // NOI18N
        jSplitPane2.setPreferredSize(new java.awt.Dimension(179, 50));
        jSplitPane1.setLeftComponent(jSplitPane2);

        mainPanel.add(jSplitPane1);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jDialog1.setTitle("Object properties");
        jDialog1.setMinimumSize(new java.awt.Dimension(200, 250));
        jDialog1.setModal(true);
        jDialog1.setName("jDialog1"); // NOI18N
        jDialog1.setResizable(false);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(csgbuilder.CSGBuilderApp.class).getContext().getResourceMap(CSGBuilderView.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        setComponent(mainPanel);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    int returnVal = jFileChooser1.showSaveDialog(this.mainPanel);
    
    if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
        javax.swing.JOptionPane.showMessageDialog(this.mainPanel,
            "Error while saving file: could not save to selected file.",
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
        
        return;
    }

    java.io.FileOutputStream fos;    
    java.io.ObjectOutputStream out;
        
    try {
        String filepath = jFileChooser1.getSelectedFile().getAbsolutePath();
        fos = new java.io.FileOutputStream(filepath);
        out = new java.io.ObjectOutputStream(fos);
        out.writeObject(lvTree);
        out.close();
    }
    catch (java.io.IOException ioe) {
        javax.swing.JOptionPane.showMessageDialog(this.mainPanel,
            "Error while saving file: could not save to selected file.",
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
        ioe.printStackTrace();
    }
}//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables


    private javax.media.opengl.GLCanvas canvas1;
    
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
