package com.github.cosycode.ext.swing.comp;

import com.github.cosycode.common.base.IValGetter;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/8/12
 * </p>
 *
 * @author CPF
 **/
public class JField extends JPanel implements IValGetter<String> {

    @Getter
    JTextField jTextField;
    @Getter
    private JLabel jLabel;

    public JField(String label) {
        initComponents(label);
    }

    public JField(String label, boolean readOnly) {
        initComponents(label);
        if (readOnly) {
            setEditable(false);
            getJTextField().setBorder(null);
        }
    }

    private void initComponents(String label) {
        final GridBagLayout mgr = new GridBagLayout();
        mgr.columnWidths = new int[]{0, 0, 0};
        mgr.rowHeights = new int[]{0, 0};
        mgr.columnWeights = new double[]{0.0, 0.0, 1.0E-4};
        mgr.rowWeights = new double[]{0.0, 1.0E-4};
        setLayout(mgr);

        jLabel = new JLabel(label);
        jTextField = new JTextField();
        jLabel.setLabelFor(jTextField);
        jLabel.setLabelFor(jTextField);
        jLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        add(jLabel, new GridBagConstraints(0, 0, 1, 1, 4.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        add(jTextField, new GridBagConstraints(1, 0, 1, 1, 8.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }


    public void setText(String text) {
        jTextField.setText(text);
    }

    public void setEditable(boolean readOnly) {
        jTextField.setEditable(readOnly);
    }

    @Override
    public String getVal() {
        return jTextField.getText();
    }
}
