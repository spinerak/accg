/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csgbuilder;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author s040379
 */
public class CSGSaveLoadDialog {
    private static JFileChooser instance;
      
    private static JFileChooser getSaveLoadDialog() {
	if (instance == null) {
	    instance = new JFileChooser();
	    instance.setFileFilter(new FileNameExtensionFilter("CSGBuilder objects", "csgb"));
	}
	
	return instance;
    }
    
    public static File getSelectedFile() {
	return getSaveLoadDialog().getSelectedFile();
    }
    
    public static int showOpenDialog(JFrame frame) {
	return getSaveLoadDialog().showOpenDialog(frame);
    }
    
    public static int showSaveDialog(JFrame frame) {
	return getSaveLoadDialog().showSaveDialog(frame);
    }
}
