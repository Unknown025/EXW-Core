package org.rainyville.exw.core;

import com.google.common.base.MoreObjects;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.Loader;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class CrashReporter {
    /**
     * Called when a crash occurs on the client.
     *
     * @param crash CrashReport instance.
     */
    public static void onCrash(CrashReport crash) {
        // Allow for the reporter to be disabled.
        if (System.getProperties().contains("exw.disableReporter")) return;

        EXWLoadingPlugin.LOGGER.fatal("Crash detected - {}", crash.getFile().getName());

        String exwVer = null;
        if (!Loader.isModLoaded("exw") && EXWClassTransformer.OBFUSCATED) {
            EXWLoadingPlugin.LOGGER.error("Expansive Weaponry not detected, skipping reporter");
            return;
        } else {
            try {
                Class<?> clazz = Class.forName("org.rainyville.modulus.ExpansiveWeaponry");
                Field field = clazz.getField("VERSION");
                exwVer = (String) field.get(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                EXWLoadingPlugin.LOGGER.error("Could not get version string from Expansive Weaponry");
            }
        }

        // If for some reason we're running in a server environment
        if (GraphicsEnvironment.isHeadless() || !promptReportDialog(crash.getCompleteReport())) {
            EXWLoadingPlugin.LOGGER.info("User skipped reporting crash");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://rainyville.org/api/v2/exw/crash");
            // Set for analytical purposes.
            post.setHeader("User-Agent", String.format("Java/%s EXW/%s EXWC/%s", System.getProperty("java.version"), MoreObjects.firstNonNull(exwVer, "NA"), EXWCoreDummyContainer.VERSION));
            List<NameValuePair> params = new ArrayList<>(2);
            params.add(new BasicNameValuePair("filename", crash.getFile().getName()));
            params.add(new BasicNameValuePair("content", crash.getCompleteReport()));
            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            HttpResponse response = client.execute(post);
            // Allow for the status code to change in the future.
            int code = response.getStatusLine().getStatusCode();
            if (code < HttpStatus.SC_OK || code >= HttpStatus.SC_MULTIPLE_CHOICES) {
                EXWLoadingPlugin.LOGGER.error("Could not upload crash report: {}", response.getStatusLine());
            } else {
                EXWLoadingPlugin.LOGGER.info("Crash report submitted");
            }
        } catch (IOException ex) {
            EXWLoadingPlugin.LOGGER.error("Error occurred during crash report upload", ex);
        }
    }

    public static boolean promptReportDialog(String content) {
        // Necessary on Linux systems.
        Mouse.setGrabbed(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            EXWLoadingPlugin.LOGGER.error("Could not set look and feel", ex);
        }

        AtomicBoolean result = new AtomicBoolean(false);

        final JDialog overlay = new JDialog();
        overlay.setUndecorated(true);
        try {
            if (Display.isFullscreen()) {
                overlay.setBounds(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
            } else {
                overlay.setBounds(Display.getX(), Display.getY(),
                        Display.getWidth(), Display.getHeight());
            }
            overlay.setBackground(new Color(0, 0, 0, 1));
            overlay.setAlwaysOnTop(true);
            overlay.setVisible(true);
        } catch (Throwable ignored) {
        }

        final JDialog frame = new JDialog(overlay);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                overlay.dispose();
            }
        });

        JTextArea textArea = new JTextArea();
        textArea.setText(content);
        textArea.setEditable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 0, 10));
        contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton report = new JButton("Report");
        report.addActionListener(e -> {
            result.set(true);
            frame.dispose();
        });
        buttonPane.add(report);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            result.set(false);
            frame.dispose();
        });
        buttonPane.add(cancelButton);

        contentPane.add(buttonPane, BorderLayout.SOUTH);

        SwingUtilities.getRootPane(frame).setDefaultButton(report);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setSize(650, 700);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setTitle("[EXW]: Minecraft Crash Reporter");
        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return result.get();
    }
}
