package com.zaq.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyProtocalClient {
	// mina��������̣�request->MyDecoder->MyHandler->MyEncode->response
	// ���ڳ���>1000�ֽڵ����ݰ����÷ֶη��͡�
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 8686);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
			BufferedReader in = new BufferedReader(inputStreamReader);
			for (int i = 0; i < 2; i++) {
				long startTime = System.currentTimeMillis(); // ��ȡ��ʼʱ��
				String str = "745guy7";
				System.out.println(str.getBytes().length);
				String temp_ = "";
				int SUB_COUNT = 3;// ��1000���ֽ�����ȡ�ַ���
				// �������ݰ�����
				out.writeInt(str.getBytes().length + 4);
				// �ֶ�����������
				for (int c = 0; c < str.getBytes().length / SUB_COUNT; c++) {
					if (temp_ == "" && temp_.length() == 0) {
						temp_ = str.substring(c * SUB_COUNT, SUB_COUNT);
						// ���͵�һ��Ҳ�ǵ�һ������
						out.write(temp_.getBytes());
						out.flush();
					} else {
						// ��1000���ֽڷֶη�������
						out.write(str.substring(c * SUB_COUNT, (c + 1) * SUB_COUNT).getBytes());
						out.flush();
						// ����ʣ��Ĳ���1000���ֽڵ����ݴ�
						int raim_ = str.substring((c + 1) * SUB_COUNT, str.getBytes().length).length();
						if (raim_ < SUB_COUNT && raim_ != 0) {
							out.write(str.substring((c + 1) * SUB_COUNT, str.getBytes().length).getBytes());
							out.flush();
						}
					}
				}
				// ����д�룬�ͷŻ���
//				out.flush();
				System.out.println(i + " sended");

				char temp[] = new char[2700];
				String backLine = "";
				in.read(temp);
				backLine = String.valueOf(temp).trim();
				System.out.println("backLine==" + backLine);
				long endTime = System.currentTimeMillis(); // ��ȡ����ʱ��
				System.out.println("��������ʱ��(����)�� " + (endTime - startTime) + "ms");
			}
			Thread.sleep(1000);
			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
