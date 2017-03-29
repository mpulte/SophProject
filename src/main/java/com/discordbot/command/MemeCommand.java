package com.discordbot.command;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class MemeCommand extends CommandListener {
	
	private static final String[] extensions = {"png", "jpg", "jpeg", "gif"};

	public MemeCommand(CommandHandler handler) {
		super(handler);
	}

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();
		java.util.List<String> args = event.getArgs();
		
		if (args.size() >= 2) {
			String fileName = args.get(0);
			
			for (String ext : extensions) {
				try {
					File path = new File(System.getProperty("user.home") + "/meme/" + fileName + "." + ext);
					final BufferedImage image = ImageIO.read(path);

					Graphics g = image.getGraphics();
					g.setFont(g.getFont().deriveFont(20f));
					g.drawString(String.join(" " + "\n", args.subList(1, args.size())), 50, 450);
					g.dispose();
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(image, "png", baos);
					InputStream is = new ByteArrayInputStream(baos.toByteArray());
					
					channel.sendFile(is, "Batman.png", (new MessageBuilder()).append("A message").build()).queue();
					break;
				} catch (IOException ignore) {
					continue;
				}
				// TODO: meme doesn't exist
			}
		} else if (!args.isEmpty() && (args.get(0).equalsIgnoreCase("help") || args.get(0).equalsIgnoreCase("list"))) {
			File folder = new File(System.getProperty("user.home") + "/meme");
			File[] fileList = folder.listFiles();
			
			MessageBuilder messageBuilder = new MessageBuilder();
			messageBuilder.append("Memes:");
			
			for (File file : fileList) {
				String fileName = file.getName();
				if (fileName.contains(".") && !fileName.endsWith(".")) {
					String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
					for (String ext : extensions) {
						if (ext.equals(extension)) {
							messageBuilder.append("\n" + fileName.substring(0, fileName.lastIndexOf('.')));
						}
					}
				}
			}
			
			channel.sendMessage(messageBuilder.build()).queue();
		}
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

}