package net.colors_wind.cglab3.ui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class GameDesignUI {
    private final UIConfig config;
    private JTextField textKd;
    private JTextField textKa;
    private JTextField textKs;
    private JPanel panel;
    private JButton btnOK;
    private JButton btnCancel;
    private JTextField textTranslate;
    private JTextField textRotate;
    private JTextField textScale;
    private JRadioButton rbCactus;
    private JRadioButton rbHorse;
    private JRadioButton rbFlatShading;
    private JRadioButton rbGouraudShading;
    private JRadioButton rbPhongShading;

    private JFrame frame = null;

    public GameDesignUI(UIConfig config) {
        this.config = config;
        btnOK.addActionListener(e -> {
            updateConfig(config);
            config.shouldUpdateGame.set(true);
            frame.setVisible(false);
        });
        btnCancel.addActionListener(e -> {
            frame.setVisible(false);
        });
    }

    public void init() {
        frame = new JFrame();
        frame.setTitle("Settings");
        frame.setContentPane(panel);
        frame.setSize(415, 270);
        frame.setResizable(false);
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void updateAndShowUI(@NotNull UIConfig config) throws InterruptedException, InvocationTargetException {
        synchronized (config.lock) {
            SwingUtilities.invokeAndWait(() -> {
                textKd.setText(UIConfig.vectorToString(config.kd));
                textKa.setText(UIConfig.vectorToString(config.ka));
                textKs.setText(UIConfig.vectorToString(config.ks));
                textTranslate.setText(UIConfig.vectorToString(config.translate));
                textRotate.setText(UIConfig.vectorToString(config.rotation));
                textScale.setText(UIConfig.vectorToString(config.scale));
                switch (config.scene) {
                    case HOUSE:
                        rbHorse.setSelected(true);
                        break;
                    case CACTUS:
                        rbCactus.setSelected(true);
                        break;
                }
                switch (config.shadingMode) {
                    case FLAD_SHADING:
                        rbFlatShading.setSelected(true);
                        break;
                    case GOURAUD_SHADING:
                        rbGouraudShading.setSelected(true);
                        break;
                    case PHONG_SHADING:
                        rbPhongShading.setSelected(true);
                        break;
                }
                show();
            });
        }
    }

    public void updateConfig(@NotNull UIConfig config) {
        synchronized (config.lock) {
            config.ka.set(UIConfig.parseStringVector(textKa.getText()));
            config.kd.set(UIConfig.parseStringVector(textKd.getText()));
            config.ks.set(UIConfig.parseStringVector(textKs.getText()));
            config.translate.set(UIConfig.parseStringVector(textTranslate.getText()));
            config.rotation.set(UIConfig.parseStringVector(textRotate.getText()));
            config.scale.set(UIConfig.parseStringVector(textScale.getText()));
            if (rbHorse.isSelected()) {
                config.scene = UIConfig.Scene.HOUSE;
            } else if (rbCactus.isSelected()) {
                config.scene = UIConfig.Scene.CACTUS;
            }
            if (rbFlatShading.isSelected()) {
                config.shadingMode = UIConfig.ShadingMode.FLAD_SHADING;
            } else if (rbGouraudShading.isSelected()) {
                config.shadingMode = UIConfig.ShadingMode.GOURAUD_SHADING;
            } else if (rbPhongShading.isSelected()) {
                config.shadingMode = UIConfig.ShadingMode.PHONG_SHADING;
            }
            config.shouldUpdateGame.set(true);
        }
    }


    public static void initSwings() {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
