package com.discordbot.command;

import com.discordbot.util.IOUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Command(tag = "meme")
public class MemeCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("MemeCommand");

    private static final String[] extensions = {"png", "jpg", "jpeg", "gif"};

    public MemeCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();
        java.util.List<String> args = event.getArgs();

        if (args.isEmpty() || args.get(0).equalsIgnoreCase("help") || args.get(0).equalsIgnoreCase("list")) {
            // they want to know what memes are available
            Path path = IOUtils.getResourcePath("meme");

            // make sure path exists
            if (!path.toFile().exists()) {
                if (path.toFile().mkdirs()) {
                    LOG.warn("Resource folder did not exist, created at: " + path.toString());
                    return;
                } else {
                    LOG.warn("Resource folder did not exist and could not be created. Check " + path.toString());
                    return;
                }
            }

            // get a list of files
            File[] fileList = path.toFile().listFiles();

            // make sure the file list isn't empty
            if (fileList == null || fileList.length == 0) {
                channel.sendMessage("No memes available, your admins need to step up their game!").queue();
                return;
            }

            // send a message with the list of files
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("Memes:");
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.contains(".") && !fileName.endsWith(".")) {
                    String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                    for (String ext : extensions) {
                        if (ext.equals(extension)) {
                            messageBuilder.append("\n").append(fileName.substring(0, fileName.lastIndexOf('.')));
                        }
                    }
                }
            }
            channel.sendMessage(messageBuilder.build()).queue();
        } else if (args.size() >= 2) {
            // they want a meme, so let's give them one
            new Thread(() -> {
                String fileName = args.get(0);

                for (String ext : extensions) {
                    try {
                        // load the meme
                        File file = IOUtils.getResourcePath("meme", fileName + '.' + ext).toFile();
                        if (!file.exists()) {
                            continue;
                        }
                        final BufferedImage image = ImageIO.read(file);

                        // get the meme graphics and bounds
                        Graphics2D graphics = (Graphics2D) image.getGraphics();
                        Rectangle2D topBounds = new Rectangle2D.Double(5, 5,
                                image.getWidth() - 10, image.getHeight() / 4);
                        Rectangle2D bottomBounds = new Rectangle2D.Double(5, image.getHeight() - 5,
                                image.getWidth() - 10, image.getHeight() / 4);

                        // add text to the meme
                        graphics.setFont(new Font("Impact", Font.PLAIN, 30));
                        addTextToGraphics(graphics, topBounds, args.get(1).toUpperCase(), Position.TOP);
                        if (args.size() >= 3) {
                            addTextToGraphics(graphics, bottomBounds, args.get(2).toUpperCase(), Position.BOTTOM);
                        }
                        graphics.dispose();

                        // convert the meme to an InputStream
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);

                        // send the meme
                        channel.sendFile(baos.toByteArray(), args.get(0) + ".png", (new MessageBuilder()).append("...").build()).queue();
                        return;
                    } catch (IOException e) {
                        LOG.log(e);
                    }
                }
                channel.sendMessage("There are no memes by the name " + args.get(0)).queue();
        }).start();
        } else {
            // no text supplied
            channel.sendMessage("Blank memes aren't fun").queue();
        }
    }

    public void addTextToGraphics(Graphics2D graphics2D, Rectangle2D bounds, String text, Position position) {
        // create a new graphics so that we can add text without changing graphic2D's settings
        Graphics2D graphics = (Graphics2D)graphics2D.create();

        // calculate the scale of the text
        Font font = graphics.getFont();
        FontRenderContext frc = graphics.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);

        double scale = Math.min(bounds.getHeight() / (textLayout.getBounds().getHeight()),
                bounds.getWidth() / (textLayout.getBounds().getWidth()));

        // convert the text to a shape
        Shape outline = textLayout.getOutline(null);
        Rectangle2D outlineBounds = outline.getBounds2D();

        // set the position to draw the text
        double xPos = bounds.getX() / scale + bounds.getWidth() / scale / 2 - outlineBounds.getWidth() / 2
                - outlineBounds.getX();
        double yPos = bounds.getY() / scale + outlineBounds.getHeight() * (position == Position.TOP ? 0 : -1)
                - outlineBounds.getY();
        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        transform.translate(xPos, yPos);
        graphics.transform(transform);

        // draw the text
        graphics.setColor(Color.WHITE);
        graphics.fill(outline);
        graphics.setColor(Color.BLACK);
        graphics.draw(outline);
        graphics.dispose();
    }

    @Override
    public boolean usesChannel(ChannelType type) {
        return true;
    }

    @Override
    public String getDescription() {
        return "Posts a meme with the caption you give it.";
    }

    @Override
    public String getHelp() {
        return "Type the name of the meme followed by the caption.";
    }

    private enum Position { TOP, BOTTOM }

}