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
import javax.swing.*;

/**
 * The application's main frame.
 */
public class CSGBuilderView extends FrameView {

    // HACK
    private CSGTree lvTree;
    
    public OperandViewer mAOperandViewer;
    public OperandViewer mBOperandViewer;
    
    private JFrame mainFrame = new JFrame("CSGBuilder");
    private JInternalFrame editorFrame = new JInternalFrame("Editor", true, false, true, true);
    private JInternalFrame viewerFrame = new JInternalFrame("Viewer", true, false, true, true);
    private JInternalFrame propertiesFrame = new JInternalFrame("Properties", true, false, false, true);
    private JInternalFrame treeFrame = new JInternalFrame("Tree", true, false, false, true);
    
    public CSGBuilderView(SingleFrameApplication app) {
        super(app);

        initComponents();

        mainFrame.setJMenuBar(jMenuBar1);
        
        // Init GLJPanlels
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        
        AOperandRenderer lvRenderer = new AOperandRenderer();
        mAOperandViewer = new OperandViewer(lvRenderer,
                new AOperandMIA(lvRenderer, this));      
		
	// Create a CSG Tree
        lvTree = new CSGTree(new CSGEllipsoid(new double[]{0.5,0.0,0.0}, new double[]{1.0,1.0,1.0}));
        //lvTree.difference(new CSGEllipsoid(new double[]{0.0,0.0,0.0}, new double[]{0.7,0.7,0.7}));
			
	// Get the mesh for this tree
        //mAOperandViewer.setTree(lvTree);
        //mAOperandViewer.startPolygonisation();
		
        // Start
	mAOperandViewer.start();
		
		
        OperandViewerRenderer lvRenderer2 = new OperandViewerRenderer();
        mBOperandViewer = new OperandViewer(lvRenderer2, new OperandViewerMIA(lvRenderer2));
        
        //CSGTree lvBOpTree = new CSGTree(new CSGEllipsoid(new double[]{0.0,0.0,0.0}, new double[]{0.5,0.5,0.5}));
        CSGTree lvBOpTree = new CSGTree(new CSGEllipsoid(new double[]{0.0,0.0,0.0}, new double[]{0.5,0.5,0.5}, new double[]{Math.PI/4, Math.PI/4, Math.PI/4}));
        viewerFrame.add(mBOperandViewer.getCanvas());
        propertiesFrame.add(new ObjectPropertyPanel(lvBOpTree, mBOperandViewer));

        
//	mBOperandViewer.setTree(lvBOpTree);
//      mBOperandViewer.startPolygonisation();
    
	mBOperandViewer.start();
        
        GLCanvas canvas1 = mAOperandViewer.getCanvas();        
        editorFrame.add(canvas1);		
        canvas1.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                switch (e.getButton()) {
                    case java.awt.event.MouseEvent.BUTTON3:
                        jDialog1.setVisible(true);
                        break;
                }
            }
        });
        
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;        
        
//        mainFrame.setBounds(0, 0, screenWidth, mainFrame.getHeight());        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        int top = mainFrame.getHeight();
        int height = screenHeight - top;
        
        JScrollPane treePane = new JScrollPane(new JTree(mBOperandViewer.getTree().CSGTree2TreeNode()));
        treeFrame.add(treePane);
        
        treeFrame.pack();
        propertiesFrame.pack();
        editorFrame.pack();
        viewerFrame.pack();
//        treeFrame.setBounds(0, top, treeFrame.getWidth(), height);
//        propertiesFrame.setBounds(screenWidth - propertiesFrame.getWidth(), top, propertiesFrame.getWidth(), height);
//        
//        int width = (screenWidth - treeFrame.getWidth() - propertiesFrame.getWidth()) / 2;
        
//        editorFrame.setBounds(0, 0, 400, 400);
//        viewerFrame.setBounds(100, 100, 400, 400);
        
//        mainFrame.setBounds(0,0,screenWidth,screenHeight);
        
//        treeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        editorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        viewerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        propertiesFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

//        treeFrame.setAlwaysOnTop(true);
//        editorFrame.setAlwaysOnTop(true);
//        viewerFrame.setAlwaysOnTop(true);
//        propertiesFrame.setAlwaysOnTop(true);
        
        JDesktopPane desktop = new JDesktopPane();
        desktop.add(treeFrame);
        desktop.add(editorFrame);
        desktop.add(viewerFrame);
        desktop.add(propertiesFrame);
        desktop.setBackground(java.awt.Color.WHITE);
        
        treeFrame.setVisible(true);
        editorFrame.setVisible(true);
        viewerFrame.setVisible(true);
        propertiesFrame.setVisible(true);
        
        mainFrame.setContentPane(desktop);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem4 = new javax.swing.JCheckBoxMenuItem();

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

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N
        jMenuBar1.add(jMenu2);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText(resourceMap.getString("jCheckBoxMenuItem1.text")); // NOI18N
        jCheckBoxMenuItem1.setName("jCheckBoxMenuItem1"); // NOI18N
        jCheckBoxMenuItem1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItem1ItemStateChanged(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem1);

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText(resourceMap.getString("jCheckBoxMenuItem2.text")); // NOI18N
        jCheckBoxMenuItem2.setName("jCheckBoxMenuItem2"); // NOI18N
        jCheckBoxMenuItem2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItem2ItemStateChanged(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem2);

        jCheckBoxMenuItem3.setSelected(true);
        jCheckBoxMenuItem3.setText(resourceMap.getString("jCheckBoxMenuItem3.text")); // NOI18N
        jCheckBoxMenuItem3.setName("jCheckBoxMenuItem3"); // NOI18N
        jCheckBoxMenuItem3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItem3ItemStateChanged(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem3);

        jCheckBoxMenuItem4.setSelected(true);
        jCheckBoxMenuItem4.setText(resourceMap.getString("jCheckBoxMenuItem4.text")); // NOI18N
        jCheckBoxMenuItem4.setName("jCheckBoxMenuItem4"); // NOI18N
        jCheckBoxMenuItem4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItem4ItemStateChanged(evt);
            }
        });
        jMenu3.add(jCheckBoxMenuItem4);

        jMenuBar1.add(jMenu3);
    }// </editor-fold>//GEN-END:initComponents
    
private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    int returnVal = jFileChooser1.showSaveDialog(this.mainFrame);
    
    if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
        javax.swing.JOptionPane.showMessageDialog(this.mainFrame,
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
        javax.swing.JOptionPane.showMessageDialog(this.mainFrame,
            "Error while saving file: could not save to selected file.",
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
        ioe.printStackTrace();
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed

}//GEN-LAST:event_jMenu3ActionPerformed

private void jCheckBoxMenuItem1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ItemStateChanged
    javax.swing.JCheckBoxMenuItem src = (javax.swing.JCheckBoxMenuItem)evt.getSource();
         
    try {
        editorFrame.setIcon(!src.getState());
    }
    catch (java.beans.PropertyVetoException e) {}
}//GEN-LAST:event_jCheckBoxMenuItem1ItemStateChanged

private void jCheckBoxMenuItem2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem2ItemStateChanged
    javax.swing.JCheckBoxMenuItem src = (javax.swing.JCheckBoxMenuItem)evt.getSource();
     
    try {
        viewerFrame.setIcon(!src.getState());
    }
    catch (java.beans.PropertyVetoException e) {}
}//GEN-LAST:event_jCheckBoxMenuItem2ItemStateChanged

private void jCheckBoxMenuItem3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem3ItemStateChanged
    javax.swing.JCheckBoxMenuItem src = (javax.swing.JCheckBoxMenuItem)evt.getSource();
     
    try {
        treeFrame.setIcon(!src.getState());
    }
    catch (java.beans.PropertyVetoException e) {}
}//GEN-LAST:event_jCheckBoxMenuItem3ItemStateChanged

private void jCheckBoxMenuItem4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem4ItemStateChanged
    javax.swing.JCheckBoxMenuItem src = (javax.swing.JCheckBoxMenuItem)evt.getSource();
     
    try {
        propertiesFrame.setIcon(!src.getState());
    }
    catch (java.beans.PropertyVetoException e) {}
}//GEN-LAST:event_jCheckBoxMenuItem4ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem4;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    // End of variables declaration//GEN-END:variables
}
