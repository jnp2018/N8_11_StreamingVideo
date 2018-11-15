package messengerclient;

import com.github.sarxos.webcam.Webcam;
import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import com.jhlabs.image.*;

class VideoConference implements Runnable {
	public String message ="";
	boolean keepListening = true;
	VideoConference(String message)
	{
		this.message = message;
	}
	public static byte[] bufferedImageToJPEGBytes(BufferedImage bi){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", baos);
			return baos.toByteArray();
		} catch (IOException e){
			return null;
		}
	}

	public static BufferedImage jpegBytesToBufferedImage(byte[] bytes){
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			return ImageIO.read(bais);
		} catch (IOException e){
			return null;
		}
	}

	public static JList<String> getAvailableCameraDevice(){
		JFrame f= new JFrame();
		DefaultListModel<String> listModel = new DefaultListModel<>();
		List<String> cameraList = new WebcamsDiscoveryListener().getCameraList();
		for (String s : cameraList) listModel.addElement(s);
		JList<String> list = new JList<>(listModel);
		return list;
	}

	public void run()
	{
		while(keepListening)
		{

			String name = "";
			String ips = "";
			System.out.println("Video class is receiving "+ message);
			StringTokenizer tokens=new StringTokenizer(message);

			String header=tokens.nextToken();
			if(tokens.hasMoreTokens())
				name=tokens.nextToken();

			// Video Receiver -- Caller
			try{
				if(name.equalsIgnoreCase("video1"))
				{
					final int[] flag = {0};

					VideoManager videoManager = new VideoManager();
					Webcam webcam1 = Webcam.getDefault();
					webcam1.setViewSize(new Dimension(640, 480));
					webcam1.open();
					ips = tokens.nextToken();
					String st="",pt="";
					st += ips;
					st = st.replace("/","");
					System.out.println("######### Caller "+st);
					ServerSocket serverSocket = new ServerSocket(8000);
					Socket socket = serverSocket.accept();

					ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

					JLabel label = new JLabel();

					JFrame frame = new JFrame("PC 1");
					frame.setSize(640,530);
					frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
					label = new JLabel();
					label.setSize(640,480);
					label.setVisible(true);
					frame.setLayout(new BorderLayout());
					frame.add(label);
					frame.setLayout(new FlowLayout());
					/////
					JButton btnDialog = new JButton("Cams");
					btnDialog.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ImageIcon icon = new ImageIcon("src/images/icon_cam.png");
							JOptionPane.showMessageDialog(null, getAvailableCameraDevice(), "Available Camera Devices", JOptionPane.PLAIN_MESSAGE, null);
						}
					});
					frame.add(btnDialog);
					/////
					JButton btnOriginal = new JButton("Orig");
					btnOriginal.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 0;
							System.out.println("Click button 1");
						}
					});
					frame.add(btnOriginal);

					JButton btnDiffuseFilter = new JButton("Ani1");
					btnDiffuseFilter.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 1;
							System.out.println("Click button 1");
						}
					});
					frame.add(btnDiffuseFilter);

					JButton btnKalei = new JButton("Ani2");
					btnKalei.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 2;
							System.out.println("Click button 2");
						}
					});
					frame.add(btnKalei);

					JButton btnPrev = new JButton("<<<");
					btnPrev.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.prev(9);
						}
					});
					frame.add(btnPrev);

					JButton btnNext = new JButton(">>>");
					btnNext.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.next(7);
						}
					});
					frame.add(btnNext);

					JButton btnLive = new JButton("|||");
					btnLive.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.live();
						}
					});
					frame.add(btnLive);

					frame.setVisible(true);

					BufferedImage in_image;
					BufferedImage out_image;
					BufferedImage dest_bm1 = null;
					BufferedImage dest_bm2 = null;
					keepListening = false;
					DiffuseFilter df = new DiffuseFilter();
					DisplaceFilter waterFilter = new DisplaceFilter();
					while (true){
						try{
							in_image = jpegBytesToBufferedImage((byte[]) is.readObject());
							videoManager.addImage(in_image);
							dest_bm1 = videoManager.getImage();
							ImageIcon img = new ImageIcon(dest_bm1);
							label.setIcon(img);

							out_image = webcam1.getImage();
							if (flag[0] == 1) df.filter(out_image,dest_bm2);
							else if (flag[0] == 2) waterFilter.filter(out_image,dest_bm2);
							else dest_bm2 = out_image;
							os.writeObject(bufferedImageToJPEGBytes(dest_bm2));
							os.flush();
							os.reset();
						} catch (Exception e){
							continue;
						}
					}
				}
				// Receiver -------------------------------------------------------------
				else if(name.equalsIgnoreCase("video"))
				{

					VideoManager videoManager = new VideoManager();
					final int[] flag = {0};
					Socket rsocket;
					ips = tokens.nextToken();

					String st="",pt="";
					st += ips;
					st = st.replace("/","");
					System.out.println("######### Receiver "+st);

					Webcam webcam = Webcam.getDefault();
					webcam.setViewSize(new Dimension(640, 480));
					webcam.open();
					rsocket = new Socket(st,8000);
					BufferedImage bm = webcam.getImage();

					ObjectOutputStream dout = new ObjectOutputStream(rsocket.getOutputStream());
					ObjectInputStream din = new ObjectInputStream(rsocket.getInputStream());
					ImageIcon is = new ImageIcon(bm);
					JFrame frame = new JFrame("PC 2");
					frame.setSize(640,530);
					frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);

					frame.setLayout(new BorderLayout());
					JLabel l = new JLabel();
					l.setVisible(true);
					frame.add(l);
					frame.setLayout(new FlowLayout());

					/////
					JButton btnDialog = new JButton("Cams");
					btnDialog.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ImageIcon icon = new ImageIcon("src/images/icon_cam.png");
							JOptionPane.showMessageDialog(null, getAvailableCameraDevice(), "Available Camera Devices", JOptionPane.PLAIN_MESSAGE, null);
						}
					});
					frame.add(btnDialog);
					//////////
					JButton btnOriginal = new JButton("Orig");
					btnOriginal.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 0;
							System.out.println("Click button 1");
						}
					});
					frame.add(btnOriginal);
					//////////
					JButton btnDiffuseFilter = new JButton("Ani1");
					btnDiffuseFilter.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 1;
							System.out.println("Click button 1");
						}
					});
					frame.add(btnDiffuseFilter);
					//////////
					JButton btnKalei = new JButton("Ani2");
					btnKalei.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flag[0] = 2;
							System.out.println("Click button 2");
						}
					});
					frame.add(btnKalei);

					JButton btnPrev = new JButton("<<<");
					btnPrev.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.prev(10);
						}
					});
					frame.add(btnPrev);

					JButton btnNext = new JButton(">>>");
					btnNext.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.next(7);
						}
					});
					frame.add(btnNext);

					JButton btnLive = new JButton("|||");
					btnLive.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							videoManager.live();
						}
					});
					frame.add(btnLive);
					frame.setVisible(true);
					////
					DiffuseFilter df = new DiffuseFilter();
					DisplaceFilter waterFilter = new DisplaceFilter();
					BufferedImage image;
					BufferedImage dest_bm =null;
					BufferedImage dest_bm2 =null;
					////
					keepListening = false;

					while(true){
						try{
							bm = webcam.getImage();
							if (flag[0] == 1) df.filter(bm,dest_bm);
							else if (flag[0] == 2) waterFilter.filter(bm,dest_bm);
							else dest_bm = bm;
							dout.writeObject(bufferedImageToJPEGBytes(dest_bm));
							dout.flush();
							dout.reset();

							image = jpegBytesToBufferedImage((byte[]) din.readObject());

							videoManager.addImage(image);
							dest_bm2 = videoManager.getImage();
							ImageIcon img = new ImageIcon(dest_bm2);
							l.setIcon(img);
						}catch (Exception e){
							System.out.println(e.getMessage());
							continue;
						}
					}
				}
			}catch (Exception e){
				System.out.println(e.getMessage() + "what ??????");
			}

		}

	}
}