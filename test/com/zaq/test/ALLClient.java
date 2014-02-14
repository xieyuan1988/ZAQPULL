package com.zaq.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zaq.business.dao.BaseDao;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.AppUser;
import com.zaq.core.vo.Login;

public class ALLClient {   

    static int SIZE = 1;   
    static InetSocketAddress ip = new InetSocketAddress("localhost", 8686);   
//    static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();   
//    static CharsetDecoder decoder=Charset.forName("UTF-8").newDecoder();

    static class Message implements Runnable {   
        protected String name;   
        String msg = "";   

        public Message(String index) {   
            this.name = index;   
        }   

        public void run() {   
            try {   
                //打开Socket通道   
                SocketChannel client = SocketChannel.open();   
                //设置为非阻塞模式   
                client.configureBlocking(false);   
                //打开选择器   
                Selector selector = Selector.open();   
                //注册连接服务端socket动作   
                client.register(selector, SelectionKey.OP_CONNECT);   
                //连接   
                client.connect(ip);

                //分配内存   
                ByteBuffer buffer = ByteBuffer.allocate(12);   
                int total = 0;   

               Exception: while(true) {   
                    selector.select();   
                    Iterator iter = selector.selectedKeys().iterator();   

                    while (iter.hasNext()) {   
                        SelectionKey key = (SelectionKey) iter.next();   
                        iter.remove();   
                        if (key.isConnectable()) {   
                            SocketChannel channel = (SocketChannel) key   
                                    .channel();   
                            if (channel.isConnectionPending())
								try {
									channel.finishConnect();
								} catch (Exception e) {
									e.printStackTrace();//TODO 连接不上
								}   
                            channel.write(ByteBuffer.wrap(name.getBytes()));   

                            channel.register(selector, SelectionKey.OP_READ);   
                        } else if (key.isReadable()) {   
                            SocketChannel channel = (SocketChannel) key   
                                    .channel();   
                            int count=0;   
                            ByteArrayOutputStream bos=new ByteArrayOutputStream();
                            while((count = channel.read(buffer))> 0){
                            	total += count; 
                            	buffer.flip(); 
//                            	CharBuffer charBuffer = decoder.decode(buffer.);
                            	byte[] tmp=buffer.array();
                            	bos.write(tmp,0,count);
                            	
                                buffer.clear(); 
                            }
                            System.out.println(new String(bos.toByteArray(),"utf-8"));
                            bos.close();
                            if(count==0){
                            	System.out.println("XXXXXXXXXXXXXXXX"+count);
                            }
                            if(count==-1){//通道关闭
                            	System.out.println(channel.isOpen()+"OOOOOOOOOOOOOOOO"+count);
                            	channel.close();
                            	System.out.println(channel.isOpen()+"OOOOOOOOOOOOOOOO"+count);
                            	break Exception;
                            }
                        }   
                    }   
                }   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }   
    }   

    public static void main(String[] args) throws IOException {   
       AppUtil.init();
       BaseDao db = BaseDao.getInstance();  
       
       Type type=new TypeToken<JsonPacket<Login>>(){}.getType();
       Gson gson=new GsonBuilder().create();
       
       String sql="select * from app_user";  
       List<AppUser> us=null;;
	try {
		us = BaseDao.getInstance().queryForOList(AppUser.class,sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}          
       for(AppUser u:us){  
           System.out.println(u.getFullname());  
       	
           Login login=new Login();  
           login.setPassword("zaq123");
           login.setUserName(u.getUsername());
           
           JsonPacket<Login> packet=new JsonPacket<Login>(login);
           
           new Thread(new Message(gson.toJson(packet,type))).start(); 
       }  
         
    
        
       
//        Type type=ParameterizedTypeImpl.make(JsonPacket.class, new Type[]{Login.class}, null);
//        Object o2= new TypeToken<JsonPacket<Login>>(){}.getClass();
//        Object o1= new TypeToken<JsonPacket<Login>>(){}.getClass().getSuperclass();
//        Object o= new TypeToken<JsonPacket<Login>>(){}.getClass().getGenericSuperclass();
        
          

    }   
}  