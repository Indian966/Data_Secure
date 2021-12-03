import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
 
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FileReceiveServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        Socket socket = null;

        // 비밀키 생성
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey secretKey = keyGenerator.generateKey();
		String transformation = "AES/ECB/PKCS5Padding";

        File encryptFile = new File("encrypt.txt");
		File decryptFile = new File("decrypt.txt");
 
        try {
            // 리스너 소켓 생성 후 대기
            serverSocket = new ServerSocket(7777);
            System.out.println("서버가 시작되었습니다.");
 
            // 연결되면 통신용 소켓 생성
            socket = serverSocket.accept();
            System.out.println("클라이언트와 연결되었습니다.");
 
            // 파일 수신 작업 시작
            FileReceiver fr = new FileReceiver(socket);
            fr.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 파일 복호화
		{
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			InputStream input = null;
			OutputStream output = null;
			try {
				input = new CipherInputStream(new BufferedInputStream(new FileInputStream(encryptFile)), cipher);
				output = new BufferedOutputStream(new
				FileOutputStream(decryptFile));
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
			} finally {
				if (output != null) try {output.close();} catch(IOException ie) {}
				if (input != null) try {input.close();} catch(IOException ie) {}
			}
		}
    }
}
 
class FileReceiver extends Thread {
    Socket socket;
    DataInputStream dis;
    FileOutputStream fos;
    BufferedOutputStream bos;
 
    public FileReceiver(Socket socket) {
        this.socket = socket;
    }
 
    @Override
    public void run() {
        try {
            System.out.println("파일 수신 작업을 시작합니다.");
            dis = new DataInputStream(socket.getInputStream());
 
            // 파일명을 전송 받고 파일명 수정.
            String fName = dis.readUTF();
            System.out.println("파일명 " + fName + "을 전송받았습니다.");
            fName = fName.replaceAll("a", "b");
 
            // 파일을 생성하고 파일에 대한 출력 스트림 생성
            File f = new File(fName);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            System.out.println(fName + "파일을 생성하였습니다.");
 
            // 바이트 데이터를 전송받으면서 기록
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = dis.read(data)) != -1) {
                bos.write(data, 0, len);
            }
 
            bos.flush();
            bos.close();
            fos.close();
            dis.close();
            System.out.println("파일 수신 작업을 완료하였습니다.");
            System.out.println("받은 파일의 사이즈 : " + f.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}