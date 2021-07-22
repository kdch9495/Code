//SendClient.java 원본파일
//ReceiveServer과 SendClient로 소켓통신하는 코드

package original_socket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SendClient {

	public static void main(String[] args) {

   String serverIp = "127.0.0.1"; // 서버 IP번호
		int port_num = 152; // 서버 포트번호
		Socket socket = null;

		try {
			// 서버 연결
			socket = new Socket(serverIp, port_num);
			System.out.println("서버에 연결되었습니다.");

			// 파일 전송용 클래스
			String filePath = "C:\\Users\\****\\Desktop\\task\\images\\sender";
			String fileNm = "test01.png"; // 현재 jpg는 안됨. png, dcm, txt 등 됨

//       /*파일 경로에 있는 파일 가져오기*/
//       File path = new File(filePath);
//       /*파일 경로에 있는 파일 리스트 fileList[] 에 넣기*/
//       File []fileList = path.listFiles();

			FileSender fs = new FileSender(socket, filePath, fileNm);
			fs.start();

			// 메세지 전송용 클래스
			/*
			 * String msg = "반가워요!"; MsgSender ms = new MsgSender(socket, msg); ms.start();
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//파일 전송용 클래스 
class FileSender extends Thread {

	String filePath;
	String fileNm;
	Socket socket;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;

	public FileSender(Socket socket, String filePath, String fileNm) {

		this.socket = socket;
		this.filePath = filePath;
		this.fileNm = fileNm;

		try {
			// 데이터 전송용 스트림 생성
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

// @Override
	public void run() {

		try {
			// 파일전송을 서버에 알린다.('file' 구분자 전송)
			dos.writeUTF("file"); //writeUTF:문자열 출력
			dos.flush(); //스트림에 남아있는 데이터를 강제로 보내주는 역할

			// 전송할 파일을 읽어서 Socket Server에 전송
			String result = fileRead(dos);
			/* test */System.out.println("result : " + result);

		} catch (IOException e) {
			e.printStackTrace();
		} finally { // 리소스 초기화
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private String fileRead(DataOutputStream dos) {

		String result;

		try {
			dos.writeUTF(fileNm);
			/* test */System.out.println("파일 이름(" + fileNm + ")을 전송하였습니다.");

			// 파일을 읽어서 서버에 전송

			File file = new File(filePath + "/" + fileNm);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			int len;
			int size = 8*1024;
			byte[] data = new byte[size];
			while ((len = bis.read(data)) != -1) {
				dos.write(data, 0, len);
			}

			// 서버에 전송
			dos.flush();

//        -- 먹통된다.
//       DataInputStream dis = new DataInputStream(socket.getInputStream());
//       result = dis.readUTF();
//       if( result.equals("SUCCESS") ){
//            /*test*/System.out.println("파일 전송 작업을 완료하였습니다.");
//            /*test*/System.out.println("보낸 파일의 사이즈 : " + file.length());
//       }else{
//            /*test*/System.out.println("파일 전송 실패!.");
//       }

			result = "SUCCESS";
		} catch (IOException e) {
			e.printStackTrace();
			result = "ERROR";
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}

//메세지 전송용 클래스
class MsgSender extends Thread {

	Socket socket;
	String msg;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;

	public MsgSender(Socket socket, String msg) {

		this.socket = socket;
		this.msg = msg;

		try {
			// 데이터 전송용 스트림 생성
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//@Override
	public void run() {

		try {

			// 파일전송 구분자 전송('msg' 전송)

			dos.writeUTF("msg");
			dos.flush(); // 서버에 전송

			dos.writeUTF(msg);
			dos.flush(); // 서버에 전송

			/* test */System.out.println("[" + msg + "] 전송");
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // 리소스 해제
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
