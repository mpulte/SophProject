package com.discordbot;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Main extends JPanel {

    private static final String TOKEN = "MjY4OTQ3MzIyNTI2NzYwOTYx.C1iThg.TivcxnvyChBaMJ9wOZMXJsRQ1k0";

    Main() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        String text = "A statement".toUpperCase();

        g.setFont(new Font("Impact", Font.PLAIN, 20));
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());

        Rectangle2D rectTop = new Rectangle2D.Double(10, 5, getWidth() - 10, getHeight() / 5);
        Rectangle2D rectBot = new Rectangle2D.Double(10, getHeight() * 4 / 5 - 9, getWidth() - 10, getHeight() / 5);

        addTextToGraphics((Graphics2D) g, rectTop, text, Position.TOP);
        addTextToGraphics((Graphics2D) g, rectBot, text, Position.BOTTOM);

        int x = getWidth() / 2;
        int y = getHeight() / 2;
        g.setColor(Color.RED);
        g.drawLine(x, 0, x, getHeight());
        g.drawLine(0, y, getWidth(), y);
    }

    public void addTextToGraphics(Graphics2D graphics2D, Rectangle2D bounds, String text, Position position) {
        // create a new graphics so that we can add text without changing graphic2D's settings
        Graphics2D graphics = (Graphics2D)graphics2D.create();
        FontMetrics fontMetrics = graphics.getFontMetrics();

//        Font font = graphics.getFont();
        double scale = Math.min(bounds.getHeight() / fontMetrics.getHeight() * 0.95,
                bounds.getWidth() / fontMetrics.stringWidth(text) * 0.95);
        Font font = graphics.getFont().deriveFont(AffineTransform.getScaleInstance(scale, scale));
        FontRenderContext frc = graphics.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);

//        double scale = Math.min(bounds.getHeight() / (textLayout.getBounds().getHeight() - getAlignmentY()),
//                bounds.getWidth() / (textLayout.getBounds().getWidth() - getAlignmentX()));

        Shape outline = textLayout.getOutline(null);
        Rectangle2D outlineBounds = outline.getBounds2D();

//        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        AffineTransform transform = new AffineTransform();
        transform.translate(getWidth() / 2 - (outlineBounds.getWidth() / 2),
                getHeight() / 2 + (outlineBounds.getHeight() / 2));
        graphics.transform(transform);

        // draw the shape
        graphics.setColor(Color.WHITE);
        graphics.fill(outline);
        graphics.setColor(Color.BLACK);
        graphics.draw(outline);
    }

    private enum Position { TOP, BOTTOM }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("test");
                frame.add(new Main());
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
