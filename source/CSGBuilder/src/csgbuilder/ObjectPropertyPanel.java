/*
 * ObjectPropertyPanel.java
 *
 * Created on 18 september 2008, 13:06
 */

package csgbuilder;

/**
 *
 * @author  s040379
 */
public class ObjectPropertyPanel extends javax.swing.JPanel {
    private CSGTree tree;
    private OperandViewer viewer;

    private java.util.HashMap<String, CSGTree> loadedTrees = 
        new java.util.HashMap<String, CSGTree>();
    
    /** Creates new form ObjectPropertyPanel */
    public ObjectPropertyPanel(CSGTree tree, OperandViewer viewer) {
        initComponents();
        setCSGObject(tree);
        setViewer(viewer);      
        resetValues();
    }
    
    public void setCSGObject(CSGTree tree) { 
        this.tree = tree;
        
        positionXSpinner.setEnabled(tree.isMovable());
        positionYSpinner.setEnabled(tree.isMovable());
        positionZSpinner.setEnabled(tree.isMovable());
        
        rotationXSpinner.setEnabled(tree.isRotatable());
        rotationYSpinner.setEnabled(tree.isRotatable());
        rotationZSpinner.setEnabled(tree.isRotatable());
        
        widthSpinner.setEnabled(tree.isResizable());
        heightSpinner.setEnabled(tree.isResizable());
        lengthSpinner.setEnabled(tree.isResizable());
        
 //       resetValues();
    }
    
    public void setViewer(OperandViewer viewer) {
        this.viewer = viewer;
        //setMesh();
    }
    
    private void setMesh() {        
        //polygoniser.setTree(tree);
        viewer.setTree(tree);
        viewer.startPolygonisation();
        //polygoniser.setViewer(viewer);
        //polygoniser.start();
    }
    
    private double[] getPosition() {
        double x = ((Double)positionXSpinner.getValue()).doubleValue();
        double y = ((Double)positionYSpinner.getValue()).doubleValue();
        double z = ((Double)positionZSpinner.getValue()).doubleValue();
        return new double[]{x, y, z};
    }
    
    private double[] getRotation() {
        double x = Math.toRadians(((Double)rotationXSpinner.getValue()).doubleValue());
        double y = Math.toRadians(((Double)rotationYSpinner.getValue()).doubleValue());
        double z = Math.toRadians(((Double)rotationZSpinner.getValue()).doubleValue());
        return new double[]{x, y, z};
    }
    
    private double[] getDimensions() {
        double x = ((Double)widthSpinner.getValue()).doubleValue();
        double y = ((Double)heightSpinner.getValue()).doubleValue();
        double z = ((Double)lengthSpinner.getValue()).doubleValue();
        return new double[]{x, y, z};        
    }
    
    /**
     * Resets the values of the components on this panel.
     */
    public void resetValues() {
        widthSpinner.setValue(tree.getDimensions()[0]);
        heightSpinner.setValue(tree.getDimensions()[1]);
        lengthSpinner.setValue(tree.getDimensions()[2]);
        
        rotationXSpinner.setValue(Math.toDegrees(tree.getRotation()[0]));
        rotationYSpinner.setValue(Math.toDegrees(tree.getRotation()[1]));
        rotationZSpinner.setValue(Math.toDegrees(tree.getRotation()[2]));
        
        positionXSpinner.setValue(tree.getPosition()[0]);
        positionYSpinner.setValue(tree.getPosition()[1]);
        positionZSpinner.setValue(tree.getPosition()[2]);
    }    

    class DoubleSpinnerModel implements javax.swing.SpinnerModel {
        private java.util.ArrayList<javax.swing.event.ChangeListener> changeListeners;
        
        private double min;
        private double max;
        private double step;
        private double value;
        
        public DoubleSpinnerModel(double min, double max, double step, double value) {
            this.min   = min;
            this.max   = max;
            this.step  = step;
            this.value = value;
            
            changeListeners = new java.util.ArrayList<javax.swing.event.ChangeListener>();
        }
        
        public void addChangeListener(javax.swing.event.ChangeListener l) {
            changeListeners.add(l);
        }
        
        public void removeChangeListener(javax.swing.event.ChangeListener l) {
            changeListeners.remove(l);
        }
        
        public Double getPreviousValue() {
            if (value - step < min) {
                return new Double(value);
            }
            else {
                return new Double(value - step);
            }
        }
        
        public Double getNextValue() {
            if (value + step > max) {
                return new Double(value);
            }
            else {
                return new Double(value + step);
            }
        }
        
        public void setValue(Object value) throws IllegalArgumentException  {
            if (value instanceof Double) {
                double v = ((Double)value).doubleValue();
                
                if ((v >= min) && (v <= max)) {
                    this.value = v;
                    stateChanged();
                    return;
                }                
            }
            
            throw new IllegalArgumentException();
        }
        
        public Double getValue() {
            return new Double(value);
        }
        
        private void stateChanged() {
            for (javax.swing.event.ChangeListener l : changeListeners) {
                l.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        positionXSpinner = new javax.swing.JSpinner();
        positionYSpinner = new javax.swing.JSpinner();
        positionZSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        widthSpinner = new javax.swing.JSpinner();
        heightSpinner = new javax.swing.JSpinner();
        lengthSpinner = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        rotationXSpinner = new javax.swing.JSpinner();
        rotationYSpinner = new javax.swing.JSpinner();
        rotationZSpinner = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        objectComboBox = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(csgbuilder.CSGBuilderApp.class).getContext().getResourceMap(ObjectPropertyPanel.class);
        setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("Form.border.title"))); // NOI18N
        setName("Form"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        positionXSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        positionXSpinner.setName("positionXSpinner"); // NOI18N
        positionXSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        positionYSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        positionYSpinner.setName("positionYSpinner"); // NOI18N
        positionYSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        positionZSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        positionZSpinner.setName("positionZSpinner"); // NOI18N
        positionZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(positionZSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(positionYSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(positionXSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(positionXSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(positionYSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(positionZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        widthSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        widthSpinner.setName("widthSpinner"); // NOI18N
        widthSpinner.setValue(0.5);
        widthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        heightSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        heightSpinner.setName("heightSpinner"); // NOI18N
        heightSpinner.setValue(0.5);
        heightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        lengthSpinner.setModel(new DoubleSpinnerModel(-10.0, 10.0, 0.1, 0.0));
        lengthSpinner.setName("lengthSpinner"); // NOI18N
        lengthSpinner.setValue(0.5);
        lengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lengthSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(heightSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(widthSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(widthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(heightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        rotationXSpinner.setModel(new DoubleSpinnerModel(0.0,360.0, 1.0, 0.0));
        rotationXSpinner.setName("rotationXSpinner"); // NOI18N
        rotationXSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        rotationYSpinner.setModel(new DoubleSpinnerModel(0.0,360.0, 1.0, 0.0));
        rotationYSpinner.setName("rotationYSpinner"); // NOI18N
        rotationYSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        rotationZSpinner.setModel(new DoubleSpinnerModel(0.0,360.0, 1.0, 0.0));
        rotationZSpinner.setName("rotationZSpinner"); // NOI18N
        rotationZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerPropertyChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotationXSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(rotationYSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(rotationZSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(rotationXSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(rotationYSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(rotationZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        objectComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cuboid", "Ellipsoid" }));
        objectComboBox.setName("objectComboBox"); // NOI18N
        objectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                objectPropertyHandler(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(csgbuilder.CSGBuilderApp.class).getContext().getActionMap(ObjectPropertyPanel.class, this);
        jButton1.setAction(actionMap.get("loadObject")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadObject(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(objectComboBox, 0, 112, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(objectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void objectPropertyHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_objectPropertyHandler
    if (evt.getSource().equals(objectComboBox)) {
        javax.swing.JComboBox source = (javax.swing.JComboBox)evt.getSource();
        
        if (source.getSelectedItem().toString().equals("Cuboid")) {
            setCSGObject(new CSGTree(new CSGCuboid(new double[] {0,0,0}, new double[] {0.5,0.5,0.5}, new double[] {0,0,0})));
        }
        else if (source.getSelectedItem().toString().equals("Ellipsoid")) {
            setCSGObject(new CSGTree(new CSGEllipsoid(new double[] {0,0,0}, new double[] {0.5,0.5,0.5}, new double[] {0,0,0})));
        }
        else {
            setCSGObject(loadedTrees.get(source.getSelectedItem().toString()));
        }
        
        setMesh();
    }
}//GEN-LAST:event_objectPropertyHandler

private void spinnerPropertyChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerPropertyChanged
    javax.swing.JSpinner source = (javax.swing.JSpinner)evt.getSource(); 
    double value = ((Double)source.getValue()).doubleValue();
    
    if (evt.getSource().equals(widthSpinner) ||
        evt.getSource().equals(heightSpinner) ||
        evt.getSource().equals(lengthSpinner)) {
        tree.resize(getDimensions());
        setMesh();
    }
    else if (evt.getSource().equals(positionXSpinner) ||
             evt.getSource().equals(positionYSpinner) ||
             evt.getSource().equals(positionZSpinner)) {
        tree.move(getPosition());
        setMesh();
    }
    else if (evt.getSource().equals(rotationXSpinner) ||
             evt.getSource().equals(rotationYSpinner) ||
             evt.getSource().equals(rotationZSpinner)) {
        tree.rotate(getRotation());       
        setMesh();
    }
}//GEN-LAST:event_spinnerPropertyChanged

private void loadObject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadObject
    int returnVal = jFileChooser1.showOpenDialog(this);
    
    if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Error while opening file: the selected file could not be opened.",
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
        
        return;
    }

    java.io.FileInputStream fis;    
    java.io.ObjectInputStream in;
    CSGTree csgTree;
        
    try {
        String filepath = jFileChooser1.getSelectedFile().getAbsolutePath();
        String filename = jFileChooser1.getSelectedFile().getName();
        fis = new java.io.FileInputStream(filepath);
        in  = new java.io.ObjectInputStream(fis);
        csgTree = (CSGTree)in.readObject();
        fis.close();
        in.close();

        objectComboBox.addItem(filename);
        loadedTrees.put(filename, csgTree);
    }
    catch(java.io.IOException ioe) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Error while opening file: the selected file could not be opened.",
            "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
                
        ioe.printStackTrace();
        System.exit(-1);
    }
    catch(ClassNotFoundException cnfe) {
        cnfe.printStackTrace();
        System.exit(-1);
    }
}//GEN-LAST:event_loadObject
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner heightSpinner;
    private javax.swing.JButton jButton1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSpinner lengthSpinner;
    private javax.swing.JComboBox objectComboBox;
    private javax.swing.JSpinner positionXSpinner;
    private javax.swing.JSpinner positionYSpinner;
    private javax.swing.JSpinner positionZSpinner;
    private javax.swing.JSpinner rotationXSpinner;
    private javax.swing.JSpinner rotationYSpinner;
    private javax.swing.JSpinner rotationZSpinner;
    private javax.swing.JSpinner widthSpinner;
    // End of variables declaration//GEN-END:variables
}
