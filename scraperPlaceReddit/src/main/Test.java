package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {
	public static void CreateThread() {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(long i=1649012567221L;i<1649116967221L;i+=60000*4) {
			System.out.println("---");
			Runnable thread=new NewThread(i);
			executor.execute(thread);
		}
//		new NewThread(0);
	}
	public static void main(String[] args) {
		File[] listFile=new File("src/data").listFiles();
		BufferedWriter wr=null;
		try {
			wr=new BufferedWriter(new FileWriter("src/data.txt"));
			int count=0;
			ArrayList<String> list=new ArrayList<>();
			for(File file:listFile) {
				list.add(file.getName());
				count++;
			}
			System.out.println(list.size());
			for (String text:list){
				System.out.println("{nameFile:\""+text+"\"},");
				wr.write(text+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
class NewThread implements Runnable {  
    long time;
    NewThread (long time){  
        this.time=time;
        Thread thread = new Thread(this);  
        thread.start();  
    }  
    public void run() {  
    	try {
    		getUrlImg(time);
//    		for(long i=1649012567221L;i<1649116967221L;i+=60000*4) {
//    			getUrlImg(i);
//    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void getUrlImg(long timestamp) throws Exception {
		String apiurl="https://gql-realtime-2.reddit.com/query";
		URL url = new URL(apiurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer -KMK6BJxu89nnpcL6s_IabjRieSgsBw");
        String payload = "{\"operationName\":\"frameHistory\",\"variables\":{\"input\":{\"actionName\":\"get_frame_history\",\"GetFrameHistoryMessageData\":"
        		+ "{\"timestamp\":"+timestamp+"}}},\"query\":\"mutation frameHistory($input: ActInput!) {\\n  act(input: $input) "
        		+ "{\\n    data {\\n      ... on BasicMessage {\\n        id\\n        data {\\n          ... on GetFrameHistoryResponseMessageData {\\n            frames {\\n              canvasIndex\\n              url\\n              __typename\\n            }\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";// This should be your json body i.e. {"Name" : "Mohsin"} 
        byte[] out = payload.getBytes(StandardCharsets.UTF_8);
        OutputStream stream = connection.getOutputStream();
        stream.write(out);
        BufferedReader br = null;
        if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
//        System.out.println(br.readLine());
        char[] w= {'u','r','l'};
        ArrayList<String> listLink=new ArrayList<String>();
        while (br.ready()) {
        	boolean status=true;
        	for(int i=0;i<w.length;i++) {
        		if (w[i]!=(char)br.read()) {
					status=false;
				}
        	}
        	if (status) {
        		String link="";
		        for(int i=0;i<83;i++) {
		        	link+=(char)br.read();
		        }
		        listLink.add(link.substring(3, link.length()));
			}
        }
//        for(int i=0;i<listLink.size();i++) {
//        	downImg(listLink.get(i), i+"-"+timestamp);
//        }
        Merging(listLink,timestamp);
        connection.disconnect();
	}
//	public static void downImg(String link, String name) throws Exception {
//			System.out.println(link);
//			URL urlImg = new URL(link);
//	    	InputStream inputStream = new BufferedInputStream(urlImg.openStream());
//	    	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("src/data/"+name+".png"));
//	    	for ( int j; (j = inputStream.read()) != -1; ) {
//	    	    outputStream.write(j);
//	    	}
//	    	inputStream.close();
//	    	outputStream.close();
//	}
	public static void Merging(ArrayList<String>list,long name){
		try {
			URL urlImg;
			BufferedImage combined=null;
			if (list.size()==1) {
				urlImg = new URL(list.get(0));
		    	InputStream inputStream1 = new BufferedInputStream(urlImg.openStream());
		    	// load source images
		    	BufferedImage image1 = ImageIO.read(inputStream1);

		    	// create the new image, canvas size is the max. of both image sizes
		    	int w = image1.getWidth();
		    	int h = image1.getWidth();
		    	combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		    	// paint both images, preserving the alpha channels
		    	Graphics g = combined.getGraphics();
		    	g.drawImage(image1, 0, 0, null);
		    	
		    	g.dispose();
		   	
		    	inputStream1.close();
			}
			if (list.size()==2) {
				String[] index = list.get(0).split("-");
				urlImg = new URL(list.get(Integer.parseInt(index[3])));
		    	InputStream inputStream1 = new BufferedInputStream(urlImg.openStream());
		    	index = list.get(1).split("-");
		    	urlImg = new URL(list.get(Integer.parseInt(index[3])));
		    	InputStream inputStream2 = new BufferedInputStream(urlImg.openStream());
		    	// load source images
		    	BufferedImage image1 = ImageIO.read(inputStream1);
		    	BufferedImage image2 = ImageIO.read(inputStream2);

		    	// create the new image, canvas size is the max. of both image sizes
		    	int w = image1.getWidth()*2;
		    	int h = image1.getWidth();
		    	combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		    	// paint both images, preserving the alpha channels
		    	Graphics g = combined.getGraphics();
		    	g.drawImage(image1, 0, 0, null);
		    	g.drawImage(image2, image1.getWidth(), 0, null);
		    	
		    	g.dispose();
		    	
		    	inputStream1.close();
		    	inputStream2.close();
			}
			if (list.size()==4) {
				System.out.println(list.get(0)+"---"+list.get(1));
				System.out.println(list.get(2)+"---"+list.get(3));
				BufferedImage image1=null;
				BufferedImage image2=null;
				BufferedImage image3=null;
				BufferedImage image4=null;
				for(String linkImg:list) {
					String[] index = linkImg.split("-");
					urlImg = new URL(linkImg);
					if(Integer.parseInt(index[3])==0){
						InputStream inputStream1 = new BufferedInputStream(urlImg.openStream());
						image1 = ImageIO.read(inputStream1);
					}
					if(Integer.parseInt(index[3])==1){
						InputStream inputStream2 = new BufferedInputStream(urlImg.openStream());
						image2 = ImageIO.read(inputStream2);
					}
					if(Integer.parseInt(index[3])==2){
						InputStream inputStream3 = new BufferedInputStream(urlImg.openStream());
						image3 = ImageIO.read(inputStream3);
					}
					if(Integer.parseInt(index[3])==3){
						InputStream inputStream4 = new BufferedInputStream(urlImg.openStream());
						image4 = ImageIO.read(inputStream4);
					}
				}
				
//				String[] index = list.get(0).split("-");
//				urlImg = new URL(list.get(Integer.parseInt(index[3])));
//		    	InputStream inputStream1 = new BufferedInputStream(urlImg.openStream());
//		    	index = list.get(1).split("-");
//		    	urlImg = new URL(list.get(Integer.parseInt(index[3])));
//		    	InputStream inputStream2 = new BufferedInputStream(urlImg.openStream());
//		    	index = list.get(2).split("-");
//		    	urlImg = new URL(list.get(Integer.parseInt(index[3])));
//		    	InputStream inputStream3 = new BufferedInputStream(urlImg.openStream());
//		    	index = list.get(3).split("-");
//		    	urlImg = new URL(list.get(Integer.parseInt(index[3])));
//		    	InputStream inputStream4 = new BufferedInputStream(urlImg.openStream());
		    	
		    	
		    	// load source images
//		    	BufferedImage image1 = ImageIO.read(inputStream1);
//		    	BufferedImage image2 = ImageIO.read(inputStream2);
//		    	BufferedImage image3 = ImageIO.read(inputStream3);
//		    	BufferedImage image4 = ImageIO.read(inputStream4);

		    	// create the new image, canvas size is the max. of both image sizes
		    	int w = image1.getWidth()*2;
		    	int h = image1.getWidth()*2;
		    	combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		    	// paint both images, preserving the alpha channels
		    	Graphics g = combined.getGraphics();
		    	g.drawImage(image1, 0, 0, null);
		    	g.drawImage(image2, image1.getWidth(), 0, null);
		    	g.drawImage(image3, 0, image1.getHeight(), null);
		    	g.drawImage(image4, image1.getWidth(), image1.getHeight(), null);
		    	
		    	g.dispose();
			}
			
	    	// Save as new image
//			System.out.println(list.size());
			System.out.println(name+".png");
	    	ImageIO.write(combined, "PNG", new File("src/data/"+name+".png"));
		} catch (Exception e) {
			// TODO: handle exception
			
		}
	}
}  
